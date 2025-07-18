package com.example.mentorlink_project.features.proposal.presenter;


import android.content.Context;

import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.proposal.contract.ProposalDetailContract;

import java.util.List;

public class ProposalDetailPresenter implements ProposalDetailContract.Presenter {
    private final ProposalDetailContract.View view;
    private final ProjectRepository projectRepo;
    private ProjectEntity currentProject;
    private String userRole;
    private int groupId;

    public ProposalDetailPresenter(Context context, ProposalDetailContract.View view) {
        this.view = view;
        this.projectRepo = new ProjectRepository(context);
    }

    @Override
    public void loadProposal(int groupId, String userRole) {
        this.groupId = groupId;
        this.userRole = userRole;
        List<ProjectEntity> projects = projectRepo.getAllProjects();
        currentProject = null;
        for (ProjectEntity p : projects) {
            if (p.getGroupId() == groupId) {
                currentProject = p;
                break;
            }
        }
        view.showProposalDetails(currentProject);

        if ("LEADER".equals(userRole)) {
            if (currentProject == null) {
                view.showAddProposalButton(true);
                view.showRemoveProposalButton(false);
            } else {
                switch (currentProject.getStatus()) {
                    case "PENDING":
                        view.showAddProposalButton(false);
                        view.showRemoveProposalButton(true);
                        break;
                    case "REJECTED":
                        view.showAddProposalButton(true);
                        view.showRemoveProposalButton(false);
                        break;
                    case "APPROVED":
                        view.showAddProposalButton(false);
                        view.showRemoveProposalButton(false);
                        break;
                }
            }
        } else {
            view.showAddProposalButton(false);
            view.showRemoveProposalButton(false);
        }
    }

    public void loadProposalById(int projectId) {
        currentProject = projectRepo.getProjectById(projectId);
        view.showProposalDetails(currentProject);
    }

    @Override
    public void onAddProposalClicked() {
        // Navigate to Create Proposal Activity (handled in Activity)
    }

    @Override
    public void onRemoveProposalClicked() {
        if (currentProject != null) {
            // Remove proposal logic (e.g., delete from DB)
            // projectRepo.deleteProject(currentProject.getId());
            view.showMessage("Proposal removed.");
            loadProposal(groupId, userRole);
        }
    }
}
