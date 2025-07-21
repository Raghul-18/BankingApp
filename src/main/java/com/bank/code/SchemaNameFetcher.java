package com.bank.code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
public class SchemaNameFetcher {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:oracle:thin:@localhost:1521/FREEPDB1";
        String username = "chathurya";
        String password = "oracle";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') FROM DUAL")) {
            if (rs.next()) {
                String currentSchema = rs.getString(1);
                System.out.println("Current Schema: " + currentSchema);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}









