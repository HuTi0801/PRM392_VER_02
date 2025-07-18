package com.example.mentorlink_project.features.group.contract;

import com.example.mentorlink_project.data.entities.GroupEntity;

import java.util.List;

public interface GroupListContract {
    interface View {
        void showGroups(List<String> groupNames); // có thể thay List<GroupEntity> nếu dùng object
    }

    interface Presenter {
        void loadGroupsManagedByLecturer(String lecturerCode);
    }
}
