package com.example.mentorlink_project.data.dao;

public class ProjectDao {
    public static final String TABLE_NAME = "Project";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "group_id INTEGER, " +
            "lecture_code TEXT, " +
            "topic TEXT, " +
            "description TEXT, " +
            "status TEXT, " +
            "created_at TEXT, " +
            "reject_reason TEXT, " +
            "document_url TEXT" +
            ");";
}
