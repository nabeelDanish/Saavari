package com.savaari.api.database;

import com.savaari.api.entity.*;
import com.savaari.api.entity.Driver;
import com.savaari.api.entity.policy.PolicyFactory;
import org.apache.commons.dbutils.DbUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;

public class OracleDBHandler implements DBHandler {

    private static final String LOG_TAG = OracleDBHandler.class.getSimpleName();

    public OracleDBHandler() {
    }

    /* Methods to close ResultSet, PreparedStatement, and Connection */
    private void closeAll(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {

            closeResultSet(resultSet);
            closeStatement(preparedStatement);
            closeConnection(connection);
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null){
            DbUtils.close(connection);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void closeStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                DbUtils.close(preparedStatement);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                DbUtils.close(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    /* End of closing util methods*/

    private static int executeUpdate(String query) {

        int numRowsUpdated;

        try {
            Connection connection = DBCPDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            numRowsUpdated = preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
            return numRowsUpdated;
        }
        catch (Exception e) {
            System.out.println("Exception in OracleDBHandler: executeUpdate\n" + query);
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean loadRideTypes(ArrayList<RideType> rideTypes) {

        if (rideTypes == null) {
            rideTypes = new ArrayList<>();
        }
        Connection connect = null;
        ResultSet resultSet = null;
        RideType fetchedRideType;

        try {
            connect = DBCPDataSource.getConnection();

            String query = "SELECT TYPE_ID, NAME, MAX_PASSENGERS, BASE_FARE, PER_KM_CHARGE, PER_MIN_CHARGE, MIN_FARE" +
                    " FROM RIDE_TYPES";

            resultSet = connect.createStatement().executeQuery(query);

            while (resultSet.next()) {
                fetchedRideType = new RideType(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getDouble(4),
                        resultSet.getDouble(5),
                        resultSet.getDouble(6),
                        resultSet.getDouble(7)

                );

                rideTypes.add(fetchedRideType);
            }

            return true;
        }
        catch (Exception e) {
            System.out.println("Exception in OracleDBHandler: loadRideTypes");
            e.printStackTrace();
            return false;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    //Add a new Rider, TODO: replace last param with RideRequest.NOT_SENT
    @Override
    public Boolean addRider(String username, String emailAddress, String password) {

        String q = String.format("INSERT INTO `RIDER_DETAILS` (`USER_ID`, `" +
                        "USER_NAME`, `PASSWORD`, `EMAIL_ADDRESS`, `FIND_STATUS`, `DRIVER_ID`) " +
                        " VALUES(%d, '%s', '%s', '%s', %d, %d)",
                0, username, password, emailAddress, 0,
                Driver.DEFAULT_ID);
        System.out.println(q);
        return (executeUpdate(q)) > 0;
    }

    @Override
    public Boolean addDriver(String username, String emailAddress, String password) {

        return (executeUpdate(String.format("INSERT INTO `DRIVER_DETAILS` (`USER_ID`, `" +
                        "USER_NAME`, `PASSWORD`, `EMAIL_ADDRESS`) VALUES(%d, '%s', '%s', '%s')",
                0,
                username,
                password,
                emailAddress))) > 0;
    }

    @Override
    public Integer loginRider(Rider rider) {

        Connection connect = null;
        ResultSet resultSet = null;
        try {
            String sqlQuery = "SELECT USER_ID, PASSWORD FROM RIDER_DETAILS WHERE EMAIL_ADDRESS = '" + rider.getEmailAddress() + "'";

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            // If email address & password verified
            if (resultSet.next() && User.verifyPassword(resultSet.getString(2), rider.getPassword())) {
                return resultSet.getInt("USER_ID");
            }
            else {
                return -1;
            }
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:loginRider()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public int loginDriver(Driver driver) {
        Connection connect = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT USER_ID, PASSWORD FROM DRIVER_DETAILS WHERE EMAIL_ADDRESS = '" + driver.getEmailAddress() + "'";

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            // If email address & password verified
            if (resultSet.next() && User.verifyPassword(resultSet.getString(2), driver.getPassword())) {
                System.out.println("Logged in!");
                return resultSet.getInt("USER_ID");
            }
            else {
                System.out.println("Login failed");
                return Driver.DEFAULT_ID;
            }
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:loginDriver()");
            e.printStackTrace();
            return Driver.DEFAULT_ID;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    /* CRUD Operations on User Object */
    @Override
    public JSONArray riderDetails() {
        Connection connect = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT USER_ID, USER_NAME, PASSWORD, EMAIL_ADDRESS FROM RIDER_DETAILS";

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            JSONArray result = new JSONArray();
            JSONObject row = new JSONObject();

            while (resultSet.next()) {
                row.put("USER_ID", resultSet.getInt(1));
                row.put("USER_NAME", resultSet.getString(2));
                row.put("PASSWORD", resultSet.getString(3));
                row.put("EMAIL_ADDRESS", resultSet.getString(4));
                result.put(row);
                row = new JSONObject();
            }
            System.out.println("RESULT: " + result.toString());
            return result;
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:riderDetails()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public JSONArray driverDetails() {
        Connection connect = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT USER_ID, USER_NAME, PASSWORD, EMAIL_ADDRESS FROM DRIVER_DETAILS";

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            JSONArray result = new JSONArray();
            JSONObject row = new JSONObject();

            while (resultSet.next()) {
                row.put("USER_ID", resultSet.getInt(1));
                row.put("USER_NAME", resultSet.getString(2));
                row.put("PASSWORD", resultSet.getString(3));
                row.put("EMAIL_ADDRESS", resultSet.getString(4));
                result.put(row);
                row = new JSONObject();
            }

            return result;
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:driverDetails()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public boolean fetchRiderData(Rider rider) {
        Connection connect = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT USER_NAME, EMAIL_ADDRESS FROM RIDER_DETAILS WHERE USER_ID = " + rider.getUserID();

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            if (resultSet.next()) {
                rider.setUserID(rider.getUserID());
                rider.setUsername(resultSet.getString(1));
                rider.setEmailAddress(resultSet.getString(2));
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:riderData()");
            e.printStackTrace();
            return false;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public boolean fetchDriverData(Driver driver) {
        Connection connect = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT USER_NAME, FIRST_NAME, LAST_NAME, PHONE_NO, CNIC, LICENSE_NO, EMAIL_ADDRESS, " +
                    " STATUS, IS_ACTIVE, ACTIVE_VEHICLE_ID FROM DRIVER_DETAILS WHERE USER_ID = " + driver.getUserID();

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            if (resultSet.next()) {

                ArrayList<Vehicle> vehicles = new ArrayList<>();
                Vehicle currentVehicle;

                driver.setUserID(driver.getUserID());
                driver.setUsername(resultSet.getString(1));
                driver.setFirstName(resultSet.getString(2));
                driver.setLastName(resultSet.getString(3));
                driver.setPhoneNo(resultSet.getString(4));
                driver.setCNIC(resultSet.getString(5));
                driver.setLicenseNumber(resultSet.getString(6));
                driver.setEmailAddress(resultSet.getString(7));
                driver.setStatus(resultSet.getInt(8));
                //driver.setActive(resultSet.getInt(9) == 1);

                int fetchedActiveVehicleID = Integer.parseInt(resultSet.getString(10));
                if (fetchedActiveVehicleID == Vehicle.DEFAULT_ID) {
                    driver.setActiveVehicle(null);
                }
                else {
                    driver.setActiveVehicle(new Vehicle(fetchedActiveVehicleID));
                }

                closeResultSet(resultSet);

                // Retrieve Driver's Vehicles
                String vehiclesQuery = "SELECT DV.VEHICLE_ID, VT.MAKE, VT.MODEL, VT.YEAR, VT.RIDE_TYPE_ID, DV.NUMBER_PLATE" +
                        ", DV.STATUS, DV.COLOR" +
                        " FROM DRIVERS_VEHICLES DV" +
                        " INNER JOIN VEHICLE_TYPES VT ON DV.VEHICLE_TYPE_ID = VT.VEHICLE_TYPE_ID" +
                        " WHERE DV.DRIVER_ID = " + driver.getUserID();

                resultSet = connect.createStatement().executeQuery(vehiclesQuery);

                while (resultSet.next()) {
                    currentVehicle = new Vehicle(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5),
                            resultSet.getString(6), resultSet.getInt(7),
                            resultSet.getString(8));
                    vehicles.add(currentVehicle);

                    if (driver.getActiveVehicle() == null && currentVehicle.getStatus() == Vehicle.VH_ACCEPTANCE_ACK)  {
                        driver.setActiveVehicle(new Vehicle(currentVehicle.getStatus()));
                    }
                }

                closeResultSet(resultSet);

                // Retrieve Driver's Vehicle requests
                String vehicleRequestQuery = "SELECT REGISTRATION_REQ_ID, MAKE, MODEL, YEAR, NUMBER_PLATE, STATUS, COLOR" +
                        " FROM VEHICLE_REGISTRATION_REQ" +
                        " WHERE DRIVER_ID = " + driver.getUserID();

                resultSet = connect.createStatement().executeQuery(vehicleRequestQuery);

                while (resultSet.next()) {
                    currentVehicle = new Vehicle(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4), 0,
                            resultSet.getString(5), resultSet.getInt(6),
                            resultSet.getString(7));
                    vehicles.add(currentVehicle);
                }

                driver.setVehicles(vehicles);

                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:driverData()");
            e.printStackTrace();
            return false;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public ArrayList<Ride> getRideLog(User user) {
        String userClause;
        int driverId = 0, riderId = 0;
        if (user instanceof Driver) {
            userClause = " WHERE RD.DRIVER_ID = " + user.getUserID();
            driverId = user.getUserID();
        }
        else {
            userClause =  " WHERE RD.RIDER_ID = " + user.getUserID();
            riderId = user.getUserID();
        }

        Connection connect = null;
        ResultSet resultSet = null;

        String sqlQuery = "SELECT RD.RIDE_ID, R.USER_NAME, D.USER_NAME, RD.PAYMENT_ID, RD.SOURCE_LAT, RD.SOURCE_LONG, " +
                "RD.DEST_LAT, RD.DEST_LONG, RD.START_TIME, RD.RIDE_TYPE, RD.ESTIMATED_FARE, RD.STATUS, D.LATITUDE, D.LONGITUDE, " +
                "RD.FARE, D.ACTIVE_VEHICLE_ID, V.MAKE, V.MODEL, V.YEAR, DV.NUMBER_PLATE, DV.COLOR, R.RATING, D.RATING," +
                " D.FIRST_NAME, D.LAST_NAME, D.PHONE_NO, RD.POLICY_ID," +
                " RT.NAME, RT.MAX_PASSENGERS, RT.BASE_FARE, RT.PER_KM_CHARGE, RT.PER_MIN_CHARGE, RT.MIN_FARE, D.PAYMENT_MODE, RD.FINISH_TIME" +
                " FROM RIDES RD\n" +
                " INNER JOIN RIDER_DETAILS R ON RD.RIDER_ID = R.USER_ID" +
                " INNER JOIN DRIVER_DETAILS D ON RD.DRIVER_ID = D.USER_ID" +
                " INNER JOIN DRIVERS_VEHICLES DV ON D.USER_ID = DV.DRIVER_ID AND D.ACTIVE_VEHICLE_ID = DV.VEHICLE_ID" +
                " INNER JOIN VEHICLE_TYPES V ON DV.VEHICLE_TYPE_ID = V.VEHICLE_TYPE_ID" +
                " INNER JOIN RIDE_TYPES RT ON RT.TYPE_ID = RD.RIDE_TYPE" +
                userClause +
                " AND RD.STATUS = " + Ride.END_ACKED;

        System.out.println("getRideLog(): " + sqlQuery);


        try {
            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            ArrayList<Ride> rideLog = new ArrayList<>();
            Ride ride;

            // Package results into ride object
            while (resultSet.next()) {

                ride = new Ride();
                ride.setRideID(resultSet.getInt(1));

                ride.setPolicy(PolicyFactory.getInstance().determinePolicy(resultSet.getInt(27)));

                // Set rider attributes
                ride.getRideParameters().getRider().setUserID(riderId);
                ride.getRideParameters().getRider().setUsername(resultSet.getString(2));

                // Set driver attributes
                ride.getRideParameters().getDriver().setUserID(driverId);
                ride.getRideParameters().getDriver().setUsername(resultSet.getString(3));
                ride.getRideParameters().getDriver().setCurrentLocation(new Location(resultSet.getDouble(13),
                        resultSet.getDouble(14)));

                // Set ride attributes
                ride.getRideParameters().setRideType(new RideType(resultSet.getInt(10),
                        resultSet.getString(28),
                        resultSet.getInt(29),
                        resultSet.getDouble(30),
                        resultSet.getDouble(31),
                        resultSet.getDouble(32),
                        resultSet.getDouble(33)));

                ride.getPayment().setPaymentID(resultSet.getInt(4));
                ride.getRideParameters().setPaymentMethod(resultSet.getInt(34));

                ride.getRideParameters().setPickupLocation(new Location(resultSet.getDouble(5), resultSet.getDouble(6)));
                ride.getRideParameters().setDropoffLocation(new Location(resultSet.getDouble(7), resultSet.getDouble(8)));
                ride.setStartTime(resultSet.getTimestamp(9).getTime());
                ride.setEndTime(resultSet.getTimestamp(35).getTime());
                ride.setEstimatedFare(resultSet.getInt(11));
                ride.setRideStatus(resultSet.getInt(12));
                ride.setFare(resultSet.getDouble(15));
                ride.getRideParameters().setFindStatus(RideRequest.PAIRED);

                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleID(resultSet.getInt(16));
                vehicle.setMake(resultSet.getString(17));
                vehicle.setModel(resultSet.getString(18));
                vehicle.setYear(resultSet.getString(19));
                vehicle.setNumberPlate(resultSet.getString(20));
                vehicle.setColor(resultSet.getString(21));

                ride.getRideParameters().getRider().setRating(resultSet.getFloat(22));
                ride.getRideParameters().getDriver().setRating(resultSet.getFloat(23));
                ride.getRideParameters().getDriver().setFirstName(resultSet.getString(24));
                ride.getRideParameters().getDriver().setLastName(resultSet.getString(25));
                ride.getRideParameters().getDriver().setPhoneNo(resultSet.getString(26));

                ride.getRideParameters().getDriver().setActiveVehicle(vehicle);

                rideLog.add(ride);
            }

            return rideLog;
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler: getRideLog()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public boolean resetRider(Rider rider, boolean checkForResponse) {
        return (executeUpdate(
                "UPDATE RIDER_DETAILS SET FIND_STATUS = " + RideRequest.NOT_SENT +", DRIVER_ID = " + Driver.DEFAULT_ID +
                        " WHERE USER_ID = " + rider.getUserID() +
                        ((checkForResponse)? " AND FIND_STATUS <> " + RideRequest.FOUND : ""))) > 0;
    }

    @Override
    public boolean resetDriver(Driver driver) {

        return (executeUpdate(
                "UPDATE DRIVER_DETAILS SET RIDE_STATUS = "  + RideRequest.MS_DEFAULT + ", RIDER_ID = " +
                        User.DEFAULT_ID + ", SOURCE_LAT = 0, SOURCE_LONG = 0, DEST_LAT = 0, DEST_LONG = 0, " +
                        " PAYMENT_MODE = -1, RIDE_TYPE = 0 WHERE USER_ID = " + driver.getUserID()) > 0);
    }

    @Override
    public Boolean deleteRider() {
        return null;
    }

    @Override
    public Boolean deleteDriver() {
        return null;
    }
    /* End of section */

    /* Rider-side matchmaking DB calls */
    @Override
    public Integer checkFindStatus(Rider rider) {

        Connection connect = null;
        ResultSet resultSet = null;

        String sqlQuery = "SELECT R.FIND_STATUS " +
                "FROM RIDER_DETAILS R " +
                "WHERE R.FIND_STATUS IN (" + RideRequest.REJECTED + "," + RideRequest.FOUND + ") AND R.USER_ID = " + rider.getUserID();

        int findStatus;
        long currentTime = System.currentTimeMillis();
        long endTime = currentTime + 36000;

        try {
            connect = DBCPDataSource.getConnection();

            while (currentTime < endTime) {
                resultSet = connect.createStatement().executeQuery(sqlQuery);

                if (resultSet.next()) {
                    findStatus = resultSet.getInt(1);

                    switch (findStatus) {
                        // Request not sent or response received
                        case -1:
                            return RideRequest.NOT_SENT;
                        case 1:
                            return RideRequest.NOT_PAIRED;
                        case 2:
                            return RideRequest.PAIRED;

                        // Default: do nothing
                    }
                }

                try {
                    Thread.sleep(2000);
                }
                catch (Exception e) {
                    System.out.println("DBHandler: checkFindStatus: Thread.sleep() exception");
                }

                currentTime = System.currentTimeMillis();
            }

            return RideRequest.NO_CHANGE;
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception in DBHandler:checkFindStatus()");
            e.printStackTrace();

            return RideRequest.STATUS_ERROR;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }


    @Override
    public ArrayList<Driver> searchDriverForRide(RideRequest rideRequest) {

        Connection connect = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT USER_ID, USER_NAME, CAST(LATITUDE AS CHAR(12)) AS LATITUDE, " +
                    "CAST(LONGITUDE AS CHAR(12)) AS LONGITUDE FROM DRIVER_DETAILS D" +
                    " INNER JOIN DRIVERS_VEHICLES DV ON DV.DRIVER_ID = D.USER_ID AND D.ACTIVE_VEHICLE_ID = DV.VEHICLE_ID" +
                    " INNER JOIN VEHICLE_TYPES VT ON DV.VEHICLE_TYPE_ID = VT.VEHICLE_TYPE_ID" +
                    " WHERE VT.RIDE_TYPE_ID = " + rideRequest.getRideType().getTypeID();

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            ArrayList<Driver> results = new ArrayList<>();
            Driver currentDriver;

            while(resultSet.next()) {
                currentDriver = new Driver();
                currentDriver.setUserID(resultSet.getInt(1));
                currentDriver.setUsername(resultSet.getString(2));
                currentDriver.setCurrentLocation(new Location(resultSet.getDouble(3),
                        resultSet.getDouble(4), null));
                results.add(currentDriver);

                /*
                row.put("USER_ID", );
                row.put("USER_NAME", resultSet.getString(2));
                row.put("LATITUDE", resultSet.getDouble(3));
                row.put("LONGITUDE", resultSet.getDouble(4));
                results.put(row);
                row = new JSONObject();*/
            }

            return results;
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:searchDriverForRide()");
            e.printStackTrace();
            return new ArrayList<>();
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    //TODO: add check for driver's vehicle's ride type
    @Override
    public boolean sendRideRequest(RideRequest rideRequest) {

        Connection connect = null;
        PreparedStatement sqlStatement = null;

        boolean requestSent = false;

        try {
            connect = DBCPDataSource.getConnection();
            sqlStatement = connect.prepareStatement(
                    "UPDATE DRIVER_DETAILS SET RIDER_ID = ?, RIDE_STATUS = 1, SOURCE_LAT = ?, SOURCE_LONG = ?," +
                            "DEST_LAT = ?, DEST_LONG = ?, PAYMENT_MODE = ?, RIDE_TYPE = ?, SPLIT_FARE = ? WHERE USER_ID = ? AND IS_ACTIVE = 1 AND RIDE_STATUS = 0");

            sqlStatement.setInt(1, rideRequest.getRider().getUserID());
            sqlStatement.setDouble(2, rideRequest.getPickupLocation().getLatitude());
            sqlStatement.setDouble(3, rideRequest.getPickupLocation().getLongitude());
            sqlStatement.setDouble(4, rideRequest.getDropoffLocation().getLatitude());
            sqlStatement.setDouble(5, rideRequest.getDropoffLocation().getLongitude());
            sqlStatement.setInt(6, rideRequest.getPaymentMethod());
            sqlStatement.setInt(7, rideRequest.getRideType().getTypeID());
            sqlStatement.setBoolean(8, rideRequest.isSplittingFare());
            sqlStatement.setInt(9, rideRequest.getDriver().getUserID());

            int numRowsUpdated = sqlStatement.executeUpdate();
            if (numRowsUpdated == 1) {
                System.out.println(LOG_TAG +  ":sendRideRequest: 1 row updated -> Request sent!");

                sqlStatement = connect.prepareStatement("UPDATE RIDER_DETAILS SET FIND_STATUS = " + RideRequest.NO_CHANGE +
                        ", DRIVER_ID = " + rideRequest.getDriver().getUserID() + " WHERE USER_ID = ?");
                sqlStatement.setInt(1, rideRequest.getRider().getUserID());

                numRowsUpdated = sqlStatement.executeUpdate();

                if (numRowsUpdated == 1) {
                    System.out.println(LOG_TAG +  ":sendRideRequest: 1 row updated -> Rider marked as NO_CHANGE!");
                    requestSent = true;
                }
                else {
                    System.out.println(LOG_TAG +  ":sendRideRequest: 1 row updated -> failure: Rider NOT marked as NO_CHANGE!");
                    requestSent = false;
                }
            }
            else {
                System.out.println(LOG_TAG + ":sendRideRequest: " + numRowsUpdated + " row updated -> FAILURE!");
                requestSent = false;
            }
            return requestSent;
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:sendRideRequest()");
            e.printStackTrace();
            return false;
        }
        finally {
            closeAll(connect, sqlStatement, null);
        }
    }
    /* End of section */


    /* Driver-side matchmaking DB calls*/
    @Override
    public boolean markDriverActive(Driver driver)
    {
        return (executeUpdate(String.format("UPDATE DRIVER_DETAILS SET IS_ACTIVE = %s WHERE USER_ID = %d AND STATUS = %d",
                driver.isActive(), driver.getUserID(), Driver.DV_REQ_APPROVED)) > 0);
    }

    @Override
    public RideRequest checkRideRequestStatus(Driver driver, int timeout)
    {
        Connection connect = null;
        PreparedStatement sqlStatement = null;
        ResultSet resultSet = null;

        try {
            connect = DBCPDataSource.getConnection();
            sqlStatement = connect.prepareStatement(
                    "SELECT D.RIDE_STATUS, D.RIDER_ID, R.USER_NAME, D.SOURCE_LAT, D.SOURCE_LONG, D.DEST_LAT, " +
                            " D.DEST_LONG, D.PAYMENT_MODE, D.RIDE_TYPE, D.SPLIT_FARE"
                            + " FROM DRIVER_DETAILS D LEFT JOIN RIDER_DETAILS R ON D.RIDER_ID = R.USER_ID"
                            + " WHERE D.USER_ID = ? AND D.IS_ACTIVE = 1");

            sqlStatement.setInt(1, driver.getUserID());

            // Preparing the Loop
            long currentTime = System.currentTimeMillis();
            long endTime = currentTime + timeout;
            while (currentTime <= endTime)
            {
                // Close result set before executing if appropriate
                resultSet = sqlStatement.executeQuery();

                //TODO: why is it returning this when a request isn't present
                RideRequest rideRequest = new RideRequest();
                rideRequest.setFindStatus(33);

                // If Rows were found
                if (resultSet.next())
                {
                    if (resultSet.getInt(1) == 0) {
                        System.out.println("db:checkRideReqStat: Ride not found!");
                        try {
                            Thread.sleep(2000);
                        }
                        catch (Exception e) {
                            System.out.println("Rider: findDriver: Thread.sleep() exception");
                        }
                        currentTime = System.currentTimeMillis();
                    }
                    if (resultSet.getInt(1) > 0) {
                        System.out.println("db:checkRideReqStat: op2");
                        rideRequest = new RideRequest();

                        rideRequest.setPaymentMethod(resultSet.getInt(8));
                        rideRequest.setFindStatus(resultSet.getInt(1));

                        Rider rider = new Rider();
                        rider.setUserID(resultSet.getInt(2));
                        rider.setUsername(resultSet.getString(3));

                        Location pickLocation = new Location();
                        Location destLocation = new Location();

                        pickLocation.setLatitude(resultSet.getDouble(4));
                        pickLocation.setLongitude(resultSet.getDouble(5));

                        destLocation.setLatitude(resultSet.getDouble(6));
                        destLocation.setLongitude(resultSet.getDouble(7));

                        rideRequest.setRideType(new RideType(resultSet.getInt(9)));
                        rideRequest.setSplittingFare(resultSet.getBoolean(10));

                        rideRequest.setPickupLocation(pickLocation);
                        rideRequest.setDropoffLocation(destLocation);

                        rideRequest.setRider(rider);
                        rideRequest.setDriver(driver);
                        return rideRequest;
                    }
                } // End if: Rows found
                else {
                    System.out.println("db:checkRideReqStat: op3");
                    return rideRequest;
                }

                closeResultSet(resultSet);
            } // End while

            return null;
        } // End of Try block
        catch (Exception e) {
            System.out.println("Exception in DBHandler: checkRideRequestStatus()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, sqlStatement, resultSet);
        }
    }


    @Override
    public boolean rejectRideRequest(RideRequest rideRequest) {

        //TODO: Add multiple queries functionality

        int numRowsUpdated = executeUpdate("UPDATE RIDER_DETAILS SET FIND_STATUS = " + RideRequest.REJECTED + ", DRIVER_ID = " + Driver.DEFAULT_ID
                + " WHERE USER_ID = " + rideRequest.getRider().getUserID() + " AND FIND_STATUS = " + RideRequest.NO_CHANGE
                + " AND DRIVER_ID = " + rideRequest.getDriver().getUserID());

        System.out.println("rejectRideRequest: numRowsUpdated: " + numRowsUpdated);
        return (resetDriver(rideRequest.getDriver()));
    }

    /*
     * Confirms ride request (signal to corresponding rider)
     * Records ride
     * */
    @Override
    public boolean confirmRideRequest(Ride ride) {

        Connection connect = null;
        PreparedStatement sqlStatement = null;

        try {
            connect = DBCPDataSource.getConnection();

            // Notify rider query
            sqlStatement = connect.prepareStatement(
                    "UPDATE RIDER_DETAILS SET FIND_STATUS = ?, DRIVER_ID = ? WHERE USER_ID = ? AND FIND_STATUS = "
                            + RideRequest.NO_CHANGE + " AND DRIVER_ID = " + ride.getRideParameters().getDriver().getUserID());

            sqlStatement.setInt(1, RideRequest.FOUND);
            sqlStatement.setInt(2, ride.getRideParameters().getDriver().getUserID());
            sqlStatement.setInt(3, ride.getRideParameters().getRider().getUserID());

            int numRowsUpdated = sqlStatement.executeUpdate();
            if (numRowsUpdated <= 0) {
                resetDriver(ride.getRideParameters().getDriver());
                return false;
            }
            closeStatement(sqlStatement);

            // Notify driver query
            sqlStatement = connect.prepareStatement("UPDATE DRIVER_DETAILS SET RIDE_STATUS = ? WHERE USER_ID = ?");
            sqlStatement.setInt(1, RideRequest.MS_REQ_ACCEPTED);
            sqlStatement.setInt(2, ride.getRideParameters().getDriver().getUserID());

            numRowsUpdated = sqlStatement.executeUpdate();

            // Returning the confirmation of this query and recording the ride in the DB
            return (numRowsUpdated > 0 && recordRide(ride));
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler: confirmRideRequest()");
            e.printStackTrace();
            resetDriver(ride.getRideParameters().getDriver());
            return false;
        }
        finally {
            closeAll(connect, sqlStatement, null);
        }
    }

    @Override
    public boolean markArrivalAtPickup(Ride ride) {
        return (executeUpdate(String.format("UPDATE RIDES SET STATUS = " + Ride.DRIVER_ARRIVED + " WHERE RIDE_ID = %d",
                ride.getRideID())) > 0);
    }

    // Starting the Ride from Driver side
    @Override
    public boolean startRide(Ride ride) {
        return (executeUpdate(String.format("UPDATE RIDES SET STATUS = %d WHERE RIDE_ID = %d",
                Ride.STARTED,
                ride.getRideID())) > 0);
    }

    @Override
    public boolean markArrivalAtDestination(Ride ride) {
        return (executeUpdate("UPDATE RIDES SET STATUS = " + Ride.ARRIVED_AT_DEST + ", DIST_TRAVELLED = " +
                ride.getDistanceTravelled() + ", FARE = " + ride.getFare()
                + ", FINISH_TIME = CURRENT_TIME() WHERE RIDE_ID = " + ride.getRideID()) > 0);
    }

    /* End of section*/

    @Override
    public void recordPayment(Payment payment) {

        Connection connect = null;
        PreparedStatement sqlStatement = null;
        ResultSet generatedKeys = null;
        String insertPaymentQuery = "INSERT INTO PAYMENTS VALUES(NULL, ?, ?, TIMESTAMP = CURRENT_TIME(), ?)";
        String[] generatedColumns = {"PAYMENT_ID"};

        int numRowsUpdated;

        try {
            connect = DBCPDataSource.getConnection();
            sqlStatement = connect.prepareStatement(insertPaymentQuery, generatedColumns);
            sqlStatement.setDouble(1, payment.getAmountPaid());
            sqlStatement.setDouble(2, payment.getChange());
            sqlStatement.setInt(3, payment.getPaymentMode());

            numRowsUpdated = sqlStatement.executeUpdate();

            if (numRowsUpdated == 0) {
                System.out.println(LOG_TAG + ":recordPayment: creating payment failed, no rows updated");
            }
            else {
                generatedKeys = sqlStatement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    payment.setPaymentID(generatedKeys.getInt(1));
                }
                else {
                    System.out.println(LOG_TAG + ":recordPayment: creating payment failed, no payment id obtained");
                }
            }
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + ":recordPayment: SQLException");
            e.printStackTrace();
        }
        finally {
            closeAll(connect, sqlStatement, generatedKeys);
        }
    }


    // Store new Ride
    @Override
    public boolean recordRide(Ride ride) {

        String query = "INSERT INTO RIDES " +
                "SELECT 0, R.USER_ID AS RIDER_ID, D.USER_ID AS DRIVER_ID, D.ACTIVE_VEHICLE_ID AS VEHICLE_ID, NULL AS PAYMENT_ID, " +
                "D.SOURCE_LAT, D.SOURCE_LONG, D.DEST_LAT, D.DEST_LONG, CURRENT_TIME(), NULL AS FINISH_TIME, 0 AS DIST_TRAVELLED, " +
                "D.RIDE_TYPE AS RIDE_TYPE, " + ride.getPolicy().getPolicyID() + " AS POLICY_ID, "
                + ride.getEstimatedFare() + " AS ESTIMATED_FARE, 0 AS FARE, " + Ride.PICKUP + " AS STATUS " +
                "FROM DRIVER_DETAILS AS D, RIDER_DETAILS AS R " +
                "WHERE D.RIDER_ID = R.USER_ID AND D.USER_ID = " + ride.getRideParameters().getDriver().getUserID() +
                " AND D.RIDER_ID = " + ride.getRideParameters().getRider().getUserID();

        return (executeUpdate(query) > 0);
    }

    @Override
    public RideRequest checkRideRequestStatus(Rider rider) {

        Connection connect = null;
        ResultSet resultSet  =null;

        String sqlQuery = "SELECT R.FIND_STATUS, R.DRIVER_ID, D.RIDE_TYPE FROM RIDER_DETAILS R" +
                " INNER JOIN DRIVER_DETAILS D ON R.DRIVER_ID = D.USER_ID" +
                " WHERE R.USER_ID = " + rider.getUserID();
        //TODO: make constants for ride request status

        try {
            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);
            RideRequest rideRequest = null;

            if (resultSet.next()) {

                int findStatus = resultSet.getInt(1);

                rideRequest = new RideRequest();
                rideRequest.setRider(rider);
                rideRequest.setDriver(new Driver());

                rideRequest.setFindStatus(findStatus);
                rideRequest.getDriver().setUserID(resultSet.getInt(2));

                rideRequest.setRideType(new RideType(resultSet.getInt(3)));
            }
            // No active request or request rejected
            return rideRequest;
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler: getRideRequestStatus(rider)");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    public Ride getRideForAdmin(int rideId) {

        Connection connect = null;
        ResultSet resultSet = null;

        // 38 COLUMNS
        String sqlQuery = "SELECT RD.RIDE_ID, R.USER_NAME, D.USER_NAME, RD.PAYMENT_ID, RD.SOURCE_LAT, RD.SOURCE_LONG, " +
                "RD.DEST_LAT, RD.DEST_LONG, RD.START_TIME, RD.RIDE_TYPE, RD.ESTIMATED_FARE, RD.STATUS, D.LATITUDE, D.LONGITUDE, " +
                "RD.FARE, D.ACTIVE_VEHICLE_ID, V.MAKE, V.MODEL, V.YEAR, DV.NUMBER_PLATE, DV.COLOR, R.RATING, D.RATING," +
                " D.FIRST_NAME, D.LAST_NAME, D.PHONE_NO, RD.POLICY_ID," +
                " RT.NAME, RT.MAX_PASSENGERS, RT.BASE_FARE, RT.PER_KM_CHARGE, RT.PER_MIN_CHARGE, RT.MIN_FARE, D.PAYMENT_MODE, RD.FINISH_TIME," +
                " PM.AMOUNT_PAID, PM.CHANGE, PM.PAYMENT_MODE" +
                " FROM RIDES RD\n" +
                " INNER JOIN RIDER_DETAILS R ON RD.RIDER_ID = R.USER_ID" +
                " INNER JOIN DRIVER_DETAILS D ON RD.DRIVER_ID = D.USER_ID" +
                " INNER JOIN DRIVERS_VEHICLES DV ON D.USER_ID = DV.DRIVER_ID AND D.ACTIVE_VEHICLE_ID = DV.VEHICLE_ID" +
                " INNER JOIN VEHICLE_TYPES V ON DV.VEHICLE_TYPE_ID = V.VEHICLE_TYPE_ID" +
                " INNER JOIN RIDE_TYPES RT ON RT.TYPE_ID = RD.RIDE_TYPE" +
                " INNER JOIN PAYMENTS PM ON RD.PAYMENT_ID = PM.PAYMENT_ID" +
                " WHERE RD.RIDE_ID = " + rideId;

        System.out.println("getRideForAdmin(): " + sqlQuery);

        //JSONObject result = new JSONObject();
        Ride ride = null;

        try {
            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            // Package results into ride object
            if (resultSet.next()) {

                ride = new Ride();
                ride.setRideID(resultSet.getInt(1));

                ride.setPolicy(PolicyFactory.getInstance().determinePolicy(resultSet.getInt(27)));

                // Set rider attributes
                ride.getRideParameters().getRider().setUsername(resultSet.getString(2));

                // Set driver attributes
                ride.getRideParameters().getDriver().setUsername(resultSet.getString(3));
                ride.getRideParameters().getDriver().setCurrentLocation(new Location(resultSet.getDouble(13),
                        resultSet.getDouble(14)));

                // Set ride attributes
                ride.getRideParameters().setRideType(new RideType(resultSet.getInt(10),
                        resultSet.getString(28),
                        resultSet.getInt(29),
                        resultSet.getDouble(30),
                        resultSet.getDouble(31),
                        resultSet.getDouble(32),
                        resultSet.getDouble(33)));

                Payment payment = ride.getPayment();
                payment.setPaymentID(resultSet.getInt(4));
                payment.setAmountPaid(resultSet.getDouble(36));
                payment.setChange(resultSet.getDouble(37));
                payment.setPaymentMode(resultSet.getInt(38));
                ride.getRideParameters().setPaymentMethod(resultSet.getInt(34));

                ride.getRideParameters().setPickupLocation(new Location(resultSet.getDouble(5), resultSet.getDouble(6)));
                ride.getRideParameters().setDropoffLocation(new Location(resultSet.getDouble(7), resultSet.getDouble(8)));

                Timestamp tempTimeStamp = resultSet.getTimestamp(9);
                ride.setStartTime((tempTimeStamp == null)? 0 : tempTimeStamp.getTime());
                tempTimeStamp = resultSet.getTimestamp(35);
                ride.setEndTime((tempTimeStamp == null)? 0 : tempTimeStamp.getTime());

                ride.setEstimatedFare(resultSet.getInt(11));
                ride.setRideStatus(resultSet.getInt(12));
                ride.setFare(resultSet.getDouble(15));
                ride.getRideParameters().setFindStatus(RideRequest.PAIRED);

                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleID(resultSet.getInt(16));
                vehicle.setMake(resultSet.getString(17));
                vehicle.setModel(resultSet.getString(18));
                vehicle.setYear(resultSet.getString(19));
                vehicle.setNumberPlate(resultSet.getString(20));
                vehicle.setColor(resultSet.getString(21));

                ride.getRideParameters().getRider().setRating(resultSet.getFloat(22));
                ride.getRideParameters().getDriver().setRating(resultSet.getFloat(23));
                ride.getRideParameters().getDriver().setFirstName(resultSet.getString(24));
                ride.getRideParameters().getDriver().setLastName(resultSet.getString(25));
                ride.getRideParameters().getDriver().setPhoneNo(resultSet.getString(26));

                ride.getRideParameters().getDriver().setActiveVehicle(vehicle);
            }
            //result.put("STATUS_CODE", 300);

            return ride;
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler: getRideForAdmin()");
            e.printStackTrace();
            //result.put("STATUS_CODE", 304);
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }


    public Ride getRide(RideRequest rideRequest) {

        Connection connect = null;
        ResultSet resultSet = null;

        String sqlQuery = "SELECT RD.RIDE_ID, R.USER_NAME, D.USER_NAME, RD.PAYMENT_ID, RD.SOURCE_LAT, RD.SOURCE_LONG, " +
                "RD.DEST_LAT, RD.DEST_LONG, RD.START_TIME, RD.RIDE_TYPE, RD.ESTIMATED_FARE, RD.STATUS, D.LATITUDE, D.LONGITUDE, " +
                "RD.FARE, D.ACTIVE_VEHICLE_ID, V.MAKE, V.MODEL, V.YEAR, DV.NUMBER_PLATE, DV.COLOR, R.RATING, D.RATING," +
                " D.FIRST_NAME, D.LAST_NAME, D.PHONE_NO, RD.POLICY_ID," +
                " RT.NAME, RT.MAX_PASSENGERS, RT.BASE_FARE, RT.PER_KM_CHARGE, RT.PER_MIN_CHARGE, RT.MIN_FARE, D.PAYMENT_MODE" +
                " FROM RIDES RD\n" +
                " INNER JOIN RIDER_DETAILS R ON RD.RIDER_ID = R.USER_ID" +
                " INNER JOIN DRIVER_DETAILS D ON RD.DRIVER_ID = D.USER_ID" +
                " INNER JOIN DRIVERS_VEHICLES DV ON D.USER_ID = DV.DRIVER_ID AND D.ACTIVE_VEHICLE_ID = DV.VEHICLE_ID" +
                " INNER JOIN VEHICLE_TYPES V ON DV.VEHICLE_TYPE_ID = V.VEHICLE_TYPE_ID" +
                " INNER JOIN RIDE_TYPES RT ON RT.TYPE_ID = RD.RIDE_TYPE" +
                " WHERE RD.RIDER_ID = " + rideRequest.getRider().getUserID() +
                " AND RD.DRIVER_ID = " + rideRequest.getDriver().getUserID() +
                " AND RD.STATUS <> " + Ride.END_ACKED;

        System.out.println("getRide(): " + sqlQuery);

        //JSONObject result = new JSONObject();
        Ride ride = null;

        try {
            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            // Package results into ride object
            if (resultSet.next()) {

                ride = new Ride();
                ride.setRideID(resultSet.getInt(1));

                ride.setPolicy(PolicyFactory.getInstance().determinePolicy(resultSet.getInt(27)));

                // Set rider attributes
                ride.getRideParameters().getRider().setUserID(rideRequest.getRider().getUserID());
                ride.getRideParameters().getRider().setUsername(resultSet.getString(2));

                // Set driver attributes
                ride.getRideParameters().getDriver().setUserID(rideRequest.getDriver().getUserID());
                ride.getRideParameters().getDriver().setUsername(resultSet.getString(3));
                ride.getRideParameters().getDriver().setCurrentLocation(new Location(resultSet.getDouble(13),
                        resultSet.getDouble(14)));

                // Set ride attributes
                ride.getRideParameters().setRideType(new RideType(resultSet.getInt(10),
                        resultSet.getString(28),
                        resultSet.getInt(29),
                        resultSet.getDouble(30),
                        resultSet.getDouble(31),
                        resultSet.getDouble(32),
                        resultSet.getDouble(33)));

                ride.getPayment().setPaymentID(resultSet.getInt(4));
                ride.getRideParameters().setPaymentMethod(resultSet.getInt(34));

                ride.getRideParameters().setPickupLocation(new Location(resultSet.getDouble(5), resultSet.getDouble(6)));
                ride.getRideParameters().setDropoffLocation(new Location(resultSet.getDouble(7), resultSet.getDouble(8)));
                ride.setStartTime(resultSet.getTimestamp(9).getTime());
                ride.setEstimatedFare(resultSet.getInt(11));
                ride.setRideStatus(resultSet.getInt(12));
                ride.setFare(resultSet.getDouble(15));
                ride.getRideParameters().setFindStatus(RideRequest.PAIRED);

                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleID(resultSet.getInt(16));
                vehicle.setMake(resultSet.getString(17));
                vehicle.setModel(resultSet.getString(18));
                vehicle.setYear(resultSet.getString(19));
                vehicle.setNumberPlate(resultSet.getString(20));
                vehicle.setColor(resultSet.getString(21));

                ride.getRideParameters().getRider().setRating(resultSet.getFloat(22));
                ride.getRideParameters().getDriver().setRating(resultSet.getFloat(23));
                ride.getRideParameters().getDriver().setFirstName(resultSet.getString(24));
                ride.getRideParameters().getDriver().setLastName(resultSet.getString(25));
                ride.getRideParameters().getDriver().setPhoneNo(resultSet.getString(26));

                ride.getRideParameters().getDriver().setActiveVehicle(vehicle);
            }
            //result.put("STATUS_CODE", 300);

            return ride;
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler: getRide()");
            e.printStackTrace();
            //result.put("STATUS_CODE", 304);
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public void fetchRideStatus(Ride ride) {

        Connection connect = null;
        ResultSet resultSet = null;

        ride.setRideStatus(RideRequest.DEFAULT);
        String sqlQuery = "SELECT STATUS, FARE FROM RIDES WHERE RIDE_ID = " + ride.getRideID();

        try {
            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            if (resultSet.next()) {
                ride.setRideStatus(resultSet.getInt(1));
                ride.setFare(resultSet.getDouble(2));
            }
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler: getRide()");
            e.printStackTrace();
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    /*
     * Param: Ride with Payment - paymentID must be initialized
     * Adds reference from ride to payment, persists payment information
     */
    @Override
    public boolean endRideWithPayment(Ride ride)
    {
        Connection connect = null;
        PreparedStatement sqlStatement = null;
        try {
            if (ride.getPayment().getPaymentID() < 0) {
                return false;
            }

            connect = DBCPDataSource.getConnection();
            sqlStatement = connect.prepareStatement(
                    "UPDATE RIDES SET PAYMENT_ID  = " + ride.getPayment().getPaymentID() +
                            ", STATUS = " + Ride.PAYMENT_MADE +
                            " WHERE RIDE_ID = ?");
            sqlStatement.setInt(1, ride.getRideID());
            int numRowsUpdated = sqlStatement.executeUpdate();

            closeStatement(sqlStatement);

            if (numRowsUpdated > 0) {
                sqlStatement = connect.prepareStatement("UPDATE PAYMENTS P INNER JOIN RIDES RD ON RD.PAYMENT_ID = P.PAYMENT_ID SET P.AMOUNT_PAID = ?, P.CHANGE = ? - RD.FARE WHERE RD.RIDE_ID = ?");

                sqlStatement.setDouble(1, ride.getPayment().getAmountPaid());
                sqlStatement.setDouble(2, ride.getPayment().getAmountPaid());
                sqlStatement.setInt(3, ride.getRideID());

                numRowsUpdated = sqlStatement.executeUpdate();

                return (numRowsUpdated > 0 && resetDriver(ride.getRideParameters().getDriver()));

            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Exception in DBHandler: endRideWitPayment");
            e.printStackTrace();
            return false;
        }
        finally {
            closeAll(connect, sqlStatement, null);
        }
    }

    @Override
    public boolean acknowledgeEndOfRide(Ride ride) {

        return (executeUpdate(String.format("UPDATE RIDES SET STATUS = %d WHERE RIDE_ID = %d",
                Ride.END_ACKED,
                ride.getRideID())) > 0);
    }


    /*
     * ---------------------------------------------
     *  DRIVER & RIDER FEEDBACK METHODS
     * ---------------------------------------------
     */

    /* TODO: Keep track of ratings in ride later? */
    @Override
    public boolean giveFeedbackForDriver(Ride ride, float rating) {

        return (executeUpdate("UPDATE DRIVER_DETAILS D" +
                " SET D.RATING = D.RATING*(cast(D.NUM_RATINGS as DECIMAL)/CAST(D.NUM_RATINGS+1 AS DECIMAL)) + ("
                + rating +"/CAST(D.NUM_RATINGS+1 AS DECIMAL)), " +
                " D.NUM_RATINGS = D.NUM_RATINGS + 1" +
                " WHERE D.USER_ID = " + ride.getRideParameters().getDriver().getUserID()) > 0);
    }

    @Override
    public boolean giveFeedbackForRider(Ride ride, float rating) {

        return (executeUpdate("UPDATE RIDER_DETAILS R" +
                " SET R.RATING = R.RATING*(cast(R.NUM_RATINGS as DECIMAL)/CAST(R.NUM_RATINGS+1 AS DECIMAL)) + ("
                + rating +"/CAST(R.NUM_RATINGS+1 AS DECIMAL)), " +
                " R.NUM_RATINGS = R.NUM_RATINGS + 1" +
                " WHERE R.USER_ID = " + ride.getRideParameters().getRider().getUserID()) > 0);
    }


    /*
     * ---------------------------------------------
     *  CREATE & LOG INTO ADMINISTRATOR ACCOUNT
     * ---------------------------------------------
     */

    @Override
    public boolean addAdmin(Administrator admin) {
        return (executeUpdate(String.format("INSERT INTO ADMIN_DETAILS" +
                        " VALUES(0, '%s', '%s', '%s', '%s', '%s', '%s', %d)",
                admin.getEmailAddress(),
                admin.getPassword(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getPhoneNo(),
                admin.getCNIC(),
                admin.getCredentials())) > 0);
    }

    @Override
    public boolean loginAdmin(Administrator admin) {
        Connection connect = null;
        ResultSet resultSet = null;
        try {
            String sqlQuery = "SELECT USER_ID, EMAIL_ADDRESS, PASSWORD, FIRST_NAME, LAST_NAME, PHONE_NO, CNIC, CREDENTIALS" +
                    " FROM ADMIN_DETAILS WHERE EMAIL_ADDRESS = '" + admin.getEmailAddress() + "'";

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            // If email address & password verified
            if (resultSet.next() && User.verifyPassword(resultSet.getString(2), admin.getPassword())) {
                admin.Initialize(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getInt(8));

                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            System.out.println("Exception in DBHandler:loginAdmin()");
            e.printStackTrace();
            return false;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }


    /*
     * --------------------------------------------------
     *  DRIVER & VEHICLE REGISTRATION REQUEST METHODS
     * --------------------------------------------------
     */

    /* Send Requests */

    @Override
    public boolean sendRegistrationRequest(Driver driver)
    {
        return (executeUpdate(String.format("UPDATE DRIVER_DETAILS SET FIRST_NAME = '%s'" +
                        ", LAST_NAME = '%s', PHONE_NO = '%s', CNIC = '%s', LICENSE_NO = '%s', STATUS = %d WHERE USER_ID = %d",
                driver.getFirstName(), driver.getLastName(), driver.getPhoneNo(), driver.getCNIC(), driver.getLicenseNumber(),
                Driver.DV_REQ_SENT, driver.getUserID()))) > 0;
    }

    /*
     * For all vehicles, if Vehicle.getStatus()
     * = VH_DEFAULT -> Send new request
     * = VH_REQ_REJECTED -> Update existing request
     * */
    @Override
    public boolean sendVehicleRegistrationRequest(Driver driver, Vehicle currentVehicleRequest) {

        //TODO: don't make new statement every time

        Connection connect = null;
        PreparedStatement statement = null;

        try {
            String sendNewRequestQuery, updateExistingRequestQuery;

            connect = DBCPDataSource.getConnection();

            // If request hasn't been sent
            if (currentVehicleRequest.getStatus() == Vehicle.VH_DEFAULT) {
                sendNewRequestQuery = "INSERT INTO VEHICLE_REGISTRATION_REQ" +
                        " VALUES(" + driver.getUserID() +", 0, '" + currentVehicleRequest.getMake() +
                        "', '" + currentVehicleRequest.getModel() + "', '" + currentVehicleRequest.getYear() +
                        "', '" + currentVehicleRequest.getNumberPlate() + "', '" + currentVehicleRequest.getColor() +
                        "', " + Vehicle.VH_REQ_SENT+ ")";
                System.out.println("sendVehicleRequest: " + sendNewRequestQuery);

                statement = connect.prepareStatement(sendNewRequestQuery);
                statement.executeUpdate();
                closeStatement(statement);

            }
            // Previously rejected request
            else if (currentVehicleRequest.getStatus() == Vehicle.VH_REQ_REJECTED) {
                updateExistingRequestQuery = "UPDATE VEHICLE_REGISTRATION_REQ" +
                        " SET MAKE = '" + currentVehicleRequest.getMake() +
                        "', MODEL = '" + currentVehicleRequest.getModel() +
                        "', YEAR = '" + currentVehicleRequest.getYear() +
                        "', NUMBER_PLATE = '" + currentVehicleRequest.getNumberPlate() +
                        "', COLOR = '" + currentVehicleRequest.getColor() +
                        "', STATUS = " + Vehicle.VH_REQ_SENT+
                        " WHERE DRIVER_ID = " + driver.getUserID() +
                        " AND REGISTRATION_REQ_ID = " + currentVehicleRequest.getVehicleID();

                System.out.println("sendVehicleRequest: " + updateExistingRequestQuery);

                statement = connect.prepareStatement(updateExistingRequestQuery);
                statement.executeUpdate();
                closeStatement(statement);
            }

            return true;
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception:sendVehicleRequest()");
            e.printStackTrace();
            return false;
        }
        finally {
            closeAll(connect, statement, null);
        }
    }

    /* Get Requests */

    @Override
    public ArrayList<Vehicle> getVehicleRequests() {
        Connection connect = null;
        ResultSet resultSet = null;

        Vehicle vehicleRequest;
        ArrayList<Vehicle> result = new ArrayList<>();

        try {
            connect = DBCPDataSource.getConnection();

            String query = "SELECT REGISTRATION_REQ_ID, MAKE, MODEL, YEAR, NUMBER_PLATE, STATUS, COLOR" +
                    " FROM VEHICLE_REGISTRATION_REQ WHERE STATUS = " + Vehicle.VH_REQ_SENT;

            resultSet = connect.createStatement().executeQuery(query);

            // Loop through and add to list of vehicle requests
            while (resultSet.next()) {
                vehicleRequest = new Vehicle(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), 0,
                        resultSet.getString(5), resultSet.getInt(6),
                        resultSet.getString(7));

                result.add(vehicleRequest);
            }

            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public ArrayList<Driver> getDriverRequests() {
        Connection connect = null;
        ResultSet resultSet = null;

        Driver driverRequest;
        ArrayList<Driver> result = new ArrayList<>();

        try {
            connect = DBCPDataSource.getConnection();

            String query = "SELECT USER_ID, USER_NAME, FIRST_NAME, LAST_NAME, PHONE_NO, CNIC, LICENSE_NO, EMAIL_ADDRESS, " +
                    " STATUS, IS_ACTIVE, ACTIVE_VEHICLE_ID FROM DRIVER_DETAILS WHERE STATUS = " + Driver.DV_REQ_SENT;

            resultSet = connect.createStatement().executeQuery(query);

            // Loop through and add to list of vehicle requests
            while (resultSet.next()) {
                driverRequest = new Driver();
                driverRequest.setUserID(resultSet.getInt(1));
                driverRequest.setUsername(resultSet.getString(2));
                driverRequest.setFirstName(resultSet.getString(3));
                driverRequest.setLastName(resultSet.getString(4));
                driverRequest.setPhoneNo(resultSet.getString(5));
                driverRequest.setCNIC(resultSet.getString(6));
                driverRequest.setLicenseNumber(resultSet.getString(7));
                driverRequest.setEmailAddress(resultSet.getString(8));
                driverRequest.setStatus(resultSet.getInt(9));
                //driverRequest.setActive(resultSet.getInt(10) == 1);
                driverRequest.setActiveVehicle(new Vehicle(resultSet.getInt(11)));

                result.add(driverRequest);
            }

            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public boolean respondToComplaint(int complaintId, int responseCategory, String responseMessage) {
        return executeUpdate(String.format("UPDATE COMPLAINTS SET STATUS = %d, RES_DATE = CURRENT_TIME(), RES_MSG = '%s'" +
                " WHERE COMPLAINT_ID = %d",
                responseCategory,
                responseMessage,
                complaintId)) > 0;
    }

    @Override
    public boolean reportProblem(User user, String problemDescription, int rideID, int categoryId) {
        return executeUpdate(String.format("INSERT INTO `COMPLAINTS`" +
                " (`COMPLAINT_ID` ,`USER_ID`, `USER_TYPE`, `RIDE_ID`, `CATEGORY`, `DESC`, `SUBMISSION_DATE`)" +
                " VALUES(%d, %d, %d, %d, %d, '%s', CURRENT_TIME())",
                0,
                user.getUserID(),
                ((user instanceof Rider)? 0: 1),
                rideID,
                categoryId,
                problemDescription)) > 0;
    }

    @Override
    public ArrayList<Complaint> fetchComplaints() {
        Connection connect = null;
        ResultSet resultSet = null;
        String sqlQuery = "";
        try {
            connect = DBCPDataSource.getConnection();
            sqlQuery = "SELECT COMPLAINT_ID, USER_ID, RIDE_ID, USER_TYPE, CATEGORY, `DESC`, STATUS, SUBMISSION_DATE, RES_DATE" +
                    " FROM COMPLAINTS WHERE STATUS <> 200";

            resultSet = connect.createStatement().executeQuery(sqlQuery);

            ArrayList<Complaint> complaints = new ArrayList<>();
            Timestamp submissionTime, resolutionTime;

            while (resultSet.next()) {
                submissionTime = resultSet.getTimestamp(8);
                resolutionTime = resultSet.getTimestamp(9);
                complaints.add(new Complaint(
                  resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(4),
                        resultSet.getInt(3),
                        resultSet.getInt(5),
                        resultSet.getString(6),
                        resultSet.getInt(7),
                        (submissionTime != null)? submissionTime.getTime() : 0,
                        (resolutionTime != null)? resolutionTime.getTime() : 0
                ));
            }

            return complaints;
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception:fetchComplaints()\n" + sqlQuery);
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    /* Respond to Requests */

    @Override
    public boolean respondToDriverRegistrationRequest(Driver driver) {

        return (executeUpdate(String.format("UPDATE DRIVER_DETAILS SET STATUS = %d WHERE USER_ID = %d",
                driver.getStatus(),
                driver.getUserID())) > 0);
    }

    @Override
    public boolean respondToVehicleRegistrationRequest(Vehicle currentVehicleRequest) {

        Connection connect = null;
        PreparedStatement approveVehicleRequestStatement = null,
                deleteVehicleRequestStatement = null,
                rejectVehicleRequestStatement = null;
        try {
            int numRowsUpdated;

            String approveVehicleRequestQuery =
                    "INSERT INTO DRIVERS_VEHICLES\n" +
                            " SELECT VRR.DRIVER_ID,  0, ?, VRR.NUMBER_PLATE, " + Vehicle.VH_ACCEPTANCE_ACK + ", VRR.COLOR" +
                            " FROM VEHICLE_REGISTRATION_REQ VRR" +
                            " WHERE VRR.REGISTRATION_REQ_ID = ?";

            String deleteVehicleRequestQuery =
                    "DELETE FROM VEHICLE_REGISTRATION_REQ" +
                            " WHERE REGISTRATION_REQ_ID = ?";

            String rejectVehicleRequestQuery = "UPDATE VEHICLE_REGISTRATION_REQ" +
                    " SET STATUS = " + Vehicle.VH_REQ_REJECTED +
                    " WHERE REGISTRATION_REQ_ID = ?";

            connect = DBCPDataSource.getConnection();
            approveVehicleRequestStatement = connect.prepareStatement(approveVehicleRequestQuery);
            deleteVehicleRequestStatement = connect.prepareStatement(deleteVehicleRequestQuery);
            rejectVehicleRequestStatement = connect.prepareStatement(rejectVehicleRequestQuery);

            // If any records 'dirty' - response received but not persisted, then persist
            if (currentVehicleRequest.getStatus() == Vehicle.VH_REQ_ACCEPTED) {
                approveVehicleRequestStatement.setInt(1, currentVehicleRequest.getVehicleTypeID());
                approveVehicleRequestStatement.setInt(2, currentVehicleRequest.getVehicleID());

                numRowsUpdated = approveVehicleRequestStatement.executeUpdate();

                if (numRowsUpdated > 0) {
                    deleteVehicleRequestStatement.setInt(1, currentVehicleRequest.getVehicleID());

                    deleteVehicleRequestStatement.executeUpdate();
                }
            }
            else if (currentVehicleRequest.getStatus() == Vehicle.VH_REQ_REJECTED) {
                rejectVehicleRequestStatement.setInt(1, currentVehicleRequest.getVehicleID());

                rejectVehicleRequestStatement.executeUpdate();
            }

            return true;
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception:respondToVehicleRequest()");
            e.printStackTrace();
            return false;
        }
        finally {
            closeStatement(approveVehicleRequestStatement);
            closeStatement(deleteVehicleRequestStatement);
            closeStatement(rejectVehicleRequestStatement);
            closeConnection(connect);
        }
    }

    @Override
    public boolean setActiveVehicle(Driver driver) {

        Connection connect = null;
        PreparedStatement setActiveVehicleStatement = null;
        ResultSet resultSet = null;

        try {
            // Check if vehicle still approved
            String checkVehicleApprovedQuery =
                    "SELECT STATUS FROM DRIVERS_VEHICLES WHERE DRIVER_ID = " + driver.getUserID() + " AND VEHICLE_ID = "
                            + driver.getActiveVehicle().getVehicleID();

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(checkVehicleApprovedQuery);

            // If approved, set as driver's active vehicle
            if (resultSet.next() && resultSet.getInt(1) == Vehicle.VH_ACCEPTANCE_ACK) {
                setActiveVehicleStatement = connect.prepareStatement("UPDATE DRIVER_DETAILS SET ACTIVE_VEHICLE_ID = "
                        + driver.getActiveVehicle().getVehicleID() +
                        " WHERE USER_ID  = " + driver.getUserID());

                // Return true if a row is updated
                return (setActiveVehicleStatement.executeUpdate() > 0);
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception:setActiveVehicle()");
            e.printStackTrace();
            return false;
        }
        finally {
            closeAll(connect, setActiveVehicleStatement, resultSet);
        }
    }


    /* Location update methods*/
    @Override
    public boolean saveRiderLocation(Rider rider) {

        return (executeUpdate("UPDATE RIDER_DETAILS SET LATITUDE = " + rider.getCurrentLocation().getLatitude()
                + ", LONGITUDE = " + rider.getCurrentLocation().getLongitude() +
                ", TIMESTAMP = CURRENT_TIME() WHERE USER_ID = " + rider.getUserID()) > 0);
    }

    @Override
    public boolean saveDriverLocation(Driver driver) {
        return (executeUpdate("UPDATE DRIVER_DETAILS SET LATITUDE = " + driver.getCurrentLocation().getLatitude()
                + ", LONGITUDE = " + driver.getCurrentLocation().getLongitude() +
                ", TIMESTAMP = CURRENT_TIME() WHERE USER_ID = " + driver.getUserID()) > 0);
    }

    @Override
    public Location getRiderLocation(Rider rider) {

        Connection connect = null;
        ResultSet resultSet = null;
        try {
            connect = DBCPDataSource.getConnection();
            String sqlQuery = "SELECT CAST(LATITUDE AS CHAR(12)) AS LATITUDE, CAST(LONGITUDE AS CHAR(12)) AS LONGITUDE" +
                    " FROM RIDER_DETAILS WHERE USER_ID = " + rider.getUserID();

            resultSet = connect.createStatement().executeQuery(sqlQuery);

            Location fetchedLocation = null;

            if (resultSet.next()) {
                fetchedLocation = new Location(resultSet.getDouble(1),
                        resultSet.getDouble(2), null);
            }

            return fetchedLocation;
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception:getRiderLocation()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public Location getDriverLocation(Driver driver) {

        Connection connect = null;
        ResultSet resultSet = null;

        try {
            connect = DBCPDataSource.getConnection();
            String sqlQuery = "SELECT CAST(LATITUDE AS CHAR(12)) AS LATITUDE, CAST(LONGITUDE AS CHAR(12)) AS LONGITUDE" +
                    " FROM DRIVER_DETAILS WHERE USER_ID = " + driver.getUserID();

            resultSet = connect.createStatement().executeQuery(sqlQuery);

            Location location;

            if (resultSet.next()) {
                location = new Location(resultSet.getDouble(1),
                        resultSet.getDouble(2), null);
            }
            else {
                location = null;
            }

            return location;
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception:getDriverLocation()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public ArrayList<Location> getRiderLocations() {

        Connection connect = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT CAST(LATITUDE AS CHAR(12)) AS LATITUDE, CAST(LONGITUDE AS CHAR(12)) AS LONGITUDE" +
                    ", TIMESTAMP FROM RIDER_DETAILS";

            // Find list of Rider Locations //TODO: Add criteria later

            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            ArrayList<Location> locations = new ArrayList<>();
            Location currentLocation;

            while (resultSet.next()) {
                currentLocation = new Location(resultSet.getDouble(1),
                        resultSet.getDouble(2),
                        null);
                locations.add(currentLocation);
            }

            return locations;
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception:getRiderLocations()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }

    @Override
    public ArrayList<Location> getDriverLocations() {

        Connection connect = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT CAST(LATITUDE AS CHAR(12)) AS LATITUDE, CAST(LONGITUDE AS CHAR(12)) AS LONGITUDE" +
                    ", TIMESTAMP FROM DRIVER_DETAILS";

            // Find list of Driver Locations //TODO: Add criteria later
            connect = DBCPDataSource.getConnection();
            resultSet = connect.createStatement().executeQuery(sqlQuery);

            ArrayList<Location> locations = new ArrayList<>();
            Location currentLocation;

            while (resultSet.next()) {
                currentLocation = new Location(resultSet.getDouble(1),
                        resultSet.getDouble(2),
                        resultSet.getTimestamp(3).getTime());
                locations.add(currentLocation);
            }

            return locations;
        }
        catch (Exception e) {
            System.out.println(LOG_TAG + "Exception:getDriverLocations()");
            e.printStackTrace();
            return null;
        }
        finally {
            closeAll(connect, null, resultSet);
        }
    }
    /* End of section */
}