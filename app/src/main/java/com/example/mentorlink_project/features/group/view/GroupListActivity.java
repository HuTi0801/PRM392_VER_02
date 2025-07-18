package com.example.mentorlink_project.features.group.view;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.repositories.GroupRepository;
import com.example.mentorlink_project.features.group.adapter.GroupListAdapter;
import com.example.mentorlink_project.features.group.contract.GroupListContract;
import com.example.mentorlink_project.features.group.presenter.GroupListPresenter;

import java.util.List;

public class GroupListActivity extends AppCompatActivity implements GroupListContract.View {
    private ListView lvGroups;
    private GroupListContract.Presenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lecturer_manage_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list_group), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvGroups = findViewById(R.id.lvGroups);

        // Khởi tạo presenter
        presenter = new GroupListPresenter(this, new GroupRepository(this));

        // Lấy userCode từ intent
        String userCode = getIntent().getStringExtra("userCode");
        if (userCode != null) {
            presenter.loadGroupsManagedByLecturer(userCode);
        }
    }
    @Override
    public void showGroups(List<String> groupNames) {
        GroupListAdapter adapter = new GroupListAdapter(this, groupNames);
        lvGroups.setAdapter(adapter);
    }
}