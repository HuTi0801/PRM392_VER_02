package com.example.mentorlink_project.features.group.contract;

import java.util.List;

public interface GroupCreateContract {
    interface View {
        void showMemberName(int index, String name);
        void showMemberError(int index, String error);
        void setSaveEnabled(boolean enabled);
        void showMessage(String msg);
        public List<String> getMemberIds();
        void clearFields();
    }
    interface Presenter {
        void onMemberIdChanged(int index, String studentId);
        void onSaveClicked(String groupName, List<String> memberIds);
        void onClearClicked();
        void initLeader(String leaderId);
    }
}
