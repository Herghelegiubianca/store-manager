package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager<T> {
    private static final Logger LOGGER=Logger.getLogger(DatabaseManager.class.getName());
    private final Class<T> myClass;

    public DatabaseManager(Class<T> myClass)
    {
        this.myClass=myClass;
    }

    public String selectQuery(List<String> fields)
    {
        String command="SELECT * FROM ";
        command=command+this.myClass.getSimpleName().toLowerCase();
        int index=0;
        if(fields!=null)
        {
            command=command+" WHERE ";
            for(String s: fields)
            {
                if(s!=null)
                {
                   command=command+s+"=? ";
                }
                if(index<(fields.size()-1))
                {
                    command=command+"AND ";
                }
                index++;
            }
            return command;
        }
        return null;
    }

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

    public T findById(int id)
    {
        Connection connection=null;
        ArrayList<String> aux=new ArrayList<>();
        aux.add("id");
        String query=selectQuery(aux);
        PreparedStatement statement=null;
        ResultSet resultSet=null;

        try{
            connection=ConnectionFactory.getConnection();
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

    public List<T> findAll()
    {
        Connection connection=null;
        String query="SELECT * FROM "+this.myClass.getSimpleName().toLowerCase();
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

    public String insertQuery()
    {
        String command="INSERT INTO ";
        command=command+this.myClass.getSimpleName().toLowerCase()+" (";
        int index=0;
        java.lang.reflect.Field [] fields=this.myClass.getDeclaredFields();
        if(fields!=null)
        {
            for(java.lang.reflect.Field f: fields)
            {
                if(f!=null && !f.getName().equals("id"))
                {
                    command=command+f.getName();
                    if(index<(fields.length-1))
                    {
                        command=command+", ";
                    }
                }
                index++;
            }

            command=command+") VALUES(";
            index=0;
            for(java.lang.reflect.Field f: fields)
            {
                if(f!=null && !f.getName().equals("id"))
                {
                    command=command+"?";
                    if(index<(fields.length-1))
                    {
                        command=command+", ";
                    }
                }
                index++;
            }
            command=command+")";
            return command;

        }
        return null;
    }

    public T insert(T obj)
    {
        Connection connection=null;
        String query=insertQuery();
        PreparedStatement statement=null;

        try{
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
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
            return obj;

        }catch(Exception e) {
            this.LOGGER.log(Level.WARNING,"Eroare DatabaseManager insert!");
        }finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);

        }

        return null;
    }

    public String deleteQuery(List<String> fields)
    {
        String command="DELETE FROM ";
        command=command+this.myClass.getSimpleName().toLowerCase();
        int index=0;

        if(fields!=null && !fields.isEmpty())
        {
            command=command+" WHERE ";
            for(String f: fields)
            {
                if(f!=null)
                {
                    command=command+f+"=? ";
                    if(index<(fields.size()-1))
                    {
                        command=command+"AND ";
                    }
                }
                index++;
            }
            return command;

        }
        return null;
    }

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

    public String updateQuery()
    {
        String command="UPDATE ";
        command=command+this.myClass.getSimpleName().toLowerCase()+" SET ";
        int index=0;

        if(this.myClass.getDeclaredFields()==null) return null;

        for(java.lang.reflect.Field f: this.myClass.getDeclaredFields())
        {
            if(f.getName().equals("id"))
            {
                index++;
                continue;
            }
            command=command+f.getName()+"=?";
            if(index<(this.myClass.getDeclaredFields().length-1))
            {
                command=command+", ";
            }
            index++;
        }

        command=command+" WHERE id=?";

        return command;
    }

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
               f.setAccessible(true);
               if(!f.getName().equals("id"))
               {
                  val=f.get(obj);
                  statement.setObject(index,val);
                  index++;

               }
               else
               {
                   idAux=f.get(obj);
               }
               f.setAccessible(false);
            }
            statement.setObject(index,idAux);
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
