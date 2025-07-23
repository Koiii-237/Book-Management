/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.DBPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Admin
 */
public class DBConnection {
    public static final String HOSTNAME = "localhost";
    public static final String PORT = "1433";
    public static final String DBNAME = "BookManagement";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "123456789";
    /**
     * Get connection to MSSQL Server
     *
     * @return Connection
     */
    public static Connection getConnection() {
        // Create a variable for the connection string.
        String connectionUrl = "jdbc:sqlserver://" + HOSTNAME + ":" + PORT + ";"
                + "databaseName=" + DBNAME + ";encrypt=false";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(connectionUrl, USERNAME, PASSWORD);
        } // Handle any errors that may have occurred.
        catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }
    
    public static String generateID(String tableName, String idColumn, String prefix) throws SQLException {
        String sql = String.format("SELECT MAX(%s) FROM %s", idColumn, tableName);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1);
                int num = Integer.parseInt(maxId.replaceAll("\\D", "")) + 1;
                return prefix + String.format("%03d", num);
            } else {
                return prefix + "001";
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println(getConnection());
    }
}
