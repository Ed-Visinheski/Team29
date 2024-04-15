package KitchenToManagement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

public class DishViewer extends JFrame {
    private JTable table;
    private JButton deleteButton;
    private HashMap<Integer, String> dishMap = new HashMap<>();
    private JButton refreshButton;
    private HashMap<Integer, String> recipeMap = new HashMap<>();
    private JTextArea textArea;

    public DishViewer() {
        // Set up the frame
        setTitle("Dish Viewer");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Table setup
        table = new JTable();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && table.getSelectedColumn() == 2) { // Assuming recipeID is in column 2
                    openRecipeDetails(table.getValueAt(table.getSelectedRow(), 2).toString()); // Pass recipe ID
                }
                if (e.getClickCount() == 1 && table.getSelectedColumn() == 1) { // Assuming that photo is in column 1
                    int row = table.getSelectedRow();
                    if (row != -1) { // Check if a valid row is selected
                        for(int i : dishMap.keySet()) {
                            if(dishMap.get(i).equals(table.getValueAt(row, 0).toString())){
                                openPhoto(i);
                                break;
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh button setup
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDishes());
        buttonPanel.add(refreshButton);  // Add the refresh button to the panel

        deleteButton = new JButton("Delete Dish");
        deleteButton.addActionListener(e -> deleteSelectedDish());
        buttonPanel.add(deleteButton);  // Add the delete button to the panel

        add(buttonPanel, BorderLayout.SOUTH);

        // Load dishes at startup
        loadDishes();
    }

    private void loadDishes() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Dish Name");
        columnNames.add("Photo");
        columnNames.add("Recipe Name");

        try (Connection connection = Kitchen.DatabaseManager.getConnection()) {
            String sql = "SELECT dishID, dishName, recipeID FROM Dish";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                try (ResultSet rs = pstm.executeQuery()) {
                    while (rs.next()) {
                        dishMap.put(rs.getInt("dishID"), rs.getString("dishName"));
                        Vector<Object> vector = new Vector<>();
                        vector.add(rs.getString("dishName"));
                        vector.add("Photo Placeholder"); // Photo handling can be complex in JTable
                        int recipeID = rs.getInt("recipeID");
                        String sql2 = "SELECT recipeName FROM Recipe WHERE recipeID = ?";
                        try (PreparedStatement pstm2 = connection.prepareStatement(sql2)) {
                            pstm2.setInt(1, recipeID);
                            try (ResultSet rs2 = pstm2.executeQuery()) {
                                if (rs2.next()) {
                                    String recipeName = rs2.getString("recipeName");
                                    vector.add(recipeName);
                                    data.add(vector);
                                    recipeMap.put(recipeID, recipeName);
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading dishes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This makes the table cells non-editable
            }
        };
        table.setModel(model);
    }

    private void deleteSelectedDish() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a dish to delete.", "No Dish Selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this dish?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = Kitchen.DatabaseManager.getConnection()) {
                int dishID = Integer.parseInt(dishMap.keySet().toArray()[row].toString()); // Assumes ID mapping is correct and consistent

                String sql = "DELETE FROM Dish WHERE dishID = ?";
                try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                    pstm.setInt(1, dishID);
                    int affectedRows = pstm.executeUpdate();
                    try{
                        String sql2 = "DELETE FROM DishIngredients WHERE dishID = ?";
                        PreparedStatement pstm2 = connection.prepareStatement(sql2);
                        pstm2.setInt(1, dishID);
                        pstm2.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error deleting dish ingredients: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Dish deleted successfully.", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                        loadDishes(); // Refresh the table to reflect the deletion
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting dish. No changes were made.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting dish: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid dish ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void openPhoto(int dishID) {
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            connection = Kitchen.DatabaseManager.getConnection();
            String sql = "SELECT dishPhoto FROM Dish WHERE dishID = ?";
            pstm = connection.prepareStatement(sql);
            pstm.setInt(1, dishID);
            rs = pstm.executeQuery();

            if (rs.next()) {
                byte[] imgBytes = rs.getBytes("dishPhoto");
                ImageIcon image = new ImageIcon(imgBytes);
                displayPhoto(image);
            } else {
                JOptionPane.showMessageDialog(this, "Photo not found for dish ID: " + dishID, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving photo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void displayPhoto(ImageIcon image) {
        JFrame photoFrame = new JFrame("Photo Viewer");
        JLabel label = new JLabel(image);
        photoFrame.add(label);
        photoFrame.pack();
        photoFrame.setLocationRelativeTo(null);
        photoFrame.setVisible(true);
    }


    private void openRecipeDetails(String recipeId) {
        // For demonstration, simply show recipe ID in a new dialog
        JDialog recipeDialog = new JDialog(this, "Recipe Details", true);
        recipeDialog.setSize(500, 400);
        recipeDialog.setLocationRelativeTo(this);
        JTextArea recipeTextArea = new JTextArea();

        // Assume we fetch recipe details from database
        recipeTextArea.setText("Recipe Details for ID: " + recipeId); // Placeholder for actual recipe fetching

        JScrollPane scrollPane = new JScrollPane(recipeTextArea);
        recipeDialog.add(scrollPane);
        recipeDialog.setVisible(true);
    }

}
