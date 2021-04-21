package com.savaari.api.database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}