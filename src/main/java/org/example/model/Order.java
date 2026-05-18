package org.example.model;

/**
 * Reprezinta o comanda plasata de un client.
 *
 * @author Bianca Herghelegiu
 */

public class Order {
    private int id;
    private int product_id;
    private int client_id;
    private int quantity;

    public Order(){}

    public Order(int id, int product_id, int client_id, int quantity) {
        this.id = id;
        this.product_id = product_id;
        this.client_id = client_id;
        this.quantity = quantity;
    }

    public Order(int product_id, int client_id, int quantity) {
        this.product_id = product_id;
        this.client_id = client_id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Order" + id +
                ", product_id=" + product_id +
                ", client_id=" + client_id +
                ", quantity=" + quantity +
                '\n';
    }
}
