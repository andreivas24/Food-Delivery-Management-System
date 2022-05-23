package presentation;


import business.*;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class ClientGUI implements Observer{


    private JFrame frmOrders;
    private JTextField textFieldNume;
    private JTextField textFieldRating;
    private JTextField textFieldCalories;
    private JTextField textFieldProtein;
    private JTextField textFieldFat;
    private JTextField textFieldSodium;
    private JTextField textFieldPrice;
    private JButton buttonOrder;
    private JButton buttonSearch;
    private JButton buttonViewAll;
    private JButton buttonBill;
    private DefaultListModel model;
    private JList list;

    public ClientGUI(DeliveryService s)   {
        initialize(s);
    }

    public void labels() {
        JLabel lblNewLabel = new JLabel("Nume produs");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(10, 377, 106, 20);
        frmOrders.getContentPane().add(lblNewLabel);

        JLabel lblRating = new JLabel("Rating");
        lblRating.setHorizontalAlignment(SwingConstants.CENTER);
        lblRating.setBounds(232, 377, 86, 20);
        frmOrders.getContentPane().add(lblRating);

        JLabel lblCalories = new JLabel("Calories");
        lblCalories.setHorizontalAlignment(SwingConstants.CENTER);
        lblCalories.setBounds(328, 377, 86, 20);
        frmOrders.getContentPane().add(lblCalories);

        JLabel lblProtein = new JLabel("Protein");
        lblProtein.setHorizontalAlignment(SwingConstants.CENTER);
        lblProtein.setBounds(424, 377, 86, 20);
        frmOrders.getContentPane().add(lblProtein);

        JLabel lblFat = new JLabel("Fat");
        lblFat.setHorizontalAlignment(SwingConstants.CENTER);
        lblFat.setBounds(520, 377, 86, 20);
        frmOrders.getContentPane().add(lblFat);

        JLabel lblSodium = new JLabel("Sodium");
        lblSodium.setHorizontalAlignment(SwingConstants.CENTER);
        lblSodium.setBounds(616, 377, 86, 20);
        frmOrders.getContentPane().add(lblSodium);

        JLabel lblPrice = new JLabel("Price");
        lblPrice.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrice.setBounds(712, 377, 86, 20);
        frmOrders.getContentPane().add(lblPrice);
    }

    public void textFields() {
        textFieldNume = new JTextField();
        textFieldNume.setBounds(10, 408, 212, 20);
        frmOrders.getContentPane().add(textFieldNume);
        textFieldNume.setColumns(10);

        textFieldRating = new JTextField();
        textFieldRating.setColumns(10);
        textFieldRating.setBounds(232, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldRating);

        textFieldCalories = new JTextField();
        textFieldCalories.setColumns(10);
        textFieldCalories.setBounds(328, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldCalories);

        textFieldProtein = new JTextField();
        textFieldProtein.setColumns(10);
        textFieldProtein.setBounds(424, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldProtein);

        textFieldFat = new JTextField();
        textFieldFat.setColumns(10);
        textFieldFat.setBounds(520, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldFat);

        textFieldSodium = new JTextField();
        textFieldSodium.setColumns(10);
        textFieldSodium.setBounds(616, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldSodium);

        textFieldPrice = new JTextField();
        textFieldPrice.setColumns(10);
        textFieldPrice.setBounds(712, 408, 86, 20);
        frmOrders.getContentPane().add(textFieldPrice);
    }

    public void buttons(DeliveryService s){
        buttonOrder = new JButton("ORDER");
        buttonOrder.setBounds(10, 546, 160, 54);
        buttonOrder.addActionListener(e->{
            ArrayList<String> x = (ArrayList<String>) list.getSelectedValuesList();
            ArrayList<MenuItem> mi = new ArrayList<>();
            for(String b : x){
                String[] f = b.split(",");
                mi.add(new BaseProduct(f[0], Float.parseFloat(f[1]), Integer.parseInt(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]), Integer.parseInt(f[5]), Integer.parseInt(f[6])));
            }
            Random r = new Random();
            int Id = r.nextInt(1000);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDateTime now = LocalDateTime.now();
            s.addOrder(new Order(Id, s.getCurrentClient().getId(), dtf.format(now)), mi);
            notify(1);
        });
        frmOrders.getContentPane().add(buttonOrder);

        buttonSearch = new JButton("SEARCH");
        buttonSearch.setBounds(542, 546, 160, 54);
        buttonSearch.addActionListener(e->{
            model.clear();
            String[] all = s.getSearchedProducts(textFieldNume.getText(), Float.parseFloat(textFieldRating.getText()), Integer.parseInt(textFieldCalories.getText()),Integer.parseInt(textFieldProtein.getText()),Integer.parseInt(textFieldFat.getText()),Integer.parseInt(textFieldSodium.getText()),Integer.parseInt(textFieldPrice.getText()));
            for (int i = 0; i < all.length; i++)
                model.addElement(all[i]);
        });
        frmOrders.getContentPane().add(buttonSearch);

        buttonViewAll = new JButton("View all");
        buttonViewAll.setBounds(782, 546, 160, 54);
        buttonViewAll.addActionListener(e->{
            model.clear();
            String[] all = s.getAllProducts();
            for (int i = 0; i < all.length; i++)
                model.addElement(all[i]);
        });
        frmOrders.getContentPane().add(buttonViewAll);

        buttonBill = new JButton("BILL");
        buttonBill.setBounds(254, 546, 231, 54);
        buttonBill.addActionListener(e->{
            try {
                s.generateBill(s.getCurrentClient());
                s.generateReport2("report", 0, 24, "luni", 3,2,0);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        frmOrders.getContentPane().add(buttonBill);
    }


    private void initialize(DeliveryService s) {
        frmOrders = new JFrame("CLIENT");
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

        labels();
        textFields();
        buttons(s);

        frmOrders.setVisible(true);
    }

    @Override
    public void notify(int a) {
        new EmployeeGUI();
    }
}

