package Kitchen;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class waste {
    private JPanel WastePanel;
    private JLabel IngredientID;
    private JLabel Quantity;
    private JLabel Reason;
    private JLabel Date;
    private JTextField textId;
    private JTextField textQuantity;
    private JTextField textDate;
    private JComboBox <String> textReason;
    private JButton buttonAdd;
    private JButton buttonUpdate;
    private JButton buttonSave;
    private JButton buttonDelete;
    private JTable table;
    private JLabel img;
    private JButton dashboardButton;
    private JButton menuManagementButton;
    private JButton inventoryManagementButton;
    private JButton ordersAndServicesButton;
    private JButton settingsButton;
    private JButton signInButton;
    private JButton signOutButton;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Waste");
        frame.setContentPane(new waste().WastePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public waste() {
        connect();
        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              //  clearFields();
                AddWasteRecord();
            }
        });
        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateWasteRecord();
            }
        });
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveWasteRecord();
            }
        });
        buttonDelete.addActionListener(new ActionListener() {
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
        String ingredientID = textId.getText();
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
        String ingredientID = textId.getText();
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
        String ingredientID = textId.getText();
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
        String ingredientID = textId.getText();

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
        textId.setText("");
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

            table.setModel(DbUtils.resultSetToTableModel(resultSet));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Failed to load waste data.");
        }
    }
}
