package org.example.connection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clasa pentru gestionarea conexiunilor la baza de date PostgreSQL.
 *
 * @author Bianca Herghelegiu
 */

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

    /**
     * Returneaza o conexiune activa la baza de date.
     *
     * @return obiectul Connection
     */
    public static Connection getConnection() {
           return singleInstance.createConnection();
    }

    /**
     * Inchide o conexiune deschisa.
     *
     * @param connection conexiunea de inchis
     */
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

    /**
     * Inchide un Statement deschis.
     *
     * @param statement statement-ul de inchis
     */
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

    /**
     * Inchide un ResultSet deschis.
     *
     * @param resultSet result set-ul de inchis
     */
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

