package com.savaari.api.database;

import com.savaari.api.entity.Administrator;
import com.savaari.api.entity.Driver;
import com.savaari.api.entity.User;
import com.savaari.api.entity.Vehicle;
import org.apache.commons.dbutils.DbUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            System.out.println("Exception in OracleDBHandler: executeUpdate");
            e.printStackTrace();
            return -1;
        }
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
    public Boolean deleteDriver() {
        return null;
    }
    /* End of section */


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

    /* Respond to Requests */

    @Override
    public boolean respondToDriverRegistrationRequest(Driver driver) {

        return (executeUpdate(String.format("UPDATE DRIVER_DETAILS SET STATUS = %d WHERE USER_ID = %d",
                driver.getStatus(),
                driver.getUserID())) > 0);
    }
}