package presentation;


import business.BaseProduct;
import business.CompositeProduct;
import business.DeliveryService;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AdministratorGUI {


    private JFrame frmOrders;
    private JTextField textFieldNume;
    private JTextField textFieldRating;
    private JTextField textFieldCalories;
    private JTextField textFieldProtein;
    private JTextField textFieldFat;
    private JTextField textFieldSodium;
    private JTextField textFieldPrice;
    private JButton buttonAddBase;
    private JButton buttonDelete;
    private JButton buttonModify;
    private JButton buttonAddComposite;
    private DefaultListModel model;
    private JList list;

    public AdministratorGUI(DeliveryService s) {
        initialize(s);
    }

    public void buttons(DeliveryService s) {
        buttonAddBase = new JButton("ADD BASE PRODUCT");
        buttonAddBase.setBounds(10, 546, 160, 54);
        buttonAddBase.addActionListener(e->{
            BaseProduct b = new BaseProduct(textFieldNume.getText(), Float.parseFloat(textFieldRating.getText()), Integer.parseInt(textFieldCalories.getText()), Integer.parseInt(textFieldProtein.getText()), Integer.parseInt(textFieldFat.getText()), Integer.parseInt(textFieldSodium.getText()), Integer.parseInt(textFieldPrice.getText()));
            s.importProduct(b);
            model.addElement(new String("" + b.getTitle() + " " + b.getRating() + " " + b.getCalories() + " " + b.getProtein() + " " + b.getFat() + " " + b.getSodium() + " " + b.getPrice()));
            try {
                s.serializationWrite();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        frmOrders.getContentPane().add(buttonAddBase);

        buttonDelete = new JButton("DELETE PRODUCT");
        buttonDelete.setBounds(542, 546, 160, 54);
        buttonDelete.addActionListener(e->{
            String[] x = ((String) list.getSelectedValue()).split(",");
            model.clear();
            s.deleteProduct(new BaseProduct(x[0], Float.parseFloat(x[1]), Integer.parseInt(x[2]), Integer.parseInt(x[3]), Integer.parseInt(x[4]), Integer.parseInt(x[5]), Integer.parseInt(x[6])));
            String[] all = s.getAllProducts();
            for (int i = 0; i < all.length; i++)
                model.addElement(all[i]);
            try {
                s.serializationWrite();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        frmOrders.getContentPane().add(buttonDelete);

        buttonModify = new JButton("MODIFY");
        buttonModify.setBounds(782, 546, 160, 54);
        buttonModify.addActionListener(e->{
            BaseProduct b = new BaseProduct(textFieldNume.getText(), Float.parseFloat(textFieldRating.getText()), Integer.parseInt(textFieldCalories.getText()), Integer.parseInt(textFieldProtein.getText()), Integer.parseInt(textFieldFat.getText()), Integer.parseInt(textFieldSodium.getText()), Integer.parseInt(textFieldPrice.getText()));
            model.clear();
            s.modifyProduct(b);
            String[] all = s.getAllProducts();
            for (int i = 0; i < all.length; i++)
                model.addElement(all[i]);
            try {
                s.serializationWrite();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        frmOrders.getContentPane().add(buttonModify);

        buttonAddComposite = new JButton("ADD COMPOSITE PRODUCT");
        buttonAddComposite.setBounds(254, 546, 231, 54);
        buttonAddComposite.addActionListener(e->{
            ArrayList<String> x = (ArrayList<String>) list.getSelectedValuesList();
            CompositeProduct cp = new CompositeProduct(textFieldNume.getText());
            for(String b : x){
                String[] f = b.split(",");
                cp.addBaseProduct(new BaseProduct(f[0], Float.parseFloat(f[1]), Integer.parseInt(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]), Integer.parseInt(f[5]), Integer.parseInt(f[6])));
            }
            model.clear();
            s.importProduct(cp);
            String[] all = s.getAllProducts();
            for (int i = 0; i < all.length; i++)
                model.addElement(all[i]);
            try {
                s.serializationWrite();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        frmOrders.getContentPane().add(buttonAddComposite);
    }

    public void textFields() {
        textFieldNume = new JTextField();
        textFieldNume.setText("NUME PRODUS");
        textFieldNume.setBounds(10, 408, 212, 20);
        frmOrders.getContentPane().add(textFieldNume);
        textFieldNume.setColumns(10);

        textFieldRating = new JTextField();
        textFieldRating.setText("RATING");
        textFieldRating.setColumns(10);
        textFieldRating.setBounds(232, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldRating);

        textFieldCalories = new JTextField();
        textFieldCalories.setText("CALORIES");
        textFieldCalories.setColumns(10);
        textFieldCalories.setBounds(328, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldCalories);

        textFieldProtein = new JTextField();
        textFieldProtein.setText("PROTEIN");
        textFieldProtein.setColumns(10);
        textFieldProtein.setBounds(424, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldProtein);

        textFieldFat = new JTextField();
        textFieldFat.setText("FAT");
        textFieldFat.setColumns(10);
        textFieldFat.setBounds(520, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldFat);

        textFieldSodium = new JTextField();
        textFieldSodium.setText("SODIUM");
        textFieldSodium.setColumns(10);
        textFieldSodium.setBounds(616, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldSodium);

        textFieldPrice = new JTextField();
        textFieldPrice.setText("PRICE");
        textFieldPrice.setColumns(10);
        textFieldPrice.setBounds(712, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldPrice);
    }

    private void initialize(DeliveryService s) {
        frmOrders = new JFrame("ADMINISTRATOR");
        frmOrders.setTitle("LOGIN");
        frmOrders.setBounds(100, 100, 968, 800);
        frmOrders.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmOrders.getContentPane().setLayout(null);

        list = new JList();
        list.setBounds(10, 11, 932, 335);

        model = new DefaultListModel();
        String[] all = s.getAllProducts();
        for (int i = 0; i < all.length; i++)
            model.addElement(all[i]);
        list.setModel(model);

        frmOrders.getContentPane().add(list);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 11, 932, 335);
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        frmOrders.getContentPane().add(scrollPane);

        buttons(s);
        textFields();

        frmOrders.setVisible(true);
    }
}
