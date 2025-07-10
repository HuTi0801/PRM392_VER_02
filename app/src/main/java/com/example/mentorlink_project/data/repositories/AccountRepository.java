package com.example.mentorlink_project.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mentorlink_project.data.AppDatabaseHelper;
import com.example.mentorlink_project.data.dao.AccountDao;
import com.example.mentorlink_project.data.entities.AccountEntity;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private final AppDatabaseHelper dbHelper;

    public AccountRepository(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    public void insertAccount(AccountEntity account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userCode", account.getUserCode());
        values.put("password", account.getPassword());
        values.put("role", account.getRole());
        values.put("full_name", account.getFullName());
        values.put("major_name", account.getMajorName());
        db.insert(AccountDao.TABLE_NAME, null, values);
        db.close();
    }

    public AccountEntity getAccountByUserCode(String userCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(AccountDao.TABLE_NAME, null, "userCode = ?",
                new String[]{userCode}, null, null, null);

        AccountEntity account = null;
        if (cursor.moveToFirst()) {
            account = new AccountEntity();
            account.setUserCode(cursor.getString(cursor.getColumnIndexOrThrow("userCode")));
            account.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            account.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            account.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
            account.setMajorName(cursor.getString(cursor.getColumnIndexOrThrow("major_name")));
        }
        cursor.close();
        db.close();
        return account;
    }

    public List<AccountEntity> getAllAccounts() {
        List<AccountEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(AccountDao.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            AccountEntity acc = new AccountEntity();
            acc.setUserCode(cursor.getString(cursor.getColumnIndexOrThrow("userCode")));
            acc.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            acc.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            acc.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
            acc.setMajorName(cursor.getString(cursor.getColumnIndexOrThrow("major_name")));
            list.add(acc);
        }
        cursor.close();
        db.close();
        return list;
    }
}
