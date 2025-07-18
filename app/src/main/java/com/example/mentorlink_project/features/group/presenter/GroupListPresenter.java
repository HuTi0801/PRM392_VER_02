package com.example.mentorlink_project.features.group.presenter;

import com.example.mentorlink_project.data.repositories.GroupRepository;
import com.example.mentorlink_project.features.group.contract.GroupListContract;

import java.util.List;

public class GroupListPresenter implements GroupListContract.Presenter {
    private final GroupListContract.View view;
    private final GroupRepository groupRepository;

    public GroupListPresenter(GroupListContract.View view, GroupRepository groupRepository) {
        this.view = view;
        this.groupRepository = groupRepository;
    }

    @Override
    public void loadGroupsManagedByLecturer(String lecturerCode) {
        List<String> groupNames = groupRepository.getGroupNamesByLecturer(lecturerCode);
        view.showGroups(groupNames);
    }
}
