package org.example.gui;

import org.example.businessLogic.BillBLL;
import org.example.businessLogic.ClientBLL;
import org.example.businessLogic.OrderBLL;
import org.example.businessLogic.ProductBLL;
import org.example.model.Client;
import org.example.model.Order;
import org.example.model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class OrderView {
    private OrderBLL orderBLL;
    private ClientBLL clientBLL;
    private BillBLL billBLL;
    private ProductBLL productBLL;
    private BillView bV;
    private JTableFactory jtableFactory;
    private JFrame frame;
    private JPanel p1;
    private JButton update;
    private JButton insert;
    private JButton delete;
    private JButton undo;
    private JSpinner quantity;
    private JComboBox<String> clientIds;
    private JComboBox<String> productIds;
    private ProductView pV;

    private JScrollPane p2;


    public OrderView(ProductBLL p, ClientBLL c, OrderBLL o,BillView b,ProductView pV)
    {
        this.pV=pV;
        this.orderBLL=o;
        this.productBLL=p;
        this.clientBLL=c;
        this.bV=b;
        this.clientIds=new JComboBox<>();
        this.productIds=new JComboBox<>();
        this.jtableFactory=new JTableFactory();
        this.frame=new JFrame();
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        this.frame.setTitle("COMENZI IN DERULARE/PROCESARE");
        this.frame.setSize(400,400);

        this.p1=new JPanel();
        this.p1.setLayout(new GridLayout(2,4));
        this.p2=new JScrollPane();
        this.update=new JButton("update");
        this.insert=new JButton("insert");
        this.delete=new JButton("finalize");
        this.undo=new JButton("undo");

        SpinnerNumberModel aux= new SpinnerNumberModel(1,1,null,1);
        this.quantity= new JSpinner(aux);



        this.p1.add(this.update);
        this.p1.add(this.insert);
        this.p1.add(this.delete);
        this.p1.add(this.undo);
        this.p1.add(clientIds);
        this.p1.add(productIds);
        this.p1.add(this.quantity);


        this.refreshTable();

        this.frame.add(p2,BorderLayout.CENTER);
        this.frame.add(p1,BorderLayout.SOUTH);

        this.delete.addActionListener(e->{
            JTable table=(JTable)p2.getViewport().getView();
            int row=table.getSelectedRow();
            if(row==-1)
            {
                JOptionPane.showMessageDialog(frame,"Trebuie sa selectati o comanda!","EROARE!",JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id=Integer.parseInt((table.getValueAt(row,0)).toString());
            try{
                Order temp=new Order();
                temp.setId(id);
                orderBLL.deleteRecord(temp, ClientBLL.ClientDeleteCases.FINALIZE);
                bV.refreshTable();
                refreshTable();
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"EROARE!",JOptionPane.ERROR_MESSAGE);
            }

        });

        this.undo.addActionListener(e->{
            JTable table=(JTable)p2.getViewport().getView();
            int row=table.getSelectedRow();
            if(row==-1)
            {
                JOptionPane.showMessageDialog(frame,"Trebuie sa selectati o comanda!","EROARE!",JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id=Integer.parseInt((table.getValueAt(row,0)).toString());
            try{
                Order temp=new Order();
                temp.setId(id);
                orderBLL.deleteRecord(temp, ClientBLL.ClientDeleteCases.UNDO);
                bV.refreshTable();
                refreshTable();
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"EROARE!",JOptionPane.ERROR_MESSAGE);
            }

        });

        this.insert.addActionListener(e->{
            try {
                if(productIds==null || clientIds==null || productIds.getSelectedItem()==null || clientIds.getSelectedItem()==null)
                {
                    JOptionPane.showMessageDialog(frame,"Nu ati selectat produsul sau clientul sau nu exista suficiente instante pentru o noua comanda!","EROARE!",JOptionPane.ERROR_MESSAGE);
                }
                orderBLL.insertRecord(new Order(Integer.parseInt(((String)productIds.getSelectedItem()).split("-")[0]),Integer.parseInt(((String)clientIds.getSelectedItem()).split("-")[0]),(int)quantity.getValue()));
                refreshTable();
                pV.refreshTable();
                bV.refreshTable();
            }catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"EROARE!",JOptionPane.ERROR_MESSAGE);
            }
        });

        this.update.addActionListener(e->{
            try {
                JTable table=(JTable)p2.getViewport().getView();
                int row=table.getSelectedRow();
                if(row==-1)
                {
                    JOptionPane.showMessageDialog(frame,"Trebuie sa selectati o comanda!","EROARE!",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int id=Integer.parseInt((table.getValueAt(row,0)).toString());
                Order cc=new Order(Integer.parseInt(((String)productIds.getSelectedItem()).split("-")[0]),Integer.parseInt(((String)clientIds.getSelectedItem()).split("-")[0]),(int)quantity.getValue());
                cc.setId(id);
                this.orderBLL.updateRecord(cc);
                pV.refreshTable();
                bV.refreshTable();
                refreshTable();
            }catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"EROARE!",JOptionPane.ERROR_MESSAGE);
            }
        });

        this.frame.setVisible(true);
    }


    public void refreshTable()
    {
        clientIds.removeAllItems();
        productIds.removeAllItems();
        List<?> orders=this.orderBLL.getDao().findAll();
        JTable table=this.jtableFactory.tableGenerator(orders);

        if(this.p2==null)
        {
            this.p2=new JScrollPane(table);
        }
        else
        {
            p2.setViewportView(table);
        }
        List<Client> clients=(List<Client>)this.clientBLL.getDao().findAll();
        if(clients!=null && !clients.isEmpty())
        {
            for(Client c: clients)
            {
                clientIds.addItem(c.getId()+"-"+c.getName());
            }
        }
        List<Product> products=(List<Product>)this.productBLL.getDao().findAll();
        if(products!=null && !products.isEmpty())
        {
            for(Product c: products)
            {
                productIds.addItem(c.getId()+"-"+c.getName());
            }
        }

        this.frame.revalidate();
        this.frame.repaint();
    }

    void updateIds()
    {
        clientIds.removeAll();
        productIds.removeAll();

    }


}
