package com.example.guiserver;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Server {
    static Connection conn = null;
    public static Connection connect() {
        String host = "localhost";
        String port = "3306";
        String database = "fixed2";
        String user = "root";
        String password = "12354678";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + user + "&password=" + password;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to database!");
            //
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
        }
        return conn;
    }
    static void main1(){
        connect();
        int port = 9999;
        MultiClientServer server = new MultiClientServer(port);
        server.start();
    }
}
