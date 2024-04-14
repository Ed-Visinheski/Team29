package Kitchen;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Stock {
    private Connection connection;
    private PreparedStatement pstm;
    private ResultSet resultSet;
    // Main Stock Management
    private JPanel StockPanel;
    private JButton searchButton, updateButton;
    private JTextField textIngredientID, textIngredientName, textStockLevel, textStockThreshold, textDeliveryArrivalDate;
    private JScrollPane TableScrollPane; private JTable stockTable;

    // Menu sidebar
    private JPanel MenuPanel;
    private JLabel logo;
    private JButton dashboardButton, menuManagementButton, inventoryManagementButton, ordersAndServicesButton, settingsButton, signInButton, signOutButton;


    public static void main(String[] args) {
        ImageIcon img = new ImageIcon("src/Kitchen/Img/Lancaster.jpeg");
        JFrame frame = new JFrame("Stock Management");
        frame.setContentPane(new Stock().StockPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setIconImage(img.getImage());
        frame.setVisible(true);
    }

    public Stock() {
        connect();
        loadStockTable();
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id, name, level, threshold, date;
                id = textIngredientID.getText();
                name = textIngredientName.getText();
                level = textStockLevel.getText();
                threshold = textStockThreshold.getText();
                date = textDeliveryArrivalDate.getText();
                try {
                    String sql = "SELECT * FROM Stock WHERE 1=1";
                    if (! id.isEmpty()) sql += " AND ingredientID = ?";
                    if (! name.isEmpty()) sql += " AND ingredientName = ?";
                    if (! level.isEmpty()) sql += " AND stockLevel = ?";
                    if (! threshold.isEmpty()) sql += " AND stockThreshold = ?";
                    if (! date.isEmpty()) sql += " AND deliveryArrivalDate = ?";

                    int parameterIndex = 1;
                    if (! id.isEmpty()) pstm.setString(parameterIndex++, id);
                    if (! name.isEmpty()) pstm.setString(parameterIndex++, name);
                    if (! level.isEmpty()) pstm.setString(parameterIndex++, level);
                    if (! threshold.isEmpty()) pstm.setString(parameterIndex++, threshold);
                    if (! date.isEmpty()) pstm.setString(parameterIndex++, date);

                    pstm = connection.prepareStatement(sql);
                    resultSet = pstm.executeQuery();
                    stockTable.setModel(DbUtils.resultSetToTableModel(resultSet));
                    if (parameterIndex == 1) JOptionPane.showMessageDialog(null, "Enter values to search for...");
                    else JOptionPane.showMessageDialog(null, "Stock Searched...");
                    clearTextFields();
                } catch (SQLException e8) {
                    e8.printStackTrace();
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id, name, level, threshold, date;
                id = textIngredientID.getText();
                name = textIngredientName.getText();
                level = textStockLevel.getText();
                threshold = textStockThreshold.getText();
                date = textDeliveryArrivalDate.getText();

                try {
                    String sql = "UPDATE Stock SET ";
                    //identifier
                    if (!id.isEmpty()) sql += "ingredientID = '" + id + "'";
                    else if (!name.isEmpty()) sql += "ingredientName = '" + name + "'";
                    else {
                        JOptionPane.showMessageDialog(null, "Enter ingredientID or ingredientName to update..."); return;
                    }

                    if (!level.isEmpty()) sql += ", stockLevel = '" + level + "'";
                    if (!threshold.isEmpty()) sql += ", stockThreshold = '" + threshold + "'";
                    if (!date.isEmpty())
                        if(date.contentEquals("NULL")) sql += ", deliveryArrivalDate = " + date;
                        else sql += ", deliveryArrivalDate = '" + date + "'";
                    sql += " WHERE ";

                    if (!id.isEmpty()) sql += "ingredientID = '" + id + "'";
                    else sql += "ingredientName = '" + name + "'";

                    pstm = connection.prepareStatement(sql);
                    int rowsAffected = pstm.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Stock Updated!");
                        System.out.println("Stock Updated...");
                        loadStockTable();
                    } else
                        JOptionPane.showMessageDialog(null, "FAILED: Stock Update");

                    clearTextFields();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //Menu Side
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  showDashboardPanel();
            }
        });
        menuManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //   openMenuManagementWindow();
            }
        });
        inventoryManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // openInventoryManagementWindow();
            }
        });
        ordersAndServicesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  openOrdersAndServicesWindow();
            }
        });
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //   openSettingsWindow();
            }
        });
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // performSignIn();
            }
        });
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  performSignOut();
            }
        });
    }

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

    private void loadStockTable() {
        if (connection == null) {
            System.out.println("Database connection is not established");
        } else {
            try {
                String query = "SELECT * FROM Stock";
                pstm = connection.prepareStatement(query);
                resultSet = pstm.executeQuery();
                stockTable.setModel(DbUtils.resultSetToTableModel(resultSet));
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: Failed to load stock data.");
            }
        }
    }

    public void clearTextFields(){
        textIngredientID.setText("");
        textIngredientName.setText("");
        textStockLevel.setText("");
        textStockThreshold.setText("");
        textDeliveryArrivalDate.setText("");
    }
}