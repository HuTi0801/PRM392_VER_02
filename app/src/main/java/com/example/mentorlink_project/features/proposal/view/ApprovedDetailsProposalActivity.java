package com.example.mentorlink_project.features.proposal.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.data.repositories.ProjectRepository;
import com.example.mentorlink_project.features.dashboard.LectureDashboardActivity;
import com.example.mentorlink_project.features.login.LoginActivity;
import com.example.mentorlink_project.features.proposal.adapter.ApprovedDetailProposalAdapter;
import com.example.mentorlink_project.features.proposal.contract.ApprovedDetailsProposalContract;
import com.example.mentorlink_project.features.proposal.presenter.ApprovedDetailsProposalPresenter;

import java.util.ArrayList;
import java.util.List;

public class ApprovedDetailsProposalActivity extends AppCompatActivity implements ApprovedDetailsProposalContract.View {

    private ListView lvApproved;
    private ApprovedDetailsProposalPresenter presenter;
    private ApprovedDetailProposalAdapter adapter;
    DrawerLayout drawerLayout;
    ImageButton btnMenu;
    Button btnProject, btnPending, btnApproved, btnRejected, btnHome, btnGroup;;
    LinearLayout submenu;
    String currentUserCode, currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approved_details_proposal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.approved_details_proposal), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        //hiển thị sidebar
        drawerLayout = findViewById(R.id.approved_details_proposal);
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

        btnHome = findViewById(R.id.nav_home);
        btnGroup = findViewById(R.id.nav_group);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, LectureDashboardActivity.class);
            intent.putExtra("USER_CODE", currentUserCode);
            intent.putExtra("ROLE", currentUserRole);
            startActivity(intent);
        });

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
            intent.putExtra("USER_CODE", currentUserCode);
            intent.putExtra("ROLE", currentUserRole);
            startActivity(intent);
        });

        btnApproved.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApprovedDetailsProposalActivity.class);
            intent.putExtra("USER_CODE", currentUserCode);
            intent.putExtra("ROLE", currentUserRole);
            startActivity(intent);
        });

        btnRejected.setOnClickListener(v -> {
            Intent intent = new Intent(this, RejectedDetailsProposalActivity.class);
            intent.putExtra("USER_CODE", currentUserCode);
            intent.putExtra("ROLE", currentUserRole);
            startActivity(intent);
        });

        lvApproved = findViewById(R.id.lvApprovedProposals);
        presenter = new ApprovedDetailsProposalPresenter(this, new ProjectRepository(this), currentUserCode);
        presenter.loadApprovedProposals();
    }

    @Override
    public void showApprovedProposals(List<ProjectEntity> proposalList) {
        if (proposalList == null) proposalList = new ArrayList<>();

        adapter = new ApprovedDetailProposalAdapter(this, proposalList, currentUserCode, currentUserRole);
        lvApproved.setAdapter(adapter);

        TextView tvEmpty = findViewById(R.id.tvEmptyApproved);
        if (proposalList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvApproved.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvApproved.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
