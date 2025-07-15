package com.example.mentorlink_project.features.proposal.contract;

import java.util.List;

public interface CreateProposalContract {
    interface View {
        void showMajors(List<String> majors);
        void showLecturers(List<String> lecturers);
        void showFileName(String fileName);
        void showMessage(String msg);
        void finishWithSuccess();
    }
    interface Presenter {
        void loadMajors();
        void loadLecturers(String major);
        void onAttachFileClicked();
        void onSendClicked(String title, String desc, String fileUrl, String major, String lecturerCode, int groupId);
    }
}