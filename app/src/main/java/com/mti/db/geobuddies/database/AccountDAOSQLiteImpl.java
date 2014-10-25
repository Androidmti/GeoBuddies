package com.mti.db.geobuddies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mti.db.geobuddies.model.GeoAccount;
import com.mti.db.geobuddies.model.GeoFriend;

import java.util.ArrayList;


/**
 * Created by 124021 on 10/20/2014.
 */
public class AccountDAOSQLiteImpl implements AccountDAO{

    private final static String TAG = "AccountDAOSQLiteImpl";

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] friendColumns = {
            MySQLiteHelper.COLUMN_USERNAME,
            MySQLiteHelper.COLUMN_FIRSTNAME,
            MySQLiteHelper.COLUMN_LASTNAME,
            MySQLiteHelper.COLUMN_EMAIL,
            MySQLiteHelper.COLUMN_LATITUDE,
            MySQLiteHelper.COLUMN_LONGITUDE
    };

    public AccountDAOSQLiteImpl(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    @Override
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        dbHelper.close();
        database.close();
    }

    @Override
    public boolean registerAccount(GeoAccount geoAccount) {

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USERNAME, geoAccount.getUserName().toLowerCase());
        values.put(MySQLiteHelper.COLUMN_FIRSTNAME, geoAccount.getFirstName());
        values.put(MySQLiteHelper.COLUMN_LASTNAME, geoAccount.getLastName());
        values.put(MySQLiteHelper.COLUMN_EMAIL, geoAccount.getEmail());
        values.put(MySQLiteHelper.COLUMN_PASSWORD, geoAccount.getPassword());
        values.put(MySQLiteHelper.COLUMN_LATITUDE, geoAccount.getLatitude());
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, geoAccount.getLongitude());

        // Check database if user already exists
        if (accountExists(geoAccount.getUserName().toLowerCase())) {
            // Return false if account already exists in database
            return false;
        }

        // Insert the new row into SQLite database, returning the primary key value of the new row
        long accountID;

        // Open database connection
        open();

        // Insert values into database and get accountID from newly created record
        accountID = database.insert(MySQLiteHelper.TABLE_USERS, null, values);

        // Assign accountID to the geoAccount object
        geoAccount.setAccountID((int)accountID);

        Log.d(TAG, "Registered new account");
        close();
        return true;
    }

    @Override
    public boolean updateAccount(GeoAccount geoAccount, boolean updatePassword) {
        return false;
    }

    @Override
    public boolean accountExists(String userName) {

        String query = "select * from Users where UserName = ?";
        open();

        Cursor cursor = null;
        try {
            // Run query using lower case username and assign retrieved information to cursor
            cursor = database.rawQuery(query, new String[]{userName.toLowerCase()});

            return (cursor.getCount() > 0);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return false;
    }

    @Override
    public GeoAccount signIn(String userName, String password) {

        // Build query
        String query = "select * from Users where UserName = ? and Password = ?";

        // Open database connection
        open();

        try {
            // Run query and assign returned information to cursor
            // Cast username to lower case to make it case insensitive
            Cursor cursor = database.rawQuery(query, new String[]{userName.toLowerCase(), password});

            // Check if database returned any entries
            if (cursor.getCount() == 0) {
                // If account wasn't found, return null
                cursor.close();
                close();
                return null;
            } else {
                // Create GeoAccount object from database and return it.
                return cursorToAccount(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, "Something went wrong with rawQuery");
            return null;
        }
    }


    @Override
    public void addFriend(String userName, String friendUserName) {

    }

    @Override
    public ArrayList<GeoFriend> getFriends(String userName) {

        //TODO: actually get a real friend list
        // Build query
        String query = "select * from Users";

        // Open database connection
        open();

        try {
            // Run query and assign returned information to cursor
            // Cast username to lower case to make it case insensitive
            Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
                    friendColumns, null, null, null, null, null);
//            Cursor cursor = database.rawQuery(query, new String[]{userName.toLowerCase()});

            // Check if database returned any entries
            if (cursor.getCount() == 0) {
                // If account wasn't found, return null
                cursor.close();
                close();
                return null;
            } else {
                // Create ArrayList of GeoFriend objects from database and return it.
                Log.d(TAG, "Count of friends from cursor: " + cursor.getCount());
                return cursorToFriends(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, "Something went wrong with rawQuery");
            return null;
        }
    }

    private GeoAccount cursorToAccount(Cursor cursor) {

        // Move to the beginning of cursor
        cursor.moveToFirst();

        // Get values from cursor and assign them to variables
        long accountID = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ID));
        String userName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_USERNAME));
        String firstName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_FIRSTNAME));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_LASTNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_EMAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PASSWORD));

        // Create new geoAccount object using values from cursor
        GeoAccount geoAccount = new GeoAccount(userName, firstName, lastName, email, password);

        // Set accountID in a new geoAccount object
        geoAccount.setAccountID((int) accountID);

        // Close cursor and database
        cursor.close();
        close();

        // Return new geoAccount object
        return geoAccount;
    }

    private ArrayList<GeoFriend> cursorToFriends(Cursor cursor) {

        ArrayList<GeoFriend> friends = new ArrayList<GeoFriend>();

        String userName;
        String firstName;
        String lastName;
        String email;
        double latitude;
        double longitude;

        // Move to the beginning of cursor
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            // Get values from cursor and assign them to variables
            userName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_USERNAME));
            firstName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_FIRSTNAME));
            lastName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_LASTNAME));
            email = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_EMAIL));
            latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_LATITUDE));
            longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_LONGITUDE));

            // Create new GeoFriend object using values from cursor
            GeoFriend geoFriend = new GeoFriend(userName, firstName, lastName, email);
            geoFriend.setLocation(latitude, longitude);
            friends.add(geoFriend);
            Log.d(TAG, "Friend added: " + geoFriend.getAll());

            // Move cursor to next record
            cursor.moveToNext();
        }

        Log.d(TAG, "Count all friends added: " + friends.size());

        // Create new geoAccount object using values from cursor
//        GeoAccount geoAccount = new GeoAccount(userName, firstName, lastName, email, password);

        // Set accountID in a new geoAccount object
//        geoAccount.setAccountID((int) accountID);

        // Close cursor and database
        cursor.close();
        close();

        // Return ArrayList of GeoFriend objects
        return friends;
    }
}
