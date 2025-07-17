package com.example.mentorlink_project.features.proposal.presenter;
import android.content.Context;

import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.data.repositories.AccountRepository;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.proposal.contract.CreateProposalContract;

import java.util.ArrayList;
import java.util.List;

public class CreateProposalPresenter implements CreateProposalContract.Presenter {
    private final CreateProposalContract.View view;
    private final AccountRepository accountRepo;
    private final ProjectRepository projectRepo;

    public CreateProposalPresenter(Context context, CreateProposalContract.View view) {
        this.view = view;
        this.accountRepo = new AccountRepository(context);
        this.projectRepo = new ProjectRepository(context);
    }

    @Override
    public void loadMajors() {
        List<String> majors = accountRepo.getAllMajors();
        view.showMajors(majors);
    }

    @Override
    public void loadLecturers(String major) {
        List<String> lecturers = accountRepo.getLecturersByMajor(major);
        view.showLecturers(lecturers);
    }

    @Override
    public void onAttachFileClicked() {
        // Handled in Activity (open file picker)
    }

    @Override
    public void onSendClicked(String title, String desc, String fileUrl, String major, String lecturerCode, int groupId) {
        if (title.isEmpty() || desc.isEmpty() || major.isEmpty() || lecturerCode.isEmpty()) {
            view.showMessage("Please fill all required fields.");
            return;
        }
        ProjectEntity project = new ProjectEntity();
        project.setGroupId(groupId);
        project.setLectureCode(lecturerCode);
        project.setTopic(title);
        project.setDescription(desc);
        project.setStatus("PENDING");
        project.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        project.setDocumentUrl(fileUrl);
        projectRepo.insertProject(project);
        view.showMessage("Proposal sent!");
        view.finishWithSuccess();
    }
}
