package KitchenToManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * The DishViewer class represents a graphical user interface for viewing and managing dishes.
 * It extends JFrame to create a window with a table displaying dish information.
 */
public class DishViewer extends JFrame {
    private JTable table;
    private JButton deleteButton;
    private HashMap<Integer, String> dishMap = new HashMap<>();
    private JButton refreshButton;
    private HashMap<Integer, String> recipeMap = new HashMap<>();
    private JTextArea textArea;

    /**
     * Constructs a DishViewer object and sets up the graphical user interface.
     * It initializes components, sets their properties, and loads dishes from the database.
     */
    public DishViewer() {
        // Set up the frame
        setTitle("Dish Viewer");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Initialize the table and button components
        table = new JTable();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Add mouse listener to the table to handle user interaction
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleTableClick(e);
            }
        });

        // Set up a scroll pane to contain the table
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Set up the refresh button and add action listener to load dishes
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDishes());
        buttonPanel.add(refreshButton);

        // Set up the delete button and add action listener to delete selected dish
        deleteButton = new JButton("Delete Dish");
        deleteButton.addActionListener(e -> deleteSelectedDish());
        buttonPanel.add(deleteButton);

        // Add the button panel to the frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Load dishes from the database at startup
        loadDishes();
    }

    /**
     * Handles mouse click events on the table.
     * It opens recipe details if the recipe column is clicked and displays the photo if the photo column is clicked.
     * @param e The MouseEvent object representing the mouse click event.
     */
    private void handleTableClick(MouseEvent e) {
        if (e.getClickCount() == 1) {
            int column = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());
            if (column == 2) { // Recipe column
                openRecipeDetails(table.getValueAt(row, 2).toString());
            } else if (column == 1) { // Photo column
                displayPhoto(Integer.parseInt(dishMap.keySet().toArray()[row].toString()));
            }
        }
    }

    /**
     * Loads dishes from the database and populates the table with the data.
     */
    private void loadDishes() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        // Define column names for the table
        columnNames.add("Dish Name");
        columnNames.add("Photo");
        columnNames.add("Recipe Name");

        try (Connection connection = Kitchen.DatabaseManager.getConnection()) {
            String sql = "SELECT dishID, dishName, recipeID FROM Dish";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                try (ResultSet rs = pstm.executeQuery()) {
                    while (rs.next()) {
                        // Store dish ID and name in a map for later reference
                        dishMap.put(rs.getInt("dishID"), rs.getString("dishName"));
                        Vector<Object> vector = new Vector<>();
                        vector.add(rs.getString("dishName"));
                        vector.add("Photo Placeholder"); // Placeholder for photo column
                        int recipeID = rs.getInt("recipeID");
                        // Fetch recipe name from the database using recipe ID
                        String sql2 = "SELECT recipeName FROM Recipe WHERE recipeID = ?";
                        try (PreparedStatement pstm2 = connection.prepareStatement(sql2)) {
                            pstm2.setInt(1, recipeID);
                            try (ResultSet rs2 = pstm2.executeQuery()) {
                                if (rs2.next()) {
                                    // Add recipe name to the vector
                                    vector.add(rs2.getString("recipeName"));
                                    // Add dish data to the table data vector
                                    data.add(vector);
                                    // Store recipe ID and name in a map for later reference
                                    recipeMap.put(recipeID, rs2.getString("recipeName"));
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Display error message if loading dishes fails
            JOptionPane.showMessageDialog(this, "Error loading dishes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Create a DefaultTableModel with the loaded data and column names
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table cells non-editable
            }
        };

        // Set the model to the table to display the data
        table.setModel(model);
    }

    /**
     * Deletes the selected dish from the database and refreshes the table.
     */
    private void deleteSelectedDish() {
        int row = table.getSelectedRow();
        if (row == -1) {
            // Display error message if no dish is selected for deletion
            JOptionPane.showMessageDialog(this, "Please select a dish to delete.", "No Dish Selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion with user
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this dish?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = Kitchen.DatabaseManager.getConnection()) {
                // Get the dish ID of the selected row
                int dishID = Integer.parseInt(dishMap.keySet().toArray()[row].toString());

                // Delete the dish from the database
                String sql = "DELETE FROM Dish WHERE dishID = ?";
                try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                    pstm.setInt(1, dishID);
                    // Execute the deletion query
                    int affectedRows = pstm.executeUpdate();
                    try {
                        // Delete dish ingredients associated with the dish
                        String sql2 = "DELETE FROM DishIngredients WHERE dishID = ?";
                        PreparedStatement pstm2 = connection.prepareStatement(sql2);
                        pstm2.setInt(1, dishID);
                        pstm2.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Display error message if deletion of dish ingredients fails
                        JOptionPane.showMessageDialog(this, "Error deleting dish ingredients: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    if (affectedRows > 0) {
                        // Display success message if deletion is successful
                        JOptionPane.showMessageDialog(this, "Dish deleted successfully.", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh the table to reflect the deletion
                        loadDishes();
                    } else {
                        // Display error message if deletion fails
                        JOptionPane.showMessageDialog(this, "Error deleting dish. No changes were made.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Display error message if deletion fails due to database error
                JOptionPane.showMessageDialog(this, "Error deleting dish: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                // Display error message if invalid dish ID is encountered
                JOptionPane.showMessageDialog(this, "Invalid dish ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a new frame to display the photo of the dish with the specified dish ID.
     * @param dishID The ID of the dish whose photo is to be displayed.
     */
    private void displayPhoto(int dishID) {
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            // Get a database connection
            connection = Kitchen.DatabaseManager.getConnection();
            // Query to retrieve dish photo from the database
            String sql = "SELECT dishPhoto FROM Dish WHERE dishID = ?";
            // Prepare the statement
            pstm = connection.prepareStatement(sql);
            pstm.setInt(1, dishID);
            // Execute the query
            rs = pstm.executeQuery();

            if (rs.next()) {
                // Get the photo data from the result set
                byte[] imgBytes = rs.getBytes("dishPhoto");
                // Create an ImageIcon from the photo data
                ImageIcon image = new ImageIcon(imgBytes);
                // Display the photo in a new frame
                displayPhotoFrame(image);
            } else {
                // Display error message if no photo is found for the dish ID
                JOptionPane.showMessageDialog(this, "Photo not found for dish ID: " + dishID, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Display error message if photo retrieval fails due to database error
            JOptionPane.showMessageDialog(this, "Error retrieving photo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close resources in finally block
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Displays the specified image in a new frame.
     * @param image The ImageIcon to be displayed.
     */
    private void displayPhotoFrame(ImageIcon image) {
        JFrame photoFrame = new JFrame("Photo Viewer");
        JLabel label = new JLabel(image);
        photoFrame.add(label);
        photoFrame.pack();
        photoFrame.setLocationRelativeTo(null);
        photoFrame.setVisible(true);
    }

    /**
     * Opens a dialog to display details of the recipe with the specified recipe ID.
     * @param recipeId The ID of the recipe whose details are to be displayed.
     */
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
