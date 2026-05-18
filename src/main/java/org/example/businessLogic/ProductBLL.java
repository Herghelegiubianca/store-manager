package org.example.businessLogic;
import org.example.dao.ProductDAO;
import org.example.model.Product;

/**
 * Logica pentru gestionarea produselor din magazin.
 *
 * @author Bianca Herghelegiu
 */

public final class ProductBLL extends AbstractBLL{
    private ProductDAO productDAO;

    public ProductBLL(){
        super(new ProductDAO());
    }

    /**
     * Valideaza si insereaza un produs nou in baza de date.
     *
     * @param obj produsul de inserat
     * @throws InvalidDataException daca numele, pretul sau stocul sunt invalide
     */
    public void insertRecord(Object obj) throws InvalidDataException{
        if(obj!=null && obj instanceof Product) {
            Product product=(Product)obj;
            if(product.getName()!=null && validName(product.getName()) && product.getPrice()>0 && product.getStock()>0){

                this.getDao().insert(product);
                return;
            }
            else{
                throw new InvalidDataException("Numele, pretul sau stocul produsului e invalid!");
            }
        }
        throw new InvalidDataException("Niciun produs selectat!");

    }

    /**
     * Verifica daca numele produsului contine doar litere, cifre, spatii si cratime.
     *
     * @param name numele de validat
     * @return true daca e valid, false altfel
     */
    boolean validName(String name) {
        int index=0;
        if(name.isEmpty() || name.equals("Product Name")) return false;
        for(Character c: name.toCharArray()) {
            if (c == ' ')
            {
                if(index!=0 && name.toCharArray()[index-1]==' ') return false;

            }

            if(c!=' ' && c!='-' && !Character.isLetter(c) && !Character.isDigit(c))
            {
                return false;
            }
            index++;


        }

        return true;
    }

    /**
     * Sterge un produs din baza de date dupa id.
     *
     * @param obj produsul de sters (se foloseste doar id-ul)
     * @throws Exception daca produsul nu e gasit
     */
    public void deleteRecord(Object obj) throws Exception{;

        if(obj!=null) {
            try {
                int id = getObjectId(obj);
                Object aux=null;
                if((aux=this.getDao().findById(id))!=null)
                {
                    this.getDao().delete(id);
                    return;
                }
            }
            catch(Exception e)
            {
                throw e;
            }
        }
        throw new InvalidDataException("Nu s-a gasit produsul!");

    }

}
