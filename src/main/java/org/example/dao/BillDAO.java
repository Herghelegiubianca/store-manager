package org.example.dao;

import org.example.connection.ConnectionFactory;
import org.example.model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * DAO dedicat facturilor, suporta doar insert si citire, fara update.
 *
 * @author Bianca Herghelegiu
 */

public class BillDAO {
    private static final Logger LOGGER=Logger.getLogger(BillDAO.class.getName());

    public BillDAO(){}

    public List<Bill> createObj(ResultSet resultSet)
    {
        if(resultSet!=null)
        {
            String fname=null;
            Object val=null;
            List<Bill> rez=new ArrayList<>();
            try{
                while(resultSet.next())
                {
                    java.time.LocalDate time=null;
                    java.sql.Date tableDate=resultSet.getDate("time");
                    if(tableDate!=null) time=tableDate.toLocalDate();

                    Bill aux=new Bill(
                            resultSet.getInt("orderId"),
                            resultSet.getInt("clientId"),
                            resultSet.getInt("productId"),
                            resultSet.getString("clientName"),
                            resultSet.getString("productName"),
                            resultSet.getDouble("price"),
                            resultSet.getDouble("totalPrice"),
                            resultSet.getInt("quantity"),
                            resultSet.getInt("bill_id"),
                            time
                    );
                    rez.add(aux);
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }

            return rez;
        }

        return null;
    }

    /**
     * Returneaza toate facturile din tabela log.
     *
     * @return lista de facturi sau null daca tabela e goala
     */
    public List<Bill> findAll()
    {
        Connection connection=null;
        String table="";
        String query="SELECT * FROM bill";
        PreparedStatement statement=null;
        ResultSet resultSet=null;

        try{
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
            resultSet= statement.executeQuery();
            List<Bill> rez=createObj(resultSet);

            if(rez!=null && !rez.isEmpty())
            {
                return rez; //am oricum un sg obiect cu id-ul ala
            }
        }catch(SQLException e) {
            this.LOGGER.log(Level.WARNING,"Eroare DatabaseManager findAll!");
        }finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);

        }

        return null;
    }



    public String insertQuery()
    {
        String command="INSERT INTO bill (";
        String columns= Arrays.stream(Bill.class.getDeclaredFields()).filter(f->!f.getName().equals("bill_id")).map(java.lang.reflect.Field::getName).collect(Collectors.joining(", "));
        command=command+columns;
        command=command+") VALUES(";

        String values=Arrays.stream(Bill.class.getDeclaredFields()).filter(f->!f.getName().equals("bill_id")).map(f->"?").collect(Collectors.joining(", "));
        command+=values+")";
        return command;
    }

    /**
     * Insereaza o factura noua in tabela log.
     *
     * @param obj factura de inserat
     * @return factura inserata sau null la eroare
     */
    public Bill insert(Bill obj)
    {
        Connection connection=null;
        String query=insertQuery();
        PreparedStatement statement=null;

        try{
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
            Object val=null;
            int index=1;

            for(java.lang.reflect.Field f: Bill.class.getDeclaredFields())
            {
                if(f!=null && !f.getName().equals("bill_id"))
                {
                    f.setAccessible(true);
                    val=f.get(obj);
                    statement.setObject(index,val);
                    index++;
                    f.setAccessible(false);
                }
            }

            statement.executeUpdate();
            return obj;

        }catch(Exception e) {
            this.LOGGER.log(Level.WARNING,"Eroare DatabaseManager insert!");
        }finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);

        }

        return null;
    }





}
