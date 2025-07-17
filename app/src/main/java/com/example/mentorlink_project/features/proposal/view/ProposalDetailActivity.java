package com.example.mentorlink_project.features.proposal.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.entities.AccountEntity;
import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.data.repositories.AccountRepository;
import com.example.mentorlink_project.features.proposal.contract.ProposalDetailContract;
import com.example.mentorlink_project.features.proposal.presenter.ProposalDetailPresenter;

import java.util.List;

public class ProposalDetailActivity extends AppCompatActivity implements ProposalDetailContract.View {
    private ProposalDetailPresenter presenter;
    private Button btnAddProposal, btnRemoveProposal;
    private TextView tvTopic, tvLecturer, tvProposalStatus, tvComment, tvDocumentUrl;
    private AccountRepository accountRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_details);

        btnAddProposal = findViewById(R.id.btn_add_proposal);
        btnRemoveProposal = findViewById(R.id.btn_remove_proposal);

        tvTopic = findViewById(R.id.tv_topic);
        tvLecturer = findViewById(R.id.tv_lecturer);
        tvProposalStatus = findViewById(R.id.tv_proposal_status);
        tvComment = findViewById(R.id.tv_comment);
        tvDocumentUrl = findViewById(R.id.tv_document_url);

        accountRepository = new AccountRepository(this);

        ProjectEntity project = null;

        // Handle both "project_id" and "proposal" (Serializable ProjectEntity) from intent
        if (getIntent().hasExtra("proposal")) {
            project = (ProjectEntity) getIntent().getSerializableExtra("proposal");
        } else if (getIntent().hasExtra("project_id")) {
            int projectId = getIntent().getIntExtra("project_id", -1);
            presenter = new ProposalDetailPresenter(this, this);
            presenter.loadProposalById(projectId);
        }

        if (project != null) {
            showProposalDetails(project);
        }

        btnAddProposal.setOnClickListener(v -> {
            if (presenter != null) presenter.onAddProposalClicked();
        });
        btnRemoveProposal.setOnClickListener(v -> {
            if (presenter != null) presenter.onRemoveProposalClicked();
        });
    }

    @Override
    public void showProposalDetails(ProjectEntity project) {
        if (project == null) {
            tvTopic.setText("");
            tvLecturer.setText("");
            tvProposalStatus.setText("");
            tvComment.setText("");
            tvDocumentUrl.setText("");
            return;
        }
        tvTopic.setText(project.getTopic());

        // Get lecturer name from code
        String lecturerCode = project.getLectureCode();
        String lecturerName = "";
        if (lecturerCode != null && !lecturerCode.isEmpty()) {
            AccountEntity lecturer = accountRepository.getAccountByUserCode(lecturerCode);
            if (lecturer != null) {
                lecturerName = lecturer.getFullName() + " (" + lecturerCode + ")";
            } else {
                lecturerName = lecturerCode;
            }
        }
        tvLecturer.setText(lecturerName);

        tvProposalStatus.setText(project.getStatus());
        tvComment.setText(project.getRejectReason() != null ? project.getRejectReason() : "");
        tvDocumentUrl.setText(project.getDocumentUrl() != null ? project.getDocumentUrl() : "");
    }

    @Override
    public void showAddProposalButton(boolean show) {
        btnAddProposal.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showRemoveProposalButton(boolean show) {
        btnRemoveProposal.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMembers(List<String> members) {
        // Set up RecyclerView adapter
    }
}
