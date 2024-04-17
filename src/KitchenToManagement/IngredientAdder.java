package KitchenToManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

/**
 * The IngredientAdder class provides a graphical user interface for managing ingredients.
 * Users can add new ingredients, view existing ingredients, and delete ingredients from the database.
 */
public class IngredientAdder extends JFrame {
    private JTextField nameField;
    private JButton addButton;
    private JButton clearButton;
    private JButton deleteButton;
    private JTable ingredientTable;
    private DefaultTableModel tableModel;

    /**
     * Constructs an IngredientAdder object and sets up the graphical user interface.
     * It initializes components, sets their properties, and loads existing ingredients from the database.
     */
    public IngredientAdder() {
        // Frame setup
        setTitle("Manage Ingredients");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Ingredient Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        addButton = new JButton("Add Ingredient");
        addButton.addActionListener(this::addIngredient);
        inputPanel.add(addButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearFields());
        inputPanel.add(clearButton);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Name"}, 0);
        ingredientTable = new JTable(tableModel);
        ingredientTable.setPreferredScrollableViewportSize(new Dimension(350, 100));
        ingredientTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(ingredientTable);

        // Delete button
        deleteButton = new JButton("Delete Selected Ingredient");
        deleteButton.addActionListener(this::deleteIngredient);

        // Adding components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.SOUTH);

        // Load existing ingredients from the database
        loadIngredients();
    }

    /**
     * Handles the action of adding a new ingredient.
     * It retrieves the ingredient name from the text field, inserts it into the database,
     * and updates the table to reflect the addition.
     * @param e The ActionEvent object representing the add ingredient action.
     */
    private void addIngredient(ActionEvent e) {
        String name = nameField.getText().trim();

        // Check if ingredient name is empty
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO Ingredients (ingredientName) VALUES (?)")) {
            // Insert the new ingredient into the database
            stmt.setString(1, name);
            stmt.executeUpdate();
            // Show success message and refresh ingredients list
            JOptionPane.showMessageDialog(this, "Ingredient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadIngredients();
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Show error message if adding ingredient fails
            JOptionPane.showMessageDialog(this, "Error adding ingredient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears the input fields.
     */
    private void clearFields() {
        nameField.setText("");
    }

    /**
     * Loads existing ingredients from the database and populates the table.
     */
    private void loadIngredients() {
        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ingredientID, ingredientName FROM Ingredients")) {
            // Clear existing data in the table
            tableModel.setRowCount(0);
            // Populate the table with ingredients from the result set
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("ingredientID"), rs.getString("ingredientName")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Show error message if loading ingredients fails
            JOptionPane.showMessageDialog(this, "Error loading ingredients: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action of deleting a selected ingredient.
     * It retrieves the selected ingredient ID from the table, deletes the ingredient from the database,
     * and updates the table to reflect the deletion.
     * @param e The ActionEvent object representing the delete ingredient action.
     */
    private void deleteIngredient(ActionEvent e) {
        int row = ingredientTable.getSelectedRow();
        // Check if a row is selected
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion with user
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this ingredient?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Get the ID of the selected ingredient from the table
            int ingredientId = (Integer) ingredientTable.getValueAt(row, 0); // Assumes ID is in column 0
            try (Connection connection = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("DELETE FROM Ingredients WHERE ingredientID = ?")) {
                // Delete the ingredient from the database
                stmt.setInt(1, ingredientId);
                stmt.executeUpdate();
                // Show success message and refresh ingredients list
                JOptionPane.showMessageDialog(this, "Ingredient deleted successfully.", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                loadIngredients(); // Refresh the list
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Show error message if deletion fails
                JOptionPane.showMessageDialog(this, "Error deleting ingredient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @deprecated
     * The main method that starts the application.
     * It creates an instance of IngredientAdder and sets its visibility to true.
     * @param args The command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IngredientAdder().setVisible(true));
    }
}
