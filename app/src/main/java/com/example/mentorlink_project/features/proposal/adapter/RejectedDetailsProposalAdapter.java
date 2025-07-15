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

public class RejectedDetailsProposalAdapter extends BaseAdapter {
    private final Context context;
    private final List<ProjectEntity> projectEntityList;

    public RejectedDetailsProposalAdapter(Context context, List<ProjectEntity> projectEntityList) {
        this.context = context;
        this.projectEntityList = projectEntityList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_item_rejected_proposal, parent, false);

        TextView title = convertView.findViewById(R.id.tvProposalTitle);
        TextView reason = convertView.findViewById(R.id.tvRejectReason);

        ProjectEntity project = projectEntityList.get(i);

        title.setText(projectEntityList.get(i).getTopic());

        String rejectReason = project.getRejectReason();
        if (rejectReason != null && !rejectReason.isEmpty()) {
            reason.setText("Lý do từ chối: " + rejectReason);
        } else {
            reason.setText("Lý do từ chối: Không có thông tin");
        }

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProposalDetailActivity.class);
            intent.putExtra("proposal", projectEntityList.get(i)); // ProjectEntity implements Serializable
            context.startActivity(intent);
        });

        return convertView;
    }
}
