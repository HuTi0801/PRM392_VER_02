package com.example.mentorlink_project;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView menuIcon;
    private FrameLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.img_logo); // Assuming this is in your header
        contentFrame = findViewById(R.id.content_frame);

        // Load the group details content by default
        loadGroupDetailsContent();

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void loadGroupDetailsContent() {
        // Inflate and add the group details layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View groupDetailsView = inflater.inflate(R.layout.activity_group_details, contentFrame, false);

        // Remove the header include from the inflated view since we already have it in main
        View headerInclude = groupDetailsView.findViewById(R.id.include_header);
        if (headerInclude != null && headerInclude.getParent() != null) {
            ((android.view.ViewGroup) headerInclude.getParent()).removeView(headerInclude);
        }

        // Clear existing content and add the new view
        contentFrame.removeAllViews();
        contentFrame.addView(groupDetailsView);

        // Set up click listeners for the buttons in group details
        setupGroupDetailsListeners(groupDetailsView);
    }

    private void setupGroupDetailsListeners(View groupDetailsView) {
        // Find and set up click listeners for buttons
        groupDetailsView.findViewById(R.id.btn_topic).setOnClickListener(v -> {
            // Handle topic button click
        });

        groupDetailsView.findViewById(R.id.btn_major).setOnClickListener(v -> {
            // Handle major button click
        });

        groupDetailsView.findViewById(R.id.btn_clear).setOnClickListener(v -> {
            // Handle clear button click
        });

        groupDetailsView.findViewById(R.id.btn_save).setOnClickListener(v -> {
            // Handle save button click
        });

        groupDetailsView.findViewById(R.id.btn_project_details).setOnClickListener(v -> {
            // Handle project details button click
        });
    }
}