package org.example.businessLogic;

import org.example.dao.ClientDAO;
import org.example.dao.OrderDAO;
import org.example.dao.ProductDAO;
import org.example.model.Client;
import org.example.model.Order;
import org.example.model.Product;

import java.util.List;

/**
 * Logica pentru gestionarea clientilor, cu validare si stergere in cascada a comenzilor.
 *
 * @author Bianca Herghelegiu
 */

public final class ClientBLL extends AbstractBLL{
    private OrderDAO orderDAO;

    /**
     * Modurile de stergere a unui client: UNDO reface stocul, FINALIZE nu.
     */
    public enum ClientDeleteCases{
        UNDO,FINALIZE
    }
    private ProductDAO productDAO;

    public ClientBLL(){
        super(new ClientDAO());
        this.orderDAO=new OrderDAO();
        this.productDAO=new ProductDAO();
    }

    /**
     * Sterge un client si toate comenzile asociate lui.
     *
     * @param obj clientul de sters (se foloseste doar id-ul)
     * @param mode UNDO sau FINALIZE
     * @throws Exception daca clientul nu e gasit
     */
    public void deleteRecord(Object obj, ClientDeleteCases mode) throws Exception{;

        if(obj!=null) {
            try {
                int id = getObjectId(obj);
                Object aux=null;
                if((aux=this.getDao().findById(id))!=null)
                {
                    List<Order> orders=this.orderDAO.findByClientId(id);
                    if(orders!=null)
                    {
                        for(Order o: orders)
                        {
                            if(mode==ClientDeleteCases.UNDO)
                            {
                                Product p=(Product)this.productDAO.findById(o.getProduct_id());
                                this.productDAO.update(new Product(p.getId(), p.getName(), p.getPrice(), p.getStock() + o.getQuantity()));}
                            this.orderDAO.delete(o.getId());
                        }
                    }
                    this.getDao().delete(id);
                    return;
                }
            }
            catch(Exception e)
            {
                throw e;
            }
        }
        throw new InvalidDataException("Nu s-a gasit clientul!");

    }

    /**
     * Valideaza si insereaza un client nou in baza de date.
     *
     * @param obj clientul de inserat
     * @throws InvalidDataException daca numele, emailul sau adresa sunt invalide
     */
    public void insertRecord(Object obj) throws InvalidDataException{
        if(obj!=null && obj instanceof Client) {
            Client client=(Client)obj;

            if(client.getName()!=null && validName(client.getName()) &&client.getEmail()!=null && validEmail(client.getEmail()) && client.getAddress()!=null && validAddress(client.getAddress())){
               this.getDao().insert(client);
               return;
            }
            else{
                throw new InvalidDataException("Numele orasului sau numele strazii sau nr strazii invalid!");
            }
        }
        throw new InvalidDataException("Niciun client selectat!");
    }

    /**
     * Verifica daca un nume contine doar litere, spatii si cratime.
     *
     * @param name numele de validat
     * @return true daca e valid, false altfel
     */
    boolean validName(String name) {
        int index=0;
         if(name.contentEquals("Name Email Street City")) return false;
         for(Character c: name.toCharArray()) {
             if (c == ' ')
             {
                 if(index!=0 && name.toCharArray()[index-1]==' ') return false;

             }

             if(c!=' ' && c!='-' && !Character.isLetter(c)) return false;
             index++;

         }

         return true;
    }

    /**
     * Verifica daca un email are @ si se termina in .com sau .ro
     *
     * @param email emailul de validat
     * @return true daca e valid, false altfel
     */
    boolean validEmail(String email) {
        if(!email.endsWith(".com") && !email.endsWith(".ro"))  return false;

        return email.chars().anyMatch(c->c=='@');
    }

    /**
     * Verifica daca adresa are formatul: oras, strada, numar.
     *
     * @param address adresa de validat
     * @return true daca e valid, false altfel
     */
    boolean validAddress(String address)
    {
        //pornesc de la premisa ca eu voi trece adresa cu formatul oras, strada, nr
        if(address==null || address.trim().isEmpty()) return false;

        String[] parts=address.split(", "); //mi-am ales eu formatul asta
        if(parts.length!=3) return false;

        if(validName(parts[0]) && validName(parts[1])) //numele orasului si al strazii
        {
            if(!parts[2].chars().allMatch(Character::isDigit)) return false;
            int nr=Integer.parseInt(parts[2]);
            if(nr>0) return true;
        }

        return false;
    }
}
