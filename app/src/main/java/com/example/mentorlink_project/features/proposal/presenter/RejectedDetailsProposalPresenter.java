package com.example.mentorlink_project.features.proposal.presenter;

import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.proposal.contract.RejectedDetailsProposalContract;

import java.util.List;

public class RejectedDetailsProposalPresenter implements RejectedDetailsProposalContract.Presenter {
    private final RejectedDetailsProposalContract.View view;
    private final ProjectRepository projectRepository;

    public RejectedDetailsProposalPresenter(RejectedDetailsProposalContract.View view, ProjectRepository projectRepository) {
        this.view = view;
        this.projectRepository = projectRepository;
    }

    @Override
    public void loadRejectedProposals() {
        List<ProjectEntity> rejectedList = projectRepository.getProjectsByStatus("REJECTED");
        view.showRejectedProposals(rejectedList);
    }
}
