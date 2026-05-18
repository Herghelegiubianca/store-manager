package org.example.dao;

import org.example.connection.ConnectionFactory;

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
 * Clasa generica DAO cu operatii CRUD pentru orice model, construite dinamic prin reflectie.
 *
 * @param <T> tipul modelului, trebuie sa corespunda cu numele tabelei din baza de date
 * @author Bianca Herghelegiu
 */

public sealed class DatabaseManager<T> permits ClientDAO,ProductDAO,OrderDAO {
    private static final Logger LOGGER=Logger.getLogger(DatabaseManager.class.getName());
    private final Class<T> myClass;

    public DatabaseManager(Class<T> myClass)
    {
        this.myClass=myClass;
    }

    public static Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * Construieste un query de SELECT cu filtrare dupa campurile date.
     *
     * @param fields lista de campuri pentru clauza WHERE
     * @return string-ul cu query-ul generat
     */
    public String selectQuery(List<String> fields)
    {
        String command="SELECT * FROM ";
        if(this.myClass.getSimpleName().toLowerCase().equals("order"))command=command+"orders";
        else command=command+this.myClass.getSimpleName().toLowerCase();
        if(fields!=null)
        {
            command=command+" WHERE ";
            String aux=fields.stream().filter(s->s!=null).map(s->s+"=?").collect(Collectors.joining(" AND "));
            command+=aux;
            return command;
        }
        return null;
    }

    /**
     * Construieste o lista de obiecte de tip T din rezultatul unui query SQL.
     *
     * @param resultSet rezultatul query-ului
     * @return lista de obiecte sau null daca rezultatul e gol
     */
    public List<T> createObj(ResultSet resultSet)
    {
        if(resultSet!=null)
        {
            String fname=null;
            Object val=null;
            List<T> rez=new ArrayList<>();
            T aux=null;
            try{
                while(resultSet.next())
                {
                    aux=myClass.getDeclaredConstructor().newInstance();
                    for(java.lang.reflect.Field field: this.myClass.getDeclaredFields())
                    {
                        fname=field.getName();
                        val=resultSet.getObject(fname);
                        field.setAccessible(true);
                        field.set(aux,val);
                        field.setAccessible(false);
                    }
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
     * Cauta o inregistrare dupa id.
     *
     * @param id id-ul cautat
     * @return obiectul gasit sau null daca nu exista
     */

    public T findById(int id)
    {
        Connection connection=null;
        ArrayList<String> aux=new ArrayList<>();
        aux.add("id");
        String query=selectQuery(aux);
        PreparedStatement statement=null;
        ResultSet resultSet=null;

        try{
            connection= ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
            statement.setInt(1,id);
            resultSet= statement.executeQuery();
            List<T> rez=createObj(resultSet);

            if(rez!=null && !rez.isEmpty())
            {
                return rez.get(0); //am oricum un sg obiect cu id-ul ala
            }
        }catch(SQLException e) {
             this.LOGGER.log(Level.WARNING,"Eroare DatabaseManager findById!");
        }finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);

        }

        return null;
    }

    /**
     * Returneaza toate inregistrarile din tabela.
     *
     * @return lista de obiecte sau null daca tabela e goala
     */
    public List<T> findAll()
    {
        Connection connection=null;
        String table="";
        if(this.myClass.getSimpleName().toLowerCase().equals("order"))table="orders";
        else table=this.myClass.getSimpleName().toLowerCase();
        String query="SELECT * FROM "+table;
        PreparedStatement statement=null;
        ResultSet resultSet=null;

        try{
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
            resultSet= statement.executeQuery();
            List<T> rez=createObj(resultSet);

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

    /**
     * Construieste un query de INSERT bazat pe campurile modelului prin reflectie.
     *
     * @return string-ul cu query-ul generat
     */
    public String insertQuery()
    {
        String command="INSERT INTO ";
        if(this.myClass.getSimpleName().toLowerCase().equals("order"))command=command+"orders (";
        else command=command+this.myClass.getSimpleName().toLowerCase()+" (";
        String columns= Arrays.stream(this.myClass.getDeclaredFields()).filter(f->!f.getName().equals("id")).map(java.lang.reflect.Field::getName).collect(Collectors.joining(", "));
        command=command+columns;
        command=command+") VALUES(";

        String values=Arrays.stream(this.myClass.getDeclaredFields()).filter(f->!f.getName().equals("id")).map(f->"?").collect(Collectors.joining(", "));
        command+=values+")";
        return command;
    }

    /**
     * Insereaza un obiect si seteaza id-ul generat pe el.
     *
     * @param obj obiectul de inserat
     * @return obiectul cu id-ul setat sau null la eroare
     */
    public T insert(T obj)
    {
        Connection connection=null;
        String query=insertQuery();
        PreparedStatement statement=null;

        try{
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query,java.sql.Statement.RETURN_GENERATED_KEYS);
            Object val=null;
            int index=1;

            for(java.lang.reflect.Field f: this.myClass.getDeclaredFields())
            {
                if(f!=null && !f.getName().equals("id"))
                {
                    f.setAccessible(true);
                    val=f.get(obj);
                    statement.setObject(index,val);
                    index++;
                    f.setAccessible(false);
                }
            }

            statement.executeUpdate();
            ResultSet aux=statement.getGeneratedKeys(); //verific daca nu e gol
            if(aux.next())
            {
                int id=aux.getInt(1);
                java.lang.reflect.Field field=this.myClass.getDeclaredField("id");
                field.setAccessible(true);
                field.set(obj,id);
                field.setAccessible(false);
            }
            return obj;

        }catch(Exception e) {
            this.LOGGER.log(Level.WARNING,"Eroare DatabaseManager insert!");
        }finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);

        }

        return null;
    }

    /**
     * Construieste un query de DELETE cu filtrare dupa campurile date.
     *
     * @param fields lista de campuri pentru clauza WHERE
     * @return string-ul cu query-ul generat
     */
    public String deleteQuery(List<String> fields)
    {
        String command="DELETE FROM ";
        if(this.myClass.getSimpleName().toLowerCase().equals("order"))command=command+"orders";
        else command=command+this.myClass.getSimpleName().toLowerCase();

        if(fields!=null && !fields.isEmpty())
        {
            command=command+" WHERE ";
            String aux=fields.stream().filter(f->f!=null).map(f->f+"=?").collect(Collectors.joining(" AND "));
            command+=aux;
            return command;

        }
        return null;
    }

    /**
     * Sterge inregistrarea cu id-ul dat.
     *
     * @param id id-ul inregistrarii de sters
     */
    public void delete(int id)
    {
        Connection connection=null;
        List<String> aux=new ArrayList<>();
        aux.add("id");
        String query=deleteQuery(aux);
        PreparedStatement statement=null;

        try
        {
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
            statement.setInt(1,id);
            statement.executeUpdate();
        }catch(Exception e)
        {
            this.LOGGER.log(Level.WARNING,"Eroare DatabaseManager delete!");
        }finally
        {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

    }

    /**
     * Construieste un query de UPDATE bazat pe campurile modelului prin reflectie.
     *
     * @return string-ul cu query-ul generat
     */
    public String updateQuery()
    {
        String command="UPDATE ";
        if(this.myClass.getSimpleName().toLowerCase().equals("order"))command=command+"orders";
        else command=command+this.myClass.getSimpleName().toLowerCase();
        command=command+" SET ";
        int index=0;

        if(this.myClass.getDeclaredFields()==null) return null;

        String aux=Arrays.stream(this.myClass.getDeclaredFields()).filter(f->!f.getName().equals("id")).map(f->f.getName()+"=?").collect(Collectors.joining(", "));
        command+=aux;
        command=command+" WHERE id=?";

        return command;
    }

    /**
     * Actualizeaza inregistrarea cu id-ul corespunzator obiectului.
     *
     * @param obj obiectul cu valorile noi
     * @return obiectul actualizat sau null la eroare
     */
    public T update(T obj)
    {
        Connection connection=null;
        String query=updateQuery();
        PreparedStatement statement=null;
        int index=1;
        Object idAux=null;
        Object val=null;

        try
        {
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);

            for(java.lang.reflect.Field f: this.myClass.getDeclaredFields())
            {
               if(!f.getName().equals("id"))
               {
                   f.setAccessible(true);
                  val=f.get(obj);
                  statement.setObject(index++,val);
                   f.setAccessible(false);
               }
            }
            java.lang.reflect.Field field=this.myClass.getDeclaredField("id");
            field.setAccessible(true);
            statement.setObject(index,field.get(obj));
            field.setAccessible(false);
            statement.executeUpdate();
            return obj;
        }catch(Exception e)
        {
            this.LOGGER.log(Level.WARNING,"Eroare DatabaseManager update!");
        }finally
        {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }




}
