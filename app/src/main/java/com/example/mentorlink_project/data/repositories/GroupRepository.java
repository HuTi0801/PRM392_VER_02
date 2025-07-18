package com.example.mentorlink_project.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mentorlink_project.data.AppDatabaseHelper;
import com.example.mentorlink_project.data.dao.GroupDao;
import com.example.mentorlink_project.data.entities.GroupEntity;

import java.util.ArrayList;
import java.util.List;

public class GroupRepository {
    private final AppDatabaseHelper dbHelper;

    public GroupRepository(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    public void insertGroup(GroupEntity group) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("project_id", group.getProjectId());
        values.put("name", group.getName());
        db.insert(GroupDao.TABLE_NAME, null, values);
        db.close();
    }

    public GroupEntity getGroupById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(GroupDao.TABLE_NAME, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        GroupEntity group = null;
        if (cursor.moveToFirst()) {
            group = new GroupEntity();
            group.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            group.setProjectId(cursor.getInt(cursor.getColumnIndexOrThrow("project_id")));
            group.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }
        cursor.close();
        db.close();
        return group;
    }

    public List<GroupEntity> getAllGroups() {
        List<GroupEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(GroupDao.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            GroupEntity group = new GroupEntity();
            group.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            group.setProjectId(cursor.getInt(cursor.getColumnIndexOrThrow("project_id")));
            group.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            list.add(group);
        }
        cursor.close();
        db.close();
        return list;
    }
}
