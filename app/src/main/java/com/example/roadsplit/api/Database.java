package com.example.roadsplit.database;

import android.util.Log;

import com.example.roadsplit.model.UserAccount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.Getter;

@Getter
public class Database {
    private Connection connection;

    // For PostgreSQL
    private final String host = "10.0.2.2";

    private final String database = "postgres";
    private final int port = 5432;
    private final String user = "buddydbuser";
    private final String pass = "split123";
    private String url = "jdbc:postgresql://%s:%d/%s";
    private boolean status;
    
    public Database()
    {
        this.url = String.format(this.url, this.host, this.port, this.database);
        connect();
        //this.disconnect();
        System.out.println("connection status:" + status);
    }

    private void connect()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(url, user, pass);
                    status = true;
                    System.out.println("connected:" + status);
                }
                catch (Exception e)
                {
                    status = false;
                    System.out.print(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try
        {
            thread.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.status = false;
        }
    }

}
