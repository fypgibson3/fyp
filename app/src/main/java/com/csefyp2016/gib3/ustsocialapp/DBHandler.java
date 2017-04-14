package com.csefyp2016.gib3.ustsocialapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DBHandler extends SQLiteOpenHelper{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "chatLog";
    private static final String KEY_MESSAGE_ID = "messageID";
    private static final String KEY_SENDTIME = "sendTime";
    private static final String KEY_USER = "user";
    private static final String KEY_MESSAGE = "message";

    private String TABLE_NAME;

    public DBHandler(Context context, String tableName) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = "chatLog_" + tableName;
        System.out.println("Database connected.");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SENDTIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                KEY_USER + " VARCHAR(30) NOT NULL, " +
                KEY_MESSAGE + " VARCHAR(255) NOT NULL" +
                ")";
        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", i, i1));

        String destroy = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(destroy);

        onCreate(sqLiteDatabase);
    }

    public boolean checkTableExisted(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'chatLog_" + tableName;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public void addMessage(String user, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newEntry = new ContentValues();
        newEntry.putNull(KEY_SENDTIME);
        newEntry.put(KEY_USER, user);
        newEntry.put(KEY_MESSAGE, message);
        db.insert(TABLE_NAME, null, newEntry);
    }

    public List<Message> getMessage() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_MESSAGE_ID + ", " + KEY_USER + ", " + KEY_MESSAGE + " FROM ( " +
                            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_MESSAGE_ID + " DESC LIMIT 10 " +
                        ") ORDER BY " + KEY_MESSAGE_ID + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() != 0) {
            List<Message> messageList = new ArrayList<>();
            cursor.moveToFirst();
            Message counter = new Message.Builder(0).username("start").message(cursor.getString(0)).build();
            messageList.add(counter);
            do {
                Message message = new Message.Builder(0).username(cursor.getString(1)).message(cursor.getString(2)).build();
                messageList.add(message);
            } while (cursor.moveToNext());
            cursor.close();
            return messageList;
        }
        cursor.close();
        return null;
    }

    public List<Message> getMessage(String start) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_MESSAGE_ID + ", " + KEY_USER + ", " + KEY_MESSAGE + " FROM ( " +
                "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_MESSAGE_ID + " DESC LIMIT 10 " +
                ") WHERE " + KEY_MESSAGE_ID + " < " + start + " ORDER BY " + KEY_MESSAGE_ID + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null || cursor.getCount() != 0) {
            List<Message> messageList = new ArrayList<>();
            cursor.moveToFirst();
            Message counter = new Message.Builder(0).username("start").message(cursor.getString(0)).build();
            messageList.add(counter);
            do {
                Message message = new Message.Builder(0).username(cursor.getString(1)).message(cursor.getString(2)).build();
                messageList.add(message);
            } while (cursor.moveToNext());
            cursor.close();
            return messageList;
        }
        cursor.close();
        return null;
    }
}
