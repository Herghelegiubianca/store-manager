package org.example.businessLogic;

import org.example.dao.BillDAO;
import org.example.model.Bill;

/**
 * Logica pentru gestionarea facturilor, doar inserare si citire.
 *
 * @author Bianca Herghelegiu
 */
public final class BillBLL{
    private BillDAO dao;

    public BillBLL(){
        this.dao=new BillDAO();
    }

    public BillDAO getDao() {
        return dao;
    }

    /**
     * Insereaza o factura noua in baza de date.
     *
     * @param obj factura de inserat
     */
    public void insertRecord(Object obj){
        if(obj!=null && obj instanceof Bill) {
            Bill bill=(Bill)obj;

            //niciodata nu apelez metoda asta fara a avea un client cu un order deci datele de intrare sunt valide
                this.dao.insert(bill);

        }
    }
}
