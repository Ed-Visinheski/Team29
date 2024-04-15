package SaharTicketOrders;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Arrays;

public class Orders {
    private JFrame frame;
    private JTextField txtOrderNumber, txtDishNumber, txtTableNumber, txtStatus, txtId;
    private JButton butSave, butUpdate, butDelete, butSearch;
    private JTable tableOrders;

    public Orders() {
        initializeUI();
        loadTableData();
    }

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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Orders::new);
    }

}