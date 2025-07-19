package com.example.mentorlink_project.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mentorlink_project.data.AppDatabaseHelper;
import com.example.mentorlink_project.data.dao.ProjectDao;
import com.example.mentorlink_project.data.entities.ProjectEntity;

import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {
    private final AppDatabaseHelper dbHelper;

    public ProjectRepository(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    public void insertProject(ProjectEntity project) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_id", project.getGroupId());
        values.put("lecture_code", project.getLectureCode());
        values.put("topic", project.getTopic());
        values.put("description", project.getDescription());
        values.put("status", project.getStatus());
        values.put("created_at", project.getCreatedAt());
        values.put("reject_reason", project.getRejectReason());
        values.put("document_url", project.getDocumentUrl());
        db.insert(ProjectDao.TABLE_NAME, null, values);
        db.close();
    }

    public List<ProjectEntity> getAllProjects() {
        List<ProjectEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ProjectDao.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            ProjectEntity p = new ProjectEntity();
            p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            p.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            p.setLectureCode(cursor.getString(cursor.getColumnIndexOrThrow("lecture_code")));
            p.setTopic(cursor.getString(cursor.getColumnIndexOrThrow("topic")));
            p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            p.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            p.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
            p.setRejectReason(cursor.getString(cursor.getColumnIndexOrThrow("reject_reason")));
            p.setDocumentUrl(cursor.getString(cursor.getColumnIndexOrThrow("document_url")));
            list.add(p);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void updateStatus(int projectId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        db.update(ProjectDao.TABLE_NAME, values, "id = ?", new String[]{String.valueOf(projectId)});
        db.close();
    }

    public void updateStatusAndRejectReason(int projectId, String status, String rejectReason) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        values.put("reject_reason", rejectReason);
        db.update(ProjectDao.TABLE_NAME, values, "id = ?", new String[]{String.valueOf(projectId)});
        db.close();
    }

    public List<ProjectEntity> getProjectsByStatus(String status, String lecturerCode) {
        List<ProjectEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query;
        String[] args;

        // Nếu lecturerCode được cung cấp, lọc theo cả status và lecturer
        if (lecturerCode != null && !lecturerCode.trim().isEmpty()) {
            query = "SELECT * FROM Project WHERE TRIM(status) = ? AND TRIM(lecture_code) = ?";
            args = new String[]{status.trim(), lecturerCode.trim()};
        } else {
            // Nếu không có lecturerCode, chỉ lọc theo status
            query = "SELECT * FROM Project WHERE TRIM(status) = ?";
            args = new String[]{status.trim()};
        }

        Cursor cursor = db.rawQuery(query, args);

        while (cursor.moveToNext()) {
            ProjectEntity p = new ProjectEntity();
            p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            p.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            p.setLectureCode(cursor.getString(cursor.getColumnIndexOrThrow("lecture_code")));
            p.setTopic(cursor.getString(cursor.getColumnIndexOrThrow("topic")));
            p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            p.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            p.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
            p.setRejectReason(cursor.getString(cursor.getColumnIndexOrThrow("reject_reason")));
            p.setDocumentUrl(cursor.getString(cursor.getColumnIndexOrThrow("document_url")));
            list.add(p);
        }

        cursor.close();
        db.close();
        return list;
    }

    public ProjectEntity getProjectById(int projectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ProjectDao.TABLE_NAME, null, "id = ?", new String[]{String.valueOf(projectId)}, null, null, null);
        ProjectEntity project = null;
        if (cursor.moveToFirst()) {
            project = new ProjectEntity();
            project.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            project.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            project.setLectureCode(cursor.getString(cursor.getColumnIndexOrThrow("lecture_code")));
            project.setTopic(cursor.getString(cursor.getColumnIndexOrThrow("topic")));
            project.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            project.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            project.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
            project.setRejectReason(cursor.getString(cursor.getColumnIndexOrThrow("reject_reason")));
            project.setDocumentUrl(cursor.getString(cursor.getColumnIndexOrThrow("document_url")));
        }
        cursor.close();
        db.close();
        return project;
    }

    public Integer getProjectIdByUserCode(String userCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Integer projectId = null;

        String query = "SELECT Project.id " +
                "FROM Project " +
                "JOIN GroupMember ON Project.group_id = GroupMember.group_id " +
                "WHERE GroupMember.user_code = ? " +
                "LIMIT 1"; // giả sử mỗi user chỉ thuộc 1 nhóm/project

        Cursor cursor = db.rawQuery(query, new String[]{userCode});
        if (cursor.moveToFirst()) {
            projectId = cursor.getInt(0);
        }
        cursor.close();
        return projectId;
    }

    public boolean hasProjectForUser(String userCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean hasProject = false;

        String query = "SELECT COUNT(*) FROM Project " +
                "JOIN GroupMember ON Project.group_id = GroupMember.group_id " +
                "WHERE GroupMember.user_code = ?";

        Cursor cursor = db.rawQuery(query, new String[]{userCode});
        if (cursor.moveToFirst()) {
            hasProject = cursor.getInt(0) > 0;
        }
        cursor.close();
        db.close();
        return hasProject;
    }
}
