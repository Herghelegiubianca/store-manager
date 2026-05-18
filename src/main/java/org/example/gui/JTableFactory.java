package org.example.gui;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class JTableFactory {
    public JTableFactory(){}

    public JTable tableGenerator(List<?> objects)
    {
        if(objects==null || objects.isEmpty()) return new JTable();

        Class aux=objects.getFirst().getClass();
        Object [][] data=new Object[objects.size()][aux.getDeclaredFields().length];
        int index=0;
        try {
        for(Object o:objects)
        {
            int cnt=0;

            for (Field f : aux.getDeclaredFields()) {
                f.setAccessible(true);
                data[index][cnt] = f.get(o);
                f.setAccessible(false);
                cnt++;
            }
            index++;
        }
        }catch(Exception e)
        {
             e.printStackTrace();
        }
        String [] names;
        names=Arrays.stream(aux.getDeclaredFields()).map(Field::getName).toArray(String[]::new);
        JTable table=new JTable(data,names);

        return table;
    }
}
