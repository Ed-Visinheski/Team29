package KitchenToManagement;
import Kitchen.DatabaseManager;
import Kitchen.DishConstructionUI;
import Kitchen.Stock;
import Kitchen.Waste;
import SaharTicketOrders.Orders;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class KitchenManagementApp extends JFrame {
    /**
     * Constructor to set up the main frame and add tabbed panels for different functionalities.
     */
    public KitchenManagementApp() {
        // Set up the main frame
        setTitle("Kitchen Management System"); // Title of the application window
        setSize(1250, 720);  // Size of the application window
        setLocationRelativeTo(null);  // Center the application window on the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation when the window is closed
        setLayout(new BorderLayout()); // Use BorderLayout for the main frame

        // Create the tabbed pane to hold different panels
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create instances of panels for various functionalities
        DishGUI dishGUIPanel = new DishGUI(); // Panel for adding a new dish
        DishViewer dishViewerPanel = new DishViewer(); // Panel for viewing and managing dishes
        IngredientAdder ingredientAdderPanel = new IngredientAdder(); // Panel for managing ingredients
        MenuCreator menuCreatorPanel = new MenuCreator(); // Panel for creating and managing menus
        DishConstructionUI dishConstructionUIPanel = new DishConstructionUI(1); // Panel for constructing dishes
        Waste wastePanel = new Waste(); // Panel for managing kitchen waste
        Orders ordersPanel = new Orders(); // Panel for managing kitchen orders
        Stock stockPanel = new Stock(); // Panel for managing kitchen stock

        // Add tabs for each panel with corresponding tooltips
        tabbedPane.addTab("Manage Ingredients", null, ingredientAdderPanel, "Manage kitchen ingredients");
        tabbedPane.addTab("Create Menus", null, menuCreatorPanel, "Create and manage menus");
        tabbedPane.addTab("View Dishes", null, dishViewerPanel, "View and manage dishes");
        tabbedPane.addTab("Add Dish", null, dishGUIPanel, "Add a new dish");
        tabbedPane.addTab("Recipe", null, dishConstructionUIPanel, "Construct and manage dishes");
        tabbedPane.addTab("Waste Management", null, wastePanel, "Manage kitchen waste");
        tabbedPane.addTab("Orders", null, ordersPanel, "Manage kitchen orders");
        tabbedPane.addTab("Stock", null, stockPanel, "Manage kitchen stock");

        // Add the tabbed pane to the main frame
        add(tabbedPane, BorderLayout.CENTER); // Add the tabbed pane to the center of the main frame
    }

    public class Stock extends JPanel {
        private JTable stockTable;
        private JTextField textIngredientID, textIngredientName, textStockLevel, textStockThreshold, textDeliveryArrivalDate;
        private JButton searchButton, updateButton;

        /**
         * Constructor to initialize the stock management panel.
         */
        public Stock() {
            initializeUI(); // Initialize the user interface components
            loadStockTable(); // Load the stock table with data from the database
        }

        /**
         * Initializes the user interface components of the stock management panel.
         */
        private void initializeUI() {
            setLayout(new BorderLayout()); // Set the layout of the panel to BorderLayout

            JPanel formPanel = new JPanel(); // Panel for input form
            formPanel.setLayout(new GridLayout(5, 2, 5, 5)); // Grid layout with consistent spacing
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to the panel

            // Add input fields and labels to the form panel
            formPanel.add(new JLabel("Ingredient ID:"));
            textIngredientID = new JTextField();
            formPanel.add(textIngredientID);

            formPanel.add(new JLabel("Ingredient Name:"));
            textIngredientName = new JTextField();
            formPanel.add(textIngredientName);

            formPanel.add(new JLabel("Stock Level:"));
            textStockLevel = new JTextField();
            formPanel.add(textStockLevel);

            formPanel.add(new JLabel("Stock Threshold:"));
            textStockThreshold = new JTextField();
            formPanel.add(textStockThreshold);

            formPanel.add(new JLabel("Delivery Arrival Date:"));
            textDeliveryArrivalDate = new JTextField();
            formPanel.add(textDeliveryArrivalDate);

            // Buttons panel for search and update operations
            JPanel buttonPanel = new JPanel();
            searchButton = new JButton("Search");
            updateButton = new JButton("Update");

            // Attach action listeners to buttons
            searchButton.addActionListener(this::searchStock);
            updateButton.addActionListener(this::updateStock);

            buttonPanel.add(searchButton);
            buttonPanel.add(updateButton);

            // Set up the table for displaying stock information
            stockTable = new JTable();
            JScrollPane scrollPane = new JScrollPane(stockTable); // Scroll pane for the table
            scrollPane.setPreferredSize(new Dimension(500, 200)); // Set preferred size for the scroll pane

            // Add components to the panel using BorderLayout
            add(formPanel, BorderLayout.NORTH); // Add the input form panel to the top
            add(buttonPanel, BorderLayout.CENTER); // Add the buttons panel to the center
            add(scrollPane, BorderLayout.SOUTH); // Add the table scroll pane to the bottom
        }

        /**
         * Loads the stock table with data from the database.
         */
        private void loadStockTable() {
            try (Connection connection = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock")) {
                ResultSet rs = pst.executeQuery();
                stockTable.setModel(DbUtils.resultSetToTableModel(rs)); // Set the table model with data from the result set
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        /**
         * Searches for stock information based on the ingredient ID.
         *
         * @param e The action event triggered by the search button.
         */
        private void searchStock(ActionEvent e) {
            try {
                int ingredientID = Integer.parseInt(textIngredientID.getText()); // Get the ingredient ID from the input field
                if (ingredientID < 0) {
                    try {
                        Connection connection = Kitchen.DatabaseManager.getConnection();
                        PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock");
                        ResultSet rs = pst.executeQuery();
                        stockTable.setModel(DbUtils.resultSetToTableModel(rs)); // Set the table model with data from the result set
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Search for stock information based on the ingredient ID
                    try (Connection connection = Kitchen.DatabaseManager.getConnection();
                         PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock WHERE ingredientID = ?")) {
                        pst.setInt(1, ingredientID); // Set the ingredient ID parameter in the prepared statement
                        ResultSet rs = pst.executeQuery();
                        stockTable.setModel(DbUtils.resultSetToTableModel(rs)); // Set the table model with data from the result set
                    }
                }
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid Ingredient ID."); // Display error message for invalid input
                ex.printStackTrace();
            }
        }

        /**
         * Updates the stock information in the database.
         *
         * @param e The action event triggered by the update button.
         */
        private void updateStock(ActionEvent e) {
            try {
                int ingredientID = Integer.parseInt(textIngredientID.getText()); // Get the ingredient ID from the input field
                int stockLevel = Integer.parseInt(textStockLevel.getText()); // Get the stock level from the input field
                int stockThreshold = Integer.parseInt(textStockThreshold.getText()); // Get the stock threshold from the input field
                // Update stock information in the database
                try (Connection connection = Kitchen.DatabaseManager.getConnection();
                     PreparedStatement pst = connection.prepareStatement(
                             "UPDATE Stock SET ingredientName = ?, stockLevel = ?, stockThreshold = ?, deliveryArrivalDate = ? WHERE ingredientID = ?")) {
                    pst.setString(1, textIngredientName.getText()); // Set the ingredient name parameter in the prepared statement
                    pst.setInt(2, stockLevel); // Set the stock level parameter in the prepared statement
                    pst.setInt(3, stockThreshold); // Set the stock threshold parameter in the prepared statement
                    pst.setString(4, textDeliveryArrivalDate.getText()); // Set the delivery arrival date parameter in the prepared statement
                    pst.setInt(5, ingredientID); // Set the ingredient ID parameter in the prepared statement
                    int result = pst.executeUpdate(); // Execute the update operation
                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "Stock updated successfully!"); // Display success message
                        loadStockTable(); // Reload the stock table with updated data
                    } else {
                        JOptionPane.showMessageDialog(null, "No records updated. Check the input data."); // Display error message
                    }
                }
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(null, "Please ensure all fields are correctly filled."); // Display error message for invalid input
                ex.printStackTrace();
            }
        }
    }




    /**
     * Represents a panel for managing kitchen orders.
     */
    public class Orders extends JPanel {
        private JTextField txtOrderNumber, txtDishNumber, txtTableNumber, txtStatus, txtId;
        private JButton butSave, butUpdate, butDelete, butSearch;
        private JTable tableOrders;

        /**
         * Constructor to initialize the orders management panel.
         */
        public Orders() {
            initializeUI(); // Initialize the user interface components
            loadTableData(); // Load order data into the table
        }

        /**
         * Initializes the user interface components of the orders management panel.
         */
        private void initializeUI() {
            setLayout(new BorderLayout()); // Set the layout of the panel to BorderLayout
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Panel for input form
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to the panel

            // Add input fields and labels to the form panel
            formPanel.add(new JLabel("Order Number:"));
            txtOrderNumber = new JTextField();
            formPanel.add(txtOrderNumber);

            formPanel.add(new JLabel("Dish Number:"));
            txtDishNumber = new JTextField();
            formPanel.add(txtDishNumber);

            formPanel.add(new JLabel("Table Number:"));
            txtTableNumber = new JTextField();
            formPanel.add(txtTableNumber);

            formPanel.add(new JLabel("Status:"));
            txtStatus = new JTextField();
            formPanel.add(txtStatus);

            formPanel.add(new JLabel("Search by ID:"));
            txtId = new JTextField();
            formPanel.add(txtId);

            JPanel buttonPanel = new JPanel(); // Panel for buttons
            butSave = new JButton("Save");
            butUpdate = new JButton("Update");
            butDelete = new JButton("Delete");
            butSearch = new JButton("Search");

            buttonPanel.add(butSave); // Add save button
            buttonPanel.add(butUpdate); // Add update button
            buttonPanel.add(butDelete); // Add delete button
            buttonPanel.add(butSearch); // Add search button

            // Attach listeners using lambda expressions for brevity
            butSave.addActionListener(e -> saveOrder());
            butUpdate.addActionListener(e -> updateOrder());
            butDelete.addActionListener(e -> deleteOrder());
            butSearch.addActionListener(e -> searchOrder());

            tableOrders = new JTable(); // Table for displaying orders
            JScrollPane scrollPane = new JScrollPane(tableOrders); // Scroll pane for the table

            // Add components to the panel using BorderLayout
            add(formPanel, BorderLayout.NORTH); // Add the input form panel to the top
            add(buttonPanel, BorderLayout.CENTER); // Add the buttons panel to the center
            add(scrollPane, BorderLayout.SOUTH); // Add the table scroll pane to the bottom
        }

        /**
         * Loads order data into the table from the database.
         */
        private void loadTableData() {
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("SELECT * FROM OrderClass")) {
                ResultSet rs = pst.executeQuery();
                tableOrders.setModel(DbUtils.resultSetToTableModel(rs)); // Set the table model with data from the result set
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Failed to load order data: " + ex.getMessage());
            }
        }

        /**
         * Saves a new order into the database.
         */
        private void saveOrder() {
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("INSERT INTO OrderClass (orderNumber, dishNumber, tableNumber, status) VALUES (?, ?, ?, ?)")) {
                // Set values for parameters in the prepared statement
                pst.setString(1, txtOrderNumber.getText());
                pst.setString(2, txtDishNumber.getText());
                pst.setString(3, txtTableNumber.getText());
                pst.setString(4, txtStatus.getText());
                int result = pst.executeUpdate(); // Execute the insert operation
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Order added successfully!"); // Display success message
                    loadTableData(); // Reload the table with updated data
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add order."); // Display error message
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding order: " + ex.getMessage()); // Display error message
            }
        }

        /**
         * Updates an existing order in the database.
         */
        private void updateOrder() {
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("UPDATE OrderClass SET dishNumber = ?, tableNumber = ?, status = ? WHERE orderNumber = ?")) {
                // Set values for parameters in the prepared statement
                pst.setString(1, txtDishNumber.getText());
                pst.setString(2, txtTableNumber.getText());
                pst.setString(3, txtStatus.getText());
                pst.setString(4, txtOrderNumber.getText());
                int result = pst.executeUpdate(); // Execute the update operation
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Order updated successfully!"); // Display success message
                    loadTableData(); // Reload the table with updated data
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update order."); // Display error message
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating order: " + ex.getMessage()); // Display error message
            }
        }

        /**
         * Deletes an order from the database.
         */
        private void deleteOrder() {
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM OrderClass WHERE orderNumber = ?")) {
                pst.setString(1, txtOrderNumber.getText()); // Set the value for the parameter in the prepared statement
                int result = pst.executeUpdate(); // Execute the delete operation
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Order deleted successfully!"); // Display success message
                    loadTableData(); // Reload the table with updated data
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete order."); // Display error message
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting order: " + ex.getMessage()); // Display error message
            }
        }

        /**
         * Searches for orders based on the order ID.
         */
        private void searchOrder() {
            if (Integer.parseInt(txtId.getText()) < 0) {
                try (Connection conn = Kitchen.DatabaseManager.getConnection();
                     PreparedStatement pst = conn.prepareStatement("SELECT * FROM OrderClass")) {
                    ResultSet rs = pst.executeQuery();
                    tableOrders.setModel(DbUtils.resultSetToTableModel(rs)); // Set the table model with data from the result set
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error searching for order: " + ex.getMessage()); // Display error message
                }
            } else {
                // Search for orders based on the order ID
                try (Connection conn = Kitchen.DatabaseManager.getConnection();
                     PreparedStatement pst = conn.prepareStatement("SELECT * FROM OrderClass WHERE orderID = ?")) {
                    pst.setString(1, txtId.getText()); // Set the value for the parameter in the prepared statement
                    ResultSet rs = pst.executeQuery();
                    tableOrders.setModel(DbUtils.resultSetToTableModel(rs)); // Set the table model with data from the result set
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * Represents a panel for managing kitchen waste records.
     */
    public class Waste extends JPanel {
        private JPanel mainPanel;
        private JTextField textIngredientID, textQuantity, textDate;
        private JComboBox<String> comboReason;
        private JTable wasteTable;
        private JScrollPane scrollPane;
        private JButton addButton, updateButton, deleteButton;

        /**
         * Constructor to initialise the waste management panel.
         */
        public Waste() {
            initializeUI(); // Initialize the user interface components
            loadTableData(); // Load waste data into the table
        }

        /**
         * Initialises the user interface components of the waste management panel.
         */
        private void initializeUI() {
            setLayout(new BorderLayout()); // Set the layout of the panel to BorderLayout

            mainPanel = new JPanel(); // Main panel to hold all components
            mainPanel.setLayout(new BorderLayout()); // Set layout of main panel to BorderLayout

            JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // Panel for input fields
            textIngredientID = new JTextField();
            textQuantity = new JTextField();
            textDate = new JTextField();
            comboReason = new JComboBox<>(new String[]{"Expired", "Damaged", "Other"}); // Combo box for reason selection

            // Add labels and input fields to the input panel
            inputPanel.add(new JLabel("Ingredient ID:"));
            inputPanel.add(textIngredientID);
            inputPanel.add(new JLabel("Quantity:"));
            inputPanel.add(textQuantity);
            inputPanel.add(new JLabel("Reason:"));
            inputPanel.add(comboReason);
            inputPanel.add(new JLabel("Date:"));
            inputPanel.add(textDate);

            addButton = new JButton("Add");
            addButton.addActionListener(e -> {
                try {
                    addWasteRecord(); // Add action listener for adding waste record
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            updateButton = new JButton("Update");
            updateButton.addActionListener(e -> {
                try {
                    updateWasteRecord(); // Add action listener for updating waste record
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> {
                try {
                    deleteWasteRecord(); // Add action listener for deleting waste record
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            JPanel buttonPanel = new JPanel(new FlowLayout()); // Panel to hold buttons
            buttonPanel.add(addButton); // Add add button
            buttonPanel.add(updateButton); // Add update button
            buttonPanel.add(deleteButton); // Add delete button

            mainPanel.add(inputPanel, BorderLayout.NORTH); // Add input panel to the top of main panel
            mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom of main panel

            wasteTable = new JTable(); // Table for displaying waste records
            scrollPane = new JScrollPane(wasteTable); // Scroll pane for the table
            mainPanel.add(scrollPane, BorderLayout.CENTER); // Add scroll pane to the center of main panel

            add(mainPanel); // Add main panel to the Waste panel
        }

        /**
         * Adds a new waste record to the database.
         */
        private void addWasteRecord() throws SQLException {
            String ingredientID = textIngredientID.getText();
            String quantity = textQuantity.getText();
            String reason = comboReason.getSelectedItem().toString();
            String date = textDate.getText();
            Connection connection = Kitchen.DatabaseManager.getConnection(); // Get database connection
            String query = "INSERT INTO Waste (ingredientID, quantity, reason, date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                // Set values for parameters in the prepared statement
                pst.setString(1, ingredientID);
                pst.setString(2, quantity);
                pst.setString(3, reason);
                pst.setString(4, date);
                int result = pst.executeUpdate(); // Execute the insert operation
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Waste record added successfully!"); // Display success message
                    loadTableData(); // Reload the table with updated data
                    connection.close(); // Close the connection
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add waste record."); // Display error message
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding waste record."); // Display error message
            }
        }

        /**
         * Updates an existing waste record in the database.
         */
        private void updateWasteRecord() throws SQLException {
            Connection connection = Kitchen.DatabaseManager.getConnection(); // Get database connection
            String ingredientID = textIngredientID.getText();
            String quantity = textQuantity.getText();
            String reason = comboReason.getSelectedItem().toString();
            String date = textDate.getText();

            String query = "UPDATE Waste SET quantity = ?, reason = ?, date = ? WHERE ingredientID = ?";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                // Set values for parameters in the prepared statement
                pst.setString(1, quantity);
                pst.setString(2, reason);
                pst.setString(3, date);
                pst.setString(4, ingredientID);
                int result = pst.executeUpdate(); // Execute the update operation
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Waste record updated successfully!"); // Display success message
                    loadTableData(); // Reload the table with updated data
                    connection.close(); // Close the connection
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update waste record."); // Display error message
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating waste record."); // Display error message
            }
        }

        /**
         * Deletes a waste record from the database.
         */
        private void deleteWasteRecord() throws SQLException {
            Connection connection = Kitchen.DatabaseManager.getConnection(); // Get database connection
            String ingredientID = textIngredientID.getText();
            String query = "DELETE FROM Waste WHERE ingredientID = ?";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, ingredientID); // Set the value for the parameter in the prepared statement
                int result = pst.executeUpdate(); // Execute the delete operation
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Waste record deleted successfully!"); // Display success message
                    loadTableData(); // Reload the table with updated data
                    connection.close(); // Close the connection
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete waste record."); // Display error message
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting waste record."); // Display error message
            }
        }

        /**
         * Loads waste data into the table from the database.
         */
        private void loadTableData() {
            try {
                Connection connection = Kitchen.DatabaseManager.getConnection(); // Get database connection
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Waste");
                wasteTable.setModel(DbUtils.resultSetToTableModel(rs)); // Set the table model with data from the result set
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Failed to load waste data: " + ex.getMessage()); // Display error message
            }
        }
    }



    /**
     * This class represents a user interface for constructing and managing recipes.
     * It allows chefs to create, edit, save, delete, and submit recipes.
     */
    public class DishConstructionUI extends JPanel {
        // UI Components
        private JPanel draftsPanel;
        private ArrayList<JButton> draftButtons;
        private JTextField fileNameField;
        private HashMap<String, String> fileMap;
        private JButton saveButton;
        private JButton deleteButton;
        private JButton submitButton;
        private JTextArea textArea;
        private DefaultListModel<String> draftsModel;
        private DefaultListModel<String> submittedModel;
        private DefaultListModel<String> recipesModel;
        private HashMap<String, Integer> recipeIdMap = new HashMap<>();
        private JList<String> draftsList;
        private JList<String> submittedList;
        private JList<String> recipesList;
        private JScrollPane draftsScrollPane;


        private int chefID;
        private JButton recipeButton;


        /**
         * Constructor for DishConstructionUI.
         * @param chefID The ID of the chef using the UI.
         */
        public DishConstructionUI(int chefID) {
            this.chefID = chefID;
            setLayout(new BorderLayout());

            // Initialize the components that will use the buttons
            createMenuHeader(); // This should instantiate the buttons

            // Now safely set initial states of the buttons
            saveButton.setEnabled(false);
            submitButton.setEnabled(false);
            deleteButton.setEnabled(false);

            fileMap = new HashMap<>();
            draftsModel = new DefaultListModel<>();
            submittedModel = new DefaultListModel<>();
            recipesModel = new DefaultListModel<>();
            retrieveDataAndPopulateLists();

            // Create the file directory with DRAFTS, SUBMITTED, and RECIPES sections
            createFileDirectory();

            // Create the text area
            createTextArea();

            setVisible(true);
        }

        /**
         * Creates the menu header containing buttons and text fields.
         */
        private void createMenuHeader() {
            // Create a panel to hold the header components
            JPanel headerPanel = new JPanel();

            // Create the text field for entering file names
            fileNameField = new JTextField("", 20);

            // Create buttons for saving, deleting, and submitting recipes
            saveButton = new JButton("SAVE");
            deleteButton = new JButton("DELETE");
            submitButton = new JButton("SUBMIT");

            // Add the components to the header panel
            headerPanel.add(fileNameField);
            headerPanel.add(saveButton);
            headerPanel.add(submitButton);
            headerPanel.add(deleteButton);

            // Create a button for recipe-related actions and hide it initially
            recipeButton = new JButton("RECIPE");
            recipeButton.setVisible(false);
            headerPanel.add(recipeButton);

            // Define the actions for each button
            defineButtonActions();

            // Add the header panel to the top of the main panel
            add(headerPanel, BorderLayout.NORTH);
        }




        /**
         * Defines the actions for the buttons in the menu header.
         * - The saveButton ActionListener saves changes made to a recipe.
         * - The deleteButton ActionListener deletes the selected recipe.
         * - The submitButton ActionListener submits a draft recipe.
         */
        private void defineButtonActions() {
            // ActionListener for the saveButton
            saveButton.addActionListener(e -> {
                // Get the name of the selected recipe
                String selectedRecipeName = draftsList.getSelectedValue();
                // Retrieve the ID of the selected recipe
                Integer recipeId = getSelectedRecipeId();
                // Get the new recipe name entered in the fileNameField
                String newRecipeName = fileNameField.getText();
                // Check if both recipe ID and name are valid
                if (recipeId != null && !newRecipeName.isEmpty()) {
                    // Check if there's no name conflict or if it's the same record
                    if (!checkIfRecipeNameExists(newRecipeName, recipeId)) {
                        // Update the recipe name and content
                        updateRecipeNameAndContent(recipeId, newRecipeName, textArea.getText());
                    }
                } else {
                    // Display an error message if no recipe is selected or the name is empty
                    JOptionPane.showMessageDialog(this, "No recipe selected or recipe name is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // ActionListener for the deleteButton
            deleteButton.addActionListener(e -> {
                // Get the index of the selected recipe in the draftsList
                int selectedIndex = draftsList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Get the name of the selected recipe
                    String selectedRecipe = draftsModel.get(selectedIndex);
                    // Get the ID of the selected recipe
                    int recipeId = getSelectedRecipeId();
                    try (Connection connection = DatabaseManager.getConnection();
                         PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Recipe WHERE recipeID = ?")) {
                        // Set the recipe ID parameter in the prepared statement
                        pstmt.setInt(1, recipeId);
                        // Execute the delete operation
                        int deletedRows = pstmt.executeUpdate();
                        if (deletedRows > 0) {
                            // Display a success message if the recipe is deleted
                            JOptionPane.showMessageDialog(this, "Recipe deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            // Remove the recipe from the draftsModel
                            draftsModel.remove(selectedIndex);
                        } else {
                            // Display an error message if the delete operation fails
                            JOptionPane.showMessageDialog(this, "Failed to delete the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        // Handle any SQL exceptions
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error deleting the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // ActionListener for the submitButton
            submitButton.addActionListener(e -> {
                // Call the submitDraft method to submit the draft recipe
                submitDraft();
            });

            // Add other button listeners here if needed
        }


        /**
         * Checks if a recipe name already exists in the database.
         *
         * @param newRecipeName  The name of the recipe to check.
         * @param currentRecipeId The ID of the current recipe being edited.
         * @return true if the recipe name already exists and is different from the current recipe ID, false otherwise.
         */
        private boolean checkIfRecipeNameExists(String newRecipeName, Integer currentRecipeId) {
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement("SELECT recipeID FROM Recipe WHERE recipeName = ?")) {
                // Set the recipeName parameter in the prepared statement
                pstmt.setString(1, newRecipeName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    // Check if the result set has any rows
                    if (rs.next()) {
                        // Get the ID of the existing recipe with the same name
                        int existingRecipeId = rs.getInt("recipeID");
                        // Check if the existing recipe ID is different from the current recipe ID
                        if (existingRecipeId != currentRecipeId) {
                            // Display an error message if the recipe name already exists and return true
                            JOptionPane.showMessageDialog(this, "Recipe name already exists. Please choose a different name.", "Name Conflict", JOptionPane.ERROR_MESSAGE);
                            return true;
                        }
                    }
                }
            } catch (SQLException ex) {
                // Handle any SQL exceptions
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error checking for recipe name: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            // Return false if the recipe name does not exist or if an exception occurs
            return false;
        }



        /**
         * Updates the name and content of a recipe in the database.
         *
         * @param recipeId The ID of the recipe to update.
         * @param newName The new name for the recipe.
         * @param content The new content for the recipe.
         */
        private void updateRecipeNameAndContent(int recipeId, String newName, String content) {
            // Check if content is empty
            if (content == null || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Content is empty, nothing to save.", "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Always update both name and content to ensure changes are committed even if the name remains the same
            String sql = "UPDATE Recipe SET recipeName = ?, recipeFile = ? WHERE recipeID = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {

                // Set the parameters in the prepared statement
                pstmt.setString(1, newName);
                pstmt.setBytes(2, content.getBytes(StandardCharsets.UTF_8));
                pstmt.setInt(3, recipeId);

                // Execute the update query
                int updatedRows = pstmt.executeUpdate();
                if (updatedRows > 0) {
                    // Display a success message and refresh the UI after the recipe is updated
                    JOptionPane.showMessageDialog(this, "Recipe updated successfully.", "Update Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshUIAfterRecipeUpdate(recipeId, newName); // Refresh UI to reflect changes
                } else {
                    // Display an error message if no changes were made
                    JOptionPane.showMessageDialog(this, "No changes were made. Ensure the recipe ID is correct.", "Update Failure", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                // Handle any SQL exceptions
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating the recipe: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        /**
         * Refreshes the user interface after updating a recipe's name.
         *
         * @param recipeId The ID of the recipe whose name was updated.
         * @param newName The new name of the recipe.
         */
        private void refreshUIAfterRecipeUpdate(int recipeId, String newName) {
            // Update the recipeIdMap to reflect the new name
            String oldName = null;
            for (Map.Entry<String, Integer> entry : recipeIdMap.entrySet()) {
                if (entry.getValue().equals(recipeId)) {
                    oldName = entry.getKey();
                    break;
                }
            }

            // Check if the old name was found in the map
            if (oldName != null) {
                // Update the map with the new name
                recipeIdMap.remove(oldName);
                recipeIdMap.put(newName, recipeId);

                // Update the UI components (list models)
                updateListModel(draftsModel, oldName, newName);
                updateListModel(submittedModel, oldName, newName);
                updateListModel(recipesModel, oldName, newName);
            } else {
                // Display an error message if the old recipe name was not found
                JOptionPane.showMessageDialog(this, "Failed to find the old recipe name for updating.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        /**
         * Updates a given list model with a new name replacing an old name.
         *
         * @param model The DefaultListModel to update.
         * @param oldName The old name to be replaced in the list.
         * @param newName The new name to replace the old name in the list.
         */
        private void updateListModel(DefaultListModel<String> model, String oldName, String newName) {
            // Find the index of the old name in the model
            int index = model.indexOf(oldName);
            // Check if the old name exists in the model
            if (index != -1) {
                // Replace the old name with the new name at the found index
                model.set(index, newName);
            }
        }



        /**
         * Creates the file directory panel containing sections for drafts, submitted recipes, and official recipes.
         * Each section includes a list of recipes with optional add/remove buttons.
         */
        private void createFileDirectory() {
            // Create the main panel for the file directory
            JPanel directoryPanel = new JPanel();
            directoryPanel.setLayout(new BoxLayout(directoryPanel, BoxLayout.Y_AXIS));
            directoryPanel.setBackground(new Color(255, 182, 193));

            // Add sections for drafts, submitted recipes, and official recipes
            directoryPanel.add(createSectionPanel("DRAFTS", true, draftsModel));
            directoryPanel.add(createSectionPanel("SUBMITTED", false, submittedModel));
            directoryPanel.add(createSectionPanel("RECIPES", false, recipesModel));

            // Create a scroll pane for the directory panel to handle overflow
            JScrollPane directoryScrollPane = new JScrollPane(directoryPanel);
            // Set preferred size to ensure proper layout
            directoryScrollPane.setPreferredSize(new Dimension(200, getHeight()));
            // Remove border to maintain visual consistency
            directoryScrollPane.setBorder(BorderFactory.createEmptyBorder());

            // Add the scroll pane to the main UI panel on the west side
            add(directoryScrollPane, BorderLayout.WEST);
        }



        /**
         * Creates a panel for a section in the file directory.
         *
         * @param title              The title of the section.
         * @param hasAddRemoveButtons Indicates whether the section should include add/remove buttons.
         * @param model              The list model containing the items to display in the section.
         * @return A JPanel representing the section.
         */
        private JPanel createSectionPanel(String title, boolean hasAddRemoveButtons, DefaultListModel<String> model) {
            // Create a panel with BorderLayout to organize components
            JPanel sectionPanel = new JPanel(new BorderLayout());
            // Set a titled border with the specified title
            sectionPanel.setBorder(BorderFactory.createTitledBorder(title));

            // Create a JList to display the items from the provided list model
            JList<String> fileList = new JList<>(model);
            fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            // Create a scroll pane for the list to handle overflow
            JScrollPane listScroller = new JScrollPane(fileList);
            // Add the list to the center of the section panel
            sectionPanel.add(listScroller, BorderLayout.CENTER);

            // Assign the list reference based on the section title
            switch (title) {
                case "DRAFTS":
                    draftsList = fileList;
                    break;
                case "SUBMITTED":
                    submittedList = fileList;
                    break;
                case "RECIPES":
                    recipesList = fileList;
                    break;
            }

            // Set up a list selection listener to handle selection changes
            setupListSelectionListener(fileList);

            // Add add/remove buttons if specified for the "DRAFTS" section
            if (hasAddRemoveButtons) {
                JPanel buttonPanel = createButtonPanel(fileList);
                // Add the button panel to the south (bottom) of the section panel
                sectionPanel.add(buttonPanel, BorderLayout.SOUTH);
            }

            return sectionPanel;
        }


        /**
         * Sets up a list selection listener for the specified list.
         * This listener handles changes in the selection of items in the list.
         *
         * @param list The JList for which the selection listener is set up.
         */
        private void setupListSelectionListener(JList<String> list) {
            // Add a list selection listener using a lambda expression
            list.addListSelectionListener(e -> {
                // Check if the selection is not being adjusted
                if (!e.getValueIsAdjusting()) {
                    // Clear selections in other lists and update UI based on the selected item
                    clearOtherListSelections(list);
                    String selectedRecipeName = list.getSelectedValue();
                    if (selectedRecipeName != null) {
                        // Set the selected recipe name to the file name field
                        fileNameField.setText(selectedRecipeName);
                        // Load content for the selected recipe from the database
                        loadContentFromDatabase(selectedRecipeName);
                        // Update button states based on the selected list
                        updateButtonStates(list);
                    } else {
                        // Clear text area and file name field, and disable buttons if nothing is selected
                        textArea.setText("");
                        fileNameField.setText("");
                        disableButtons();
                    }
                }
            });
        }


        /**
         * Creates a panel containing "+" and "-" buttons for adding and removing items from the list.
         *
         * @param list The JList for which the buttons are created.
         * @return A JPanel containing the "+" and "-" buttons.
         */
        private JPanel createButtonPanel(JList<String> list) {
            // Create a JPanel with left-aligned FlowLayout to hold the buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            // Create "+" button
            JButton addButton = new JButton("+");
            addButton.setPreferredSize(new Dimension(20, 20));
            // Add ActionListener to handle addition of new item
            addButton.addActionListener(e -> {
                // Generate a new draft name
                String newDraftName = "New Draft " + (draftsModel.getSize() + 1);
                // Add the new draft to the database and list
                addNewDraftToDatabase(newDraftName, list);
            });

            // Create "-" button
            JButton removeButton = new JButton("-");
            removeButton.setPreferredSize(new Dimension(20, 20));
            // Add ActionListener to handle removal of selected item
            removeButton.addActionListener(e -> {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Remove the selected draft from the database and list
                    removeDraftFromDatabase(list.getModel().getElementAt(selectedIndex), selectedIndex, list);
                }
            });

            // Add buttons to the button panel
            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);

            return buttonPanel;
        }



        /**
         * Adds a new draft with the specified name to the database and updates the list.
         *
         * @param draftName The name of the new draft to be added.
         * @param list      The JList to be updated with the new draft.
         */
        private void addNewDraftToDatabase(String draftName, JList<String> list) {
            // SQL query to insert a new draft into the Recipe table
            String sql = "INSERT INTO Recipe (recipeName, chefID, recipeStatus, recipeFile) VALUES (?, ?, 'DRAFT', '')";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // Set parameters for the prepared statement
                pstmt.setString(1, draftName);  // Draft name
                pstmt.setInt(2, chefID);  // Chef ID
                // Execute the SQL statement to insert the new draft
                int insertedRows = pstmt.executeUpdate();

                // If insertion was successful
                if (insertedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            // Retrieve the generated recipe ID
                            int newRecipeId = generatedKeys.getInt(1);
                            System.out.println("New draft added with ID: " + newRecipeId);
                            // Display success message
                            JOptionPane.showMessageDialog(this, "New draft added successfully with ID: " + newRecipeId, "Success", JOptionPane.INFORMATION_MESSAGE);
                            // Update the recipe ID map and the list model with the new draft
                            recipeIdMap.put(draftName, newRecipeId);
                            ((DefaultListModel<String>) list.getModel()).addElement(draftName);
                        } else {
                            // If no ID was obtained
                            throw new SQLException("Creating draft failed, no ID obtained.");
                        }
                    }
                } else {
                    // If insertion failed
                    JOptionPane.showMessageDialog(this, "Failed to add new draft.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                // Catch and handle SQL exceptions
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding new draft: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        /**
         * Removes the selected draft from the database and updates the list.
         *
         * @param recipeName The name of the recipe to be removed.
         * @param index      The index of the recipe in the list.
         * @param list       The JList containing the recipes.
         */
        private void removeDraftFromDatabase(String recipeName, int index, JList<String> list) {
            // Retrieve the ID of the selected recipe
            int recipeId = getSelectedRecipeId();
            // SQL query to delete the recipe from the Recipe table
            String sql = "DELETE FROM Recipe WHERE recipeID = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // Set the recipe ID parameter
                pstmt.setInt(1, recipeId);
                // Execute the SQL statement to delete the recipe
                int deletedRows = pstmt.executeUpdate();
                // If deletion was successful
                if (deletedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Recipe deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Remove the recipe from the list model
                    ((DefaultListModel<String>) list.getModel()).remove(index);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        /**
         * Updates the visibility and enabled/disabled states of buttons based on the selected list.
         *
         * @param selectedList The JList that is currently selected.
         */
        private void updateButtonStates(JList<String> selectedList) {
            // Determine if the selected list is drafts or submitted
            boolean isDrafts = selectedList == draftsList;
            boolean isSubmitted = selectedList == submittedList;
            // Set visibility and enabled/disabled states of buttons based on the selected list
            saveButton.setVisible(isDrafts);
            submitButton.setVisible(isDrafts);
            deleteButton.setVisible(isDrafts);
            recipeButton.setVisible(isSubmitted);
            saveButton.setEnabled(isDrafts);
            submitButton.setEnabled(isDrafts);
            deleteButton.setEnabled(isDrafts);
            // Set text area and file name field editability based on the selected list
            textArea.setEditable(isDrafts || isSubmitted);
            fileNameField.setEditable(isDrafts);
            // Add action listener to recipe button if the selected list is submitted
            if (isSubmitted) {
                recipeButton.addActionListener(e -> moveRecipeToOfficial());
            }
        }

        /**
         * Disables buttons and clears text areas when no item is selected.
         */
        private void disableButtons() {
            saveButton.setVisible(false);
            submitButton.setVisible(false);
            deleteButton.setVisible(false);
            textArea.setEditable(false);
            fileNameField.setEditable(false);
        }


        /**
         * Clears the selections of other lists when a list item is selected.
         *
         * @param activeList The list that is currently active.
         */
        private void clearOtherListSelections(JList<String> activeList) {
            // Clear selection of drafts list if activeList is not draftsList
            if (activeList != draftsList && draftsList != null) {
                draftsList.clearSelection();
            }
            // Clear selection of submitted list if activeList is not submittedList
            if (activeList != submittedList && submittedList != null) {
                submittedList.clearSelection();
            }
            // Clear selection of recipes list if activeList is not recipesList
            if (activeList != recipesList && recipesList != null) {
                recipesList.clearSelection();
            }
        }

        /**
         * Adds "+" and "-" buttons to the button panel if the section is "DRAFTS".
         *
         * @param title        The title of the section.
         * @param sectionPanel The panel to which buttons will be added.
         * @param model        The list model associated with the section.
         * @param fileList     The JList associated with the section.
         */
        private void addButtonsIfRequired(String title, JPanel sectionPanel, DefaultListModel<String> model, JList<String> fileList) {
            // Check if the section is "DRAFTS"
            if (!"DRAFTS".equals(title)) {
                return;
            }
            // Create button panel and buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addButton = new JButton("+");
            addButton.setPreferredSize(new Dimension(20, 20));
            JButton removeButton = new JButton("-");
            removeButton.setPreferredSize(new Dimension(20, 20));

            // Add action listeners to buttons
            addButton.addActionListener(e -> {
                String newDraftName = "New Draft " + (draftsModel.getSize() + 1);
                addNewDraftToDatabase(newDraftName);
                model.addElement(newDraftName);
            });

            removeButton.addActionListener(e -> {
                int selectedIndex = fileList.getSelectedIndex();
                if (selectedIndex != -1) {
                    model.remove(selectedIndex);
                }
            });

            // Add buttons to button panel and button panel to section panel
            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            sectionPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        /**
         * Adds a new draft to the database with the provided name and updates the UI accordingly.
         *
         * @param draftName The name of the new draft.
         */
        private void addNewDraftToDatabase(String draftName) {
            // SQL query to insert a new draft into the Recipe table
            String sql = "INSERT INTO Recipe (recipeName, chefID, recipeStatus, recipeFile) VALUES (?, ?, 'DRAFT', '')";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // Set parameters for the SQL query
                pstmt.setString(1, draftName);
                pstmt.setInt(2, chefID);
                // Execute the SQL query to insert the new draft
                int insertedRows = pstmt.executeUpdate();

                if (insertedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newRecipeId = generatedKeys.getInt(1);
                            System.out.println("New draft added with ID: " + newRecipeId);
                            // Show success message
                            JOptionPane.showMessageDialog(this, "New draft added successfully with ID: " + newRecipeId, "Success", JOptionPane.INFORMATION_MESSAGE);
                            // Store the new recipe ID in the map and update the list model
                            recipeIdMap.put(draftName, newRecipeId);
                            draftsModel.addElement(draftName); // Update the list model
                        } else {
                            throw new SQLException("Creating draft failed, no ID obtained.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add new draft.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding new draft: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }



        /**
         * Loads the content of the selected recipe from the database and displays it in the text area.
         *
         * @param recipeName The name of the selected recipe.
         */
        private void loadContentFromDatabase(String recipeName) {
            // Check if the recipe name is empty or null
            if (recipeName == null || recipeName.isEmpty()) {
                textArea.setText("No recipe selected.");
                return;
            }

            // SQL query to retrieve the recipe content from the Recipe table
            String sql = "SELECT recipeFile FROM Recipe WHERE recipeName = ? AND chefID = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // Set parameters for the SQL query
                pstmt.setString(1, recipeName);
                pstmt.setInt(2, chefID);

                // Execute the SQL query
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Blob recipeBlob = rs.getBlob("recipeFile");
                        // Check if the recipe content is not null and not empty
                        if (recipeBlob != null && recipeBlob.length() > 0) {
                            byte[] bytes = recipeBlob.getBytes(1, (int) recipeBlob.length());
                            String content = new String(bytes, StandardCharsets.UTF_8);
                            textArea.setText(content);
                        } else {
                            textArea.setText("No content available for this recipe.");
                        }
                    } else {
                        textArea.setText("Recipe details not found in the database.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                textArea.setText("Failed to load recipe content.");
            }
        }


        /**
         * Submits the selected draft by updating its content and status in the database.
         * Moves the submitted draft to the "Submitted" list model.
         */
        private void submitDraft() {
            // Get the ID of the selected recipe
            int recipeId = getSelectedRecipeId();
            // Check if the recipe name exists and there is no conflict
            if (!checkIfRecipeNameExists(fileNameField.getText(), recipeId)) {
                // Get the index of the selected draft
                int selectedIndex = draftsList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Get the selected item from the drafts list
                    String selectedItem = draftsModel.getElementAt(selectedIndex);
                    // Capture the content before it might get cleared or changed
                    String content = textArea.getText();
                    // Submit the draft and check if update is successful
                    boolean updateSuccess = updateRecipeContent(selectedItem, content, true);

                    if (updateSuccess) {
                        // Add the submitted draft to the submitted list model
                        submittedModel.addElement(selectedItem);
                        // Remove the submitted draft from the drafts list model
                        draftsModel.remove(selectedIndex);
                        // Select the same item in the submitted list to load content
                        submittedList.setSelectedValue(selectedItem, true);
                        loadContentFromDatabase(selectedItem);
                    }
                }
            }
        }


        /**
         * Creates a text area component for displaying recipe content.
         */
        private void createTextArea() {
            textArea = new JTextArea();
            textArea.setBackground(Color.WHITE);
            textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane textAreaScrollPane = new JScrollPane(textArea);
            textAreaScrollPane.setPreferredSize(new Dimension(300, 400)); // Example size
            textAreaScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(textAreaScrollPane, BorderLayout.CENTER);
        }


        private void styleButton(JButton button, Color color) {
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
        }

        /**
         * Retrieves data from the database and populates the drafts, submitted, and recipes lists.
         */
        private void retrieveDataAndPopulateLists() {
            try {
                System.out.println("Retrieving data for chefID: " + chefID);
                // Establish database connection
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                // Execute SQL query to retrieve recipe data
                ResultSet resultSet = statement.executeQuery("SELECT recipeID, recipeName, recipeStatus FROM Recipe WHERE chefID = " + chefID + " OR  recipeStatus = 'RECIPE'");
                while (resultSet.next()) {
                    // Extract data from the result set
                    int id = resultSet.getInt("recipeID");
                    String name = resultSet.getString("recipeName");
                    String status = resultSet.getString("recipeStatus");
                    // Store recipe ID and name in the map
                    recipeIdMap.put(name, id);
                    // Add recipe to the appropriate list model based on its status
                    if ("DRAFT".equals(status)) {
                        draftsModel.addElement(name);
                    } else if ("SUBMITTED".equals(status)) {
                        submittedModel.addElement(name);
                    } else if ("RECIPE".equals(status)) {
                        recipesModel.addElement(name);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        /**
         * Retrieves the ID of the selected recipe from the recipe ID map.
         *
         * @return The ID of the selected recipe, or null if no recipe is selected.
         */
        private Integer getSelectedRecipeId() {
            // Get the name of the selected recipe
            String selectedRecipeName = draftsList.getSelectedValue();
            // Check if the selected recipe name exists in the map
            if (selectedRecipeName != null && recipeIdMap.containsKey(selectedRecipeName)) {
                return recipeIdMap.get(selectedRecipeName);
            }
            return null;
        }

        /**
         * Updates the content and status of a recipe in the database.
         *
         * @param recipeName   The name of the recipe to be updated.
         * @param newContent   The new content of the recipe.
         * @param isSubmitted  True if the recipe is being submitted, false otherwise.
         * @return True if the update is successful, false otherwise.
         */
        private boolean updateRecipeContent(String recipeName, String newContent, boolean isSubmitted) {
            try {
                // Establish database connection
                Connection connection = DatabaseManager.getConnection();
                // SQL query to update the recipe content and status
                PreparedStatement pstmt = connection.prepareStatement("UPDATE Recipe SET recipeFile = ?, recipeStatus = ? WHERE recipeName = ? AND chefID = ?");
                // Set parameters for the SQL query
                pstmt.setBytes(1, newContent.getBytes(StandardCharsets.UTF_8));
                pstmt.setString(2, isSubmitted ? "SUBMITTED" : "DRAFT");
                pstmt.setString(3, recipeName);
                pstmt.setInt(4, chefID);
                // Execute the SQL query
                int updatedRows = pstmt.executeUpdate();
                if (updatedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Recipe updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update the recipe. Make sure the recipe exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        /**
         * Moves the selected submitted recipe to the official recipes list.
         * Updates the status of the recipe in the database.
         */
        private void moveRecipeToOfficial() {
            // Get the name of the selected submitted recipe
            String selectedRecipeName = submittedList.getSelectedValue();
            // Get the ID of the selected recipe from the map
            Integer recipeId = recipeIdMap.get(selectedRecipeName);
            if (recipeId != null) {
                try (Connection connection = DatabaseManager.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement("UPDATE Recipe SET recipeStatus = 'RECIPE' WHERE recipeID = ?")) {
                    // Set parameter for the SQL query
                    pstmt.setInt(1, recipeId);
                    // Execute the SQL query
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(null, "Recipe moved to official recipes successfully!");
                        // Add the recipe to the official recipes list model
                        recipesModel.addElement(selectedRecipeName);
                        // Remove the recipe from the submitted list model
                        submittedModel.removeElement(selectedRecipeName);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to move recipe to official recipes.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * A GUI panel for managing dish details including dish name, photo upload, recipe selection,
     * ingredients table, and saving dish information to the database.
     */
    public class DishGUI extends JPanel {
        private JTextField dishNameField; // Text field for entering dish name
        private JTextField photoPathField; // Text field to display photo file name
        private JTable ingredientsTable; // Table for displaying ingredients
        private DefaultTableModel tableModel; // Table model for ingredients table
        private JComboBox<String> recipeComboBox; // Combo box for selecting a recipe
        private HashMap<Integer, String> recipeMap = new HashMap<>(); // Map to store recipe IDs and names

        /**
         * Constructs a new DishGUI panel with components for managing dish details.
         */
        public DishGUI() {
            setTitle("Dish Details");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // Create and configure the form panel
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
            formPanel.add(new JLabel("Dish Name:"));
            dishNameField = new JTextField();
            formPanel.add(dishNameField);

            formPanel.add(new JLabel("Select Recipe:"));
            recipeComboBox = new JComboBox<>();
            formPanel.add(recipeComboBox);
            loadRecipeBox();

            JButton photoButton = new JButton("Upload Photo");
            formPanel.add(photoButton);
            photoPathField = new JTextField();
            photoPathField.setEditable(false);
            formPanel.add(photoPathField);

            add(formPanel, BorderLayout.NORTH);

            // Create and configure the ingredients table
            String[] columnNames = {"Ingredient", "Quantity"};
            tableModel = new DefaultTableModel(columnNames, 0);
            ingredientsTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(ingredientsTable);
            add(scrollPane, BorderLayout.CENTER);

            // Create and configure the button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add Ingredient");
            JButton removeButton = new JButton("Remove Selected");
            JButton saveButton = new JButton("Save Dish");

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            buttonPanel.add(saveButton);

            add(buttonPanel, BorderLayout.SOUTH);

            // Add action listeners to buttons
            addButton.addActionListener(e -> addIngredient());
            removeButton.addActionListener(e -> removeSelectedIngredient());
            saveButton.addActionListener(e -> saveDish());
            photoButton.addActionListener(e -> uploadPhoto());

            setLocationRelativeTo(null);
        }

        /**
         * Adds a new row to the ingredients table with default values.
         */
        private void addIngredient() {
            tableModel.addRow(new Object[]{"New Ingredient", 0});
        }

        /**
         * Removes the selected row from the ingredients table.
         */
        private void removeSelectedIngredient() {
            int selectedRow = ingredientsTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        }

        /**
         * Loads recipe names into the recipe combo box from the database.
         */
        public void loadRecipeBox() {
            try {
                Connection connection = DatabaseManager.getConnection();
                String sql = "SELECT recipeID, recipeName FROM Recipe";
                PreparedStatement pstm = connection.prepareStatement(sql);
                ResultSet resultSet = pstm.executeQuery();
                int recipeID;
                String recipeName;
                while (resultSet.next()) {
                    recipeID = resultSet.getInt("recipeID");
                    recipeName = resultSet.getString("recipeName");
                    recipeMap.put(recipeID, recipeName);
                    recipeComboBox.addItem(recipeName);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Saves the dish details including name, photo, recipe, and ingredients to the database.
         */
        private void saveDish() {
            Connection connection = null;
            PreparedStatement pstm = null;
            ResultSet generatedKeys = null;
            FileInputStream fis = null;
            try {
                // Read the selected photo file
                File imageFile = new File(photoPathField.getText());
                fis = new FileInputStream(imageFile);

                // Establish database connection
                connection = DatabaseManager.getConnection();
                String sql = "INSERT INTO Dish (dishName, dishPhoto, recipeID) VALUES (?, ?, ?)";
                pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstm.setString(1, dishNameField.getText());
                pstm.setBlob(2, fis, imageFile.length());

                // Find the recipe ID corresponding to the selected recipe name
                boolean recipeFound = false;
                for (int recipeID : recipeMap.keySet()) {
                    if (recipeMap.get(recipeID).equals(recipeComboBox.getSelectedItem())) {
                        pstm.setInt(3, recipeID);
                        recipeFound = true;
                        break;
                    }
                }

                // If recipe not found, throw exception
                if (!recipeFound) {
                    throw new RuntimeException("Recipe not found");
                }

                // Execute the insert query
                int affectedRows = pstm.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating dish failed, no rows affected.");
                }

                // Get the generated dish ID
                generatedKeys = pstm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long dishID = generatedKeys.getLong(1); // Retrieve the first field of the generated keys, which is the dish ID

                    // Insert ingredients into DishIngredients table
                    while (tableModel.getRowCount() > 0) {
                        String ingredient = tableModel.getValueAt(0, 0).toString();
                        int quantity = Integer.parseInt(tableModel.getValueAt(0, 1).toString());
                        String sql1 = "SELECT ingredientID FROM Ingredients WHERE ingredientName = ?";
                        PreparedStatement pstm1 = connection.prepareStatement(sql1);
                        pstm1.setString(1, ingredient);
                        ResultSet resultSet = pstm1.executeQuery();
                        if (resultSet.next()) {
                            int ingredientID = resultSet.getInt("ingredientID");

                            String sql2 = "INSERT INTO DishIngredients (dishID, ingredientID, quantity) VALUES (?, ?, ?)";
                            PreparedStatement pstm2 = connection.prepareStatement(sql2);
                            pstm2.setLong(1, dishID);
                            pstm2.setInt(2, ingredientID);
                            pstm2.setInt(3, quantity);
                            pstm2.executeUpdate();
                            pstm2.close();
                        }
                        pstm1.close();
                        tableModel.removeRow(0);
                    }
                } else {
                    throw new SQLException("Creating dish failed, no ID obtained.");
                }

                JOptionPane.showMessageDialog(this, "Dish saved with Recipe: " + recipeComboBox.getSelectedItem(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving dish: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Close resources in finally block
                try {
                    if (generatedKeys != null) generatedKeys.close();
                    if (fis != null) fis.close();
                    if (pstm != null) pstm.close();
                    if (connection != null) connection.close();
                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * Opens a file chooser dialog for uploading a photo file and sets the selected file path to the photoPathField.
         */
        private void uploadPhoto() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp"));

            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                photoPathField.setText(file.getAbsolutePath()); // Display file path instead of image preview
            }
        }
    }


    /**
     * A GUI panel for viewing dishes, their photos, and associated recipe details.
     */
    public class DishViewer extends JPanel {
        private JTable table; // Table for displaying dish information
        private JButton deleteButton; // Button for deleting selected dish
        private HashMap<Integer, String> dishMap = new HashMap<>(); // Map to store dish IDs and names
        private JButton refreshButton; // Button for refreshing dish information
        private HashMap<Integer, String> recipeMap = new HashMap<>(); // Map to store recipe IDs and names
        private JTextArea textArea; // Text area for displaying recipe details

        /**
         * Constructs a new DishViewer panel with components for viewing dishes.
         */
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
                        openRecipeDetails(table.getValueAt(table.getSelectedRow(), 2).toString());
                    }
                    if (e.getClickCount() == 1 && table.getSelectedColumn() == 1) { // Assuming that photo is in column 1
                        openPhoto(Integer.parseInt(dishMap.keySet().toArray()[table.getSelectedRow()].toString()));
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            // Refresh button setup
            refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> loadDishes());
            buttonPanel.add(refreshButton); // Add the refresh button to the panel

            deleteButton = new JButton("Delete Dish");
            deleteButton.addActionListener(e -> deleteSelectedDish());
            buttonPanel.add(deleteButton); // Add the delete button to the panel

            add(buttonPanel, BorderLayout.SOUTH);

            // Load dishes at startup
            loadDishes();
        }

        /**
         * Loads dish information into the table from the database.
         */
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

        /**
         * Deletes the selected dish from the database.
         */
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
                        try {
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

        /**
         * Opens a photo viewer dialog to display the photo associated with the given dish ID.
         */
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

        /**
         * Displays the given image in a new JFrame.
         */
        private void displayPhoto(ImageIcon image) {
            JFrame photoFrame = new JFrame("Photo Viewer");
            JLabel label = new JLabel(image);
            photoFrame.add(label);
            photoFrame.pack();
            photoFrame.setLocationRelativeTo(null);
            photoFrame.setVisible(true);
        }

        /**
         * Opens a dialog to display recipe details based on the given recipe ID.
         */
        private void openRecipeDetails(String recipeId) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            JDialog recipeDialog = new JDialog(parentFrame, "Recipe Details", true);
            recipeDialog.setSize(500, 400);
            recipeDialog.setLocationRelativeTo(this);
            JTextArea recipeTextArea = new JTextArea();

            try (Connection connection = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pstm = connection.prepareStatement("SELECT recipeFile FROM Recipe WHERE recipeID = ?")) {
                pstm.setInt(1, Integer.parseInt(recipeId));
                try (ResultSet resultSet = pstm.executeQuery()) {
                    if (resultSet.next()) {
                        Blob recipeBlob = resultSet.getBlob("recipeFile");
                        byte[] bytes = recipeBlob.getBytes(1, (int) recipeBlob.length());
                        String content = new String(bytes, StandardCharsets.UTF_8);
                        recipeTextArea.setText(content);
                    } else {
                        recipeTextArea.setText("No recipe details found for this ID.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                recipeTextArea.setText("Failed to load recipe details: " + ex.getMessage());
            }

            JScrollPane scrollPane = new JScrollPane(recipeTextArea);
            recipeDialog.add(scrollPane);
            recipeDialog.setVisible(true);
        }
    }



    /**
     * A JPanel for managing ingredients, including adding and deleting ingredients.
     */
    public class IngredientAdder extends JPanel {
        private JTextField nameField; // Text field for entering ingredient name
        private JButton addButton; // Button for adding ingredient
        private JButton clearButton; // Button for clearing input fields
        private JButton deleteButton; // Button for deleting selected ingredient
        private JTable ingredientTable; // Table for displaying ingredients
        private DefaultTableModel tableModel; // Table model for managing ingredient data

        /**
         * Constructs a new IngredientAdder panel with components for managing ingredients.
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

            loadIngredients(); // Load ingredients from the database
        }

        /**
         * Adds a new ingredient to the database based on user input.
         *
         * @param e ActionEvent triggered by the addIngredient button
         */
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
                loadIngredients(); // Refresh the table with updated data
            } catch (SQLException ex) {
                ex.printStackTrace();
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
         * Loads ingredients from the database and populates the table.
         */
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

        /**
         * Deletes the selected ingredient from the database.
         *
         * @param e ActionEvent triggered by the deleteButton
         */
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
                    loadIngredients(); // Refresh the table with updated data
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting ingredient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * A JPanel for creating and managing menus, including adding courses, dishes, and saving menus to a database.
     */

    public class MenuCreator extends JPanel {
        private JTextField menuNameField, preparationTimeField, dateField, courseNameField;
        private JButton deleteMenuButton;
        private JTextArea descriptionArea;
        private JButton setDateButton, saveMenuButton, addCourseButton;
        private JPanel coursesPanel, detailsPanel; // Now a class member
        private List<JComboBox<String>> dishDropdowns = new ArrayList<>();
        private DefaultComboBoxModel<String> dishesModel = new DefaultComboBoxModel<>();
        private HashMap<Integer, String> dishMap = new HashMap<>();
        private JTabbedPane tabbedPane;
        private JPanel createMenuPanel;
        private JPanel viewMenuPanel;
        private JScrollPane mainScrollPane, menuScrollPane; // Added for better control
        private JSplitPane splitPane;

        /**
         * Constructs a new MenuCreator panel with components for creating and viewing menus.
         */

        public MenuCreator() {
            setTitle("Menu Creator");
            setSize(800, 800);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            tabbedPane = new JTabbedPane();

            createMenuPanel = new JPanel(new BorderLayout());
            setupCreateMenuPanel();  // This method will contain your existing setup code for creating menus

            viewMenuPanel = new JPanel(new BorderLayout());
            setupViewMenuPanel();  // This method will be defined to view saved menus

            tabbedPane.addTab("Create Menu", createMenuPanel);
            tabbedPane.addTab("View Menus", viewMenuPanel);

            add(tabbedPane, BorderLayout.CENTER);
        }


        /**
         * Setup the panel for viewing saved menus.
         */
        private void setupViewMenuPanel() {
            viewMenuPanel.setLayout(new BorderLayout());

            // Table setup
            JTable menuTable = new JTable();
            menuScrollPane = new JScrollPane(menuTable); // This will display the list of menus

            // Button setup
            deleteMenuButton = new JButton("Delete Menu");
            deleteMenuButton.addActionListener(e -> deleteSelectedMenu(menuTable));

            // Refresh button setup
            JButton refreshButton = new JButton("Refresh Menus");
            refreshButton.addActionListener(e -> loadMenusIntoTable(menuTable));

            // Panel for holding the table and delete button
            JPanel tableControlPanel = new JPanel(new BorderLayout());
            tableControlPanel.add(menuScrollPane, BorderLayout.CENTER);

            // Optionally, place the delete button at the SOUTH or NORTH of the tableControlPanel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Align button to the left
            buttonPanel.add(deleteMenuButton);
            buttonPanel.add(refreshButton); // Add the refresh button next to the delete button
            tableControlPanel.add(buttonPanel, BorderLayout.SOUTH); // Adds the button below the table

            // Details panel
            detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

            // SplitPane setup
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableControlPanel, new JScrollPane(detailsPanel));
            splitPane.setDividerLocation(300); // Adjust the initial position of the divider

            // Adding splitPane to the main panel
            viewMenuPanel.add(splitPane, BorderLayout.CENTER);

            // Load menu items into the table
            loadMenusIntoTable(menuTable);
        }

        /**
         * Deletes the selected menu from the database.
         *
         * @param table The table containing the list of menus
         */
        private void deleteSelectedMenu(JTable table) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a menu to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int menuId = (Integer) table.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this menu?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = getDatabaseConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM Menu WHERE menuId = ?")) {
                    stmt.setInt(1, menuId);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Menu deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        detailsPanel.removeAll();  // Clear details panel if the deleted menu was displayed
                        detailsPanel.revalidate();
                        detailsPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete the menu.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting menu: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        /**
         * Displays the details of a selected menu.
         *
         * @param menuId The ID of the menu to display details for
         */
        private void displayMenuDetails(int menuId) {
            detailsPanel.removeAll(); // Clear previous contents

            try (Connection conn = getDatabaseConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT courseName FROM Course WHERE menuId = ?")) {
                stmt.setInt(1, menuId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String courseName = rs.getString("courseName");
                    JLabel courseLabel = new JLabel("Course: " + courseName);
                    detailsPanel.add(courseLabel);

                    // Fetch and display dishes for the current course
                    PreparedStatement stmt2 = conn.prepareStatement("SELECT dishName FROM Dish WHERE dishID IN (SELECT dishID FROM CourseDish WHERE courseID IN (SELECT courseID FROM Course WHERE menuId = ? AND courseName = ?))");
                    stmt2.setInt(1, menuId);
                    stmt2.setString(2, courseName);
                    ResultSet rs2 = stmt2.executeQuery();
                    while (rs2.next()) {
                        String dishName = rs2.getString("dishName");
                        JLabel dishLabel = new JLabel("Dish: " + dishName);
                        detailsPanel.add(dishLabel);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading course details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }

            detailsPanel.revalidate();
            detailsPanel.repaint();
            splitPane.setRightComponent(new JScrollPane(detailsPanel));  // Ensure the updated panel is visible
            detailsPanel.revalidate();
            detailsPanel.repaint();
            splitPane.revalidate();
        }

        /**
         * Loads menus from the database into the table.
         *
         * @param table The table to load menus into
         */
        private void loadMenusIntoTable(JTable table) {
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;  // Make table cells non-editable
                }
            };
            model.addColumn("Menu ID");
            model.addColumn("Menu Name");
            model.addColumn("Menu Date");

            try (Connection conn = getDatabaseConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT menuId, menuName, menuDate FROM Menu")) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getInt("menuId"), rs.getString("menuName"), rs.getDate("menuDate")});
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading menus: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }

            table.setModel(model);
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = table.getSelectedRow();
                        if (row != -1) {
                            int menuId = (Integer) table.getValueAt(row, 0);
                            displayMenuDetails(menuId);
                        }
                    }
                }
            });
        }

        /**
         * Setup the panel for creating menus.
         */
        private void setupCreateMenuPanel() {
            JPanel topPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            addComponentsToTopPanel(topPanel);
            coursesPanel = new JPanel();
            coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
            mainScrollPane = new JScrollPane(coursesPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            mainScrollPane.setPreferredSize(new Dimension(780, 500));
            createMenuPanel.add(topPanel, BorderLayout.NORTH);
            createMenuPanel.add(mainScrollPane, BorderLayout.CENTER);
            loadDishes();
        }

        /**
         * Adds components to the top panel for creating menus.
         *
         * @param panel The panel to add components to
         */
        private void addComponentsToTopPanel(JPanel panel) {
            menuNameField = new JTextField();
            descriptionArea = new JTextArea(2, 20);
            descriptionArea.setLineWrap(true);
            JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
            descriptionScrollPane.setPreferredSize(new Dimension(200, 50));

            preparationTimeField = new JTextField();
            dateField = new JTextField(LocalDate.now().toString());
            courseNameField = new JTextField(20);

            setDateButton = new JButton("Set Date");
            setDateButton.addActionListener(this::setDate);
            saveMenuButton = new JButton("Save Menu");
            saveMenuButton.addActionListener(this::saveMenu);
            addCourseButton = new JButton("Add Course");
            addCourseButton.addActionListener(this::addCourse);

            panel.add(new JLabel("Menu Name:"));
            panel.add(menuNameField);
            panel.add(new JLabel("Description:"));
            panel.add(descriptionScrollPane);
            panel.add(new JLabel("Preparation Time:"));
            panel.add(preparationTimeField);
            panel.add(new JLabel("Menu Date (YYYY-MM-DD):"));
            panel.add(dateField);
            panel.add(new JLabel("Course Name:"));
            panel.add(courseNameField);
            panel.add(addCourseButton);
            panel.add(saveMenuButton);
        }


        /**
         * Saves a new menu to the database.
         *
         * @param actionEvent The ActionEvent triggered by clicking the save menu button
         */
        private void saveMenu(ActionEvent actionEvent) {
            String menuName = menuNameField.getText();
            String description = descriptionArea.getText();
            String preparationTime = preparationTimeField.getText();
            String date = dateField.getText();

            if (menuName.isEmpty() || description.isEmpty() || preparationTime.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = getDatabaseConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO Menu (menuName, menuDescription, menuDate, menuStatus,preparationTime ) VALUES (?, ?, ?, ?,?)",
                         Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, menuName);
                stmt.setString(2, description);
                stmt.setInt(5, Integer.parseInt(preparationTime));
                stmt.setBoolean(4, true);
                stmt.setDate(3, Date.valueOf(LocalDate.parse(date)));

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    JOptionPane.showMessageDialog(this, "Creating menu failed, no rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int menuID = generatedKeys.getInt(1);
                        saveCourses(menuID);
                    } else {
                        JOptionPane.showMessageDialog(this, "Creating menu failed, no ID obtained.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving menu: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        /**
         * Saves courses associated with a menu to the database.
         *
         * @param menuID The ID of the menu to save courses for
         */

        private void saveCourses(int menuID) {
            try (Connection conn = getDatabaseConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO Course (courseName, menuId) VALUES (?, ?)",
                         Statement.RETURN_GENERATED_KEYS)) {
                for (Component component : coursesPanel.getComponents()) {
                    if (component instanceof JPanel) {
                        JPanel coursePanel = (JPanel) component;
                        String courseName = ((TitledBorder) coursePanel.getBorder()).getTitle();
                        stmt.setString(1, courseName);
                        stmt.setInt(2, menuID);

                        int affectedRows = stmt.executeUpdate();
                        if (affectedRows == 0) {
                            JOptionPane.showMessageDialog(this, "Creating course failed, no rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int courseID = generatedKeys.getInt(1);
                                System.out.println(courseID);
                                saveDishes(coursePanel, courseID);
                            } else {
                                JOptionPane.showMessageDialog(this, "Creating course failed, no ID obtained.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Error saving courses: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving courses: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        /**
         * Saves dishes associated with a course to the database.
         *
         * @param coursePanel The panel containing course information
         * @param courseID    The ID of the course to save dishes for
         */

        private void saveDishes(JPanel coursePanel, int courseID) {
            try (Connection conn = getDatabaseConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO CourseDish (courseID, dishID) VALUES (?, ?)")) {

                // Iterate through components in the coursePanel, looking for a JComboBox
                for (Component component : coursePanel.getComponents()) {
                    if (component instanceof JComboBox) {
                        JComboBox<String> dishDropdown = (JComboBox<String>) component;
                        String selectedDish = (String) dishDropdown.getSelectedItem();
                        Integer dishID = null;

                        // Iterate through the dishMap to find the dishID of the selected dish
                        for (Map.Entry<Integer, String> entry : dishMap.entrySet()) {
                            if (entry.getValue().equals(selectedDish)) {
                                dishID = entry.getKey();
                                break;
                            }
                        }

                        if (dishID != null) {
                            stmt.setInt(1, courseID);
                            stmt.setInt(2, dishID);
                            stmt.executeUpdate();
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving dishes: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Sets the date for the menu.
         *
         * @param actionEvent The ActionEvent triggered by clicking the set date button
         */


        private void setDate(ActionEvent actionEvent) {
        }
        /**
         * Loads dishes from the database into the dishesModel.
         */

        private void loadDishes() {
            try (Connection conn = getDatabaseConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT dishID, dishName FROM Dish")) {
                while (rs.next()) {
                    dishMap.put(rs.getInt("dishID"), rs.getString("dishName"));
                    System.out.println(rs.getString("dishName"));
                    System.out.println(rs.getInt("dishID"));
                    dishesModel.addElement(rs.getString("dishName"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading dishes: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Adds a new course to the menu.
         *
         * @param e The ActionEvent triggered by clicking the add course button
         */


        private void addCourse(ActionEvent e) {
            String courseName = courseNameField.getText().trim();
            if (courseName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Creating a new panel for the course with a vertical layout
            JPanel coursePanel = new JPanel();
            coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
            coursePanel.setBorder(BorderFactory.createTitledBorder(courseName));
            coursePanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure alignment for aesthetics

            // Dropdown for selecting dishes, assuming dishesModel is populated with dish names
            JComboBox<String> dishDropdown = new JComboBox<>(dishesModel);
            dishDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25)); // Ensure width stretches

            // Button to add a selected dish to the course
            JButton addDishButton = new JButton("Add Dish");
            addDishButton.addActionListener(ev -> addDishToCourse(coursePanel, dishDropdown));

            // Add components to the course panel
            coursePanel.add(dishDropdown);
            coursePanel.add(addDishButton);

            // Add the course panel to the main courses panel
            coursesPanel.add(coursePanel);
            coursesPanel.revalidate(); // Important for updating the layout
            coursesPanel.repaint(); // Ensure the GUI updates to display new components

            courseNameField.setText(""); // Clear the input field after adding the course

            // Ensure the newly added course is visible
            mainScrollPane.getVerticalScrollBar().setValue(mainScrollPane.getVerticalScrollBar().getMaximum());
        }
        /**
         * Adds a dish to the selected course.
         *
         * @param coursePanel  The panel representing the course
         * @param dishDropdown The dropdown containing dish options
         */


        private void addDishToCourse(JPanel coursePanel, JComboBox<String> dishDropdown) {
            String selectedDish = (String) dishDropdown.getSelectedItem();
            if (selectedDish == null) {
                JOptionPane.showMessageDialog(this, "No dish selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JLabel dishLabel = new JLabel(selectedDish);
            coursePanel.add(dishLabel);

            coursePanel.revalidate();
            coursePanel.repaint(); // Refresh the panel to show the newly added dish
        }

        /**
         * Retrieves a database connection.
         *
         * @return A connection to the database
         * @throws SQLException If a database access error occurs
         */

        private Connection getDatabaseConnection() throws SQLException {
            return Kitchen.DatabaseManager.getConnection();  // Assuming Kitchen.DatabaseManager is correctly set up
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            KitchenManagementApp frame = new KitchenManagementApp();
//            frame.setVisible(true);
//        });
//    }
}
