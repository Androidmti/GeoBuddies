/**
 * GeoAccount class holds information about account, including
 * location of the user of this account
 */
package com.mti.db.geobuddies.model;

public class GeoAccount
{
    private int accountID; // account ID
    protected String userName; // username
    protected String firstName; // first name
    protected String lastName; // last name
    protected String email; // email
    private String password; // password
    protected double latitude; // users latitude location
    protected double longitude; // users longitude location

    /**
     * Default constructor
     */
    public GeoAccount() {}

    /**
     * Constructor
     * -Saves username, first name, last name, birthdate, password into local variables
     * @param userName - User name of account holder
     * @param firstName - First name of account holder
     * @param lastName - last name of account holder
     * @param email - Email of account holder
     * @param password - Password to the account
     */
    public GeoAccount(String userName, String firstName, String lastName,
                      String email, String password)
    {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    } // GeoAccount(String userName, String firstName, String lastName,...

    public int getAccountID() {
        return accountID;
    } // getAccountID()

    public String getUserName()
    {
        return userName;
    } // getUserName()

    public String getFirstName()
    {
        return firstName;
    } // getFirstName()

    public String getLastName()
    {
        return lastName;
    } // getLastName()

    public String getEmail()
    {
        return email;
    } // getEmail()

    public String getPassword()
    {
        return password;
    } // getPassword()

    public double getLatitude()
    {
        return latitude;
    } // getLatitude()

    public double getLongitude()
    {
        return longitude;
    } // getLongitude()

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    } // setAccountID(int accountID)

    /**
     * Method Description
     * -Sets location of user using latitude and longitude
     * @param latitude
     * @param longitude 
     */
    public void setLocation(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    } // setLocation(double latitude, double longitude)

    /**
     * Method Description
     * -Checks this GeoAccount with passed in object. If passed in object is
     * of type GeoAccount and has the same username, then they are equal
     * @param obj
     * @return - boolean
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) // if reference is the same, objects are the same
        {
            return true;
        }
        else if (obj == null) // null parameter, can't be the same
        {
            return false;
        }
        else if (getClass() != obj.getClass()) // type mismatch
        {
            return false;
        } // if (this == obj)

        GeoAccount compareGeoAccount = (GeoAccount) obj; // typecast obj to GeoAccount
        // two accounts are the same if both have the same username
        return (this.userName.equals(compareGeoAccount.getUserName()));
    } // equals(Object obj)

    public String getAll() {
        return
        "accountID: " + accountID + " " +       // account ID
        "userName: " + userName + " " +         // username
        "firstName" + firstName + " " +         // first name
        "lastName" + lastName + " " +           // last name
        "email" + email + " " +                 // email
        "password" + password + " " +           // password
        "latitude" + latitude + " " +           // users latitude location
        "longitude" + longitude;                // users longitude location
    }
} // class GeoAccount
