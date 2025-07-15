package com.example.mentorlink_project.data.dao;

public class GroupMemberDao {
    public static final String TABLE_NAME = "GroupMember";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "group_id INTEGER, " +
            "user_code TEXT, " +
            "roles TEXT" +
            ");";
}
