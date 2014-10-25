package com.mti.db.geobuddies.test;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.mti.db.geobuddies.database.AccountDAO;
import com.mti.db.geobuddies.database.GeoBuddiesDAOFactory;
import com.mti.db.geobuddies.model.GeoAccount;

import java.util.ArrayList;

/**
 * Created by Daniel on 10/25/2014.
 */
public class GenerateAccounts {

    private static final String TAG = "GenerateAccounts";
    private static final int USERS = 5;
    ArrayList<GeoAccount> accounts;
    UserRegisterTask userRegisterTask = null;
    Context context;
    LocationGenerator locationGenerator;

    public GenerateAccounts(Context context) {

        this.context = context;
        locationGenerator = new LocationGenerator();
        double randomLat;
        double randomLon;

        accounts = new ArrayList<GeoAccount>();

        String[] userNames = new String[] {"Hammer", "Mike", "Tom", "Raj", "Patrice"};
        String[] firstNames = new String[] {"Troy", "Michael", "Thomas", "Rajiv", "Patrice"};
        String[] lastNames = new String[] {"Hammer", "Walton", "Fitzpatrick", "Sudahari", "Bergeron"};
        String[] emails = new String[] {"a@b.com","hmm@hmm.net","ham@spam.com","d@zzz.org","fit@z.net"};
        String[] passwords = new String[] {"pass","p","a","pass4","pass5"};

        for (int i = 0; i < USERS; i++) {
            accounts.add(new GeoAccount(userNames[i], firstNames[i], lastNames[i],
                    emails[i], passwords[i]));

            // Generate random location
            randomLat = locationGenerator.generateLatitude();
            randomLon = locationGenerator.generateLongitude();

            // Set accounts with random location
            accounts.get(i).setLocation(randomLat, randomLon);

        }

        userRegisterTask = new UserRegisterTask(accounts);
        userRegisterTask.execute((Void) null);

    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {


        ArrayList<GeoAccount> accounts = new ArrayList<GeoAccount>();

        // Set up DAO factory.
        GeoBuddiesDAOFactory factory = new GeoBuddiesDAOFactory(context);

        // Set up account database implementation.
        AccountDAO daoAccount;

        /**
         * Constructor
         * @param accounts - ArrayList containing sample accounts for registering
         */
        UserRegisterTask (ArrayList<GeoAccount> accounts) {

            this.accounts = accounts;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // Create account database implementation.
            daoAccount = factory.createAccountDAO();

            for (GeoAccount account : accounts) {

                boolean result;
                result = daoAccount.registerAccount(account);
                if (result) {
                    Log.d(TAG, "Registered sample user: " + account.getUserName() + " " +
                    account.getLatitude() + " : " + account.getLongitude());
                } else {
                    Log.e(TAG, "Failed to register sample user: " + account.getUserName());
                }

            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            userRegisterTask = null;

        }

        @Override
        protected void onCancelled() {

        }
    }
}
