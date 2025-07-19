package com.example.mentorlink_project.features.proposal.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.entities.ProjectEntity;
import com.example.mentorlink_project.features.proposal.view.ProposalDetailActivity;

import java.util.List;

public class ApprovedDetailProposalAdapter extends BaseAdapter {
    private final Context context;
    private final List<ProjectEntity> projectEntityList;
    private final String userCode, userRole;

    public ApprovedDetailProposalAdapter(Context context, List<ProjectEntity> projectEntityList, String userCode, String userRole) {
        this.context = context;
        this.projectEntityList = projectEntityList;
        this.userCode = userCode;
        this.userRole = userRole;
    }

    @Override
    public int getCount() {
        return projectEntityList.size();
    }

    @Override
    public Object getItem(int i) {
        return projectEntityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return projectEntityList.get(i).getId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_item_approved_proposal, parent, false);

        TextView tvTitle = convertView.findViewById(R.id.tvProposalTitle);
        tvTitle.setText(projectEntityList.get(i).getTopic());

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProposalDetailActivity.class);
            intent.putExtra("proposal", projectEntityList.get(i));
            intent.putExtra("USER_CODE", userCode);
            intent.putExtra("ROLE", userRole);
            context.startActivity(intent);
        });

        return convertView;
    }
}
