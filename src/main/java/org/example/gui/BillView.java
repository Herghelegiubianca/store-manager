package org.example.gui;

import org.example.businessLogic.BillBLL;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BillView {
    private BillBLL billBLL;
    private JTableFactory jtableFactory;
    private JFrame frame;
    private JScrollPane p2;

    public BillView(BillBLL billBLL) {
        this.billBLL = billBLL;
        this.jtableFactory = new JTableFactory();
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        this.frame.setTitle("FACTURI/ISTORIC");
        this.frame.setSize(400,400);

        this.refreshTable();

        this.frame.add(p2, BorderLayout.CENTER);
        this.frame.setVisible(true);
    }


    public void refreshTable()
    {
        List<?> bills=this.billBLL.getDao().findAll();
        JTable table=this.jtableFactory.tableGenerator(bills);

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
