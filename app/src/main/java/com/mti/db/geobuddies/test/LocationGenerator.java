package com.mti.db.geobuddies.test;

import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

/**
 * Created by 124021 on 10/25/2014.
 */
public class LocationGenerator {

    // 38.660955, -121.342451 MTI college coordinates (center)
    // constants mark the boundaries within which to generate random locations
    private final double MIN_LATITUDE = 38.600000;
    private final double MAX_LATITUDE = 38.700000;
    private final double MIN_LONGITUDE = -121.300000;
    private final double MAX_LONGITUDE = -121.400000;

    /**
     * Method Description
     * -Generates random latitude and random longitude within set boundaries
     */
    public LatLng generateLatLng()
    {
        double randomLat;
        double randomLon;

        Random random = new Random();
        randomLat = MIN_LATITUDE + ((MAX_LATITUDE - MIN_LATITUDE) * random.nextDouble());
        randomLon = MIN_LONGITUDE + ((MAX_LONGITUDE - MIN_LONGITUDE) * random.nextDouble());

        return new LatLng(randomLat, randomLon);
    } // generate()

    public double generateLatitude()
    {
        Random random = new Random();
        return MIN_LATITUDE + ((MAX_LATITUDE - MIN_LATITUDE) * random.nextDouble());
    }

    public double generateLongitude()
    {
        Random random = new Random();
        return MIN_LONGITUDE + ((MAX_LONGITUDE - MIN_LONGITUDE) * random.nextDouble());
    }
}
