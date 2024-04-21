package SaharTicketOrders;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Arrays;


/**
 * This class manages the order handling interface for a kitchen management system.
 * It provides UI elements to add, update, delete, and search orders within a database.
 */

public class Orders {
    private JFrame frame;
    private JTextField txtOrderNumber, txtDishNumber, txtTableNumber, txtStatus, txtId;
    private JButton butSave, butUpdate, butDelete, butSearch;
    private JTable tableOrders;

    /**
     * Constructor that initializes the user interface and loads initial table data.
     */

    public Orders() {
        initializeUI();
        loadTableData();
    }

    /**
     * Initializes the user interface for managing orders.
     * This method sets up the JFrame with all necessary UI components including text fields,
     * buttons, and a table to display order data.
     */

    private void initializeUI() {
        frame = new JFrame("Orders Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Use 0 to allow any number of rows
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Adding labels and text fields with labels on left and fields on right
        formPanel.add(new JLabel("Order Number:"));
        txtOrderNumber = new JTextField(10);
        formPanel.add(txtOrderNumber);

        formPanel.add(new JLabel("Dish Number:"));
        txtDishNumber = new JTextField(10);
        formPanel.add(txtDishNumber);

        formPanel.add(new JLabel("Table Number:"));
        txtTableNumber = new JTextField(10);
        formPanel.add(txtTableNumber);

        formPanel.add(new JLabel("Status:"));
        txtStatus = new JTextField(10);
        formPanel.add(txtStatus);

        formPanel.add(new JLabel("Search by ID:"));
        txtId = new JTextField(10);
        formPanel.add(txtId);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        butSave = new JButton("Save");
        butUpdate = new JButton("Update");
        butDelete = new JButton("Delete");
        butSearch = new JButton("Search");

        buttonPanel.add(butSave);
        buttonPanel.add(butUpdate);
        buttonPanel.add(butDelete);
        buttonPanel.add(butSearch);

        // Setting up the table
        tableOrders = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableOrders); // Ensure table is scrollable
        scrollPane.setPreferredSize(new Dimension(500, 200)); // Setting preferred size for scrollPane

        // Adding components to frame
        frame.add(formPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }


    /**
     * Loads data from the OrderClass table in the database and displays it in the tableOrders JTable.
     */


    private void loadTableData() {
        try {
            Connection conn = Kitchen.DatabaseManager.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM OrderClass");
            ResultSet rs = pst.executeQuery();
            tableOrders.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Failed to load order data: " + ex.getMessage());
        }
    }

    /**
     * @deprecated
     * Handles the action events for the buttons in the UI.
     *
     * @param e The ActionEvent object representing the event source and action command.
     * @throws SQLException If an SQL exception occurs while performing database operations.
     */

    private void actionPerformed(ActionEvent e) throws SQLException {
        JButton source = (JButton) e.getSource();
        if (source == butSave) {
            saveOrder();
        } else if (source == butUpdate) {
            updateOrder();
        } else if (source == butDelete) {
            deleteOrder();
        } else if (source == butSearch) {
            searchOrder();
        }
    }

    /**
     * Saves the order details entered in the text fields to the database.
     *
     * @throws SQLException If an SQL exception occurs while performing database operations.
     */
    private void saveOrder() throws SQLException {
        Connection conn = Kitchen.DatabaseManager.getConnection();
        String orderNumber = txtOrderNumber.getText();
        String dishNumber = txtDishNumber.getText();
        String tableNumber = txtTableNumber.getText();
        String status = txtStatus.getText();
        String query = "INSERT INTO OrderClass (orderNumber, dishNumber, tableNumber, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, orderNumber);
            pst.setString(2, dishNumber);
            pst.setString(3, tableNumber);
            pst.setString(4, status);
            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Order added successfully!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add order.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding order.");
        }
    }

    /**
     * Updates the order details in the database based on the order number.
     *
     * @throws SQLException If an SQL exception occurs while performing database operations.
     */

    private void updateOrder() throws SQLException {
        Connection conn = Kitchen.DatabaseManager.getConnection();
        String orderNumber = txtOrderNumber.getText();
        String dishNumber = txtDishNumber.getText();
        String tableNumber = txtTableNumber.getText();
        String status = txtStatus.getText();
        String query = "UPDATE OrderClass SET dishNumber = ?, tableNumber = ?, status = ? WHERE orderNumber = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, dishNumber);
            pst.setString(2, tableNumber);
            pst.setString(3, status);
            pst.setString(4, orderNumber);
            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Order updated successfully!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update order.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating order.");
        }
    }

    /**
     * Deletes the order from the database based on the order number.
     *
     * @throws SQLException If an SQL exception occurs while performing database operations.
     */


    private void deleteOrder() throws SQLException {
        Connection conn = Kitchen.DatabaseManager.getConnection();
        String orderNumber = txtOrderNumber.getText();
        String query = ("DELETE FROM OrderClass WHERE orderID = ?");
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, orderNumber);
            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Order deleted successfully!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete order.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting order.");
        }
    }

    /**
     * Searches for an order in the database based on the order number.
     * @throws SQLException
     */

    private void searchOrder() throws SQLException {
        Connection conn = Kitchen.DatabaseManager.getConnection();
        String orderNumber = txtId.getText();
        String query = ("SELECT * FROM OrderClass WHERE orderID = ?");
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, orderNumber);
            ResultSet rs = pst.executeQuery();
            tableOrders.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching for order.");
        }

    }

    /**
     * @deprecated
     * Main method to run the Orders class.
     * @param args Command-line arguments.
     */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Orders::new);
    }

}