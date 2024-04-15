package Kitchen;

import SaharTicketOrders.Orders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BaseGUI extends JFrame {
    private JPanel BaseGUIPanel;
    private JPanel MenuPanel;
    private JPanel OtherPanel;
    private JFrame j;
    private JLabel logo;
    private JButton dashboardButton;
    private JButton menuManagementButton;
    private JButton courseManagementButton;
    private JButton dishManagementButton;
    private JButton StockManagementButton;
    private JButton WasteManagementButton;
    private JButton ordersAndServicesButton;
    private JButton settingsButton;
    private JButton signOutButton;
    private Connection connection;
    Statement stmt = null;
    int chef = 0;
    int pageID = 1;
    private Stock stock;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel StockPanel;
    private JPanel WastePanel;
    private JPanel OrderPanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("BaseGUI");
        frame.setContentPane(new BaseGUI().BaseGUIPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public BaseGUI() {
        ImageIcon img = new ImageIcon("src/Kitchen/Img/Lancaster.jpeg");
        setIconImage(img.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        // stock = new Stock(connection);
        //Waste waste = new Waste(connection);
        //CONNECTING
        String url = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29";
        String user = "in2033t29_a";
        String password = "NvG2lCOEy_g";

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  showDashboardPanel();
                pageID = 1;
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
                // openStockManagementWindow();
                pageID = 5;
                changePage(pageID);

            }
        });
        WasteManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // openWasteManagementWindow();
                j.dispose();
                Waste waste = new Waste();
            }
        });
        ordersAndServicesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  openOrdersAndServicesWindow();
                j.dispose();
                Orders o = new Orders();
            }
        });
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //   openSettingsWindow();
            }
        });
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  performSignOut();
            }
        });
    }

    private void changePage(int pageID) {
        switch (pageID) {
            case 1:
                System.out.println("dashboard");
                break;
            case 2:
                System.out.println("menu");
                break;
            case 3:
                System.out.println("couse management");
                break;
            case 4:
                System.out.println("dishmanagement");
                break;
            case 5:
                System.out.println("stock");
                // add(stock);
                break;
            case 6:
                System.out.println("waste");
                break;
            case 7:
                System.out.println("orders");
                break;
        }
    }
}
