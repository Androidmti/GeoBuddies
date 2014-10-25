package com.mti.db.geobuddies.database;

import android.content.Context;

/**
 * Created by 124021 on 10/22/2014.
 */
public class GeoBuddiesDAOFactory {

    Context context;

    public GeoBuddiesDAOFactory(Context context) {
        this.context = context;
    }

    public AccountDAO createAccountDAO() {

        return new AccountDAOSQLiteImpl(context);
    }
}
