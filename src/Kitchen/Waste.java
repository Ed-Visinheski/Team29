package Kitchen;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class Waste extends JFrame {
    private JFrame frame;
    private JPanel mainPanel;
    private JTextField textIngredientID, textQuantity, textDate;
    private JComboBox<String> comboReason;
    private JTable wasteTable;
    private JScrollPane scrollPane;
    private JButton addButton, updateButton, deleteButton;
    private Connection connection;

    public Waste() {
        initializeUI();
        connectDatabase();
        loadTableData();
    }

    private void initializeUI() {
        frame = new JFrame("Waste Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        textIngredientID = new JTextField();
        textQuantity = new JTextField();
        textDate = new JTextField();
        comboReason = new JComboBox<>(new String[]{"Expired", "Damaged", "Other"});

        inputPanel.add(new JLabel("Ingredient ID:"));
        inputPanel.add(textIngredientID);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(textQuantity);
        inputPanel.add(new JLabel("Reason:"));
        inputPanel.add(comboReason);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(textDate);

        addButton = new JButton("Add");
        addButton.addActionListener(e -> addWasteRecord());
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateWasteRecord());
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteWasteRecord());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        wasteTable = new JTable();
        scrollPane = new JScrollPane(wasteTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private void connectDatabase() {
        try {
            Connection conn = Kitchen.DatabaseManager.getConnection();
            System.out.println("Connected to database successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + ex.getMessage());
        }
    }

    private void addWasteRecord() {
        String ingredientID = textIngredientID.getText();
        String quantity = textQuantity.getText();
        String reason = comboReason.getSelectedItem().toString();
        String date = textDate.getText();

        String query = "INSERT INTO Waste (ingredientID, quantity, reason, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, ingredientID);
            pst.setString(2, quantity);
            pst.setString(3, reason);
            pst.setString(4, date);
            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Waste record added successfully!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add waste record.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding waste record.");
        }
    }

    private void updateWasteRecord() {
        String ingredientID = textIngredientID.getText();
        String quantity = textQuantity.getText();
        String reason = comboReason.getSelectedItem().toString();
        String date = textDate.getText();

        String query = "UPDATE Waste SET quantity = ?, reason = ?, date = ? WHERE ingredientID = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, quantity);
            pst.setString(2, reason);
            pst.setString(3, date);
            pst.setString(4, ingredientID);
            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Waste record updated successfully!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update waste record.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating waste record.");
        }
    }

    private void deleteWasteRecord() {
        String ingredientID = textIngredientID.getText();
        String query = "DELETE FROM Waste WHERE ingredientID = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, ingredientID);
            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Waste record deleted successfully!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete waste record.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting waste record.");
        }
    }

    private void loadTableData() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Waste");
            wasteTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Failed to load waste data: " + ex.getMessage());
        }
    }

}
