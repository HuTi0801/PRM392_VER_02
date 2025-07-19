package com.example.mentorlink_project.features.proposal.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.mentorlink_project.data.repositories.GroupMemberRepository;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.dashboard.LectureDashboardActivity;
import com.example.mentorlink_project.features.dashboard.StudentDashboardActivity;
import com.example.mentorlink_project.features.group.view.GroupDetailActivity;
import com.example.mentorlink_project.features.login.LoginActivity;
import com.example.mentorlink_project.features.proposal.contract.ProposalDetailContract;
import com.example.mentorlink_project.features.proposal.presenter.ProposalDetailPresenter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProposalDetailActivity extends AppCompatActivity implements ProposalDetailContract.View {
    private ProposalDetailPresenter presenter;
    private Button btnAddProposal, btnRemoveProposal, btnLogout;
    private TextView tvTopic, tvLecturer, tvProposalStatus, tvComment, tvDocumentUrl;
    private AccountRepository accountRepository;
    private ProjectRepository projectRepository;
    private GroupMemberRepository groupMemberRepo;
    private String userCode;
    private String currentUserCode, currentUserRole;
    private DrawerLayout drawerLayout;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final int REQUEST_CREATE_PROPOSAL = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.proposal_details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        accountRepository = new AccountRepository(this);
        projectRepository = new ProjectRepository(this);
        groupMemberRepo = new GroupMemberRepository(this);

        initializeViews();

        currentUserCode = getIntent().getStringExtra("USER_CODE");
        currentUserRole = getIntent().getStringExtra("ROLE");

        if (currentUserCode == null || currentUserRole == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        setupSidebar();

        if (getIntent().hasExtra("proposal")) {
            ProjectEntity project = (ProjectEntity) getIntent().getSerializableExtra("proposal");
            showProposalDetails(project);
        } else if (getIntent().hasExtra("project_id")) {
            int projectId = getIntent().getIntExtra("project_id", -1);
            initializePresenter();
            presenter.loadProposalById(projectId);
        } else {
            userCode = currentUserCode;
            loadProjectByUserCode();
            checkUserRoleAndHandleProjectCreation();
        }

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_PROPOSAL && resultCode == RESULT_OK) {
            loadProjectByUserCode();
        }
    }

    private void initializeViews() {
        btnAddProposal = findViewById(R.id.btn_add_proposal);
        btnRemoveProposal = findViewById(R.id.btn_remove_proposal);
        tvTopic = findViewById(R.id.tv_topic);
        tvLecturer = findViewById(R.id.tv_lecturer);
        tvProposalStatus = findViewById(R.id.tv_proposal_status);
        tvComment = findViewById(R.id.tv_comment);
        tvDocumentUrl = findViewById(R.id.tv_document_url);

        btnAddProposal.setOnClickListener(v -> redirectToCreateProposal());
        btnRemoveProposal.setOnClickListener(v -> {
            if (presenter != null) presenter.onRemoveProposalClicked();
        });
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new ProposalDetailPresenter(this, this);
        }
    }

    private void loadProjectByUserCode() {
        executorService.execute(() -> {
            boolean hasProject = projectRepository.hasProjectForUser(userCode);
            mainHandler.post(() -> {
                if (hasProject) {
                    Integer projectId = projectRepository.getProjectIdByUserCode(userCode);
                    if (projectId != null) {
                        initializePresenter();
                        presenter.loadProposalById(projectId);
                    }
                    showAddProposalButton(false);
                } else {
                    showMessage("No project found for this user");
                    showRemoveProposalButton(false);
                    checkUserRoleAndHandleProjectCreation();
                }
            });
        });
    }

    private void checkUserRoleAndHandleProjectCreation() {
        executorService.execute(() -> {
            boolean isLeader = groupMemberRepo.isUserLeaderInAnyGroup(userCode);
            boolean hasProject = projectRepository.hasProjectForUser(userCode);
            mainHandler.post(() -> {
                showAddProposalButton(isLeader && !hasProject);
                Log.d("ButtonState", "Leader: " + isLeader + ", HasProject: " + hasProject);
            });
        });
    }

    private void setupSidebar() {
        drawerLayout = findViewById(R.id.proposal_details);
        ImageButton btnMenu = findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        inflateSidebarByRole();
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

            btnHome.setOnClickListener(v -> startWithUser(StudentDashboardActivity.class));
            btnGroup.setOnClickListener(v -> startWithUser(GroupDetailActivity.class));
            btnProposal.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        } else if ("LECTURER".equalsIgnoreCase(currentUserRole)) {
            sidebarView = inflater.inflate(R.layout.sidebar_lecturer, sidebarContainer, false);
            sidebarContainer.addView(sidebarView);

            Button btnHome = sidebarView.findViewById(R.id.nav_home);
//            Button btnGroup = sidebarView.findViewById(R.id.nav_group);
            Button btnProject = sidebarView.findViewById(R.id.nav_project);
            LinearLayout submenu = sidebarView.findViewById(R.id.project_submenu);
            Button btnPending = sidebarView.findViewById(R.id.nav_pending);
            Button btnApproved = sidebarView.findViewById(R.id.nav_approved);
            Button btnRejected = sidebarView.findViewById(R.id.nav_rejected);

            btnHome.setOnClickListener(v -> startWithUser(LectureDashboardActivity.class));
//            btnGroup.setOnClickListener(v -> startWithUser(GroupDetailActivity.class));

            btnProject.setOnClickListener(v -> {
                boolean isVisible = submenu.getVisibility() == View.VISIBLE;
                submenu.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                btnProject.setText(isVisible ? "Project ▼" : "Project ▲");
            });

            btnPending.setOnClickListener(v -> startWithUser(PendingDetailsProposalActivity.class));
            btnApproved.setOnClickListener(v -> startWithUser(ApprovedDetailsProposalActivity.class));
            btnRejected.setOnClickListener(v -> startWithUser(RejectedDetailsProposalActivity.class));
        }
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
            clearFields();
            checkUserRoleAndRedirectToCreate();
            return;
        }

        tvTopic.setText(project.getTopic());

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
        showAddProposalButton(false);
    }

    private void checkUserRoleAndRedirectToCreate() {
        executorService.execute(() -> {
            boolean isLeader = groupMemberRepo.isUserLeaderInAnyGroup(userCode);
            mainHandler.post(() -> {
                if (isLeader) {
                    redirectToCreateProposal();
                } else {
                    showMessage("No project found for this user");
                    showAddProposalButton(false);
                    showRemoveProposalButton(false);
                }
            });
        });
    }

    private void redirectToCreateProposal() {
        Log.d("ProposalDetail", "Redirecting to CreateProposalActivity");
        Intent intent = new Intent(this, CreateProposalActivity.class);
        intent.putExtra("USER_CODE", userCode);
        startActivity(intent);
    }

    private void clearFields() {
        tvTopic.setText("");
        tvLecturer.setText("");
        tvProposalStatus.setText("");
        tvComment.setText("");
        tvDocumentUrl.setText("");
    }

    @Override
    public void showAddProposalButton(boolean show) {
        btnAddProposal.setVisibility(show ? View.VISIBLE : View.GONE);
        btnAddProposal.setEnabled(show);
        Log.d("ProposalDetail", "Add proposal button visible: " + show + ", enabled: " + show);
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
        // Future implementation
    }
}