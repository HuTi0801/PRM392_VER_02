package com.example.mentorlink_project.features.group.contract;

import java.util.List;

public interface GroupDetailContract {
    interface View {
        void showGroupName(String name, boolean editable);
        void showMembers(List<String> members);
        void showLeaderActions(boolean showRemove, int memberCount);
        void showMessage(String msg);
        void setDetailMode(boolean isDetail, boolean isLeader);
    }
    interface Presenter {
        void loadGroupDetails(int groupId, String userRole);
        void onEditGroupNameClicked(String newName);
        void onAddMemberClicked();
        void onRemoveMemberClicked();
        void onTransferLeaderClicked();
    }
}