package KitchenToManagement;

import Kitchen.DatabaseManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

/**
 * The DishGUI class provides a graphical user interface for managing dish details.
 */
public class DishGUI extends JFrame {
    // Text field for entering the dish name
    private JTextField dishNameField;
    // Text field for displaying the selected photo file path
    private JTextField photoPathField;
    // Table for displaying ingredients
    private JTable ingredientsTable;
    // Model for managing data in the ingredients table
    private DefaultTableModel tableModel;
    // Combo box for selecting recipes
    private JComboBox<String> recipeComboBox;
    // Map for storing recipe IDs and names
    private HashMap<Integer, String> recipeMap = new HashMap<>();

    /**
     * Constructs a new DishGUI instance and sets up the graphical user interface.
     */
    public DishGUI() {
        setTitle("Dish Details"); // Set the title of the window
        setSize(600, 400); // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Specify what happens when the window is closed
        setLayout(new BorderLayout()); // Set the layout manager for the frame

        // Form Panel: Contains components for entering dish details
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Dish Name:")); // Label for the dish name field
        dishNameField = new JTextField(); // Text field for entering the dish name
        formPanel.add(dishNameField);

        formPanel.add(new JLabel("Select Recipe:")); // Label for the recipe combo box
        recipeComboBox = new JComboBox<>(); // Combo box for selecting recipes
        formPanel.add(recipeComboBox);
        loadRecipeBox(); // Load recipes into the combo box

        JButton photoButton = new JButton("Upload Photo"); // Button for uploading a photo
        formPanel.add(photoButton);
        photoPathField = new JTextField(); // Text field for displaying the selected photo file path
        photoPathField.setEditable(false); // Make the text field read-only
        formPanel.add(photoPathField);

        add(formPanel, BorderLayout.NORTH); // Add the form panel to the top of the frame

        // Ingredients Table: Displays ingredients for the dish
        String[] columnNames = {"Ingredient", "Quantity"}; // Column names for the ingredients table
        tableModel = new DefaultTableModel(columnNames, 0); // Create a default table model with specified column names
        ingredientsTable = new JTable(tableModel); // Create a table with the default table model
        JScrollPane scrollPane = new JScrollPane(ingredientsTable); // Create a scroll pane for the ingredients table
        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center of the frame

        // Button Panel: Contains buttons for adding/removing ingredients and saving the dish
        JPanel buttonPanel = new JPanel(); // Create a panel for holding buttons
        JButton addButton = new JButton("Add Ingredient"); // Button for adding an ingredient
        JButton removeButton = new JButton("Remove Selected"); // Button for removing a selected ingredient
        JButton saveButton = new JButton("Save Dish"); // Button for saving the dish

        // Add buttons to the button panel
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH); // Add the button panel to the bottom of the frame

        // Button Action Listeners: Define actions for each button
        addButton.addActionListener(e -> addIngredient()); // Add action listener for adding ingredient button
        removeButton.addActionListener(e -> removeSelectedIngredient()); // Add action listener for removing ingredient button
        saveButton.addActionListener(e -> saveDish()); // Add action listener for saving dish button
        photoButton.addActionListener(e -> uploadPhoto()); // Add action listener for uploading photo button

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    /**
     * Adds a new row to the ingredients table with default values.
     */
    private void addIngredient() {
        tableModel.addRow(new Object[]{"New Ingredient", 0}); // Add a new row to the table model with default values
    }

    /**
     * Removes the selected row from the ingredients table.
     */
    private void removeSelectedIngredient() {
        int selectedRow = ingredientsTable.getSelectedRow(); // Get the index of the selected row
        if (selectedRow != -1) { // Check if a row is selected
            tableModel.removeRow(selectedRow); // Remove the selected row from the table model
        }
    }

    /**
     * Loads recipes from the database and adds them to the recipe combo box.
     */
    public void loadRecipeBox() {
        try {
            Connection connection = DatabaseManager.getConnection(); // Get a connection to the database
            String sql = "SELECT recipeID, recipeName FROM Recipe"; // SQL query to retrieve recipe IDs and names
            PreparedStatement pstm = connection.prepareStatement(sql); // Create a prepared statement for executing the query
            ResultSet resultSet = pstm.executeQuery(); // Execute the query and get the result set
            int recipeID;
            String recipeName;
            while (resultSet.next()) { // Loop through the result set
                recipeID = resultSet.getInt("recipeID"); // Get the recipe ID from the result set
                recipeName = resultSet.getString("recipeName"); // Get the recipe name from the result set
                recipeMap.put(recipeID, recipeName); // Add the recipe ID and name to the recipe map
                recipeComboBox.addItem(recipeName); // Add the recipe name to the combo box
            }
        } catch (SQLException e) {
            throw new RuntimeException(e); // Throw a runtime exception if an SQL exception occurs
        }
    }

    /**
     * Saves the dish details to the database.
     */
    private void saveDish() {
        Connection connection = null; // Initialize a database connection variable
        PreparedStatement pstm = null; // Initialize a prepared statement variable
        ResultSet generatedKeys = null; // Initialize a result set for generated keys
        FileInputStream fis = null; // Initialize a file input stream for reading the photo file
        try {
            File imageFile = new File(photoPathField.getText()); // Get the selected photo file
            fis = new FileInputStream(imageFile); // Create a file input stream for the photo file

            connection = DatabaseManager.getConnection(); // Get a connection to the database
            String sql = "INSERT INTO Dish (dishName, dishPhoto, recipeID) VALUES (?, ?, ?)"; // SQL query to insert dish details
            pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // Create a prepared statement with generated keys
            pstm.setString(1, dishNameField.getText()); // Set the dish name parameter
            pstm.setBlob(2, fis, imageFile.length()); // Set the dish photo parameter

            boolean recipeFound = false; // Variable to track whether the recipe is found
            for (int recipeID : recipeMap.keySet()) { // Loop through recipe IDs in the recipe map
                if (recipeMap.get(recipeID).equals(recipeComboBox.getSelectedItem())) { // Check if the recipe name matches the selected item in the combo box
                    pstm.setInt(3, recipeID); // Set the recipe ID parameter
                    recipeFound = true; // Set recipe found to true
                    break; // Exit the loop
                }
            }

            if (!recipeFound) { // Check if the recipe is not found
                throw new RuntimeException("Recipe not found"); // Throw an exception if the recipe is not found
            }

            int affectedRows = pstm.executeUpdate(); // Execute the insert query and get the number of affected rows
            if (affectedRows == 0) { // Check if no rows are affected
                throw new SQLException("Creating dish failed, no rows affected."); // Throw an SQL exception
            }

            generatedKeys = pstm.getGeneratedKeys(); // Get the generated keys from the executed statement
            if (generatedKeys.next()) { // Check if there are generated keys
                long dishID = generatedKeys.getLong(1); // Get the generated dish ID

                while (tableModel.getRowCount() > 0) { // Loop through rows in the ingredients table
                    String ingredient = tableModel.getValueAt(0, 0).toString(); // Get the ingredient name from the table
                    int quantity = Integer.parseInt(tableModel.getValueAt(0, 1).toString()); // Get the ingredient quantity from the table
                    String sql1 = "SELECT ingredientID FROM Ingredients WHERE ingredientName = ?"; // SQL query to retrieve ingredient ID
                    PreparedStatement pstm1 = connection.prepareStatement(sql1); // Create a prepared statement for the query
                    pstm1.setString(1, ingredient); // Set the ingredient name parameter
                    ResultSet resultSet = pstm1.executeQuery(); // Execute the query and get the result set
                    if (resultSet.next()) { // Check if a result is found
                        int ingredientID = resultSet.getInt("ingredientID"); // Get the ingredient ID from the result set

                        String sql2 = "INSERT INTO DishIngredients (dishID, ingredientID, quantity) VALUES (?, ?, ?)"; // SQL query to insert dish ingredients
                        PreparedStatement pstm2 = connection.prepareStatement(sql2); // Create a prepared statement for the query
                        pstm2.setLong(1, dishID); // Set the dish ID parameter
                        pstm2.setInt(2, ingredientID); // Set the ingredient ID parameter
                        pstm2.setInt(3, quantity); // Set the quantity parameter
                        pstm2.executeUpdate(); // Execute the insert query for dish ingredients
                        pstm2.close(); // Close the prepared statement
                    }
                    pstm1.close(); // Close the prepared statement
                    tableModel.removeRow(0); // Remove the processed row from the table model
                }
            } else { // If no generated keys are found
                throw new SQLException("Creating dish failed, no ID obtained."); // Throw an SQL exception
            }

            JOptionPane.showMessageDialog(this, "Dish saved with Recipe: " + recipeComboBox.getSelectedItem(), "Success", JOptionPane.INFORMATION_MESSAGE); // Show success message
        } catch (Exception e) { // Catch any exceptions
            JOptionPane.showMessageDialog(this, "Error saving dish: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Show error message
        } finally { // Perform cleanup operations
            try {
                if (generatedKeys != null) generatedKeys.close(); // Close the generated keys result set
                if (fis != null) fis.close(); // Close the file input stream
                if (pstm != null) pstm.close(); // Close the prepared statement
                if (connection != null) connection.close(); // Close the database connection
            } catch (IOException | SQLException ex) {
                ex.printStackTrace(); // Print stack trace for any exceptions during cleanup
            }
        }
    }

    /**
     * Opens a file chooser dialog for uploading a photo.
     */
    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser(); // Create a file chooser dialog
        fileChooser.setAcceptAllFileFilterUsed(false); // Disable the "All Files" filter
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp")); // Add file filters for image files

        int option = fileChooser.showOpenDialog(this); // Show the file chooser dialog
        if (option == JFileChooser.APPROVE_OPTION) { // Check if a file is selected
            File file = fileChooser.getSelectedFile(); // Get the selected file
            photoPathField.setText(file.getAbsolutePath()); // Set the text field with the absolute path of the selected file
        }
    }
}
