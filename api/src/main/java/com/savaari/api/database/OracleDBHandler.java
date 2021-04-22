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
     * -------------------------------------
     *  ADMIN SYSTEM REQUESTS
     * -------------------------------------
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
}