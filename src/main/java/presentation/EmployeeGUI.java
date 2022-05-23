package presentation;


import javax.swing.*;
import java.awt.Font;

public class EmployeeGUI {

    private JFrame frmOrders;

    public EmployeeGUI() {
        initialize();
    }


    private void initialize() {
        frmOrders = new JFrame("EMPLOYEE");
        frmOrders.setBounds(100, 100, 600, 600);
        frmOrders.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frmOrders.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("NEW ORDER");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(69, 110, 431, 306);
        frmOrders.getContentPane().add(lblNewLabel);

        frmOrders.setVisible(true);
    }
}
