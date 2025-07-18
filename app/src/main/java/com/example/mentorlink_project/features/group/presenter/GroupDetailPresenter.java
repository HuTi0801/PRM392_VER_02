package com.example.mentorlink_project.features.group.presenter;

import android.content.Context;
import com.example.mentorlink_project.data.repositories.GroupRepository;
import com.example.mentorlink_project.data.repositories.GroupMemberRepository;
import com.example.mentorlink_project.data.entities.GroupEntity;
import com.example.mentorlink_project.data.entities.GroupMemberEntity;
import com.example.mentorlink_project.features.group.contract.GroupDetailContract;
import java.util.ArrayList;
import java.util.List;

public class GroupDetailPresenter implements GroupDetailContract.Presenter {
    private final GroupDetailContract.View view;
    private final GroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;
    private String userRole;
    private int groupId;

    public GroupDetailPresenter(Context context, GroupDetailContract.View view) {
        this.view = view;
        this.groupRepo = new GroupRepository(context);
        this.memberRepo = new GroupMemberRepository(context);
    }

    @Override
    public void loadGroupDetails(int groupId, String userRole) {
        this.groupId = groupId;
        this.userRole = userRole;
        GroupEntity group = groupRepo.getGroupById(groupId);
        List<GroupMemberEntity> members = memberRepo.getMembersByGroupId(groupId);
        List<String> memberNames = new ArrayList<>();
        for (GroupMemberEntity m : members) {
            memberNames.add(m.getUserCode() + " (" + m.getRoles() + ")");
        }
        boolean editable = "LEADER".equals(userRole);
        view.setDetailMode(true, editable);
        view.showGroupName(group != null ? group.getName() : "", editable);
        view.showMembers(memberNames);

        if ("LEADER".equals(userRole)) {
            boolean showRemove = members.size() > 3 && members.size() < 6;
            view.showLeaderActions(showRemove, members.size());
        } else {
            view.showLeaderActions(false, members.size());
        }
    }


    @Override
    public void onEditGroupNameClicked(String newName) {
        // Implement update logic
        // groupRepo.updateGroupName(groupId, newName);
        view.showMessage("Group name updated.");
    }

    @Override
    public void onAddMemberClicked() {
        // Implement add member logic
        view.showMessage("Add member clicked.");
    }

    @Override
    public void onRemoveMemberClicked() {
        // Implement remove member logic
        view.showMessage("Remove member clicked.");
    }

    @Override
    public void onTransferLeaderClicked() {
        // Implement transfer leader logic
        view.showMessage("Transfer leader clicked.");
    }
}
