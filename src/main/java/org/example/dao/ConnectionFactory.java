package org.example.dao;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {
private static final Logger LOGGER = Logger.getLogger(
        ConnectionFactory.class.getName());
private static final String DRIVER = "org.postgresql.Driver";
private static final String DB_URL = "jdbc:postgresql://localhost:5432/ordersdb";
private static final String USER = "postgres";
private static final String PASS = "danmarius.86";
private static ConnectionFactory singleInstance = new ConnectionFactory();

   private ConnectionFactory() {
       try{
           Class.forName(DRIVER);}
       catch(ClassNotFoundException e) {
           e.printStackTrace();
       }
   }

    private Connection createConnection() {
            Connection connection=null;
            try {
                connection= DriverManager.getConnection(DB_URL,USER,PASS);

            }catch(SQLException e) {
                LOGGER.log(Level.SEVERE,"Eroare la conectare");
            }
            return connection;
    }

    public static Connection getConnection() {
           return singleInstance.createConnection();
    }
    public static void close(Connection connection) {
           if(connection!=null) {
               try{
                   connection.close();
               }catch(SQLException e)
               {
                   LOGGER.log(Level.WARNING,"Eroare la inchiderea conexiunii!");
               }
           }
    }

    public static void close(Statement statement) {
           if(statement!=null) {
               try{
                   statement.close();
               }catch(SQLException e)
               {
                   LOGGER.log(Level.WARNING,"Eroare la inchiderea statementului!");
               }
           }
    }

    public static void close(ResultSet resultSet) {
          if(resultSet!=null) {
              try{
                  resultSet.close();
              }catch(SQLException e)
              {
                  LOGGER.log(Level.WARNING,"Eroare la inchiderea result set-ului!");
              }
          }
    }
}

