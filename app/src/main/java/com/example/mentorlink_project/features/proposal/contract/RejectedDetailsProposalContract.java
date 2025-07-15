package com.example.mentorlink_project.features.proposal.contract;

import com.example.mentorlink_project.data.entities.ProjectEntity;

import java.util.List;

public interface RejectedDetailsProposalContract {
    interface View {
        void showRejectedProposals(List<ProjectEntity> proposalList);
        void showMessage(String message);
    }

    interface Presenter {
        void loadRejectedProposals();
    }
}
