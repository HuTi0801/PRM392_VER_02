package com.example.mentorlink_project.features.proposal.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.mentorlink_project.data.repositories.GroupMemberRepository;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.proposal.contract.ProposalDetailContract;
import com.example.mentorlink_project.features.proposal.presenter.ProposalDetailPresenter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProposalDetailActivity extends AppCompatActivity implements ProposalDetailContract.View {
    private ProposalDetailPresenter presenter;
    private Button btnAddProposal, btnRemoveProposal;
    private TextView tvTopic, tvLecturer, tvProposalStatus, tvComment, tvDocumentUrl;
    private AccountRepository accountRepository;
    private ProjectRepository projectRepository;
    private String userCode;
    DrawerLayout drawerLayout;
    ImageButton btnMenu;
    Button btnProject, btnPending, btnApproved, btnRejected;
    LinearLayout submenu;
    // Add this constant
    private static final int REQUEST_CREATE_PROPOSAL = 1001;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private GroupMemberRepository groupMemberRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.proposal_details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize repositories
        accountRepository = new AccountRepository(this);
        projectRepository = new ProjectRepository(this);
        groupMemberRepo = new GroupMemberRepository(this);

        initializeViews();
        setupSidebar();

        // Handle different intent cases
        if (getIntent().hasExtra("proposal")) {
            // Case 1: Directly passed ProjectEntity
            ProjectEntity project = (ProjectEntity) getIntent().getSerializableExtra("proposal");
            showProposalDetails(project);
        } else if (getIntent().hasExtra("project_id")) {
            // Case 2: Passed project ID
            int projectId = getIntent().getIntExtra("project_id", -1);
            initializePresenter();
            presenter.loadProposalById(projectId);
        } else {
            // Case 3: Only user_code available - find project by user
            userCode = getIntent().getStringExtra("USER_CODE");
            if (userCode == null || userCode.trim().isEmpty()) {
                showMessage("User information not available");
                finish();
                return;
            }
            loadProjectByUserCode();
            checkUserRoleAndHandleProjectCreation();
        }
    }

    // Add this to handle activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_PROPOSAL && resultCode == RESULT_OK) {
            // Refresh the proposal details after creation
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

        // Remove these presenter checks - we'll handle clicks directly
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
                    // Project exists - load and display it
                    Integer projectId = projectRepository.getProjectIdByUserCode(userCode);
                    if (projectId != null) {
                        initializePresenter();
                        presenter.loadProposalById(projectId);
                    }
                    // Disable Add button
                    showAddProposalButton(false);
                } else {
                    showMessage("No project found for this user");
                    // Hide Remove button
                    showRemoveProposalButton(false);
                    // Check if user is leader to show Add button
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
                // Only show if leader AND no project exists
                showAddProposalButton(isLeader && !hasProject);
                Log.d("ButtonState", "Leader: " + isLeader + ", HasProject: " + hasProject);
            });
        });
    }

    private void setupSidebar() {
        drawerLayout = findViewById(R.id.proposal_details);
        btnMenu = findViewById(R.id.btn_menu);

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

        btnPending = findViewById(R.id.nav_pending);
        btnApproved = findViewById(R.id.nav_approved);
        btnRejected = findViewById(R.id.nav_rejected);

        btnPending.setOnClickListener(v -> {
            Intent intent = new Intent(this, PendingDetailsProposalActivity.class);
            intent.putExtra("user_code", userCode);
            startActivity(intent);
        });

        btnApproved.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApprovedDetailsProposalActivity.class);
            intent.putExtra("user_code", userCode);
            startActivity(intent);
        });

        btnRejected.setOnClickListener(v -> {
            Intent intent = new Intent(this, RejectedDetailsProposalActivity.class);
            intent.putExtra("user_code", userCode);
            startActivity(intent);
        });
    }

    @Override
    public void showProposalDetails(ProjectEntity project) {
        if (project == null) {
            clearFields();
            checkUserRoleAndRedirectToCreate();
            return;
        }

        // Show project details (keep existing code)
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

        // Disable Add Proposal button when project exists
        showAddProposalButton(false);
    }

    private void checkUserRoleAndRedirectToCreate() {
        executorService.execute(() -> {
            try {
                boolean isLeader = groupMemberRepo.isUserLeaderInAnyGroup(userCode);
                mainHandler.post(() -> {
                    if (isLeader) {
                        // Only redirect if user is a leader
                        redirectToCreateProposal();
                    } else {
                        showMessage("No project found for this user");
                        showAddProposalButton(false);
                        showRemoveProposalButton(false);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> showMessage("Error checking user role"));
            }
        });
    }

    private void redirectToCreateProposal() {
        // Add logging to verify this is being called
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
        btnAddProposal.setEnabled(show);  // Also control enabled state
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
        // Implement if needed for member display
    }
}