package com.example.mentorlink_project.data.dao;

public class GroupDao {
    public static final String TABLE_NAME = "GroupProject";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "project_id INTEGER, " +
            "name TEXT" +
            ");";
}
