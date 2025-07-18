package com.example.mentorlink_project.features.proposal.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;


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
    DrawerLayout drawerLayout;
    ImageButton btnMenu;
    Button btnProject, btnPending, btnApproved, btnRejected;;
    LinearLayout submenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.proposal_details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        //hiển thị sidebar
        drawerLayout = findViewById(R.id.proposal_details);
        btnMenu = findViewById(R.id.btn_menu); // nằm trong header.xml

        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        btnProject = findViewById(R.id.nav_project);
        submenu = findViewById(R.id.project_submenu);

        btnProject.setOnClickListener(v -> {
            if (submenu.getVisibility() == View.GONE) {
                submenu.setVisibility(View.VISIBLE);
                btnProject.setText("Project ▲");
            } else {
                submenu.setVisibility(View.GONE);
                btnProject.setText("Project ▼");
            }
        });

        //chuyển trang khi bấm vào trong các button ở sidebar
        btnPending = findViewById(R.id.nav_pending);
        btnApproved = findViewById(R.id.nav_approved);
        btnRejected = findViewById(R.id.nav_rejected);

        btnPending.setOnClickListener(v -> {
            Intent intent = new Intent(this, PendingDetailsProposalActivity.class);
            startActivity(intent);
        });

        btnApproved.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApprovedDetailsProposalActivity.class);
            startActivity(intent);
        });

        btnRejected.setOnClickListener(v -> {
            Intent intent = new Intent(this, RejectedDetailsProposalActivity.class);
            startActivity(intent);
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
