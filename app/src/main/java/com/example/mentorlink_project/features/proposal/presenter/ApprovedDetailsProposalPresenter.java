package com.example.mentorlink_project.features.proposal.presenter;

import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.proposal.contract.ApprovedDetailsProposalContract;

import java.util.List;

public class ApprovedDetailsProposalPresenter implements ApprovedDetailsProposalContract.Presenter{
    private final ApprovedDetailsProposalContract.View view;
    private final ProjectRepository projectRepository;

    public ApprovedDetailsProposalPresenter(ApprovedDetailsProposalContract.View view, ProjectRepository projectRepository) {
        this.view = view;
        this.projectRepository = projectRepository;
    }

    @Override
    public void loadApprovedProposals() {
        List<ProjectEntity> list = projectRepository.getProjectsByStatus("APPROVED");
        if (list.isEmpty()) {
            view.showMessage("Không có đề tài đã duyệt.");
        } else {
            view.showApprovedProposals(list);
        }
    }
}
