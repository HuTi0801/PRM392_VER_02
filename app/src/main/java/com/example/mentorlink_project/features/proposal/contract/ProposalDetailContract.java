package com.example.mentorlink_project.features.proposal.contract;

import com.example.mentorlink_project.data.entities.ProjectEntity;

import java.util.List;

public interface ProposalDetailContract {
    interface View {
        void showProposalDetails(ProjectEntity project);
        void showAddProposalButton(boolean show);
        void showRemoveProposalButton(boolean show);
        void showMessage(String msg);
        void showMembers(List<String> members);
    }
    interface Presenter {
        void loadProposal(int groupId, String userRole);
        void onAddProposalClicked();
        void onRemoveProposalClicked();
    }
}
