package com.example.mentorlink_project.features.proposal.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.features.proposal.contract.ProposalDetailContract;
import com.example.mentorlink_project.features.proposal.presenter.ProposalDetailPresenter;

import java.util.List;

public class ProposalDetailActivity extends AppCompatActivity implements ProposalDetailContract.View {
    private ProposalDetailPresenter presenter;
    private Button btnAddProposal, btnRemoveProposal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_details);

        btnAddProposal = findViewById(R.id.btn_add_proposal);
        btnRemoveProposal = findViewById(R.id.btn_remove_proposal);

        // Example: get from intent/session
        String userRole = "LEADER"; // or "MEMBER", "LECTURER"
        int groupId = 1;

        presenter = new ProposalDetailPresenter(this, this);
        presenter.loadProposal(groupId, userRole);

        btnAddProposal.setOnClickListener(v -> presenter.onAddProposalClicked());
        btnRemoveProposal.setOnClickListener(v -> presenter.onRemoveProposalClicked());
    }

    @Override
    public void showProposalDetails(ProjectEntity project) {
        // Set all TextViews with project info or clear if null
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
