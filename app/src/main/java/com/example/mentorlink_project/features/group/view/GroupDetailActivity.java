package com.example.mentorlink_project.features.group.view;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mentorlink_project.R;
import com.example.mentorlink_project.features.group.contract.GroupDetailContract;
import com.example.mentorlink_project.features.group.presenter.GroupDetailPresenter;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity implements GroupDetailContract.View {
    private EditText edtGroupName;
    private RecyclerView rvMembers;
    private LinearLayout leaderButtons, layoutMemberInputs, layoutCreateButtons;
    private TextView tvTitle;
    private Button btnEditGroupName, btnAddMember, btnRemoveMember, btnTransferLeader;
    private GroupDetailPresenter presenter;
    private String userRole = "LEADER"; // or "MEMBER"
    private int groupId = 1; // get from intent/session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        tvTitle = findViewById(R.id.tv_title);
        edtGroupName = findViewById(R.id.edt_group_name);
        rvMembers = findViewById(R.id.rv_members);
        leaderButtons = findViewById(R.id.leader_buttons);
        layoutMemberInputs = findViewById(R.id.layout_member_inputs);
        layoutCreateButtons = findViewById(R.id.layout_create_buttons);
        btnEditGroupName = findViewById(R.id.btn_edit_group_name);
        btnAddMember = findViewById(R.id.btn_add_member);
        btnRemoveMember = findViewById(R.id.btn_remove_member);
        btnTransferLeader = findViewById(R.id.btn_transfer_leader);

        // If in detail mode, load group details
        boolean isDetail = getIntent().getBooleanExtra("isDetail", false);
        userRole = getIntent().getStringExtra("userRole"); // "LEADER" or "MEMBER"
        groupId = getIntent().getIntExtra("groupId", 1);

        presenter = new GroupDetailPresenter(this, this);
        if (isDetail) {
            presenter.loadGroupDetails(groupId, userRole);
        } else {
            setDetailMode(false, false);
        }

        btnEditGroupName.setOnClickListener(v -> presenter.onEditGroupNameClicked(edtGroupName.getText().toString().trim()));
        btnAddMember.setOnClickListener(v -> presenter.onAddMemberClicked());
        btnRemoveMember.setOnClickListener(v -> presenter.onRemoveMemberClicked());
        btnTransferLeader.setOnClickListener(v -> presenter.onTransferLeaderClicked());
    }

    @Override
    public void setDetailMode(boolean isDetail, boolean isLeader) {
        if (isDetail) {
            tvTitle.setText("Group Details");
            layoutMemberInputs.setVisibility(View.GONE);
            layoutCreateButtons.setVisibility(View.GONE);
            rvMembers.setVisibility(View.VISIBLE);
            leaderButtons.setVisibility(isLeader ? View.VISIBLE : View.GONE);
            edtGroupName.setEnabled(isLeader);
        } else {
            tvTitle.setText("Create/Edit Group");
            layoutMemberInputs.setVisibility(View.VISIBLE);
            layoutCreateButtons.setVisibility(View.VISIBLE);
            rvMembers.setVisibility(View.GONE);
            leaderButtons.setVisibility(View.GONE);
            edtGroupName.setEnabled(true);
        }
    }

    @Override
    public void showGroupName(String name, boolean editable) {
        edtGroupName.setText(name);
        edtGroupName.setEnabled(editable);
    }

    @Override
    public void showMembers(List<String> members) {
        // Set up RecyclerView adapter to display member list
        // Example:
        // rvMembers.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, members));
    }

    @Override
    public void showLeaderActions(boolean showRemove, int memberCount) {
        btnRemoveMember.setVisibility(showRemove ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
