package Kitchen;

import SaharTicketOrders.Orders;
import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class Waste implements ActionListener {
    private JPanel MenuPanel;

    private JFrame j;
    private JLabel logo;
    private JButton dashboardButton;
    private JButton menuManagementButton;
    private JButton StockManagementButton;
    private JButton ordersAndServicesButton;
    private JButton settingsButton;
    private JButton signInButton;
    private JButton signOutButton;
    private JPanel WastePanel;
    private JLabel IngredientID;
    private JLabel Quantity;
    private JLabel Reason;
    private JLabel Date;
    private JTextField textIngredientID;
    private JTextField textQuantity;
    private JComboBox <String> textReason;
    private JTextField textDate;
    private JTextField textSearch;
    private JButton searchButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton saveButton;
    private JButton deleteButton;
    private JTable table_1;
    private JScrollPane TableScrollPane;
    private JTable wasteTable;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    Statement stmt = null;
    int chef = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Waste");
        frame.setContentPane(new Waste().WastePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    void table_load(){
        try{
            stmt = connection.createStatement();
            resultSet = stmt.executeQuery("SELECT * FROM Waste");
            //rs.getString("orderID");
            // pst = con.prepareStatement("select * from OrderClass");
            //  ResultSet rs = pst.executeQuery();
            table_1.setModel(DbUtils.resultSetToTableModel(resultSet));

        }
        catch(SQLException e3){e3.printStackTrace();}
    }

    public Waste() {
        connect();
        table_load();
        j = new JFrame();
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  clearFields();
                AddWasteRecord();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateWasteRecord();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveWasteRecord();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteWasteRecord();
            }
        });
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
                chef = 1;
                j.dispose();
                DishConstructionUI dish = new DishConstructionUI(chef);
            }
        });
        StockManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // openInventoryManagementWindow();
                j.dispose();
                Stock stock = new Stock();
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

     /*private void openInventoryManagementWindow() {
     JFrame inventoryFrame = new JFrame("Inventory Management");
    frame.setContentPane(new InventoryManagementPanel());
    inventoryFrame.pack();
    inventoryFrame.setVisible(true);
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

    // add button not working
    private void AddWasteRecord() {
        String ingredientID = textIngredientID.getText();
        String quantity = textQuantity.getText();
        String reason = (String) textReason.getSelectedItem();
        String date = textDate.getText();

        if (connection == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not established.");
            return;
        }

        try {
            String query = "INSERT INTO Waste (ingredientID, quantity, reason, date) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ingredientID);
            preparedStatement.setString(2, quantity);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, date);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Added to Waste record successfully!");
                clearFields();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add to Waste record.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Failed adding to Waste record.");
        }
    }

    //save button not working
    private void saveWasteRecord() {
        String ingredientID = textIngredientID.getText();
        String quantity = textQuantity.getText();
        String reason = (String) textReason.getSelectedItem();
        String date = textDate.getText();

        System.out.println("Ingredient ID: " + ingredientID);
        System.out.println("Quantity: " + quantity);
        System.out.println("Reason: " + reason);
        System.out.println("Date: " + date);

        try {
            String query = "INSERT INTO Waste (ingredientID, quantity, reason, date) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ingredientID);
            preparedStatement.setString(2, quantity);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, date);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Waste record added successfully!");
            clearFields();
            loadTableData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Failed to add waste record.");
        }
    }

    private void updateWasteRecord() {
        String ingredientID = textIngredientID.getText();
        String quantity = textQuantity.getText();
        String reason = (String) textReason.getSelectedItem();
        String date = textDate.getText();

        try {
            String query = "UPDATE Waste SET quantity = ?, reason = ?, date = ? WHERE ingredientID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, quantity);
            preparedStatement.setString(2, reason);
            preparedStatement.setString(3, date);
            preparedStatement.setString(4, ingredientID);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Waste record updated successfully!");
            clearFields();
            loadTableData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Failed to update waste record.");
        }
    }

    private void deleteWasteRecord() {
        String ingredientID = textIngredientID.getText();

        try {
            String query = "DELETE FROM Waste WHERE ingredientID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ingredientID);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Waste record deleted successfully!");
            clearFields();
            loadTableData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Failed to delete waste record.");
        }
    }

    private void clearFields() {
        textIngredientID.setText("");
        textQuantity.setText("");
        textReason.setSelectedIndex(0);
        textDate.setText("");
    }

    //table not loading, only after updating
    private void loadTableData() {
        if (connection == null) {
            System.out.println("Database connection is not established");
            return;
        }
        try {
            String query = "SELECT * FROM Waste";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            wasteTable.setModel(DbUtils.resultSetToTableModel(resultSet));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Failed to load waste data.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==ordersAndServicesButton){
            j.dispose();
            Orders o = new Orders();
        }
    }
}

