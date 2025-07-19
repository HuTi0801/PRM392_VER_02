package com.example.mentorlink_project.features.proposal.view;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.repositories.GroupMemberRepository;
import com.example.mentorlink_project.features.login.LoginActivity;
import com.example.mentorlink_project.features.proposal.contract.CreateProposalContract;
import com.example.mentorlink_project.features.proposal.presenter.CreateProposalPresenter;

import java.util.List;

public class CreateProposalActivity extends AppCompatActivity implements CreateProposalContract.View {
    private EditText edtTitle, edtDesc;
    private Spinner spinnerMajor, spinnerLecturer;
    private Button btnAttachFile, btnSend, btnLogout;
    private TextView tvFileName;
    private CreateProposalPresenter presenter;
    private String fileUrl = "";
    private int groupId = 1; // Get from session/intent
    private String userCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_proposal);

        edtTitle = findViewById(R.id.edt_proposal_title);
        edtDesc = findViewById(R.id.edt_description);
        spinnerMajor = findViewById(R.id.spinner_major);
        spinnerLecturer = findViewById(R.id.spinner_lecturer);
        btnAttachFile = findViewById(R.id.btn_attach_file);
        btnSend = findViewById(R.id.btn_send);
        tvFileName = findViewById(R.id.tv_file_name);

        presenter = new CreateProposalPresenter(this, this);
        presenter.loadMajors();
        userCode = getIntent().getStringExtra("USER_CODE");
        if (userCode == null || userCode.isEmpty()) {
            showMessage("User information not available");
            finish();
            return;
        }

        loadGroupId();

        spinnerMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String major = (String) parent.getItemAtPosition(pos);
                presenter.loadLecturers(major);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAttachFile.setOnClickListener(v -> {
            // Open file picker
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 1001);
        });

        btnSend.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();
            String major = (String) spinnerMajor.getSelectedItem();
            String lecturer = (String) spinnerLecturer.getSelectedItem();
            presenter.onSendClicked(title, desc, fileUrl, major, lecturer, groupId);
        });

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadGroupId() {
        new Thread(() -> {
            groupId = new GroupMemberRepository(this).getGroupIdByUserCode(userCode);
            runOnUiThread(() -> {
                if (groupId == -1) {
                    showMessage("Failed to load group information");
                    finish();
                }
            });
        }).start();
    }

    @Override
    public void showMajors(List<String> majors) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, majors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMajor.setAdapter(adapter);
    }

    @Override
    public void showLecturers(List<String> lecturers) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lecturers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLecturer.setAdapter(adapter);
    }

    @Override
    public void showFileName(String fileName) {
        tvFileName.setText(fileName);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishWithSuccess() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                fileUrl = uri.toString();
                String fileName = uri.getLastPathSegment();
                showFileName(fileName);
                presenter.onFileAttached(fileName); // Notify presenter if needed
            }
        }
    }
}