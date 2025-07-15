package com.example.mentorlink_project.data.dao;

public class AccountDao {
    public static final String TABLE_NAME = "Account";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "userCode TEXT PRIMARY KEY," +
            "password TEXT," +
            "role TEXT," +
            "full_name TEXT," +
            "major_name TEXT" +
            ");";
}
