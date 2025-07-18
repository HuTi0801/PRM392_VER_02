package com.example.mentorlink_project.features.proposal.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.example.mentorlink_project.features.dashboard.LectureDashboardActivity;
import com.example.mentorlink_project.features.dashboard.StudentDashboardActivity;
import com.example.mentorlink_project.features.group.view.GroupDetailActivity;
import com.example.mentorlink_project.features.login.LoginActivity;
import com.example.mentorlink_project.features.proposal.contract.ProposalDetailContract;
import com.example.mentorlink_project.features.proposal.presenter.ProposalDetailPresenter;

import java.util.List;

public class ProposalDetailActivity extends AppCompatActivity implements ProposalDetailContract.View {
    private ProposalDetailPresenter presenter;
    private Button btnAddProposal, btnRemoveProposal;
    private TextView tvTopic, tvLecturer, tvProposalStatus, tvComment, tvDocumentUrl;
    private AccountRepository accountRepository;
    DrawerLayout drawerLayout;
    private String currentUserCode, currentUserRole;

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

        //Hiển thị sidebar
        drawerLayout = findViewById(R.id.proposal_details);
        ImageButton btnMenu = findViewById(R.id.btn_menu); // nằm trong header

        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //lấy role để hiển thị đúng sidebar
        currentUserCode = getIntent().getStringExtra("USER_CODE");
        currentUserRole = getIntent().getStringExtra("ROLE");

        if (currentUserCode == null || currentUserRole == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear stack
            startActivity(intent);
            finish();
            return;
        }

        // Ánh xạ View
        btnAddProposal = findViewById(R.id.btn_add_proposal);
        btnRemoveProposal = findViewById(R.id.btn_remove_proposal);
        tvTopic = findViewById(R.id.tv_topic);
        tvLecturer = findViewById(R.id.tv_lecturer);
        tvProposalStatus = findViewById(R.id.tv_proposal_status);
        tvComment = findViewById(R.id.tv_comment);
        tvDocumentUrl = findViewById(R.id.tv_document_url);
        drawerLayout = findViewById(R.id.proposal_details);
        accountRepository = new AccountRepository(this);

        // Sidebar động
        inflateSidebarByRole();

        // Lấy project
        project = null;
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

    private void inflateSidebarByRole() {
        FrameLayout sidebarContainer = findViewById(R.id.sidebar_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        View sidebarView;

        if ("STUDENT".equalsIgnoreCase(currentUserRole)) {
            sidebarView = inflater.inflate(R.layout.sidebar_student, sidebarContainer, false);
            sidebarContainer.addView(sidebarView);

            Button btnHome = sidebarView.findViewById(R.id.nav_home);
            Button btnGroup = sidebarView.findViewById(R.id.nav_group);
            Button btnProposal = sidebarView.findViewById(R.id.nav_proposal);

            btnHome.setOnClickListener(v -> {
                startWithUser(StudentDashboardActivity.class);
            });

            btnGroup.setOnClickListener(v -> {
                startWithUser(GroupDetailActivity.class);
            });

            btnProposal.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        } else if ("LECTURER".equalsIgnoreCase(currentUserRole)) {
            sidebarView = inflater.inflate(R.layout.sidebar_lecturer, sidebarContainer, false);
            sidebarContainer.addView(sidebarView);

            Button btnHome = sidebarView.findViewById(R.id.nav_home);
            Button btnGroup = sidebarView.findViewById(R.id.nav_group);
            Button btnProject = sidebarView.findViewById(R.id.nav_project);
            LinearLayout submenu = sidebarView.findViewById(R.id.project_submenu);
            Button btnPending = sidebarView.findViewById(R.id.nav_pending);
            Button btnApproved = sidebarView.findViewById(R.id.nav_approved);
            Button btnRejected = sidebarView.findViewById(R.id.nav_rejected);

            btnHome.setOnClickListener(v -> startWithUser(LectureDashboardActivity.class));

            btnProject.setOnClickListener(v -> {
                submenu.setVisibility(submenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                btnProject.setText(submenu.getVisibility() == View.VISIBLE ? "Project ▲" : "Project ▼");
            });

            btnPending.setOnClickListener(v -> startWithUser(PendingDetailsProposalActivity.class));
            btnApproved.setOnClickListener(v -> startWithUser(ApprovedDetailsProposalActivity.class));
            btnRejected.setOnClickListener(v -> startWithUser(RejectedDetailsProposalActivity.class));
        }

        // Mở Drawer menu
        ImageButton btnMenu = findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void startWithUser(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("USER_CODE", currentUserCode);
        intent.putExtra("ROLE", currentUserRole);
        startActivity(intent);
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
