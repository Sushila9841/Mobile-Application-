package com.collegeproj.journeyjournal;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    static String name = "JourneyJornalDb";
    static int version = 1;

    String createUserTableSql = "CREATE TABLE if not exists `user` (\n" +
            "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`email`\tTEXT,\n" +
            "\t`address`\tTEXT,\n" +
            "\t`fullname`\tTEXT,\n" +
            "\t`phone`\tTEXT,\n" +
            "\t`password`\tTEXT\n" +
            ")";
    String createJournalTableSql = "CREATE TABLE if not exists `journal` (\n" +
            "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`title`\tTEXT,\n" +
            "\t`userid`\tTEXT,\n" +
            "\t`date`\tTEXT,\n" +
            "\t`description`\tTEXT,\n" +
            "\t`location`\tTEXT,\n" +
            "\t`image`\tBLOB\n" +
            ")";


    public DbHelper(@Nullable Context context) {
        super(context, name, null, version);
        getWritableDatabase().execSQL(createUserTableSql);
        getWritableDatabase().execSQL(createJournalTableSql);
    }


    public boolean insertUser(ContentValues contentValues) {
        if (!isLoginAlreadyRegistered(contentValues.getAsString("email"))
        ) {
            getWritableDatabase().insert("user", "", contentValues);
            return true;
        } else
            return false;


    }

    public void updateUser(String id, ContentValues contentValues) {
        getWritableDatabase().update("user", contentValues, "id=" + id, null);
    }

    public void deleteUser(String id) {
        getWritableDatabase().delete("user", "id=" + id, null);
    }

    public boolean isLoginAlreadyRegistered(String email) {
        String sql = "Select count(*) from user where email='" + email + "'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long l = statement.simpleQueryForLong();
        if (l > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLoginSuccess(String email, String password) {
        String sql = "Select count(*) from user where email='" + email + "' and password='" + password + "'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long l = statement.simpleQueryForLong();
        if (l == 1) {
            return true;
        } else {
            return false;
        }
    }

    public String getUserid(String email, String password) {
        String sql = "Select id from user where email='" + email + "' and password='" + password + "'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        String id = statement.simpleQueryForString();
        return id;
    }
    public String getUsername(String email, String password) {
        String sql = "Select fullname from user where email='" + email + "' and password='" + password + "'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        String id = statement.simpleQueryForString();
        return id;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void insertJournal(ContentValues contentValues) {

        getWritableDatabase().insert("journal", "", contentValues);


    }

    public void updateJournal(String id, ContentValues contentValues) {
        getWritableDatabase().update("journal", contentValues, "id=" + id, null);
    }

    public void deleteJournal(String id) {
        getWritableDatabase().delete("journal", "id=" + id, null);
    }

    @SuppressLint("Range")
    public ArrayList<JournalInfo> getJournalList(String userid) {
        ArrayList<JournalInfo> list = new ArrayList<>();
        String sql = "select * from journal where userid="+userid;
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            JournalInfo info = new JournalInfo();
            info.id = cursor.getString(cursor.getColumnIndex("id"));
            info.title = cursor.getString(cursor.getColumnIndex("title"));
            info.date = cursor.getString(cursor.getColumnIndex("date"));
            info.description = cursor.getString(cursor.getColumnIndex("description"));
            info.location = cursor.getString(cursor.getColumnIndex("location"));
            info.userid = cursor.getString(cursor.getColumnIndex("userid"));
            info.image = cursor.getBlob(cursor.getColumnIndex("image"));
            list.add(info);

        }
        return list;

    }

    @SuppressLint("Range")
    public JournalInfo getJournalInfo(String id) {
        String sql = "select * from journal where id=" + id;
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        JournalInfo info = new JournalInfo();
        while (cursor.moveToNext()) {
            info.id = cursor.getString(cursor.getColumnIndex("id"));
            info.title = cursor.getString(cursor.getColumnIndex("title"));
            info.date = cursor.getString(cursor.getColumnIndex("date"));
            info.description = cursor.getString(cursor.getColumnIndex("description"));
            info.location = cursor.getString(cursor.getColumnIndex("location"));
            info.userid = cursor.getString(cursor.getColumnIndex("userid"));
            info.image = cursor.getBlob(cursor.getColumnIndex("image"));


        }
        return info;

    }


}
