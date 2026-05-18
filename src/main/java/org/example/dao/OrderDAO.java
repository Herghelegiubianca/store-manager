package org.example.dao;

import org.example.connection.ConnectionFactory;
import org.example.model.Client;
import org.example.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * DAO specific pentru comenzi, extinde DatabaseManager cu o cautare dupa client.
 *
 * @author Bianca Herghelegiu
 */

public final class OrderDAO extends DatabaseManager<Order>{
    public OrderDAO() {
        super(Order.class);
    }

    /**
     * Returneaza toate comenzile unui client dupa id-ul acestuia.
     *
     * @param id id-ul clientului
     * @return lista de comenzi sau null daca nu are comenzi
     */
    public List<Order> findByClientId(int id)
    {
        Connection connection=null;
        ArrayList<String> aux=new ArrayList<>();
        aux.add("client_id");
        String query=selectQuery(aux);
        PreparedStatement statement=null;
        ResultSet resultSet=null;

        try{
            connection= ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
            statement.setInt(1,id);
            resultSet= statement.executeQuery();
            List<Order> rez=createObj(resultSet);

            if(rez!=null && !rez.isEmpty())
            {
                return rez;
            }
        }catch(SQLException e) {
            DatabaseManager.getLogger().log(Level.WARNING,"Eroare OrderDAO findByClientId!");
        }finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);

        }

        return null;
    }
}
