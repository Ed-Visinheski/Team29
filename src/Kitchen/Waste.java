package Kitchen;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * The Waste class manages the recording, updating, and deleting of waste records in the kitchen.
 */
public class Waste extends JFrame {
    private JFrame frame; // Frame for the application
    private JPanel mainPanel; // Main panel containing all components
    private JTextField textIngredientID, textQuantity, textDate; // Text fields for ingredient ID, quantity, and date
    private JComboBox<String> comboReason; // Combo box for selecting the reason of waste
    private JTable wasteTable; // Table to display waste records
    private JScrollPane scrollPane; // Scroll pane for the table
    private JButton addButton, updateButton, deleteButton; // Buttons for adding, updating, and deleting records
    private Connection connection; // Connection to the database

    /**
     * Constructs the Waste object and initializes the user interface.
     */
    public Waste() {
        initializeUI(); // Initialise the user interface
        connectDatabase(); // Connect to the database
        loadTableData(); // Load data into the table
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        frame = new JFrame("Waste Management"); // Set the frame title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set close operation
        frame.setSize(800, 600); // Set frame size
        frame.setLocationRelativeTo(null); // Center frame on screen

        mainPanel = new JPanel(); // Create main panel
        mainPanel.setLayout(new BorderLayout()); // Set layout for main panel

        // Create input panel with grid layout
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        textIngredientID = new JTextField(); // Ingredient ID text field
        textQuantity = new JTextField(); // Quantity text field
        textDate = new JTextField(); // Date text field
        comboReason = new JComboBox<>(new String[]{"Expired", "Damaged", "Other"}); // Reason combo box

        // Add labels and input fields to the input panel
        inputPanel.add(new JLabel("Ingredient ID:"));
        inputPanel.add(textIngredientID);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(textQuantity);
        inputPanel.add(new JLabel("Reason:"));
        inputPanel.add(comboReason);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(textDate);

        // Create buttons for adding, updating, and deleting records
        addButton = new JButton("Add");
        addButton.addActionListener(e -> addWasteRecord()); // Add action listener for add button
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateWasteRecord()); // Add action listener for update button
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteWasteRecord()); // Add action listener for delete button

        // Create button panel with flow layout
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Add input panel, button panel, and table to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        wasteTable = new JTable(); // Create table for displaying waste records
        scrollPane = new JScrollPane(wasteTable); // Create scroll pane for the table
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(mainPanel); // Set main panel as content pane of the frame
        frame.setVisible(true); // Set frame visibility
    }

    /**
     * Connects to the database.
     */
    private void connectDatabase() {
        try {
            connection = Kitchen.DatabaseManager.getConnection(); // Get connection to the database
            System.out.println("Connected to database successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + ex.getMessage());
        }
    }

    /**
     * Adds a new waste record to the database.
     */
    private void addWasteRecord() {
        String ingredientID = textIngredientID.getText(); // Get ingredient ID from text field
        String quantity = textQuantity.getText(); // Get quantity from text field
        String reason = comboReason.getSelectedItem().toString(); // Get reason from combo box
        String date = textDate.getText(); // Get date from text field

        String query = "INSERT INTO Waste (ingredientID, quantity, reason, date) VALUES (?, ?, ?, ?)"; // SQL query
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, ingredientID); // Set ingredient ID parameter
            pst.setString(2, quantity); // Set quantity parameter
            pst.setString(3, reason); // Set reason parameter
            pst.setString(4, date); // Set date parameter
            int result = pst.executeUpdate(); // Execute update query
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Waste record added successfully!");
                loadTableData(); // Reload table data
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add waste record.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding waste record.");
        }
    }

    /**
     * Updates an existing waste record in the database.
     */
    private void updateWasteRecord() {
        String ingredientID = textIngredientID.getText(); // Get ingredient ID from text field
        String quantity = textQuantity.getText(); // Get quantity from text field
        String reason = comboReason.getSelectedItem().toString(); // Get reason from combo box
        String date = textDate.getText(); // Get date from text field

        String query = "UPDATE Waste SET quantity = ?, reason = ?, date = ? WHERE ingredientID = ?"; // SQL query
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, quantity); // Set quantity parameter
            pst.setString(2, reason); // Set reason parameter
            pst.setString(3, date); // Set date parameter
            pst.setString(4, ingredientID); // Set ingredient ID parameter
            int result = pst.executeUpdate(); // Execute update query
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Waste record updated successfully!");
                loadTableData(); // Reload table data
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update waste record.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating waste record.");
        }
    }

    /**
     * Deletes a waste record from the database.
     */
    private void deleteWasteRecord() {
        String ingredientID = textIngredientID.getText(); // Get ingredient ID from text field
        String query = "DELETE FROM Waste WHERE ingredientID = ?"; // SQL query
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, ingredientID); // Set ingredient ID parameter
            int result = pst.executeUpdate(); // Execute update query
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Waste record deleted successfully!");
                loadTableData(); // Reload table data
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete waste record.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting waste record.");
        }
    }

    /**
     * Loads waste data from the database and populates the table.
     */
    private void loadTableData() {
        try {
            Statement stmt = connection.createStatement(); // Create statement
            ResultSet rs = stmt.executeQuery("SELECT * FROM Waste"); // Execute query
            wasteTable.setModel(DbUtils.resultSetToTableModel(rs)); // Set table model using ResultSet
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Failed to load waste data: " + ex.getMessage());
        }
    }
}
