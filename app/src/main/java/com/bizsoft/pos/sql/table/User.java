package com.bizsoft.pos.sql.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.BaseAdapter;

import com.bizsoft.pos.dataobject.Store;

import java.util.ArrayList;

/**
 * Created by shri on 13/6/17.
 */

public class User {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserTable.TABLE_NAME + " (" +
                    UserTable._ID + " INTEGER PRIMARY KEY," +
                    UserTable.COLUMN_NAME_USERNAME + " TEXT," +
                    UserTable.COLUMN_NAME_PASSWORD + " TEXT," +
                    UserTable.COLUMN_NAME_FIRSTNAME + " TEXT," +
                    UserTable.COLUMN_NAME_LASTNAME + " TEXT," +
                    UserTable.COLUMN_NAME_NICKNAME + " TEXT," +
                    UserTable.COLUMN_NAME_PROFILEPIC + " TEXT," +
                    UserTable.COLUMN_NAME_ROLE+ " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserTable.TABLE_NAME;
    public User() {

    }

    public static final class UserTable implements BaseColumns
    {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_FIRSTNAME = "firstname";
        public static final String COLUMN_NAME_LASTNAME = "lastname";
        public static final String COLUMN_NAME_NICKNAME = "nickname";
        public static final String COLUMN_NAME_PROFILEPIC = "profile_pic";
        public static final String COLUMN_NAME_ROLE= "role";

    }




    public class UserSqlHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "bizpos.db";

        public UserSqlHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public void createTable(Context context,SQLiteDatabase db)
    {
        UserSqlHelper userSqlHelper = new UserSqlHelper(context);
        userSqlHelper.onCreate(db);

    }
    public long insertTable(SQLiteDatabase db, ContentValues contentValues)
    {

        long newRowId = db.insert(UserTable.TABLE_NAME, null, contentValues);
        return newRowId;
    }
    public void readTable(SQLiteDatabase db)
    {
        ArrayList<com.bizsoft.pos.dataobject.User> userList = new ArrayList<com.bizsoft.pos.dataobject.User>();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){


            res.moveToNext();
        }
    }


}
