package com.mti.db.geobuddies.database;


import com.mti.db.geobuddies.model.GeoAccount;
import com.mti.db.geobuddies.model.GeoFriend;

import java.util.ArrayList;

/**
 * Created by 124021 on 10/20/2014.
 */
public interface AccountDAO {

    public void open();
    public void close();
    public boolean registerAccount(GeoAccount geoAccount);
    public boolean updateAccount(GeoAccount geoAccount, boolean updatePassword);
    public boolean accountExists(String userName);
    public GeoAccount signIn(String userName, String password);
    public void addFriend(String userName, String friendUserName);
    public ArrayList<GeoFriend> getFriends(String userName);

//    public void confirmFriend
//    public GeoAccount[] getAllAccounts() throws DAOException;
}
