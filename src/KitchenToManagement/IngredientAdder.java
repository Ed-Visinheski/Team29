package KitchenToManagement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class IngredientAdder extends JFrame {
    private JTextField nameField;
    private JButton addButton;
    private JButton clearButton;
    private JButton deleteButton;
    private JTable ingredientTable;
    private DefaultTableModel tableModel;

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

        loadIngredients();
    }

    private void addIngredient(ActionEvent e) {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO Ingredients (ingredientName) VALUES (?)")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Ingredient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadIngredients();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding ingredient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
    }

    private void loadIngredients() {
        try (Connection connection = Kitchen.DatabaseManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ingredientID, ingredientName FROM Ingredients")) {
            tableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("ingredientID"), rs.getString("ingredientName")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading ingredients: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteIngredient(ActionEvent e) {
        int row = ingredientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this ingredient?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int ingredientId = (Integer) ingredientTable.getValueAt(row, 0); // Assumes ID is in column 0
            try (Connection connection = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("DELETE FROM Ingredients WHERE ingredientID = ?")) {
                stmt.setInt(1, ingredientId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Ingredient deleted successfully.", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                loadIngredients(); // Refresh the list
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting ingredient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IngredientAdder().setVisible(true));
    }
}
