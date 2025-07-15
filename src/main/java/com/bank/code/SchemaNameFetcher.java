package com.bank.code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

public class SchemaNameFetcher {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521/FREEPDB1"; // Your JDBC URL
        String username = "RAGHUL"; // Your DB username
        String password = "6379362346"; // Your DB password

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            // Get schema name (for reference)
            System.out.println("Current Schema: " + conn.getSchema());

            // Query the users table
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(query);

            // Get metadata to dynamically handle columns
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column names
            System.out.println("\nUsers in the 'users' table:");
            System.out.println("--------------------------------------------------");
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-20s", metaData.getColumnName(i));
            }
            System.out.println("\n--------------------------------------------------");

            // Print user data
            boolean hasUsers = false;
            while (rs.next()) {
                hasUsers = true;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-20s", rs.getString(i) != null ? rs.getString(i) : "NULL");
                }
                System.out.println();
            }

            if (!hasUsers) {
                System.out.println("No users found in the 'users' table.");
            }

            // Verify table existence
            ResultSet tables = conn.getMetaData().getTables(null, username, "USERS", new String[]{"TABLE"});
            if (!tables.next()) {
                System.out.println("Error: The 'users' table does not exist in the schema " + username);
            } else {
                System.out.println("The 'users' table exists in the schema " + username);
            }

        } catch (Exception e) {
            System.err.println("Error accessing the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}