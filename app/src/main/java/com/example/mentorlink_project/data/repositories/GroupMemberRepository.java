package com.example.mentorlink_project.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mentorlink_project.data.AppDatabaseHelper;
import com.example.mentorlink_project.data.dao.GroupMemberDao;
import com.example.mentorlink_project.data.entities.GroupMemberEntity;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberRepository {
    private final AppDatabaseHelper dbHelper;

    public GroupMemberRepository(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    public void insertGroupMember(GroupMemberEntity member) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_id", member.getGroupId());
        values.put("user_code", member.getUserCode());
        values.put("roles", member.getRoles());
        db.insert(GroupMemberDao.TABLE_NAME, null, values);
        db.close();
    }

    public List<GroupMemberEntity> getMembersByGroupId(int groupId) {
        List<GroupMemberEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(GroupMemberDao.TABLE_NAME, null, "group_id = ?",
                new String[]{String.valueOf(groupId)}, null, null, null);
        while (cursor.moveToNext()) {
            GroupMemberEntity member = new GroupMemberEntity();
            member.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            member.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            member.setUserCode(cursor.getString(cursor.getColumnIndexOrThrow("user_code")));
            member.setRoles(cursor.getString(cursor.getColumnIndexOrThrow("roles")));
            list.add(member);
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean checkStudentHasGroup(String userCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM GroupMember WHERE user_code = ?",
                new String[]{userCode}
        );
        boolean hasGroup = cursor.moveToFirst();
        cursor.close();
        return hasGroup;
    }

    /**
     * Checks if the given user is already a member of any group.
     * @param userCode The student/user code to check.
     * @return true if the user is in any group, false otherwise.
     */
    public boolean isUserInAnyGroup(String userCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                GroupMemberDao.TABLE_NAME,
                new String[]{"id"},
                "user_code = ?",
                new String[]{userCode},
                null, null, null
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public GroupMemberEntity getMemberByUserCode(String userCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        GroupMemberEntity member = null;

        Cursor cursor = db.query(
                GroupMemberDao.TABLE_NAME,
                null,
                "user_code = ?",
                new String[]{userCode},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            member = new GroupMemberEntity();
            member.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            member.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("group_id")));
            member.setUserCode(cursor.getString(cursor.getColumnIndexOrThrow("user_code")));
            member.setRoles(cursor.getString(cursor.getColumnIndexOrThrow("roles")));
        }

        cursor.close();
        db.close();
        return member;
    }

    public int getGroupIdByUserCode(String userCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int groupId = -1;

        Cursor cursor = db.query(
                GroupMemberDao.TABLE_NAME,
                new String[]{"group_id"},  // Only select the group_id column
                "user_code = ?",
                new String[]{userCode},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            groupId = cursor.getInt(cursor.getColumnIndexOrThrow("group_id"));
        }

        cursor.close();
        db.close();
        return groupId;
    }

    public void removeMember(int groupId, String userCode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(GroupMemberDao.TABLE_NAME,
                "group_id = ? AND user_code = ?",
                new String[]{String.valueOf(groupId), userCode});
        db.close();
    }

    public void updateMemberRole(int groupId, String userCode, String newRole) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("roles", newRole);
        db.update(GroupMemberDao.TABLE_NAME, values,
                "group_id = ? AND user_code = ?",
                new String[]{String.valueOf(groupId), userCode});
        db.close();
    }
}
