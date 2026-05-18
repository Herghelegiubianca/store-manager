package org.example.dao;

import org.example.model.Product;

/**
 * DAO pentru produse, mosteneste toate operatiile CRUD din DatabaseManager.
 *
 * @author Bianca Herghelegiu
 */
public final class ProductDAO extends DatabaseManager<Product>{

    public ProductDAO() {
        super(Product.class);
    }
}
