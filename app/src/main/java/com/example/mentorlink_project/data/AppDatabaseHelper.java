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
    private static final int DB_VERSION = 4;

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
                "('SE0011', '123', 'STUDENT', 'Nguyen Thi Lien', 'Software Engineering')," +
                "('SE0012', '123', 'STUDENT', 'Tran Van Minh', 'Software Engineering')," +
                "('SE0013', '123', 'STUDENT', 'Le Thi Hanh', 'Software Engineering')," +
                "('SE0014', '123', 'STUDENT', 'Hoang Van Tu', 'Software Engineering')," +
                "('SE0015', '123', 'STUDENT', 'Pham Thi Mai', 'Software Engineering')," +
                "('SE0016', '123', 'STUDENT', 'Do Van Thanh', 'Software Engineering')," +
                "('SE0017', '123', 'STUDENT', 'Vo Thi Bich', 'Software Engineering')," +
                "('SE0018', '123', 'STUDENT', 'Dang Van Long', 'Software Engineering')," +
                "('SE0019', '123', 'STUDENT', 'Nguyen Van Quang', 'Software Engineering')," +
                "('SE0020', '123', 'STUDENT', 'Nguyen Thi X', 'Software Engineering')," +
                "('SE0021', '123', 'STUDENT', 'Tran Van Y', 'Software Engineering')," +
                "('SE0022', '123', 'STUDENT', 'Le Thi Z', 'Software Engineering')," +
                "('LEC01', '123', 'LECTURER', 'Le Minh', 'Software Engineering')," +
                "('LEC02', '123', 'LECTURER', 'Tran Hung', 'Software Engineering');");

        db.execSQL("INSERT INTO GroupProject (id, project_id, name) VALUES " +
                "(1, 1, 'AI Team')," +
                "(2, 2, 'FarmX')," +
                "(3, 3, 'FPT Network')," +
                "(4, 4, 'HealthCare AI')," +
                "(5, 5, 'Green Energy')," +
                "(6, 6, 'Smart Library');");

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
                "(12, 3, 'LEC01',  'LECTURER')," +

                "(13, 4, 'SE0011', 'LEADER')," +
                "(14, 4, 'SE0012', 'MEMBER')," +
                "(15, 4, 'SE0013', 'MEMBER')," +
                "(16, 4, 'LEC01',  'LECTURER')," +

                "(17, 5, 'SE0014', 'LEADER')," +
                "(18, 5, 'SE0015', 'MEMBER')," +
                "(19, 5, 'SE0016', 'MEMBER')," +
                "(20, 5, 'LEC02',  'LECTURER')," +

                "(21, 6, 'SE0017', 'LEADER')," +
                "(22, 6, 'SE0018', 'MEMBER')," +
                "(23, 6, 'SE0019', 'MEMBER')," +
                "(24, 6, 'LEC01',  'LECTURER');");

        db.execSQL("INSERT INTO Project (id, group_id, lecture_code, topic, description, status, created_at, reject_reason, document_url) VALUES " +
                "(1, 1, 'LEC01', 'AI Chatbot for Education', 'Chatbot hỗ trợ sinh viên trả lời câu hỏi môn học', 'PENDING', '2025-07-10', NULL, 'chatbot_ai.pdf')," +
                "(2, 2, 'LEC02', 'Smart Farming', 'Ứng dụng IoT và AI cho trang trại', 'APPROVED', '2025-07-08', NULL, 'smart_farming.pdf')," +
                "(3, 3, 'LEC01', 'FPT Student Social', 'Mạng xã hội nội bộ cho sinh viên FPT', 'REJECTED', '2025-07-05', 'Trùng với đề tài đã phê duyệt năm trước', 'fpt_social.pdf')," +
                "(4, 4, 'LEC01', 'HealthCare AI Assistant', 'Ứng dụng AI hỗ trợ chăm sóc sức khỏe từ xa', 'PENDING', '2025-07-11', NULL, 'healthcare_ai.pdf')," +
                "(5, 5, 'LEC02', 'Green Energy Monitor', 'Giám sát năng lượng tái tạo qua IoT', 'APPROVED', '2025-07-11', NULL, 'green_energy.pdf')," +
                "(6, 6, 'LEC01', 'Smart Library System', 'Hệ thống thư viện thông minh cho trường đại học', 'REJECTED', '2025-07-11', 'Chưa rõ phạm vi triển khai', 'smart_library.pdf');");
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

    public synchronized void refreshConnection() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}

