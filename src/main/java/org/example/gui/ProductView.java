package org.example.gui;

import org.example.businessLogic.ProductBLL;
import org.example.model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductView {
    private ProductBLL productBLL;
    private JTableFactory jtableFactory;
    private JFrame frame;
    private JPanel p1;
    private JButton update;
    private JButton insert;
    private JButton delete;
    private JTextField name;
    private JSpinner stock;
    private JSpinner price;
    private JScrollPane p2;
    private OrderView oV;

    public ProductView(ProductBLL productBLL, OrderView ov)
    {
        this.productBLL=productBLL;
        this.oV=ov;
        this.jtableFactory=new JTableFactory();
        this.frame=new JFrame();
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        this.frame.setTitle("PRODUSE DISPONIBILE IN MAGAZIN");
        this.frame.setSize(400,400);
        this.p1=new JPanel();
        this.p1.setLayout(new GridLayout(2,3));
        this.p2=new JScrollPane();
        this.update=new JButton("update");
        this.insert=new JButton("insert");
        this.delete=new JButton("delete");
        this.name=new JTextField("Product Name");
        SpinnerNumberModel aux= new SpinnerNumberModel(1,1,null,1);
        this.stock= new JSpinner(aux);
        SpinnerNumberModel aux2= new SpinnerNumberModel(1.00,0.01,null,0.01);
        this.price= new JSpinner(aux2);

        this.p1.add(this.update);
        this.p1.add(this.insert);
        this.p1.add(this.delete);
        this.p1.add(this.name);
        this.p1.add(this.stock);
        this.p1.add(this.price);

        this.refreshTable();

        this.frame.add(p2,BorderLayout.CENTER);
        this.frame.add(p1,BorderLayout.SOUTH);

        this.delete.addActionListener(e->{
            JTable table=(JTable)p2.getViewport().getView();
            int row=table.getSelectedRow();
            if(row==-1)
            {
                JOptionPane.showMessageDialog(frame,"Trebuie sa selectati un produs!","EROARE!",JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id=Integer.parseInt((table.getValueAt(row,0)).toString());
            try{
                Product temp=new Product();
                temp.setId(id);
                productBLL.deleteRecord(temp);
                oV.refreshTable();
                refreshTable();
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"EROARE!",JOptionPane.ERROR_MESSAGE);
            }

        });

        this.insert.addActionListener(e->{
            try {
                productBLL.insertRecord(new Product(name.getText(), (double)price.getValue(),(int)stock.getValue()));
                name.setText("Product Name");
                oV.refreshTable();
                refreshTable();
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
                    JOptionPane.showMessageDialog(frame,"Trebuie sa selectati un client!","EROARE!",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int id=Integer.parseInt((table.getValueAt(row,0)).toString());
                Product p=new Product();
                p.setId(id);
                if(name.getText().isEmpty() || name.getText().equals("Product Name")) p.setName(table.getValueAt(row,1).toString());
                else p.setName(name.getText());
                p.setPrice((double)price.getValue());
                p.setStock((int)stock.getValue());
                productBLL.updateRecord(p);
                name.setText("Product Name");
                oV.refreshTable();
                refreshTable();
            }catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"EROARE!",JOptionPane.ERROR_MESSAGE);
            }
        });

        this.frame.setVisible(true);
    }

    public void setoV(OrderView oV) {
        this.oV = oV;
    }

    public void refreshTable()
    {
        List<?> products=this.productBLL.getDao().findAll();
        JTable table=this.jtableFactory.tableGenerator(products);

        if(this.p2==null)
        {
            this.p2=new JScrollPane(table);
        }
        else
        {
            p2.setViewportView(table);
        }
        this.frame.revalidate();
        this.frame.repaint();
    }

}
