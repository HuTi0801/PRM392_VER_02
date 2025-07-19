package com.example.mentorlink_project.features.proposal.presenter;

import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.proposal.contract.ApprovedDetailsProposalContract;

import java.util.List;

public class ApprovedDetailsProposalPresenter implements ApprovedDetailsProposalContract.Presenter{
    private final ApprovedDetailsProposalContract.View view;
    private final ProjectRepository projectRepository;
    private final String lecturerCode;

    public ApprovedDetailsProposalPresenter(ApprovedDetailsProposalContract.View view, ProjectRepository projectRepository, String lecturerCode) {
        this.view = view;
        this.projectRepository = projectRepository;
        this.lecturerCode = lecturerCode;
    }

    @Override
    public void loadApprovedProposals() {
        List<ProjectEntity> list = projectRepository.getProjectsByStatus("APPROVED", lecturerCode);
        if (list.isEmpty()) {
            view.showMessage("Không có đề tài đã duyệt.");
        } else {
            view.showApprovedProposals(list);
        }
    }
}
