package org.example.gui;

import org.example.businessLogic.ClientBLL;
import org.example.model.Client;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClientView {
    private ClientBLL clientBLL;
    private JTableFactory jtableFactory;
    private JFrame frame;
    private JPanel p1;
    private JButton update;
    private JButton insert;
    private JButton delete;
    private JRadioButton finalizeOrders;
    private JRadioButton undoOrders;
    private JTextField name;
    private JTextField email;
    private JTextField street;
    private JTextField city;
    private JSpinner streetNumber;
    private JScrollPane p2;
    private OrderView ordV;
    private ProductView pV;

    public ClientView(OrderView ordV, ProductView pV, ClientBLL clientBLL)
    {
        this.ordV=ordV;
        this.pV=pV;
        this.clientBLL=clientBLL;
        this.jtableFactory=new JTableFactory();
        this.frame=new JFrame();
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        this.frame.setTitle("CLIENTI");
        this.frame.setSize(400,400);
        this.p1=new JPanel();
        this.p1.setLayout(new GridLayout(4,5));
        this.p2=new JScrollPane();
        this.update=new JButton("update");
        this.insert=new JButton("insert");
        this.delete=new JButton("delete");
        this.finalizeOrders=new JRadioButton("by finalizing all orders");
        this.undoOrders=new JRadioButton("by abandoning all orders");
        ButtonGroup group=new ButtonGroup();
        group.add(this.finalizeOrders);
        group.add(this.undoOrders);
        this.name=new JTextField("Name");
        this.email=new JTextField("Email");
        this.street=new JTextField("Street");
        this.city=new JTextField("City");
        SpinnerNumberModel aux= new SpinnerNumberModel(1,1,null,1);
        this.streetNumber= new JSpinner(aux);

        this.p1.add(new JLabel(""));
        this.p1.add(this.update);
        this.p1.add(this.insert);
        this.p1.add(new JLabel(""));
        this.p1.add(new JLabel(""));
        this.p1.add(new JLabel(""));
        this.p1.add(this.delete);
        this.p1.add(this.finalizeOrders);
        this.p1.add(this.undoOrders);
        this.p1.add(new JLabel(""));
        this.p1.add(new JLabel(""));
        this.p1.add(new JLabel(""));
        this.p1.add(this.name);
        this.p1.add(this.email);
        this.p1.add(new JLabel(""));
        this.p1.add(new JLabel(""));
        this.p1.add(new JLabel("Address:"));
        this.p1.add(this.city);
        this.p1.add(this.street);
        this.p1.add(this.streetNumber);

        this.refreshTable();

        this.frame.add(p2,BorderLayout.CENTER);
        this.frame.add(p1,BorderLayout.SOUTH);

        this.delete.addActionListener(e->{
            JTable table=(JTable)p2.getViewport().getView();
            int row=table.getSelectedRow();
            if(row==-1)
            {
                JOptionPane.showMessageDialog(frame,"Trebuie sa selectati un client!","EROARE!",JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(!finalizeOrders.isSelected() && !undoOrders.isSelected())
            {
                JOptionPane.showMessageDialog(frame,"Trebuie sa selectati o modalitate de stergere a clientului!","EROARE!",JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id=Integer.parseInt((table.getValueAt(row,0)).toString());
            ClientBLL.ClientDeleteCases way= ClientBLL.ClientDeleteCases.UNDO;
            if(finalizeOrders.isSelected()) way=ClientBLL.ClientDeleteCases.FINALIZE;
            try{
                Client temp=new Client();
                temp.setId(id);
                clientBLL.deleteRecord(temp,way);
                refreshTable();
                pV.refreshTable();
                ordV.refreshTable();
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"EROARE!",JOptionPane.ERROR_MESSAGE);
            }

        });

        this.insert.addActionListener(e->{
            try {
                clientBLL.insertRecord(new Client(name.getText(), city.getText() + ", " + street.getText() + ", " + streetNumber.getValue(), email.getText()));
                name.setText("Name");
                email.setText("Email");
                city.setText("City");
                street.setText("Street");
                refreshTable();
                ordV.refreshTable();
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
                Client c=new Client();
                c.setId(id);
                if(name.getText().isEmpty() || name.getText().equals("Name")) c.setName(table.getValueAt(row,1).toString());
                else c.setName(name.getText());
                if(email.getText().isEmpty() || email.getText().equals("Email")) c.setEmail(table.getValueAt(row,3).toString());
                else c.setEmail(email.getText());
                String oldAddress = table.getValueAt(row, 2).toString();
                String[] addressParts = oldAddress.split(", ");
                String oldCity = (addressParts.length > 0) ? addressParts[0] : "";
                String oldStreet = (addressParts.length > 1) ? addressParts[1] : "";

                String newAddress="";
                if(city.getText().isEmpty() || city.getText().equals("City")) newAddress=oldCity+", ";
                else newAddress=city.getText()+", ";
                if(street.getText().isEmpty() || street.getText().equals("Street")) newAddress+=oldStreet+", "+streetNumber.getValue();
               else newAddress+=street.getText()+", "+streetNumber.getValue();
               c.setAddress(newAddress);
                clientBLL.updateRecord(c);
                name.setText("Name");
                email.setText("Email");
                city.setText("City");
                street.setText("Street");
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
        List<?> clients=this.clientBLL.getDao().findAll();
        JTable table=this.jtableFactory.tableGenerator(clients);

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
