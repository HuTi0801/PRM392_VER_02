package com.example.mentorlink_project.features.group.presenter;

import android.content.Context;

import com.example.mentorlink_project.data.entities.AccountEntity;
import com.example.mentorlink_project.data.repositories.AccountRepository;
import com.example.mentorlink_project.data.repositories.GroupMemberRepository;
import com.example.mentorlink_project.features.group.contract.GroupCreateContract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupCreatePresenter implements GroupCreateContract.Presenter {
    private final GroupCreateContract.View view;
    private final AccountRepository accountRepo;
    private final GroupMemberRepository groupMemberRepo;
    private String leaderId;

    public GroupCreatePresenter(Context context, GroupCreateContract.View view) {
        this.view = view;
        this.accountRepo = new AccountRepository(context);
        this.groupMemberRepo = new GroupMemberRepository(context);
    }

    @Override
    public void initLeader(String leaderId) {
        this.leaderId = leaderId;
        AccountEntity leader = accountRepo.getAccountByUserCode(leaderId);
        if (leader != null) {
            view.showMemberName(0, leader.getFullName() + " (" + leaderId + ")");
        }
    }

    @Override
    public void onSaveClicked(String groupName, List<String> memberIds) {
        // Validate again, then save group and members
        // ...insert group, insert members
        view.showMessage("Group created successfully!");
        view.clearFields();
    }

    @Override
    public void onClearClicked() {
        view.clearFields();
    }

    // Add this method to collect member IDs from the view (implement getMemberIds() in your View)
    private List<String> getAllMemberIds() {
        return view.getMemberIds(); // Implement this in your View to return all entered IDs (including leader)
    }

    private void validateMembers() {
        List<String> memberIds = getAllMemberIds();
        Set<String> uniqueIds = new HashSet<>();
        boolean allValid = true;
        int count = 0;
        for (int i = 0; i < memberIds.size(); i++) {
            String id = memberIds.get(i);
            if (id == null || id.isEmpty()) continue;
            if (!uniqueIds.add(id)) {
                allValid = false;
                view.showMemberError(i, "Duplicate ID");
                continue;
            }
            AccountEntity acc = accountRepo.getAccountByUserCode(id);
            if (acc == null) {
                allValid = false;
                view.showMemberError(i, "Not found");
            } else if (groupMemberRepo.isUserInAnyGroup(id)) {
                allValid = false;
                view.showMemberError(i, "Already in a group");
            }
            count++;
        }
        boolean valid = allValid && count >= 3 && count <= 5;
        view.setSaveEnabled(valid);
    }

    @Override
    public void onMemberIdChanged(int index, String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            view.showMemberName(index, "");
            validateMembers();
            return;
        }
        AccountEntity acc = accountRepo.getAccountByUserCode(studentId);
        if (acc == null) {
            view.showMemberError(index, "Not found");
            view.showMemberName(index, "");
        } else if (groupMemberRepo.isUserInAnyGroup(studentId)) {
            view.showMemberError(index, "Already in a group");
            view.showMemberName(index, acc.getFullName() + " (" + studentId + ")");
        } else {
            view.showMemberName(index, acc.getFullName() + " (" + studentId + ")");
        }
        validateMembers();
    }
}
