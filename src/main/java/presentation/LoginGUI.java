package presentation;

import business.Client;
import business.DeliveryService;
import com.opencsv.CSVWriter;

import javax.swing.*;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class LoginGUI {


    private JFrame frmOrders;
    private JTextField textField;
    private JPasswordField textField_1;

    public LoginGUI(DeliveryService s) {
        initialize(s);
    }

    public void labels() {
        JLabel lblNewLabel = new JLabel("Username:");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel.setBounds(43, 123, 110, 55);
        frmOrders.getContentPane().add(lblNewLabel);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPassword.setBounds(43, 189, 110, 55);
        frmOrders.getContentPane().add(lblPassword);
    }

    public void textFields() {
        textField = new JTextField();
        textField.setBounds(163, 142, 205, 20);
        frmOrders.getContentPane().add(textField);
        textField.setColumns(10);

        textField_1 = new JPasswordField();
        textField_1.setBounds(163, 208, 205, 20);
        frmOrders.getContentPane().add(textField_1);
        textField_1.setColumns(10);
    }

    private void initialize(DeliveryService s) {
        frmOrders = new JFrame("ORDERS");
        frmOrders.setTitle("LOGIN");
        frmOrders.setBounds(100, 100, 500, 500);
        frmOrders.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmOrders.getContentPane().setLayout(null);

        JButton btnNewButton = new JButton("LOGIN");
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnNewButton.setBounds(163, 315, 188, 60);
        btnNewButton.addActionListener(e->{
            String username = textField.getText();
            String password = new String(textField_1.getPassword());
            if(username.equals("administrator") && password.equals("administrator")){
                frmOrders.dispose();
                new AdministratorGUI(s);
            }
            else
            {
                boolean exists = false;
                for(Client c : s.getClients()){
                    if(c.getNume().equals(username)){
                        exists = true;
                        break;
                    }
                }
                if(exists) {
                    for(Client c : s.getClients()) {
                        if(c.getNume().equals(username)){
                            if(c.getPassword().equals(password)) {
                                frmOrders.dispose();
                                s.setCurrentClient(c);
                                new ClientGUI(s);
                            }
                        }
                    }
                }
                else {
                    frmOrders.dispose();
                    s.getClients().add(new Client(s.getClients().size()+1,username,password));
                    File f = new File("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\database\\users.csv");
                    try {
                        FileWriter outputFile = new FileWriter(f);
                        CSVWriter csvWriter = new CSVWriter(outputFile);
                        for(Client c : s.getClients()) {
                            String[] data = {String.valueOf(c.getId()), c.getNume(), c.getPassword()};
                            csvWriter.writeNext(data);
                        }
                        csvWriter.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    s.setCurrentClient(s.getClients().get(s.getClients().size()-1));
                    new ClientGUI(s);
                }

            }
        });
        frmOrders.getContentPane().add(btnNewButton);

        labels();
        textFields();

        frmOrders.setVisible(true);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DeliveryService s = new DeliveryService();
        new LoginGUI(s);
    }
}
