package com.mti.db.geobuddies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mti.db.geobuddies.test.GenerateAccounts;

/**
 * Created by Daniel on 10/20/2014.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "geobuddies.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    // Table Users and its columns
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_ID = "AccountID";
    public static final String COLUMN_USERNAME = "UserName";
    public static final String COLUMN_FIRSTNAME = "FirstName";
    public static final String COLUMN_LASTNAME = "LastName";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";

    // Table Friends and its columns


    // Database creation sql statement
    private static final String TABLE_USERS_CREATE =
            "create table " + TABLE_USERS + " (" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_USERNAME + " text not null, " +
            COLUMN_FIRSTNAME + " text not null, " +
            COLUMN_LASTNAME + " text not null, " +
            COLUMN_EMAIL + " text not null, " +
            COLUMN_PASSWORD + " text not null, " +
            COLUMN_LATITUDE + " real, " +
            COLUMN_LONGITUDE + " real);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_USERS_CREATE);

        // Generate sample accounts
        new GenerateAccounts(context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

}
