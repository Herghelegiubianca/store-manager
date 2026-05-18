package org.example;

import org.example.businessLogic.BillBLL;
import org.example.businessLogic.ClientBLL;
import org.example.businessLogic.OrderBLL;
import org.example.businessLogic.ProductBLL;
import org.example.gui.BillView;
import org.example.gui.ClientView;
import org.example.gui.OrderView;
import org.example.gui.ProductView;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ProductBLL productBLL = new ProductBLL();
        ClientBLL clientBLL = new ClientBLL();
        OrderBLL orderBLL = new OrderBLL();
        BillBLL billBLL = new BillBLL();
        BillView billView = new BillView(billBLL);
        ProductView productView = new ProductView(productBLL,null);
        OrderView orderView = new OrderView(productBLL, clientBLL, orderBLL, billView, productView);
        productView.setoV(orderView);

        ClientView clientView = new ClientView(orderView, productView, clientBLL);
    }
}