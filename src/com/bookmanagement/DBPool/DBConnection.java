/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.DBPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
    
    public static synchronized String generateID(String tableName, String idColumn, String prefix) throws SQLException {
        String sql = "SELECT MAX(" + idColumn + ") FROM " + tableName;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1);
                String numberPart = maxId.replaceAll("\\D", ""); // Lấy phần số
                int num = Integer.parseInt(numberPart) + 1;
                return prefix + String.format("%0" + numberPart.length() + "d", num);
            } else {
                return prefix + "001"; // ID đầu tiên
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println(getConnection());
    }
}
