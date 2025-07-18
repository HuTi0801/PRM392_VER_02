package com.example.mentorlink_project.features.group.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.entities.AccountEntity;
import com.example.mentorlink_project.data.entities.GroupEntity;
import com.example.mentorlink_project.data.entities.GroupMemberEntity;
import com.example.mentorlink_project.data.repositories.AccountRepository;
import com.example.mentorlink_project.data.repositories.GroupMemberRepository;
import com.example.mentorlink_project.data.repositories.GroupRepository;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {
    private EditText edtGroupName;
    private EditText edtMemberId1, edtMemberId2, edtMemberId3, edtMemberId4, edtMemberId5;
    private TextView tvMemberName1, tvMemberName2, tvMemberName3, tvMemberName4, tvMemberName5;
    private Button btnCreateGroup;
    private String userCode;
    private GroupMemberRepository groupMemberRepo;
    private GroupRepository groupRepo;
    private AccountRepository accountRepo;
    private Button btnEditName, btnRemove2, btnRemove3, btnRemove4, btnRemove5, btnTransferLeader, btnAddMember;
    private boolean isEditMode = false;
    private int groupId = -1; // Initialize with invalid ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        try {
            initializeRepositories();
            setupUserInfo();
            initializeViews();
            checkUserGroupStatus();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeRepositories() {
        groupMemberRepo = new GroupMemberRepository(this);
        groupRepo = new GroupRepository(this);
        accountRepo = new AccountRepository(this);
    }

    private void setupUserInfo() {
        userCode = getIntent().getStringExtra("USER_CODE");
        if (userCode == null || userCode.trim().isEmpty()) {
            Toast.makeText(this, "Invalid user information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void initializeViews() {
        edtGroupName = findViewById(R.id.edt_group_name);
        edtMemberId1 = findViewById(R.id.edt_member_id_1);
        edtMemberId2 = findViewById(R.id.edt_member_id_2);
        edtMemberId3 = findViewById(R.id.edt_member_id_3);
        edtMemberId4 = findViewById(R.id.edt_member_id_4);
        edtMemberId5 = findViewById(R.id.edt_member_id_5);

        tvMemberName1 = findViewById(R.id.tv_member_name_1);
        tvMemberName2 = findViewById(R.id.tv_member_name_2);
        tvMemberName3 = findViewById(R.id.tv_member_name_3);
        tvMemberName4 = findViewById(R.id.tv_member_name_4);
        tvMemberName5 = findViewById(R.id.tv_member_name_5);

        btnCreateGroup = findViewById(R.id.btn_create_group);
        btnEditName = findViewById(R.id.btn_edit_group_name);
        btnRemove2 = findViewById(R.id.btn_remove_2);
        btnRemove3 = findViewById(R.id.btn_remove_3);
        btnRemove4 = findViewById(R.id.btn_remove_4);
        btnRemove5 = findViewById(R.id.btn_remove_5);
        btnTransferLeader = findViewById(R.id.btn_transfer_leader_1);
        btnAddMember = findViewById(R.id.btn_add_member);

        // Set up leader information
        AccountEntity leader = accountRepo.getAccountByUserCode(userCode);
        if (leader != null) {
            edtMemberId1.setText(userCode);
            edtMemberId1.setEnabled(false);
            tvMemberName1.setText(leader.getFullName() + " (Leader)");
        }

        setupButtons();
    }

    private void setupButtons() {
        if (btnEditName != null) {
            btnEditName.setOnClickListener(v -> toggleEditMode());
        }
        if (btnRemove2 != null) {
            btnRemove2.setOnClickListener(v -> removeMember(2));
        }
        if (btnRemove3 != null) {
            btnRemove3.setOnClickListener(v -> removeMember(3));
        }
        if (btnRemove4 != null) {
            btnRemove4.setOnClickListener(v -> removeMember(4));
        }
        if (btnRemove5 != null) {
            btnRemove5.setOnClickListener(v -> removeMember(5));
        }
        if (btnTransferLeader != null) {
            btnTransferLeader.setOnClickListener(v -> transferLeaderRole());
        }
        if (btnAddMember != null) {
            btnAddMember.setOnClickListener(v -> addMember());
        }
    }

    /**
     * Check if the current user is the leader of the group
     */
    private boolean isCurrentUserLeader() {
        try {
            if (groupId == -1) return false;

            List<GroupMemberEntity> members = groupMemberRepo.getMembersByGroupId(groupId);
            if (members != null) {
                for (GroupMemberEntity member : members) {
                    if (member.getUserCode().equals(userCode) && "LEADER".equals(member.getRoles())) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Update UI elements based on leader permissions
     */
    private void updateUIBasedOnLeaderPermissions() {
        boolean isLeader = isCurrentUserLeader();

        // Show/hide management buttons based on leader status
        if (btnEditName != null) {
            btnEditName.setVisibility(isLeader ? View.VISIBLE : View.GONE);
        }
        if (btnRemove2 != null) {
            btnRemove2.setVisibility(isLeader ? View.VISIBLE : View.GONE);
        }
        if (btnRemove3 != null) {
            btnRemove3.setVisibility(isLeader ? View.VISIBLE : View.GONE);
        }
        if (btnRemove4 != null) {
            btnRemove4.setVisibility(isLeader ? View.VISIBLE : View.GONE);
        }
        if (btnRemove5 != null) {
            btnRemove5.setVisibility(isLeader ? View.VISIBLE : View.GONE);
        }
        if (btnTransferLeader != null) {
            btnTransferLeader.setVisibility(isLeader ? View.VISIBLE : View.GONE);
        }
        if (btnAddMember != null) {
            btnAddMember.setVisibility(isLeader ? View.VISIBLE : View.GONE);
        }

        // Update remove buttons visibility based on member count
        if (isLeader) {
            updateRemoveButtonsVisibility();
        }
    }

    private void checkUserGroupStatus() {
        try {
            if (groupMemberRepo.isUserInAnyGroup(userCode)) {
                // User already has a group - Get group ID and show details
                groupId = groupMemberRepo.getGroupIdByUserCode(userCode);
                if (groupId != -1) {
                    showExistingGroupDetails(groupId);
                    updateUIBasedOnLeaderPermissions();
                } else {
                    Toast.makeText(this, "Error loading group information", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                // User has no group - Show create group interface
                setupGroupCreation();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error checking group status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void toggleEditMode() {
        // Check leader permission
        if (!isCurrentUserLeader()) {
            Toast.makeText(this, "Only group leaders can edit group name", Toast.LENGTH_SHORT).show();
            return;
        }

        isEditMode = !isEditMode;
        if (btnEditName != null) {
            btnEditName.setText(isEditMode ? "Save" : "Edit");
        }
        if (edtGroupName != null) {
            edtGroupName.setEnabled(isEditMode);
        }

        if (isEditMode) {
            if (edtGroupName != null) {
                edtGroupName.requestFocus();
            }
        } else {
            // Save changes
            if (edtGroupName != null) {
                updateGroupName(edtGroupName.getText().toString().trim());
            }
        }
    }

    private void updateGroupName(String newName) {
        // Check leader permission
        if (!isCurrentUserLeader()) {
            Toast.makeText(this, "Only group leaders can update group name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newName.isEmpty()) {
            Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            GroupEntity group = groupRepo.getGroupById(groupId);
            if (group != null) {
                group.setName(newName);
                groupRepo.updateGroup(group);
                Toast.makeText(this, "Group name updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error updating group name", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error updating group: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeMember(int memberIndex) {
        // Check leader permission
        if (!isCurrentUserLeader()) {
            Toast.makeText(this, "Only group leaders can remove members", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText memberField = null;
        TextView nameField = null;
        switch (memberIndex) {
            case 2:
                memberField = edtMemberId2;
                nameField = tvMemberName2;
                break;
            case 3:
                memberField = edtMemberId3;
                nameField = tvMemberName3;
                break;
            case 4:
                memberField = edtMemberId4;
                nameField = tvMemberName4;
                break;
            case 5:
                memberField = edtMemberId5;
                nameField = tvMemberName5;
                break;
        }

        if (memberField != null && !memberField.getText().toString().isEmpty()) {
            // Show confirmation dialog
            EditText finalMemberField = memberField;
            TextView finalNameField = nameField;
            new AlertDialog.Builder(this)
                    .setTitle("Remove Member")
                    .setMessage("Are you sure you want to remove this member from the group?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        try {
                            String userCodeToRemove = finalMemberField.getText().toString();
                            groupMemberRepo.removeMember(groupId, userCodeToRemove);

                            // Clear fields and shift remaining members up
                            finalMemberField.setText("");
                            if (finalNameField != null) finalNameField.setText("");
                            rearrangeMemberFields();

                            // Update remove button visibility
                            updateRemoveButtonsVisibility();
                            Toast.makeText(this, "Member removed successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Error removing member: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void addMember() {
        // Check leader permission
        if (!isCurrentUserLeader()) {
            Toast.makeText(this, "Only group leaders can add members", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if group is full
        int currentMemberCount = countMembers();
        if (currentMemberCount >= 5) {
            Toast.makeText(this, "Group is full (maximum 5 members)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create input dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Member");
        builder.setMessage("Enter student ID:");

        final EditText input = new EditText(this);
        input.setHint("Student ID");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newMemberCode = input.getText().toString().trim();
            if (!newMemberCode.isEmpty()) {
                validateAndAddMember(newMemberCode);
            } else {
                Toast.makeText(this, "Please enter a student ID", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void validateAndAddMember(String newMemberCode) {
        try {
            // Check if user exists
            AccountEntity newMember = accountRepo.getAccountByUserCode(newMemberCode);
            if (newMember == null) {
                Toast.makeText(this, "Student ID not found: " + newMemberCode, Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user is already in any group
            if (groupMemberRepo.isUserInAnyGroup(newMemberCode)) {
                Toast.makeText(this, "Student " + newMemberCode + " is already in a group", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if trying to add self
            if (newMemberCode.equals(userCode)) {
                Toast.makeText(this, "You are already the leader of this group", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add member to database
            GroupMemberEntity member = new GroupMemberEntity();
            member.setGroupId(groupId);
            member.setUserCode(newMemberCode);
            member.setRoles("MEMBER");
            groupMemberRepo.insertGroupMember(member);

            // Update UI
            fillNextEmptyMemberField(newMemberCode, newMember.getFullName());
            updateRemoveButtonsVisibility();

            Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error adding member: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void transferLeaderRole() {
        // Check leader permission
        if (!isCurrentUserLeader()) {
            Toast.makeText(this, "Only group leaders can transfer leadership", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create list of available members
            List<String> memberOptions = new ArrayList<>();
            List<String> memberIds = new ArrayList<>();

            if (edtMemberId2 != null && !edtMemberId2.getText().toString().isEmpty()) {
                memberOptions.add(tvMemberName2.getText() + " (" + edtMemberId2.getText() + ")");
                memberIds.add(edtMemberId2.getText().toString());
            }
            if (edtMemberId3 != null && !edtMemberId3.getText().toString().isEmpty()) {
                memberOptions.add(tvMemberName3.getText() + " (" + edtMemberId3.getText() + ")");
                memberIds.add(edtMemberId3.getText().toString());
            }
            if (edtMemberId4 != null && !edtMemberId4.getText().toString().isEmpty()) {
                memberOptions.add(tvMemberName4.getText() + " (" + edtMemberId4.getText() + ")");
                memberIds.add(edtMemberId4.getText().toString());
            }
            if (edtMemberId5 != null && !edtMemberId5.getText().toString().isEmpty()) {
                memberOptions.add(tvMemberName5.getText() + " (" + edtMemberId5.getText() + ")");
                memberIds.add(edtMemberId5.getText().toString());
            }

            if (memberOptions.isEmpty()) {
                Toast.makeText(this, "No members available to transfer leadership", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select New Leader")
                    .setMessage("Warning: You will lose leader privileges after transfer.")
                    .setItems(memberOptions.toArray(new String[0]), (dialog, which) -> {
                        if (which >= 0 && which < memberIds.size()) {
                            updateLeaderRole(memberIds.get(which));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Error transferring leadership: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLeaderRole(String newLeaderCode) {
        try {
            // Update old leader to member
            groupMemberRepo.updateMemberRole(groupId, userCode, "MEMBER");
            // Update new leader
            groupMemberRepo.updateMemberRole(groupId, newLeaderCode, "LEADER");

            Toast.makeText(this, "Leader role transferred successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity as current user is no longer leader
        } catch (Exception e) {
            Toast.makeText(this, "Error updating leader role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRemoveButtonsVisibility() {
        if (!isCurrentUserLeader()) {
            // Hide all remove buttons for non-leaders
            if (btnRemove2 != null) btnRemove2.setVisibility(View.GONE);
            if (btnRemove3 != null) btnRemove3.setVisibility(View.GONE);
            if (btnRemove4 != null) btnRemove4.setVisibility(View.GONE);
            if (btnRemove5 != null) btnRemove5.setVisibility(View.GONE);
            return;
        }

        // Show remove buttons only for filled positions (leaders only)
        if (btnRemove2 != null) {
            btnRemove2.setVisibility(
                    (edtMemberId2 != null && !edtMemberId2.getText().toString().isEmpty())
                            ? View.VISIBLE : View.GONE
            );
        }
        if (btnRemove3 != null) {
            btnRemove3.setVisibility(
                    (edtMemberId3 != null && !edtMemberId3.getText().toString().isEmpty())
                            ? View.VISIBLE : View.GONE
            );
        }
        if (btnRemove4 != null) {
            btnRemove4.setVisibility(
                    (edtMemberId4 != null && !edtMemberId4.getText().toString().isEmpty())
                            ? View.VISIBLE : View.GONE
            );
        }
        if (btnRemove5 != null) {
            btnRemove5.setVisibility(
                    (edtMemberId5 != null && !edtMemberId5.getText().toString().isEmpty())
                            ? View.VISIBLE : View.GONE
            );
        }
    }

    private int countMembers() {
        int count = 1; // Leader
        if (edtMemberId2 != null && !edtMemberId2.getText().toString().isEmpty()) count++;
        if (edtMemberId3 != null && !edtMemberId3.getText().toString().isEmpty()) count++;
        if (edtMemberId4 != null && !edtMemberId4.getText().toString().isEmpty()) count++;
        if (edtMemberId5 != null && !edtMemberId5.getText().toString().isEmpty()) count++;
        return count;
    }

    private void rearrangeMemberFields() {
        // Collect all non-empty member fields
        List<String> memberIds = new ArrayList<>();
        List<String> memberNames = new ArrayList<>();

        if (edtMemberId2 != null && !edtMemberId2.getText().toString().isEmpty()) {
            memberIds.add(edtMemberId2.getText().toString());
            memberNames.add(tvMemberName2.getText().toString());
        }
        if (edtMemberId3 != null && !edtMemberId3.getText().toString().isEmpty()) {
            memberIds.add(edtMemberId3.getText().toString());
            memberNames.add(tvMemberName3.getText().toString());
        }
        if (edtMemberId4 != null && !edtMemberId4.getText().toString().isEmpty()) {
            memberIds.add(edtMemberId4.getText().toString());
            memberNames.add(tvMemberName4.getText().toString());
        }
        if (edtMemberId5 != null && !edtMemberId5.getText().toString().isEmpty()) {
            memberIds.add(edtMemberId5.getText().toString());
            memberNames.add(tvMemberName5.getText().toString());
        }

        // Clear all fields
        clearMemberFields();

        // Refill in order
        for (int i = 0; i < memberIds.size(); i++) {
            switch (i) {
                case 0:
                    if (edtMemberId2 != null && tvMemberName2 != null) {
                        edtMemberId2.setText(memberIds.get(i));
                        tvMemberName2.setText(memberNames.get(i));
                    }
                    break;
                case 1:
                    if (edtMemberId3 != null && tvMemberName3 != null) {
                        edtMemberId3.setText(memberIds.get(i));
                        tvMemberName3.setText(memberNames.get(i));
                    }
                    break;
                case 2:
                    if (edtMemberId4 != null && tvMemberName4 != null) {
                        edtMemberId4.setText(memberIds.get(i));
                        tvMemberName4.setText(memberNames.get(i));
                    }
                    break;
                case 3:
                    if (edtMemberId5 != null && tvMemberName5 != null) {
                        edtMemberId5.setText(memberIds.get(i));
                        tvMemberName5.setText(memberNames.get(i));
                    }
                    break;
            }
        }
    }

    private void setupGroupCreation() {
        clearAllFields();
        setFieldsEnabled(true);
        setupMemberFieldListeners();

        if (btnCreateGroup != null) {
            btnCreateGroup.setVisibility(View.VISIBLE);
            btnCreateGroup.setOnClickListener(v -> validateAndCreateGroup());
        }
    }

    private void setupMemberFieldListeners() {
        TextWatcher memberWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateMembers();
            }
        };

        if (edtMemberId2 != null) edtMemberId2.addTextChangedListener(memberWatcher);
        if (edtMemberId3 != null) edtMemberId3.addTextChangedListener(memberWatcher);
        if (edtMemberId4 != null) edtMemberId4.addTextChangedListener(memberWatcher);
        if (edtMemberId5 != null) edtMemberId5.addTextChangedListener(memberWatcher);
    }

    private void validateAndCreateGroup() {
        try {
            String groupName = edtGroupName != null ? edtGroupName.getText().toString().trim() : "";
            if (groupName.isEmpty()) {
                Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> memberIds = new ArrayList<>();
            memberIds.add(userCode); // Leader

            // Collect member IDs
            if (edtMemberId2 != null && !edtMemberId2.getText().toString().trim().isEmpty())
                memberIds.add(edtMemberId2.getText().toString().trim());
            if (edtMemberId3 != null && !edtMemberId3.getText().toString().trim().isEmpty())
                memberIds.add(edtMemberId3.getText().toString().trim());
            if (edtMemberId4 != null && !edtMemberId4.getText().toString().trim().isEmpty())
                memberIds.add(edtMemberId4.getText().toString().trim());
            if (edtMemberId5 != null && !edtMemberId5.getText().toString().trim().isEmpty())
                memberIds.add(edtMemberId5.getText().toString().trim());

            // Validate member count
            if (memberIds.size() < 3) {
                Toast.makeText(this, "Group must have at least 3 members", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate all members
            for (String memberId : memberIds) {
                AccountEntity member = accountRepo.getAccountByUserCode(memberId);
                if (member == null) {
                    Toast.makeText(this, "Invalid student ID: " + memberId, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!memberId.equals(userCode) && groupMemberRepo.isUserInAnyGroup(memberId)) {
                    Toast.makeText(this, "Student " + memberId + " is already in another group", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // All validations passed - Create group
            createGroup(groupName, memberIds);
        } catch (Exception e) {
            Toast.makeText(this, "Error creating group: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createGroup(String groupName, List<String> memberIds) {
        try {
            // Create group
            GroupEntity group = new GroupEntity();
            group.setName(groupName);
            groupRepo.insertGroup(group);

            // Add members
            for (String memberId : memberIds) {
                GroupMemberEntity member = new GroupMemberEntity();
                member.setGroupId(group.getId());
                member.setUserCode(memberId);
                member.setRoles(memberId.equals(userCode) ? "LEADER" : "MEMBER");
                groupMemberRepo.insertGroupMember(member);
            }

            Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating group: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showExistingGroupDetails(int groupId) {
        try {
            GroupEntity group = groupRepo.getGroupById(groupId);
            if (group == null) {
                Toast.makeText(this, "Error loading group details", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Display group info
            if (edtGroupName != null) {
                edtGroupName.setText(group.getName());
                edtGroupName.setEnabled(false);
            }

            // Display members
            List<GroupMemberEntity> members = groupMemberRepo.getMembersByGroupId(groupId);
            if (members != null) {
                for (GroupMemberEntity member : members) {
                    AccountEntity acc = accountRepo.getAccountByUserCode(member.getUserCode());
                    String displayName = acc != null ? acc.getFullName() : member.getUserCode();

                    if ("LEADER".equals(member.getRoles())) {
                        if (edtMemberId1 != null && tvMemberName1 != null) {
                            edtMemberId1.setText(member.getUserCode());
                            tvMemberName1.setText(displayName + " (Leader)");
                        }
                    } else if("MEMBER".equals(member.getRoles())){
                        fillNextEmptyMemberField(member.getUserCode(), displayName);
                    }
                }
            }

            // Disable all inputs for viewing
            setFieldsEnabled(false);
            if (btnCreateGroup != null) {
                btnCreateGroup.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying group details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fillNextEmptyMemberField(String userCode, String displayName) {
        if (edtMemberId2 != null && tvMemberName2 != null && edtMemberId2.getText().toString().isEmpty()) {
            edtMemberId2.setText(userCode);
            tvMemberName2.setText(displayName);
        } else if (edtMemberId3 != null && tvMemberName3 != null && edtMemberId3.getText().toString().isEmpty()) {
            edtMemberId3.setText(userCode);
            tvMemberName3.setText(displayName);
        } else if (edtMemberId4 != null && tvMemberName4 != null && edtMemberId4.getText().toString().isEmpty()) {
            edtMemberId4.setText(userCode);
            tvMemberName4.setText(displayName);
        } else if (edtMemberId5 != null && tvMemberName5 != null && edtMemberId5.getText().toString().isEmpty()) {
            edtMemberId5.setText(userCode);
            tvMemberName5.setText(displayName);
        }
    }

    private void clearAllFields() {
        if (edtGroupName != null) edtGroupName.setText("");
        clearMemberFields();
    }

    private void clearMemberFields() {
        if (edtMemberId2 != null) edtMemberId2.setText("");
        if (edtMemberId3 != null) edtMemberId3.setText("");
        if (edtMemberId4 != null) edtMemberId4.setText("");
        if (edtMemberId5 != null) edtMemberId5.setText("");
        if (tvMemberName2 != null) tvMemberName2.setText("");
        if (tvMemberName3 != null) tvMemberName3.setText("");
        if (tvMemberName4 != null) tvMemberName4.setText("");
        if (tvMemberName5 != null) tvMemberName5.setText("");
    }

    private void setFieldsEnabled(boolean enabled) {
        if (edtGroupName != null) edtGroupName.setEnabled(enabled);
        if (edtMemberId2 != null) edtMemberId2.setEnabled(enabled);
        if (edtMemberId3 != null) edtMemberId3.setEnabled(enabled);
        if (edtMemberId4 != null) edtMemberId4.setEnabled(enabled);
        if (edtMemberId5 != null) edtMemberId5.setEnabled(enabled);
    }

    private void validateMembers() {
        boolean isValid = true;
        int memberCount = 1; // Start with 1 for leader

        // Validate each member field
        if (edtMemberId2 != null && !edtMemberId2.getText().toString().isEmpty()) {
            if (validateMember(edtMemberId2, tvMemberName2)) memberCount++;
            else isValid = false;
        }
        if (edtMemberId3 != null && !edtMemberId3.getText().toString().isEmpty()) {
            if (validateMember(edtMemberId3, tvMemberName3)) memberCount++;
            else isValid = false;
        }
        if (edtMemberId4 != null && !edtMemberId4.getText().toString().isEmpty()) {
            if (validateMember(edtMemberId4, tvMemberName4)) memberCount++;
            else isValid = false;
        }
        if (edtMemberId5 != null && !edtMemberId5.getText().toString().isEmpty()) {
            if (validateMember(edtMemberId5, tvMemberName5)) memberCount++;
            else isValid = false;
        }

        if (btnCreateGroup != null) {
            btnCreateGroup.setEnabled(isValid && memberCount >= 3);
        }
    }

    private boolean validateMember(EditText memberField, TextView nameField) {
        if (memberField == null || nameField == null) return false;

        String memberId = memberField.getText().toString().trim();

        if (memberId.isEmpty()) return false;

        try {
            AccountEntity member = accountRepo.getAccountByUserCode(memberId);
            if (member == null) {
                nameField.setText("User not found");
                return false;
            }

            if (groupMemberRepo.isUserInAnyGroup(memberId)) {
                nameField.setText("Already in a group");
                return false;
            }

            nameField.setText(member.getFullName());
            return true;
        } catch (Exception e) {
            nameField.setText("Error validating user");
            return false;
        }
    }
}