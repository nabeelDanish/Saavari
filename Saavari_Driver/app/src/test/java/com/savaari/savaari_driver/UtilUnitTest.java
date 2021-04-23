package com.savaari.savaari_driver;

// Imports
import org.junit.Test;
import static org.junit.Assert.assertEquals;

// Unit Testing Class
public class UtilUnitTest {
    @Test
    public void distanceBetweenCoordinates() {
        int num_cases = 2;
        double[] latA = {30.197270, 30.205049};
        double[] lngA = {71.450082, 71.458069};
        double[] latB = {30.201136, 30.203946};
        double[] lngB = {71.459421, 71.459786};
        double[] expe = {995, 205};

        for (int i =0; i < num_cases; ++i) {
            double dist = Util.distance(latA[i], lngA[i], latB[i], lngB[i]);
            assertEquals(expe[i], dist, 5.0);
        }
    }
}