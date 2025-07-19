package com.example.mentorlink_project.features.group.view;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.entities.AccountEntity;
import com.example.mentorlink_project.data.entities.GroupEntity;
import com.example.mentorlink_project.data.entities.GroupMemberEntity;
import com.example.mentorlink_project.data.repositories.AccountRepository;
import com.example.mentorlink_project.data.repositories.GroupMemberRepository;
import com.example.mentorlink_project.data.repositories.GroupRepository;
import com.example.mentorlink_project.features.login.LoginActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupDetailActivity extends AppCompatActivity {
    private EditText edtGroupName;
    private EditText[] memberIdFields;
    private TextView[] memberNameViews;
    private Button[] removeButtons;
    private Button btnCreateGroup, btnSave, btnClear, btnLogout;
    private String userCode;
    private GroupMemberRepository groupMemberRepo;
    private GroupRepository groupRepo;
    private AccountRepository accountRepo;
    private Button btnTransferLeader;
    private int groupId = -1;
    private boolean isCreatingNewGroup = false;
    private Set<String> originalMemberIds = new HashSet<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isCurrentUserLeader = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        initializeRepositories();
        setupUserInfo();
        initializeViews();
        checkUserGroupStatus();

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("GROUP_ID", groupId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void initializeRepositories() {
        groupMemberRepo = new GroupMemberRepository(this);
        groupRepo = new GroupRepository(this);
        accountRepo = new AccountRepository(this);
    }

    private void setupUserInfo() {
        userCode = getIntent().getStringExtra("USER_CODE");
        if (userCode == null || userCode.trim().isEmpty()) {
            showToast("Invalid user information");
            finish();
            return;
        }

        AccountEntity currentUser = accountRepo.getAccountByUserCode(userCode);
        if (currentUser != null && "LECTURER".equalsIgnoreCase(currentUser.getRole())) {
            showToast("Lecturers cannot access group management");
            finish();
        }
    }

    private void initializeViews() {
        edtGroupName = findViewById(R.id.edt_group_name);

        memberIdFields = new EditText[]{
                findViewById(R.id.edt_member_id_1),
                findViewById(R.id.edt_member_id_2),
                findViewById(R.id.edt_member_id_3),
                findViewById(R.id.edt_member_id_4),
                findViewById(R.id.edt_member_id_5)
        };

        memberNameViews = new TextView[]{
                findViewById(R.id.tv_member_name_1),
                findViewById(R.id.tv_member_name_2),
                findViewById(R.id.tv_member_name_3),
                findViewById(R.id.tv_member_name_4),
                findViewById(R.id.tv_member_name_5)
        };

        removeButtons = new Button[]{
                null,
                findViewById(R.id.btn_remove_2),
                findViewById(R.id.btn_remove_3),
                findViewById(R.id.btn_remove_4),
                findViewById(R.id.btn_remove_5)
        };

        btnCreateGroup = findViewById(R.id.btn_create_group);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);
        btnTransferLeader = findViewById(R.id.btn_transfer_leader_1);

        AccountEntity currentUser = accountRepo.getAccountByUserCode(userCode);
        if (currentUser != null) {
            memberIdFields[0].setText(userCode);
            memberIdFields[0].setEnabled(false);
            memberNameViews[0].setText(currentUser.getFullName() + " (You - Leader)");
        }

        setupButtonListeners();
        setupTextWatchers();
    }

    private void setupButtonListeners() {
        btnCreateGroup.setOnClickListener(v -> validateAndCreateGroup());
        btnSave.setOnClickListener(v -> saveChanges());
        btnClear.setOnClickListener(v -> clearAllFields());
        btnTransferLeader.setOnClickListener(v -> transferLeaderRole());

        for (int i = 1; i < removeButtons.length; i++) {
            final int index = i;
            removeButtons[i].setOnClickListener(v -> removeMember(index));
        }
    }

    private void setupTextWatchers() {
        TextWatcher memberWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateMembers();
                updateSaveButtonState();
            }
        };

        for (int i = 1; i < memberIdFields.length; i++) {
            memberIdFields[i].addTextChangedListener(memberWatcher);
        }

        edtGroupName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateSaveButtonState();
            }
        });
    }

    private void checkUserGroupStatus() {
        executorService.execute(() -> {
            groupMemberRepo.refreshDatabaseConnection();
            boolean hasGroup = groupMemberRepo.isUserInAnyGroup(userCode);
            mainHandler.post(() -> {
                if (hasGroup) {
                    groupId = groupMemberRepo.getGroupIdByUserCode(userCode);
                    if (groupId != -1) {
                        showExistingGroupDetails(groupId);
                    } else {
                        showErrorAndFinish("Error loading group");
                    }
                } else {
                    setupGroupCreation();
                }
            });
        });
    }

    private void setupGroupCreation() {
        isCreatingNewGroup = true;
        btnCreateGroup.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        btnClear.setVisibility(View.VISIBLE);
        enableAllFields();
    }

    private void showExistingGroupDetails(int groupId) {
        executorService.execute(() -> {
            GroupEntity group = groupRepo.getGroupById(groupId);
            if (group != null) {
                List<GroupMemberEntity> members = groupMemberRepo.getMembersByGroupId(groupId);

                List<GroupMemberEntity> nonLecturerMembers = new ArrayList<>();
                for (GroupMemberEntity member : members) {
                    AccountEntity acc = accountRepo.getAccountByUserCode(member.getUserCode());
                    if (acc != null && !"LECTURER".equalsIgnoreCase(acc.getRole())) {
                        nonLecturerMembers.add(member);
                        originalMemberIds.add(member.getUserCode());
                        if (member.getUserCode().equals(userCode)) {
                            isCurrentUserLeader = "LEADER".equals(member.getRoles());
                        }
                    }
                }

                mainHandler.post(() -> {
                    edtGroupName.setText(group.getName());
                    edtGroupName.setEnabled(isCurrentUserLeader);

                    for (int i = 0; i < nonLecturerMembers.size() && i < memberIdFields.length; i++) {
                        GroupMemberEntity member = nonLecturerMembers.get(i);
                        AccountEntity acc = accountRepo.getAccountByUserCode(member.getUserCode());
                        String displayName = acc != null ? acc.getFullName() : member.getUserCode();

                        memberIdFields[i].setText(member.getUserCode());
                        if (i == 0) {
                            memberNameViews[i].setText(displayName + (isCurrentUserLeader ? " (Leader)" : ""));
                        } else {
                            memberNameViews[i].setText(displayName);
                        }
                    }

                    for (int i = nonLecturerMembers.size(); i < memberIdFields.length; i++) {
                        memberIdFields[i].setText("");
                        memberNameViews[i].setText("");
                    }

                    setupUIBasedOnRole();
                });
            } else {
                showErrorAndFinish("Error loading group");
            }
        });
    }

    private void setupUIBasedOnRole() {
        if (isCreatingNewGroup) {
            enableAllFields();
            return;
        }

        for (int i = 1; i < memberIdFields.length; i++) {
            memberIdFields[i].setEnabled(isCurrentUserLeader);
            if (removeButtons[i] != null) {
                removeButtons[i].setVisibility(isCurrentUserLeader ? View.VISIBLE : View.GONE);
            }
        }

        btnCreateGroup.setVisibility(View.GONE);
        btnSave.setVisibility(isCurrentUserLeader ? View.VISIBLE : View.GONE);
        btnClear.setVisibility(isCurrentUserLeader ? View.VISIBLE : View.GONE);
        btnTransferLeader.setVisibility(isCurrentUserLeader ? View.VISIBLE : View.GONE);
    }

    private void validateAndCreateGroup() {
        String groupName = edtGroupName.getText().toString().trim();
        if (groupName.isEmpty()) {
            showToast("Please enter group name");
            return;
        }

        List<String> memberIds = getCurrentMemberIds();
        if (memberIds.size() < 3) {
            showToast("Group must have at least 3 members");
            return;
        }

        if (hasDuplicateMembers()) {
            showToast("Duplicate member IDs found");
            return;
        }

        if (!validateAllMembers()) {
            showToast("Some members are invalid");
            return;
        }

        createGroup(groupName, memberIds);
    }

    private void createGroup(String groupName, List<String> memberIds) {
        executorService.execute(() -> {
            SQLiteDatabase db = null;
            try {
                db = groupRepo.getWritableDatabase();
                db.beginTransaction();

                // Create group
                ContentValues groupValues = new ContentValues();
                groupValues.put("name", groupName);
                groupValues.put("project_id", 0);
                long groupId = db.insert("GroupProject", null, groupValues);

                if (groupId == -1) {
                    throw new Exception("Failed to create group");
                }

                // Add creator as leader
                ContentValues leaderValues = new ContentValues();
                leaderValues.put("group_id", groupId);
                leaderValues.put("user_code", userCode);
                leaderValues.put("roles", "LEADER");
                db.insert("GroupMember", null, leaderValues);

                // Add other members
                for (String memberId : memberIds) {
                    if (!memberId.equals(userCode)) {
                        ContentValues memberValues = new ContentValues();
                        memberValues.put("group_id", groupId);
                        memberValues.put("user_code", memberId);
                        memberValues.put("roles", "MEMBER");
                        db.insert("GroupMember", null, memberValues);
                    }
                }

                db.setTransactionSuccessful();

                mainHandler.post(() -> {
                    showToast("Group created successfully");
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                Log.e("GroupDetail", "Error creating group", e);
                mainHandler.post(() -> showToast("Error creating group: " + e.getMessage()));
            } finally {
                if (db != null) {
                    try {
                        db.endTransaction();
                        db.close();
                    } catch (Exception e) {
                        Log.e("GroupDetail", "Error closing DB", e);
                    }
                }
            }
        });
    }

    private void saveChanges() {
        if (groupId == -1) return;
        if (!isCurrentUserLeader) {
            showToast("Only leaders can save changes");
            return;
        }

        String groupName = edtGroupName.getText().toString().trim();
        if (groupName.isEmpty()) {
            showToast("Please enter group name");
            return;
        }

        List<String> currentMemberIds = getCurrentMemberIds();
        if (currentMemberIds.size() < 3) {
            showToast("Group must have at least 3 members");
            return;
        }

        if (hasDuplicateMembers()) {
            showToast("Duplicate member IDs found");
            return;
        }

        if (!validateAllMembers()) {
            showToast("Some members are invalid");
            return;
        }

        updateGroup(groupName, currentMemberIds);
    }

    private void updateGroup(String groupName, List<String> currentMemberIds) {
        executorService.execute(() -> {
            try {
                // Update group name
                GroupEntity group = groupRepo.getGroupById(groupId);
                if (group != null) {
                    group.setName(groupName);
                    groupRepo.updateGroup(group);
                }

                // Update members
                Set<String> currentMembers = new HashSet<>(currentMemberIds);

                // Remove deleted members
                for (String originalId : originalMemberIds) {
                    if (!currentMembers.contains(originalId)) {
                        groupMemberRepo.removeMember(groupId, originalId);
                    }
                }

                // Add new members
                for (String memberId : currentMemberIds) {
                    if (!originalMemberIds.contains(memberId)) {
                        GroupMemberEntity member = new GroupMemberEntity();
                        member.setGroupId(groupId);
                        member.setUserCode(memberId);
                        member.setRoles("MEMBER");
                        groupMemberRepo.insertGroupMember(member);
                    }
                }

                mainHandler.post(() -> {
                    showToast("Changes saved successfully");
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                mainHandler.post(() -> showToast("Error saving changes"));
            }
        });
    }

    private boolean isCurrentUserLeader() {
        if (groupId == -1) return false;
        GroupMemberEntity member = groupMemberRepo.getMemberByUserCode(userCode);
        return member != null && "LEADER".equals(member.getRoles());
    }

    private void removeMember(int memberIndex) {
        if (!isCurrentUserLeader) {
            showToast("Only leaders can remove members");
            return;
        }
        if (memberIndex < 1 || memberIndex >= memberIdFields.length) return;

        String memberId = memberIdFields[memberIndex].getText().toString().trim();
        if (memberId.isEmpty()) return;

        new AlertDialog.Builder(this)
                .setTitle("Remove Member")
                .setMessage("Are you sure you want to remove this member?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    memberIdFields[memberIndex].setText("");
                    memberNameViews[memberIndex].setText("");
                    updateSaveButtonState();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void transferLeaderRole() {
        if (!isCurrentUserLeader) {
            showToast("Only leaders can transfer leadership");
            return;
        }
        List<String> memberOptions = new ArrayList<>();
        List<String> memberIds = new ArrayList<>();

        for (int i = 1; i < memberIdFields.length; i++) {
            String memberId = memberIdFields[i].getText().toString().trim();
            if (!memberId.isEmpty()) {
                memberOptions.add(memberNameViews[i].getText() + " (" + memberId + ")");
                memberIds.add(memberId);
            }
        }

        if (memberOptions.isEmpty()) {
            showToast("No members available");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Transfer Leadership")
                .setItems(memberOptions.toArray(new String[0]), (dialog, which) -> {
                    if (which >= 0 && which < memberIds.size()) {
                        transferLeadership(memberIds.get(which));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void transferLeadership(String newLeaderId) {
        executorService.execute(() -> {
            try {
                groupMemberRepo.updateMemberRole(groupId, userCode, "MEMBER");
                groupMemberRepo.updateMemberRole(groupId, newLeaderId, "LEADER");

                mainHandler.post(() -> {
                    showToast("Leadership transferred");
                    finish();
                });
            } catch (Exception e) {
                mainHandler.post(() -> showToast("Error transferring leadership"));
            }
        });
    }

    private List<String> getCurrentMemberIds() {
        Set<String> memberIds = new HashSet<>();
        memberIds.add(userCode); // Always include creator as leader

        for (int i = 1; i < memberIdFields.length; i++) {
            String memberId = memberIdFields[i].getText().toString().trim();
            if (!memberId.isEmpty() && !memberId.equals(userCode)) {
                memberIds.add(memberId);
            }
        }
        return new ArrayList<>(memberIds);
    }
    private boolean hasDuplicateMembers() {
        Set<String> memberIds = new HashSet<>();
        memberIds.add(userCode);

        for (int i = 1; i < memberIdFields.length; i++) {
            String memberId = memberIdFields[i].getText().toString().trim();
            if (!memberId.isEmpty() && !memberIds.add(memberId)) {
                return true;
            }
        }
        return false;
    }

    private boolean validateAllMembers() {
        boolean allValid = true;

        for (int i = 1; i < memberIdFields.length; i++) {
            String memberId = memberIdFields[i].getText().toString().trim();
            if (!memberId.isEmpty() && !validateMember(i, memberId)) {
                allValid = false;
            }
        }
        return allValid;
    }

    private boolean validateMember(int index, String memberId) {
        // Prevent adding yourself as a member (you're already the leader)
        if (memberId.equals(userCode)) {
            memberNameViews[index].setText("You are already the leader");
            return false;
        }

        AccountEntity member = accountRepo.getAccountByUserCode(memberId);
        if (member == null) {
            memberNameViews[index].setText("User not found");
            return false;
        }

        // Explicitly block lecturers from being added
        if ("LECTURER".equalsIgnoreCase(member.getRole())) {
            memberNameViews[index].setText("Lecturers cannot be group members");
            return false;
        }

        // Check if user is in another group (only if we're editing an existing group)
        if (groupId != -1) {
            int userGroupId = groupMemberRepo.getGroupIdByUserCode(memberId);
            if (userGroupId != -1 && userGroupId != groupId) {
                memberNameViews[index].setText("Already in another group");
                return false;
            }
        } else {
            // For new group creation, just check if user is in any group
            if (groupMemberRepo.isUserInAnyGroup(memberId)) {
                memberNameViews[index].setText("Already in another group");
                return false;
            }
        }

        memberNameViews[index].setText(member.getFullName());
        return true;
    }

    private void validateMembers() {
        for (int i = 1; i < memberIdFields.length; i++) {
            String memberId = memberIdFields[i].getText().toString().trim();
            if (!memberId.isEmpty()) {
                validateMember(i, memberId);
            } else {
                memberNameViews[i].setText("");
            }
        }
    }

    private void updateSaveButtonState() {
        if (isCreatingNewGroup) {
            boolean isValid = !edtGroupName.getText().toString().trim().isEmpty() &&
                    getCurrentMemberIds().size() >= 3 &&
                    !hasDuplicateMembers() &&
                    validateAllMembers();

            btnCreateGroup.setEnabled(isValid);
        } else {
            btnSave.setEnabled(hasChanges());
        }
    }

    private boolean hasChanges() {
        GroupEntity group = groupRepo.getGroupById(groupId);
        if (group != null && !group.getName().equals(edtGroupName.getText().toString().trim())) {
            return true;
        }

        Set<String> currentMembers = new HashSet<>(getCurrentMemberIds());
        if (currentMembers.size() != originalMemberIds.size()) {
            return true;
        }

        for (String memberId : currentMembers) {
            if (!originalMemberIds.contains(memberId)) {
                return true;
            }
        }

        return false;
    }

    private void clearAllFields() {
        edtGroupName.setText("");
        for (int i = 1; i < memberIdFields.length; i++) {
            memberIdFields[i].setText("");
            memberNameViews[i].setText("");
        }
        updateSaveButtonState();
    }

    private void enableAllFields() {
        edtGroupName.setEnabled(true);
        for (int i = 1; i < memberIdFields.length; i++) {
            memberIdFields[i].setEnabled(true);
            if (removeButtons[i] != null) {
                removeButtons[i].setVisibility(View.VISIBLE);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorAndFinish(String message) {
        mainHandler.post(() -> {
            showToast(message);
            finish();
        });
    }
}