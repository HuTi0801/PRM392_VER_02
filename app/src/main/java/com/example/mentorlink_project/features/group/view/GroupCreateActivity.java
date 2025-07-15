package com.example.mentorlink_project.features.group.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mentorlink_project.R;
import com.example.mentorlink_project.features.group.contract.GroupCreateContract;
import com.example.mentorlink_project.features.group.presenter.GroupCreatePresenter;

import java.util.ArrayList;
import java.util.List;

public class GroupCreateActivity extends AppCompatActivity implements GroupCreateContract.View {
    private EditText edtGroupName, edtMemberId1, edtMemberId2, edtMemberId3, edtMemberId4, edtMemberId5;
    private TextView tvMemberName1, tvMemberName2, tvMemberName3, tvMemberName4, tvMemberName5;
    private Button btnSave, btnClear;
    private GroupCreatePresenter presenter;
    private String leaderId = "SE0001"; // get from session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        edtGroupName = findViewById(R.id.edt_group_name);
        edtMemberId1 = findViewById(R.id.edt_member_id_1); // <-- ADD THIS
        edtMemberId2 = findViewById(R.id.edt_member_id_2);
        edtMemberId3 = findViewById(R.id.edt_member_id_3);
        edtMemberId4 = findViewById(R.id.edt_member_id_4);
        edtMemberId5 = findViewById(R.id.edt_member_id_5);

        tvMemberName1 = findViewById(R.id.tv_member_name_1); // <-- ADD THIS
        tvMemberName2 = findViewById(R.id.tv_member_name_2);
        tvMemberName3 = findViewById(R.id.tv_member_name_3);
        tvMemberName4 = findViewById(R.id.tv_member_name_4);
        tvMemberName5 = findViewById(R.id.tv_member_name_5);

        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);

        presenter = new GroupCreatePresenter(this, this);
        presenter.initLeader(leaderId);

        // Set leader ID (auto-filled, disabled)
        edtMemberId1.setText(leaderId);
        edtMemberId1.setEnabled(false);

        // ...add TextWatchers for edtMemberId2-5 as before...

        btnSave.setOnClickListener(v -> {
            List<String> memberIds = new ArrayList<>();
            memberIds.add(edtMemberId1.getText().toString().trim()); // leader
            memberIds.add(edtMemberId2.getText().toString().trim());
            memberIds.add(edtMemberId3.getText().toString().trim());
            memberIds.add(edtMemberId4.getText().toString().trim());
            memberIds.add(edtMemberId5.getText().toString().trim());
            presenter.onSaveClicked(edtGroupName.getText().toString().trim(), memberIds);
        });

        btnClear.setOnClickListener(v -> presenter.onClearClicked());
    }

    @Override
    public List<String> getMemberIds() {
        List<String> ids = new ArrayList<>();
        ids.add(edtMemberId1.getText().toString().trim()); // leader
        ids.add(edtMemberId2.getText().toString().trim());
        ids.add(edtMemberId3.getText().toString().trim());
        ids.add(edtMemberId4.getText().toString().trim());
        ids.add(edtMemberId5.getText().toString().trim());
        return ids;
    }

    @Override
    public void showMemberName(int index, String name) {
        switch (index) {
            case 0: tvMemberName1.setText(name); break;
            case 1: tvMemberName2.setText(name); break;
            case 2: tvMemberName3.setText(name); break;
            case 3: tvMemberName4.setText(name); break;
            case 4: tvMemberName5.setText(name); break;
        }
    }

    @Override
    public void showMemberError(int index, String error) {
        // Example: show error as Toast or set error on EditText
        switch (index) {
            case 0: edtMemberId1.setError(error); break;
            case 1: edtMemberId2.setError(error); break;
            case 2: edtMemberId3.setError(error); break;
            case 3: edtMemberId4.setError(error); break;
            case 4: edtMemberId5.setError(error); break;
        }
    }

    @Override
    public void setSaveEnabled(boolean enabled) {
        btnSave.setEnabled(enabled);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearFields() {
        edtGroupName.setText("");
        // Do not clear leader ID
        edtMemberId2.setText("");
        edtMemberId3.setText("");
        edtMemberId4.setText("");
        edtMemberId5.setText("");
        tvMemberName1.setText("(Leader)");
        tvMemberName2.setText("");
        tvMemberName3.setText("");
        tvMemberName4.setText("");
        tvMemberName5.setText("");
    }


}
