package com.example.mentorlink_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mentorlink_project.data.dao.AccountDao;
import com.example.mentorlink_project.data.dao.GroupDao;
import com.example.mentorlink_project.data.dao.GroupMemberDao;
import com.example.mentorlink_project.data.dao.ProjectDao;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mentorlink.db";
    private static final int DB_VERSION = 1;

    public AppDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(AccountDao.CREATE_TABLE);
        db.execSQL(GroupDao.CREATE_TABLE);
        db.execSQL(GroupMemberDao.CREATE_TABLE);
        db.execSQL(ProjectDao.CREATE_TABLE);

        // Insert fixed data (seed)
        db.execSQL("INSERT INTO Account (userCode, password, role, full_name, major_name) VALUES " +
                "('SE0001', '123', 'STUDENT', 'Nguyen Van A', 'Software Engineering')," +
                "('SE0002', '123', 'STUDENT', 'Tran Thi B', 'Software Engineering')," +
                "('SE0003', '123', 'STUDENT', 'Le Van C', 'Software Engineering')," +
                "('SE0004', '123', 'STUDENT', 'Mai Thi D', 'Software Engineering')," +
                "('SE0005', '123', 'STUDENT', 'Pham Van E', 'Software Engineering')," +
                "('SE0006', '123', 'STUDENT', 'Hoang Van F', 'Software Engineering')," +
                "('SE0007', '123', 'STUDENT', 'Do Thi G', 'Software Engineering')," +
                "('SE0008', '123', 'STUDENT', 'Bui Van H', 'Software Engineering')," +
                "('SE0009', '123', 'STUDENT', 'Dang Thi I', 'Software Engineering')," +
                "('SE0010', '123', 'STUDENT', 'Ngo Van K', 'Software Engineering')," +
                "('LEC01', '123', 'LECTURER', 'Le Minh', 'Software Engineering')," +
                "('LEC02', '123', 'LECTURER', 'Tran Hung', 'Software Engineering');");

        db.execSQL("INSERT INTO GroupProject (id, project_id, name) VALUES " +
                "(1, 1, 'AI Team')," +
                "(2, 2, 'FarmX')," +
                "(3, 3, 'FPT Network');");

        db.execSQL("INSERT INTO GroupMember (id, group_id, user_code, roles) VALUES " +
                "(1, 1, 'SE0001', 'LEADER')," +
                "(2, 1, 'SE0002', 'MEMBER')," +
                "(3, 1, 'SE0003', 'MEMBER')," +
                "(4, 1, 'LEC01',  'LECTURER')," +
                "(5, 2, 'SE0004', 'LEADER')," +
                "(6, 2, 'SE0005', 'MEMBER')," +
                "(7, 2, 'SE0006', 'MEMBER')," +
                "(8, 2, 'LEC02',  'LECTURER')," +
                "(9, 3, 'SE0007', 'LEADER')," +
                "(10, 3, 'SE0008', 'MEMBER')," +
                "(11, 3, 'SE0009', 'MEMBER')," +
                "(12, 3, 'LEC01',  'LECTURER');");

        db.execSQL("INSERT INTO Project (id, group_id, lecture_code, topic, description, status, created_at, reject_reason, document_url) VALUES " +
                "(1, 1, 'LEC01', 'AI Chatbot for Education', 'Chatbot hỗ trợ sinh viên trả lời câu hỏi môn học', 'PENDING', '2025-07-10', NULL, 'chatbot_ai.pdf')," +
                "(2, 2, 'LEC02', 'Smart Farming', 'Ứng dụng IoT và AI cho trang trại', 'APPROVED', '2025-07-08', NULL, 'smart_farming.pdf')," +
                "(3, 3, 'LEC01', 'FPT Student Social', 'Mạng xã hội nội bộ cho sinh viên FPT', 'REJECTED', '2025-07-05', 'Trùng với đề tài đã phê duyệt năm trước', 'fpt_social.pdf');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop and recreate
        db.execSQL("DROP TABLE IF EXISTS Project;");
        db.execSQL("DROP TABLE IF EXISTS GroupMember;");
        db.execSQL("DROP TABLE IF EXISTS GroupProject;");
        db.execSQL("DROP TABLE IF EXISTS Account;");
        onCreate(db);
    }
}

