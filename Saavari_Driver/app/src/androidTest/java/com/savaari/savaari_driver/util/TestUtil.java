package com.savaari.savaari_driver.util;

import com.savaari.savaari_driver.entity.Driver;
import com.savaari.savaari_driver.entity.Ride;
import com.savaari.savaari_driver.entity.RideRequest;
import com.savaari.savaari_driver.entity.Rider;
import com.savaari.savaari_driver.entity.Vehicle;

import java.util.ArrayList;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class TestUtil {
    private static TestUtil instance = new TestUtil();

    public static TestUtil getInstance() {
        return instance;
    }
    public void failTest() {
        assert false;
    }
    public WaitForTextAction waitForText(String text, long timeout) {
        return new WaitForTextAction(text, timeout);
    }
    public WaitForViewAction waitForView(int view) {
        return new WaitForViewAction(withId(view));
    }
    public Driver loadDataForTesting() {
        Driver driver = new Driver();

        Vehicle vehicle = new Vehicle();
        vehicle.setMake("HONDA");
        vehicle.setModel("ACCORD");
        vehicle.setStatus(Vehicle.VH_ACCEPTANCE_ACK);
        vehicle.setRideTypeID(4);

        ArrayList<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle);
        driver.setVehicles(vehicles);
        return driver;
    }

    public RideRequest loadRideRequestForTesting() {
        RideRequest rideRequest = new RideRequest();
        Rider rider = new Rider();
        rider.setUsername("Farjad Ilyas");
        rider.setRating((float) 2.3);
        rideRequest.setRider(rider);
        return rideRequest;
    }
    public Ride loadRideForTesting() {
        Ride ride = new Ride();
        ride.setDistanceTravelled(5000);
        return ride;
    }
}
