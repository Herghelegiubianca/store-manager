package org.example.businessLogic;
import org.example.dao.BillDAO;
import org.example.dao.ClientDAO;
import org.example.dao.OrderDAO;
import org.example.dao.ProductDAO;
import org.example.model.Bill;
import org.example.model.Client;
import org.example.model.Order;
import org.example.model.Product;

import java.time.LocalDate;

/**
 * Logica pentru gestionarea comenzilor, inclusiv actualizarea stocului si generarea facturilor.
 *
 * @author Bianca Herghelegiu
 */

public final class OrderBLL extends AbstractBLL{
    private ProductDAO productDAO;
    private ClientDAO clientDAO;
    private BillDAO billDAO;
    public OrderBLL(){
        super(new OrderDAO());
        this.productDAO=new ProductDAO();
        this.clientDAO=new ClientDAO();
        this.billDAO=new BillDAO();
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public ClientDAO getClientDAO() {
        return clientDAO;
    }

    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    /**
     * Insereaza o comanda noua, scade stocul si genereaza o factura.
     *
     * @param obj comanda de inserat
     * @throws InvalidDataException daca id-urile sunt gresite sau stocul e insuficient
     */
    public void insertRecord(Object obj) throws InvalidDataException{
        if(obj!=null && obj instanceof Order) {
            Order order=(Order)obj;
            Product product=null;
            Client client=null;
            if((client=(Client)this.clientDAO.findById(order.getClient_id()))!=null && (product=(Product)this.productDAO.findById(order.getProduct_id()))!=null && product.getStock()>=order.getQuantity()){
                this.getDao().insert(order);
                product.setStock(product.getStock()-order.getQuantity());
                this.productDAO.update(product);
                Bill bill=new Bill(order.getId(),client.getId(),product.getId(),client.getName(),product.getName(),product.getPrice(),product.getPrice()*order.getQuantity(),order.getQuantity(),0,LocalDate.now());
                this.billDAO.insert(bill);
                return;
            }
            else{
                throw new InvalidDataException("Id-urile incorecte sau stoc insuficient!");
            }
        }
        throw new InvalidDataException("Nicio comanda selectat!");

    }

    /**
     * Actualizeaza o comanda existenta si ajusteaza stocul corespunzator.
     *
     * @param obj comanda cu valorile noi
     * @throws Exception daca comanda nu e gasita sau stocul e insuficient
     */
    @Override
    public void updateRecord(Object obj) throws Exception{
        String auxs="comanda";

        if(obj!=null) {
            try {
                int id = getObjectId(obj);
                Object aux=null;
                if((aux=this.getDao().findById(id))!=null)
                {
                    Order order=(Order)aux;
                    Product p=(Product)this.productDAO.findById(((Order) aux).getProduct_id());
                    Client c=(Client)this.clientDAO.findById(((Order)aux).getClient_id());
                    if(p==null || c==null)
                    {
                        throw new InvalidDataException("Comanda curenta nu poate fi modificata, intrucat clientul sau produsul implicat nu mai exista in baza de date a magazinului!");
                    }
                    Order newOrder=(Order)obj;
                    newOrder.setId(order.getId());
                    Product newP=(Product)this.productDAO.findById(newOrder.getProduct_id());
                    if(newP.getId()==p.getId())
                    {
                    updateModifiesCurrentProduct(newOrder,order,p);
                    return;
                }
                 else {
                      updateDifferentProduct(newOrder,order,p,newP);
                      return;
                    }

                }
            }
            catch(Exception e)
            {
                throw e;
            }
        }
        throw new InvalidDataException("Nu s-a gasit "+auxs+"!");

    }

    /**
     * Trateaza cazul in care comanda modificata afecteaza acelasi produs.
     *
     * @param newOrder comanda cu valorile noi
     * @param order comanda veche din baza de date
     * @param p produsul implicat
     * @throws InvalidDataException daca stocul e insuficient
     */
    void updateModifiesCurrentProduct(Order newOrder, Order order, Product p) throws InvalidDataException
    {
        boolean erased=false;
        if(newOrder.getQuantity()>order.getQuantity() )
        {
            int newQuantity=0;
            if((newQuantity=(newOrder.getQuantity()-order.getQuantity()))>p.getStock())
            {
                throw new InvalidDataException("Cantitate insuficienta!");
            }
            else
            {
                this.productDAO.update(new Product(p.getId(),p.getName(),p.getPrice(),p.getStock()-newQuantity));
            }
        }
        else if(newOrder.getQuantity()==0)
        {
            this.productDAO.update(new Product(p.getId(),p.getName(),p.getPrice(),p.getStock()+order.getQuantity()));
            this.getDao().delete(order.getId());
            erased=true;
        }
        else{
            int newQuantity=order.getQuantity()-newOrder.getQuantity();
            this.productDAO.update(new Product(p.getId(),p.getName(),p.getPrice(),p.getStock()+newQuantity));
        }


        if(!erased)
        {
            this.billDAO.insert(new Bill(newOrder.getId(),newOrder.getClient_id(),newOrder.getProduct_id(),((Client)this.clientDAO.findById(newOrder.getClient_id())).getName(),p.getName(),p.getPrice(),p.getPrice()*newOrder.getQuantity(),newOrder.getQuantity(),0,LocalDate.now()));
            this.getDao().update(newOrder);//nu au voie sa fie modificate - am zis ca bill e final/imutabila
        }
    }

    /**
     * Trateaza cazul in care comanda modificata schimba produsul comandat.
     *
     * @param newOrder comanda cu valorile noi
     * @param order comanda veche din baza de date
     * @param p produsul vechi
     * @param newP produsul nou
     * @throws InvalidDataException daca stocul noului produs e insuficient
     */
    void updateDifferentProduct(Order newOrder, Order order, Product p, Product newP) throws InvalidDataException
    {
        if(newP.getStock()<newOrder.getQuantity())
        {
            throw new InvalidDataException("Cantitate insuficienta!");
        }
        this.productDAO.update(new Product(p.getId(),p.getName(),p.getPrice(),p.getStock()+order.getQuantity()));
        this.productDAO.update(new Product(newP.getId(),newP.getName(),newP.getPrice(),newP.getStock()-newOrder.getQuantity()));
        this.billDAO.insert(new Bill(newOrder.getId(),newOrder.getClient_id(),newOrder.getProduct_id(),((Client)this.clientDAO.findById(newOrder.getClient_id())).getName(),newP.getName(),newP.getPrice(),newP.getPrice()*newOrder.getQuantity(),newOrder.getQuantity(),0,LocalDate.now()));
        this.getDao().update(newOrder);
    }

    //deleteRecord pt order=finalizarea comenzii
    /**
     * Finalizeaza sau anuleaza o comanda prin stergerea ei.
     *
     * @param obj comanda de sters (se foloseste doar id-ul)
     * @param mode UNDO reface stocul, FINALIZE nu
     * @throws Exception daca comanda nu e gasita
     */
    public void deleteRecord(Object obj, ClientBLL.ClientDeleteCases mode) throws Exception{;

        if(obj!=null) {
            try {
                int id = getObjectId(obj);
                Object aux=null;
                if((aux=this.getDao().findById(id))!=null)
                {
                    Order order=(Order)aux;
                    Product p=(Product)this.productDAO.findById(((Order) aux).getProduct_id());
                    Client c=(Client)this.clientDAO.findById(((Order)aux).getClient_id());
                    if(p==null || c==null)
                    {
                        throw new InvalidDataException("Comanda curenta nu poate fi modificata, intrucat clientul sau produsul implicat nu mai exista in baza de date a magazinului!");
                    }
                    if(mode== ClientBLL.ClientDeleteCases.UNDO)
                    {
                        this.productDAO.update(new Product(p.getId(),p.getName(),p.getPrice(),p.getStock()+order.getQuantity()));
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
        throw new InvalidDataException("Nu s-a gasit comanda!");

    }

}
