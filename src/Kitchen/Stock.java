package Kitchen;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

/**
 * The Stock class manages the stock information for ingredients in the kitchen.
 */
public class Stock {
    private JFrame frame; // The main frame for the application
    private JTable stockTable; // Table to display stock information
    private JTextField textIngredientID;
    private JTextField textIngredientName;
    private JTextField textStockLevel;
    private JTextField textStockThreshold;
    private JTextField textDeliveryArrivalDate; // Text fields for input
    private JButton searchButton, updateButton; // Buttons for searching and updating stock

    /**
     * Constructs a new Stock object.
     */
    public Stock() {
        initializeUI(); // Initialize the user interface
        loadStockTable(); // Load the initial stock information into the table
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        frame = new JFrame("Stock Management"); // Create the main frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set close operation
        frame.setLayout(new BorderLayout(10, 10)); // Set layout with padding

        // Input panel setup
        JPanel inputPanel = new JPanel(); // Create input panel
        inputPanel.setLayout(new GridLayout(5, 2, 5, 5)); // Set grid layout
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Set border

        // Add labels and text fields for input
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Create button panel
        searchButton = new JButton("Search"); // Create search button
        updateButton = new JButton("Update"); // Create update button
        buttonPanel.add(searchButton); // Add search button
        buttonPanel.add(updateButton); // Add update button

        // Table setup
        stockTable = new JTable(); // Create table
        JScrollPane scrollPane = new JScrollPane(stockTable); // Create scroll pane for table
        scrollPane.setPreferredSize(new Dimension(700, 200)); // Set preferred size

        // Add components to frame
        frame.add(inputPanel, BorderLayout.NORTH); // Add input panel to north
        frame.add(buttonPanel, BorderLayout.CENTER); // Add button panel to center
        frame.add(scrollPane, BorderLayout.SOUTH); // Add scroll pane to south

        // Add action listeners for buttons
        searchButton.addActionListener(this::searchStock);
        updateButton.addActionListener(this::updateStock);

        // Finalize frame setup
        frame.pack(); // Pack components
        frame.setLocationRelativeTo(null); // Center frame on screen
        frame.setVisible(true); // Set frame visible
    }

    /**
     * Loads the stock information into the table from the database.
     */
    void loadStockTable() {
        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock")) {
            ResultSet rs = pst.executeQuery();
            stockTable.setModel(DbUtils.resultSetToTableModel(rs)); // Load result set into table using DbUtils
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Searches for stock information based on the entered ingredient ID.
     *
     * @param e The action event associated with the search button click.
     */
    void searchStock(ActionEvent e) {
        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock WHERE ingredientID = ?")) {
            pst.setInt(1, Integer.parseInt(textIngredientID.getText())); // Set ingredient ID parameter
            ResultSet rs = pst.executeQuery();
            stockTable.setModel(DbUtils.resultSetToTableModel(rs)); // Load result set into table using DbUtils
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates the stock information in the database.
     *
     * @param e The action event associated with the update button click.
     */
    void updateStock(ActionEvent e) {
        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("UPDATE Stock SET ingredientName = ?, stockLevel = ?, stockThreshold = ?, deliveryArrivalDate = ? WHERE ingredientID = ?")) {
            // Set parameters for update query
            pst.setString(1, textIngredientName.getText());
            pst.setInt(2, Integer.parseInt(textStockLevel.getText()));
            pst.setInt(3, Integer.parseInt(textStockThreshold.getText()));
            pst.setString(4, textDeliveryArrivalDate.getText());
            pst.setInt(5, Integer.parseInt(textIngredientID.getText()));
            pst.executeUpdate(); // Execute update query
            loadStockTable(); // Reload stock table after update
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @deprecated 
     * The main method to start the Stock Management application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Stock::new); // Start the application on the event dispatch thread
    }

    public JFrame getFrame() {
        return frame;
    }
    public JTable getStockTable() {
        return stockTable;
    }
    public JTextField getTextIngredientID() {
        return textIngredientID;
    }
    public JTextField getTextIngredientName() {
        return textIngredientName;
    }
    public JTextField getTextStockLevel() {
        return textStockLevel;
    }
    public JTextField getTextStockThreshold() {
        return textStockThreshold;
    }
    public JTextField getTextDeliveryArrivalDate() {
        return textDeliveryArrivalDate;
    }
}
