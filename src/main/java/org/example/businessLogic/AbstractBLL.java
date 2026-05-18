package org.example.businessLogic;

import org.example.dao.DatabaseManager;
import org.example.model.Client;
import org.example.model.Order;
import org.example.model.Product;

import java.lang.reflect.Field;

/**
 * Clasa de baza pentru BLL. Contine operatii comune si extragerea id-ului prin reflectie.
 *
 * @author Bianca Herghelegiu
 */

public sealed abstract class AbstractBLL permits ClientBLL, ProductBLL, OrderBLL{
    private DatabaseManager dao;

    public AbstractBLL(DatabaseManager dao)
    {
        this.dao=dao;
    }

    public DatabaseManager getDao() {
        return dao;
    }

    public void setDao(DatabaseManager dao) {
        this.dao = dao;
    }

    /**
     * Extrage id-ul unui obiect folosind reflectie.
     *
     * @param obj obiectul din care se extrage id-ul
     * @return id-ul ca int
     * @throws Exception daca campul id nu exista sau nu e accesibil
     */

    public int getObjectId(Object obj) throws Exception
    {
        try {
            Field id = obj.getClass().getDeclaredField("id");
            id.setAccessible(true);
            int rez=(int)id.get(obj);
            id.setAccessible(false);
            return rez;
        }catch(Exception e)
        {
            throw e;
        }
    }

    /**
     * Insereaza un obiect in baza de date dupa validare.
     *
     * @param obj obiectul de inserat
     * @throws InvalidDataException daca datele sunt invalide
     */
    public abstract void insertRecord(Object obj) throws InvalidDataException;

    /**
     * Actualizeaza un obiect existent in baza de date.
     *
     * @param obj obiectul cu valorile noi, trebuie sa aiba id valid
     * @throws Exception daca obiectul nu e gasit
     */
    public void updateRecord(Object obj) throws Exception{
        String auxs=null;
        if(obj instanceof Client) auxs="clientul";
        else if(obj instanceof Product) auxs="produsul";
        else auxs="comanda";

        if(obj!=null) {
            try {
                int id = getObjectId(obj);
                if(this.dao.findById(id)!=null)
                {
                    this.dao.update(obj);
                    return;
                }
            }
            catch(Exception e)
            {
                throw e;
            }
        }
        throw new InvalidDataException("Nu s-a gasit "+auxs+"!");

    }
}
