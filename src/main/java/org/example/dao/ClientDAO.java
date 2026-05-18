package org.example.dao;

import org.example.model.Client;

/**
 * DAO pentru clienti, mosteneste toate operatiile CRUD din DatabaseManager.
 *
 * @author Bianca Herghelegiu
 */
public final class ClientDAO extends DatabaseManager<Client>{


    public ClientDAO() {
        super(Client.class);
    }
}
