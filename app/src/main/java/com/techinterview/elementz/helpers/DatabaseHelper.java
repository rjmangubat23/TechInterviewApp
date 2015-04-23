package com.techinterview.elementz.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class DatabaseHelper {
    public static final String USER_ID = "id";
    public static final String USER_PHOTO = "photo";

    private SQLiteHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    private static final String DATABASE_NAME = "AddUserDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String USERS_TABLE = "Users";

    private static final String CREATE_USERS_TABLE = "create table "
            + USERS_TABLE + " (" + USER_ID
            + " integer primary key autoincrement, " + USER_PHOTO
            + " blob not null );";

    public static class SQLiteHelper extends SQLiteOpenHelper
    {
        SQLiteHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_USERS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
           /* Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
           */ db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);

            onCreate(db);
        }

    }
/*    public void Reset() {
        mDbHelper.onUpgrade(this.mDatabase, 1, 1);
    }*/

    public DatabaseHelper(Context context) {
       // Context mContext = context;
        mDbHelper = new SQLiteHelper(context);
    }

    public com.techinterview.elementz.helpers.DatabaseHelper open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertUserDetails(Bitmap image) {
        ContentValues values = new ContentValues();
        values.put(USER_PHOTO, Utility.getBytes(image));
        mDatabase.insert(USERS_TABLE, null, values);
    }

/*    public Bitmap retrieveUserDetails() throws SQLException {
        Cursor cur = mDatabase.query(true, USERS_TABLE, new String[] { USER_PHOTO,
                *//*USER_NAME, USER_AGE*//* }, null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            byte[] blob = cur.getBlob(cur.getColumnIndex(USER_PHOTO));
            *//*String name = cur.getString(cur.getColumnIndex(USER_NAME));
            int age = cur.getInt(cur.getColumnIndex(USER_AGE));
            *//*
            cur.close();
            return Utility.getPhoto(blob);
        }
        cur.close();
        return null;
    }*/

}

