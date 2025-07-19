package com.example.mentorlink_project.features.proposal.presenter;

import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.proposal.contract.RejectedDetailsProposalContract;

import java.util.List;

public class RejectedDetailsProposalPresenter implements RejectedDetailsProposalContract.Presenter {
    private final RejectedDetailsProposalContract.View view;
    private final ProjectRepository projectRepository;
    private final String lecturerCode;

    public RejectedDetailsProposalPresenter(RejectedDetailsProposalContract.View view, ProjectRepository projectRepository, String lecturerCode) {
        this.view = view;
        this.projectRepository = projectRepository;
        this.lecturerCode = lecturerCode;
    }

    @Override
    public void loadRejectedProposals() {
        List<ProjectEntity> rejectedList = projectRepository.getProjectsByStatus("REJECTED", lecturerCode);
        view.showRejectedProposals(rejectedList);
    }
}
