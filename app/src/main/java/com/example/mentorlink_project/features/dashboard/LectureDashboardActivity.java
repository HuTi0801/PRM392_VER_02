package com.example.mentorlink_project.features.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mentorlink_project.R;
import com.example.mentorlink_project.features.login.LoginActivity;
import com.example.mentorlink_project.features.proposal.view.ApprovedDetailsProposalActivity;
import com.example.mentorlink_project.features.proposal.view.PendingDetailsProposalActivity;
import com.example.mentorlink_project.features.proposal.view.RejectedDetailsProposalActivity;

public class LectureDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_dashboard);

        Button btnPending  = findViewById(R.id.btnPending);
        Button btnApproved = findViewById(R.id.btnApproved);
        Button btnRejected = findViewById(R.id.btnRejected);
        Button btnManageGroup = findViewById(R.id.btnManageGroup);

        String currentUserCode = getIntent().getStringExtra("USER_CODE");
        String currentUserRole = getIntent().getStringExtra("ROLE");

        if (currentUserCode == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear stack
            startActivity(intent);
            finish();
            return;
        }

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
    }
}
