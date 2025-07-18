package com.example.mentorlink_project.features.group.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mentorlink_project.R;

import java.util.List;

public class GroupListAdapter extends ArrayAdapter <String> {
    public GroupListAdapter(Context context, List<String> groupNames) {
        super(context, 0, groupNames);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String groupName = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_group, parent, false);
        }

        TextView tvLabel = convertView.findViewById(R.id.tvName);
        TextView tvGroupName = convertView.findViewById(R.id.tvGroupName);

        tvLabel.setText("Tên group:");
        tvGroupName.setText(groupName);

        return convertView;
    }
}
