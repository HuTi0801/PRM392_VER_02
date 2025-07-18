package com.example.mentorlink_project.features.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.repositories.GroupMemberRepository;
import com.example.mentorlink_project.features.group.view.GroupDetailActivity;
import com.example.mentorlink_project.features.login.LoginActivity;
import com.example.mentorlink_project.features.proposal.view.ProposalDetailActivity;

public class StudentDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        Button btnGroupInfo = findViewById(R.id.btnGroupInfo);
        Button btnProposalInfo = findViewById(R.id.btnProposalInfo);

        // Lấy userCode từ intent
        String currentUserCode = getIntent().getStringExtra("USER_CODE");
        String currentUserRole = getIntent().getStringExtra("ROLE");

        if (currentUserCode == null || currentUserRole == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear stack
            startActivity(intent);
            finish();
            return;
        }

        GroupMemberRepository groupMemberRepository = new GroupMemberRepository(this);

        btnGroupInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupDetailActivity.class);
            intent.putExtra("USER_CODE", currentUserCode);
            intent.putExtra("ROLE", currentUserRole);
            startActivity(intent);
        });

        btnProposalInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProposalDetailActivity.class);
            intent.putExtra("USER_CODE", currentUserCode);
            intent.putExtra("ROLE", currentUserRole);
            startActivity(intent);
        });
    }
}

