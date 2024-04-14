package Kitchen;

import SaharTicketOrders.Orders;
import com.sun.scenario.Settings;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Dashboard {
    private JList list1;
    private JFrame j;
    private JPanel DashboardPanel;
    private JPanel MenuPanel;
    private JLabel logo;
    private JButton dashboardButton;
    private JButton menuManagementButton;
    private JButton StockManagementButton;
    private JButton WasteManagementButton;
    private JButton ordersAndServicesButton;
    private JButton settingsButton;
    private JButton signOutButton;
    private JTextField textIngredientID;
    private JTextField textQuantity;
    private JComboBox<String> textReason;
    private JTextField textDate;
    private JTextField textSearch;
    private JButton searchButton;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    Statement stmt = null;
    int chef = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dashboard");
        frame.setContentPane(new Dashboard().DashboardPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Dashboard() {
        connect();
        j = new JFrame();
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==dashboardButton){
                    j.dispose();
                    Dashboard d = new Dashboard();
                }
            }
        });
        menuManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //   openMenuManagementWindow();
                chef = 1;
                j.dispose();
                DishConstructionUI dish = new DishConstructionUI(chef);
            }
        });
        StockManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==StockManagementButton){
                    j.dispose();
                    Stock s = new Stock();
                }
            }
        });
        WasteManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==WasteManagementButton){
                    j.dispose();
                    Waste w = new Waste();
                }
            }
        });
        ordersAndServicesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==ordersAndServicesButton){
                    j.dispose();
                    Orders o = new Orders();
                }
            }
        });
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==settingsButton){
                    j.dispose();
                 //   Settings s = new Settings();
                }
            }
        });
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==signOutButton){
                    j.dispose();
                    //  SignOut signOut = new SignOut();
                }
            }
        });
    }

    /*private void showDashboardPanel() {
        JFrame frame = new JFrame("Dashboard");
        frame.setContentPane(new waste().dashboardPanel);
        dashboardFrame.pack();
        dashboardFrame.setVisible(true);
    }**/

     /*private void openMenuManagementWindow() {
        JFrame frame = new JFrame("Menu Management");
        frame.setContentPane(new waste().menuManagementPanel);
        menuManagementFrame.pack();
        menuManagementFrame.setVisible(true);
    }**/

     /*private void openStockManagementWindow() {
     JFrame stockFrame = new JFrame("Stock Management");
    frame.setContentPane(new StockManagementPanel());
    stockFrame.pack();
    stockFrame.setVisible(true);
    }**/

    /*private void openWasteManagementWindow() {
     JFrame wasteFrame = new JFrame("Waste Management");
    frame.setContentPane(new WasteManagementPanel());
    wasteFrame.pack();
    wasteFrame.setVisible(true);
    }**/

     /*private void openOrdersAndServicesWindow() {
     JFrame ordersFrame = new JFrame("Orders and Services");
    frame.setContentPane(new OrdersAndServicesPanel());
    ordersFrame.pack();
    ordersFrame.setVisible(true);
    }**/

     /*private void performSignIn() {
    }**/

     /*private void performSignOut() {
    }**/

    public void connect() {
        String url = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29";
        String user = "in2033t29_a";
        String password = "NvG2lCOEy_g";

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

