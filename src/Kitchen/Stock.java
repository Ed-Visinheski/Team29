package Kitchen;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class Stock {
    private JFrame frame;
    private JTable stockTable;
    private JTextField textIngredientID, textIngredientName, textStockLevel, textStockThreshold, textDeliveryArrivalDate;
    private JButton searchButton, updateButton;

    public Stock() {
        initializeUI();
        loadStockTable();
    }

    private void initializeUI() {
        frame = new JFrame("Stock Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10)); // Add padding between components

        // Input panel setup
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 5, 5)); // Consistent spacing between grid elements
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the panel

        inputPanel.add(new JLabel("Ingredient ID:"));
        textIngredientID = new JTextField();
        inputPanel.add(textIngredientID);

        inputPanel.add(new JLabel("Ingredient Name:"));
        textIngredientName = new JTextField();
        inputPanel.add(textIngredientName);

        inputPanel.add(new JLabel("Stock Level:"));
        textStockLevel = new JTextField();
        inputPanel.add(textStockLevel);

        inputPanel.add(new JLabel("Stock Threshold:"));
        textStockThreshold = new JTextField();
        inputPanel.add(textStockThreshold);

        inputPanel.add(new JLabel("Delivery Arrival Date:"));
        textDeliveryArrivalDate = new JTextField();
        inputPanel.add(textDeliveryArrivalDate);

        // Button panel setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchButton = new JButton("Search");
        updateButton = new JButton("Update");
        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);

        // Table setup
        stockTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(stockTable); // Ensures the table is scrollable
        scrollPane.setPreferredSize(new Dimension(700, 200)); // Set preferred size to influence layout sizing

        // Adding components to frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // Action listeners for buttons
        searchButton.addActionListener(this::searchStock);
        updateButton.addActionListener(this::updateStock);

        // Finalize frame setup
        frame.pack(); // Pack to respect preferred sizes
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    private void loadStockTable() {
        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock")) {
            ResultSet rs = pst.executeQuery();
            stockTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchStock(ActionEvent e) {
        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock WHERE ingredientID = ?")) {
            pst.setInt(1, Integer.parseInt(textIngredientID.getText()));
            ResultSet rs = pst.executeQuery();
            stockTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateStock(ActionEvent e) {
        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("UPDATE Stock SET ingredientName = ?, stockLevel = ?, stockThreshold = ?, deliveryArrivalDate = ? WHERE ingredientID = ?")) {
            pst.setString(1, textIngredientName.getText());
            pst.setInt(2, Integer.parseInt(textStockLevel.getText()));
            pst.setInt(3, Integer.parseInt(textStockThreshold.getText()));
            pst.setString(4, textDeliveryArrivalDate.getText());
            pst.setInt(5, Integer.parseInt(textIngredientID.getText()));
            pst.executeUpdate();
            loadStockTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Stock::new);
    }
}
