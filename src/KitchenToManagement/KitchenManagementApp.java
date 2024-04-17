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
    private int chefID;
    public KitchenManagementApp() {
        // Set up the main frame
        setTitle("Kitchen Management System");
        setSize(1250, 720);  // Adjust the size based on your needs
        setLocationRelativeTo(null);  // Center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create instances of your panel classes
        DishGUI dishGUIPanel = new DishGUI();
        DishViewer dishViewerPanel = new DishViewer();
        IngredientAdder ingredientAdderPanel = new IngredientAdder();
        MenuCreator menuCreatorPanel = new MenuCreator();
        DishConstructionUI dishConstructionUIPanel = new DishConstructionUI(1);
        Waste wastePanel = new Waste();
        Orders ordersPanel = new Orders();
        Stock stockPanel = new Stock();

        // Add tabs for each panel
        tabbedPane.addTab("Manage Ingredients", null, ingredientAdderPanel, "Manage kitchen ingredients");
        tabbedPane.addTab("Create Menus", null, menuCreatorPanel, "Create and manage menus");
        tabbedPane.addTab("View Dishes", null, dishViewerPanel, "View and manage dishes");
        tabbedPane.addTab("Add Dish", null, dishGUIPanel, "Add a new dish");
        tabbedPane.addTab("Recipe", null, dishConstructionUIPanel, "Construct and manage dishes");
        tabbedPane.addTab("Waste Management", null, wastePanel, "Manage kitchen waste");
        tabbedPane.addTab("Orders", null, ordersPanel, "Manage kitchen orders");
        tabbedPane.addTab("Stock", null, stockPanel, "Manage kitchen stock");

        // Add the tabbed pane to the main frame
        add(tabbedPane, BorderLayout.CENTER);
    }

    public class Stock extends JPanel {
        private JTable stockTable;
        private JTextField textIngredientID, textIngredientName, textStockLevel, textStockThreshold, textDeliveryArrivalDate;
        private JButton searchButton, updateButton;

        public Stock() {
            initializeUI();
            loadStockTable();
        }

        private void initializeUI() {
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridLayout(5, 2, 5, 5)); // Consistent spacing between grid elements
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the panel

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

            // Buttons panel
            JPanel buttonPanel = new JPanel();
            searchButton = new JButton("Search");
            updateButton = new JButton("Update");

            // Attach action listeners to buttons
            searchButton.addActionListener(this::searchStock);
            updateButton.addActionListener(this::updateStock);

            buttonPanel.add(searchButton);
            buttonPanel.add(updateButton);

            // Setting up the table
            stockTable = new JTable();
            JScrollPane scrollPane = new JScrollPane(stockTable); // Ensure table is scrollable
            scrollPane.setPreferredSize(new Dimension(500, 200)); // Setting preferred size for scrollPane

            // Adding components to the panel
            add(formPanel, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.CENTER);
            add(scrollPane, BorderLayout.SOUTH);
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
            try {
                int ingredientID = Integer.parseInt(textIngredientID.getText());
                if(ingredientID < 0){
                    try{
                        Connection connection = Kitchen.DatabaseManager.getConnection();
                        PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock");
                        ResultSet rs = pst.executeQuery();
                        stockTable.setModel(DbUtils.resultSetToTableModel(rs));
                    }catch (SQLException ex){
                        ex.printStackTrace();
                    }
                }else{
                try (Connection connection = Kitchen.DatabaseManager.getConnection();
                     PreparedStatement pst = connection.prepareStatement("SELECT * FROM Stock WHERE ingredientID = ?")) {
                    pst.setInt(1, ingredientID);
                    ResultSet rs = pst.executeQuery();
                    stockTable.setModel(DbUtils.resultSetToTableModel(rs));
                }
            } }catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid Ingredient ID.");
                ex.printStackTrace();
            }
        }

        private void updateStock(ActionEvent e) {
            try {
                int ingredientID = Integer.parseInt(textIngredientID.getText());
                int stockLevel = Integer.parseInt(textStockLevel.getText());
                int stockThreshold = Integer.parseInt(textStockThreshold.getText());
                try (Connection connection = Kitchen.DatabaseManager.getConnection();
                     PreparedStatement pst = connection.prepareStatement(
                             "UPDATE Stock SET ingredientName = ?, stockLevel = ?, stockThreshold = ?, deliveryArrivalDate = ? WHERE ingredientID = ?")) {
                    pst.setString(1, textIngredientName.getText());
                    pst.setInt(2, stockLevel);
                    pst.setInt(3, stockThreshold);
                    pst.setString(4, textDeliveryArrivalDate.getText());
                    pst.setInt(5, ingredientID);
                    int result = pst.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "Stock updated successfully!");
                        loadStockTable();
                    } else {
                        JOptionPane.showMessageDialog(null, "No records updated. Check the input data.");
                    }
                }
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(null, "Please ensure all fields are correctly filled.");
                ex.printStackTrace();
            }
        }
    }



    public class Orders extends JPanel {
        private JTextField txtOrderNumber, txtDishNumber, txtTableNumber, txtStatus, txtId;
        private JButton butSave, butUpdate, butDelete, butSearch;
        private JTable tableOrders;

        public Orders() {
            initializeUI();
            loadTableData();
        }

        private void initializeUI() {
            setLayout(new BorderLayout());
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

            JPanel buttonPanel = new JPanel();
            butSave = new JButton("Save");
            butUpdate = new JButton("Update");
            butDelete = new JButton("Delete");
            butSearch = new JButton("Search");

            buttonPanel.add(butSave);
            buttonPanel.add(butUpdate);
            buttonPanel.add(butDelete);
            buttonPanel.add(butSearch);

            // Attach listeners using lambda expressions for brevity
            butSave.addActionListener(e -> saveOrder());
            butUpdate.addActionListener(e -> updateOrder());
            butDelete.addActionListener(e -> deleteOrder());
            butSearch.addActionListener(e -> searchOrder());

            tableOrders = new JTable();
            JScrollPane scrollPane = new JScrollPane(tableOrders);

            add(formPanel, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.CENTER);
            add(scrollPane, BorderLayout.SOUTH);
        }

        private void loadTableData() {
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("SELECT * FROM OrderClass")) {
                ResultSet rs = pst.executeQuery();
                tableOrders.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Failed to load order data: " + ex.getMessage());
            }
        }

        private void saveOrder() {
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("INSERT INTO OrderClass (orderNumber, dishNumber, tableNumber, status) VALUES (?, ?, ?, ?)")) {
                pst.setString(1, txtOrderNumber.getText());
                pst.setString(2, txtDishNumber.getText());
                pst.setString(3, txtTableNumber.getText());
                pst.setString(4, txtStatus.getText());
                int result = pst.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Order added successfully!");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add order.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding order: " + ex.getMessage());
            }
        }

        private void updateOrder() {
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("UPDATE OrderClass SET dishNumber = ?, tableNumber = ?, status = ? WHERE orderNumber = ?")) {
                pst.setString(1, txtDishNumber.getText());
                pst.setString(2, txtTableNumber.getText());
                pst.setString(3, txtStatus.getText());
                pst.setString(4, txtOrderNumber.getText());
                int result = pst.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Order updated successfully!");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update order.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating order: " + ex.getMessage());
            }
        }

        private void deleteOrder() {
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM OrderClass WHERE orderNumber = ?")) {
                pst.setString(1, txtOrderNumber.getText());
                int result = pst.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Order deleted successfully!");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete order.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting order: " + ex.getMessage());
            }
        }

        private void searchOrder() {
            if(Integer.parseInt(txtId.getText()) < 0){
                try (Connection conn = Kitchen.DatabaseManager.getConnection();
                     PreparedStatement pst = conn.prepareStatement("SELECT * FROM OrderClass")) {
                    ResultSet rs = pst.executeQuery();
                    tableOrders.setModel(DbUtils.resultSetToTableModel(rs));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error searching for order: " + ex.getMessage());
                }
            }else{
                loadTableData();
            try (Connection conn = Kitchen.DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement("SELECT * FROM OrderClass WHERE orderID = ?")) {
                pst.setString(1, txtId.getText());
                ResultSet rs = pst.executeQuery();
                tableOrders.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            }
        }
    }

    public class Waste extends JPanel {
        private JPanel mainPanel;
        private JTextField textIngredientID, textQuantity, textDate;
        private JComboBox<String> comboReason;
        private JTable wasteTable;
        private JScrollPane scrollPane;
        private JButton addButton, updateButton, deleteButton;
        private Connection connection;

        public Waste() {
            initializeUI();
            loadTableData();
        }

        private void initializeUI() {
            setLayout(new BorderLayout());

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
            addButton.addActionListener(e -> {
                try {
                    addWasteRecord();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            updateButton = new JButton("Update");
            updateButton.addActionListener(e -> {
                try {
                    updateWasteRecord();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> {
                try {
                    deleteWasteRecord();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(addButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);

            mainPanel.add(inputPanel, BorderLayout.NORTH);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            wasteTable = new JTable();
            scrollPane = new JScrollPane(wasteTable);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            add(mainPanel);
        }

        private void connectDatabase() {
            try {
                Connection conn = Kitchen.DatabaseManager.getConnection();
                System.out.println("Connected to database successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database connection failed: " + ex.getMessage());
            }
        }

        private void addWasteRecord() throws SQLException {
            String ingredientID = textIngredientID.getText();
            String quantity = textQuantity.getText();
            String reason = comboReason.getSelectedItem().toString();
            String date = textDate.getText();
            Connection connection = Kitchen.DatabaseManager.getConnection();
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
                    connection.close();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add waste record.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding waste record.");
            }
        }

        private void updateWasteRecord() throws SQLException {
            Connection connection = Kitchen.DatabaseManager.getConnection();
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
                    connection.close();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update waste record.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating waste record.");
            }
        }

        private void deleteWasteRecord() throws SQLException {
            Connection connection = Kitchen.DatabaseManager.getConnection();
            String ingredientID = textIngredientID.getText();
            String query = "DELETE FROM Waste WHERE ingredientID = ?";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, ingredientID);
                int result = pst.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Waste record deleted successfully!");
                    loadTableData();
                    connection.close();
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
                Connection connection = Kitchen.DatabaseManager.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Waste");
                wasteTable.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Failed to load waste data: " + ex.getMessage());
            }
        }

    }



    public class DishConstructionUI extends JPanel {
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


        private void createMenuHeader() {
            JPanel headerPanel = new JPanel();
            fileNameField = new JTextField("", 20);
            saveButton = new JButton("SAVE");
            deleteButton = new JButton("DELETE");
            submitButton = new JButton("SUBMIT");

            headerPanel.add(fileNameField);
            headerPanel.add(saveButton);
            headerPanel.add(submitButton);
            headerPanel.add(deleteButton);
            recipeButton = new JButton("RECIPE");
            headerPanel.add(recipeButton);
            recipeButton.setVisible(false);  // Initially hidden

            defineButtonActions();
            add(headerPanel, BorderLayout.NORTH);
        }



        private void defineButtonActions() {
            saveButton.addActionListener(e -> {
                String selectedRecipeName = draftsList.getSelectedValue();
                Integer recipeId = getSelectedRecipeId(); // This method needs to retrieve the ID based on the selected name
                String newRecipeName = fileNameField.getText();
                if (recipeId != null && !newRecipeName.isEmpty()) {
                    if (!checkIfRecipeNameExists(newRecipeName, recipeId)) { // Only update if there is no name conflict or if it's the same record
                        updateRecipeNameAndContent(recipeId, newRecipeName, textArea.getText());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No recipe selected or recipe name is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            deleteButton.addActionListener(e -> {
                int selectedIndex = draftsList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedRecipe = draftsModel.get(selectedIndex);
                    int recipeId = getSelectedRecipeId();
                    try (Connection connection = DatabaseManager.getConnection();
                         PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Recipe WHERE recipeID = ?")) {
                        pstmt.setInt(1, recipeId);

                        int deletedRows = pstmt.executeUpdate();
                        if (deletedRows > 0) {
                            JOptionPane.showMessageDialog(this, "Recipe deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            draftsModel.remove(selectedIndex);
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error deleting the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            submitButton.addActionListener(e -> {
                submitDraft();
            });
            // Add other button listeners
        }

        private boolean checkIfRecipeNameExists(String newRecipeName, Integer currentRecipeId) {
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement("SELECT recipeID FROM Recipe WHERE recipeName = ?")) {
                pstmt.setString(1, newRecipeName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int existingRecipeId = rs.getInt("recipeID");
                        if (existingRecipeId != currentRecipeId) {
                            JOptionPane.showMessageDialog(this, "Recipe name already exists. Please choose a different name.", "Name Conflict", JOptionPane.ERROR_MESSAGE);
                            return true;
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error checking for recipe name: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }


        private void updateRecipeNameAndContent(int recipeId, String newName, String content) {
            // Check if content is empty
            if (content == null || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Content is empty, nothing to save.", "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Always update both name and content to ensure changes are committed even if name remains the same
            String sql = "UPDATE Recipe SET recipeName = ?, recipeFile = ? WHERE recipeID = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {

                pstmt.setString(1, newName);
                pstmt.setBytes(2, content.getBytes(StandardCharsets.UTF_8));
                pstmt.setInt(3, recipeId);

                int updatedRows = pstmt.executeUpdate();
                if (updatedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Recipe updated successfully.", "Update Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshUIAfterRecipeUpdate(recipeId, newName); // Refresh UI to reflect changes
                } else {
                    JOptionPane.showMessageDialog(this, "No changes were made. Ensure the recipe ID is correct.", "Update Failure", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating the recipe: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void refreshUIAfterRecipeUpdate(int recipeId, String newName) {
            // Update the recipeIdMap to reflect the new name
            String oldName = null;
            for (Map.Entry<String, Integer> entry : recipeIdMap.entrySet()) {
                if (entry.getValue().equals(recipeId)) {
                    oldName = entry.getKey();
                    break;
                }
            }

            if (oldName != null) {
                // Update the map with the new name
                recipeIdMap.remove(oldName);
                recipeIdMap.put(newName, recipeId);

                // Update the UI components (list models)
                updateListModel(draftsModel, oldName, newName);
                updateListModel(submittedModel, oldName, newName);
                updateListModel(recipesModel, oldName, newName);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to find the old recipe name for updating.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateListModel(DefaultListModel<String> model, String oldName, String newName) {
            int index = model.indexOf(oldName);
            if (index != -1) {
                model.set(index, newName);
            }
        }


        private void createFileDirectory() {
            JPanel directoryPanel = new JPanel();
            directoryPanel.setLayout(new BoxLayout(directoryPanel, BoxLayout.Y_AXIS));
            directoryPanel.setBackground(new Color(255, 182, 193));

            directoryPanel.add(createSectionPanel("DRAFTS", true, draftsModel));
            directoryPanel.add(createSectionPanel("SUBMITTED", false, submittedModel));
            directoryPanel.add(createSectionPanel("RECIPES", false, recipesModel));

            JScrollPane directoryScrollPane = new JScrollPane(directoryPanel);
            directoryScrollPane.setPreferredSize(new Dimension(200, getHeight()));
            directoryScrollPane.setBorder(BorderFactory.createEmptyBorder());
            add(directoryScrollPane, BorderLayout.WEST);
        }


        private JPanel createSectionPanel(String title, boolean hasAddRemoveButtons, DefaultListModel<String> model) {
            JPanel sectionPanel = new JPanel(new BorderLayout());
            sectionPanel.setBorder(BorderFactory.createTitledBorder(title));

            // Create the list that will display the items
            JList<String> fileList = new JList<>(model);
            fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane listScroller = new JScrollPane(fileList);
            sectionPanel.add(listScroller, BorderLayout.CENTER);

            // Assign the correct list reference based on the title
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

            // Setup list selection listener
            setupListSelectionListener(fileList);

            // Add "+" and "-" buttons if this is the "DRAFTS" section
            if (hasAddRemoveButtons) {
                JPanel buttonPanel = createButtonPanel(fileList);
                sectionPanel.add(buttonPanel, BorderLayout.SOUTH);
            }

            return sectionPanel;
        }

        private void setupListSelectionListener(JList<String> list) {
            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    clearOtherListSelections(list);
                    String selectedRecipeName = list.getSelectedValue();
                    if (selectedRecipeName != null) {
                        fileNameField.setText(selectedRecipeName);
                        loadContentFromDatabase(selectedRecipeName);
                        updateButtonStates(list);
                    } else {
                        textArea.setText("");  // Clear text area when no selection
                        fileNameField.setText("");  // Reset file name field
                        disableButtons();  // Disable buttons if nothing is selected
                    }
                }
            });
        }

        private JPanel createButtonPanel(JList<String> list) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addButton = new JButton("+");
            addButton.setPreferredSize(new Dimension(20, 20));
            JButton removeButton = new JButton("-");
            removeButton.setPreferredSize(new Dimension(20, 20));

            addButton.addActionListener(e -> {
                String newDraftName = "New Draft " + (draftsModel.getSize() + 1);
                addNewDraftToDatabase(newDraftName, list);
            });

            removeButton.addActionListener(e -> {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1) {
                    removeDraftFromDatabase(list.getModel().getElementAt(selectedIndex), selectedIndex, list);
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            return buttonPanel;
        }


        private void addNewDraftToDatabase(String draftName, JList<String> list) {
            String sql = "INSERT INTO Recipe (recipeName, chefID, recipeStatus, recipeFile) VALUES (?, ?, 'DRAFT', '')";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, draftName);
                pstmt.setInt(2, chefID);
                int insertedRows = pstmt.executeUpdate();

                if (insertedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newRecipeId = generatedKeys.getInt(1);
                            System.out.println("New draft added with ID: " + newRecipeId);
                            JOptionPane.showMessageDialog(this, "New draft added successfully with ID: " + newRecipeId, "Success", JOptionPane.INFORMATION_MESSAGE);
                            recipeIdMap.put(draftName, newRecipeId);
                            ((DefaultListModel<String>) list.getModel()).addElement(draftName);
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

        private void removeDraftFromDatabase(String recipeName, int index, JList<String> list) {
            int recipeId = getSelectedRecipeId();
            String sql = "DELETE FROM Recipe WHERE recipeID = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, recipeId);
                int deletedRows = pstmt.executeUpdate();

                if (deletedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Recipe deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    ((DefaultListModel<String>) list.getModel()).remove(index);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        private void updateButtonStates(JList<String> selectedList) {
            boolean isDrafts = selectedList == draftsList;
            boolean isSubmitted = selectedList == submittedList;

            saveButton.setVisible(isDrafts);
            submitButton.setVisible(isDrafts);
            deleteButton.setVisible(isDrafts);
            recipeButton.setVisible(isSubmitted);
            saveButton.setEnabled(isDrafts);
            submitButton.setEnabled(isDrafts);
            deleteButton.setEnabled(isDrafts);

            textArea.setEditable(isDrafts || isSubmitted);
            fileNameField.setEditable(isDrafts);

            if (isSubmitted) {
                recipeButton.addActionListener(e -> moveRecipeToOfficial());
            }
        }

        private void disableButtons() {
            saveButton.setVisible(false);
            submitButton.setVisible(false);
            deleteButton.setVisible(false);
            textArea.setEditable(false);
            fileNameField.setEditable(false);
        }


        private void clearOtherListSelections(JList<String> activeList) {
            if (activeList != draftsList && draftsList != null) {
                draftsList.clearSelection();
            }
            if (activeList != submittedList && submittedList != null) {
                submittedList.clearSelection();
            }
            if (activeList != recipesList && recipesList != null) {
                recipesList.clearSelection();
            }
        }

        private void addButtonsIfRequired(String title, JPanel sectionPanel, DefaultListModel<String> model, JList<String> fileList) {
            if (!"DRAFTS".equals(title)) {
                return;
            }
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addButton = new JButton("+");
            addButton.setPreferredSize(new Dimension(20, 20));
            JButton removeButton = new JButton("-");
            removeButton.setPreferredSize(new Dimension(20, 20));

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

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            sectionPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        private void addNewDraftToDatabase(String draftName) {
            String sql = "INSERT INTO Recipe (recipeName, chefID, recipeStatus, recipeFile) VALUES (?, ?, 'DRAFT', '')";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, draftName);
                pstmt.setInt(2, chefID);
                int insertedRows = pstmt.executeUpdate();

                if (insertedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newRecipeId = generatedKeys.getInt(1);
                            System.out.println("New draft added with ID: " + newRecipeId);
                            JOptionPane.showMessageDialog(this, "New draft added successfully with ID: " + newRecipeId, "Success", JOptionPane.INFORMATION_MESSAGE);
                            // Store newRecipeId in the map and update list model
                            recipeIdMap.put(draftName, newRecipeId);
                            draftsModel.addElement(draftName);  // Ensure the list model is updated
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



        private void loadContentFromDatabase(String recipeName) {
            if (recipeName == null || recipeName.isEmpty()) {
                textArea.setText("No recipe selected.");
                return;
            }

            String sql = "SELECT recipeFile FROM Recipe WHERE recipeName = ? AND chefID = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, recipeName);
                pstmt.setInt(2, chefID);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Blob recipeBlob = rs.getBlob("recipeFile");
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


        // Call this method when the "SUBMIT" button is clicked
        private void submitDraft() {
            int recipeId = getSelectedRecipeId();
            if(! checkIfRecipeNameExists(fileNameField.getText(), recipeId)) {
                int selectedIndex = draftsList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedItem = draftsModel.getElementAt(selectedIndex);
                    String content = textArea.getText();  // Capture the content before it might get cleared or changed
                    boolean updateSuccess = updateRecipeContent(selectedItem, content, true); // Submit the draft

                    if (updateSuccess) {
                        submittedModel.addElement(selectedItem);  // Add to submitted list model
                        draftsModel.remove(selectedIndex);  // Remove from drafts list model

                        // After moving, select the same item in the submitted list to load content
                        submittedList.setSelectedValue(selectedItem, true);
                        loadContentFromDatabase(selectedItem);
                    }
                }
            }
        }


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

        private void retrieveDataAndPopulateLists() {
            try {
                System.out.println("Retrieving data for chefID: " + chefID);
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT recipeID, recipeName, recipeStatus FROM Recipe WHERE chefID = " + chefID+ " OR  recipeStatus = 'RECIPE'");
                while (resultSet.next()) {
                    int id = resultSet.getInt("recipeID");
                    String name = resultSet.getString("recipeName");
                    String status = resultSet.getString("recipeStatus");
                    recipeIdMap.put(name, id);
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

        private Integer getSelectedRecipeId() {
            String selectedRecipeName = draftsList.getSelectedValue();  // Assuming you're currently working with drafts
            if (selectedRecipeName != null && recipeIdMap.containsKey(selectedRecipeName)) {
                return recipeIdMap.get(selectedRecipeName);
            }
            return null;
        }

        private boolean updateRecipeContent(String recipeName, String newContent, boolean isSubmitted) {
            try {
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement pstmt = connection.prepareStatement("UPDATE Recipe SET recipeFile = ?, recipeStatus = ? WHERE recipeName = ? AND chefID = ?");
                pstmt.setBytes(1, newContent.getBytes(StandardCharsets.UTF_8));
                pstmt.setString(2, isSubmitted ? "SUBMITTED" : "DRAFT");
                pstmt.setString(3, recipeName);
                pstmt.setInt(4, chefID);

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

        private void moveRecipeToOfficial() {
            String selectedRecipeName = submittedList.getSelectedValue();
            Integer recipeId = recipeIdMap.get(selectedRecipeName);
            if (recipeId != null) {
                try (Connection connection = DatabaseManager.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement("UPDATE Recipe SET recipeStatus = 'RECIPE' WHERE recipeID = ?")) {
                    pstmt.setInt(1, recipeId);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(null, "Recipe moved to official recipes successfully!");
                        recipesModel.addElement(selectedRecipeName);
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

    public class DishGUI extends JPanel {
        private JTextField dishNameField;
        private JTextField photoPathField; // Text field to display photo file name
        private JTable ingredientsTable;
        private JButton refreshButton;
        private DefaultTableModel tableModel;
        private JComboBox<String> recipeComboBox;
        private HashMap<Integer, String> recipeMap = new HashMap<>();

        public DishGUI() {
            setTitle("Dish Details");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

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

            String[] columnNames = {"Ingredient", "Quantity"};
            tableModel = new DefaultTableModel(columnNames, 0);
            ingredientsTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(ingredientsTable);
            add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add Ingredient");
            JButton removeButton = new JButton("Remove Selected");
            JButton saveButton = new JButton("Save Dish");
            JButton refreshButton = new JButton("Refresh");

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            buttonPanel.add(saveButton);
            buttonPanel.add(refreshButton);

            add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(e -> addIngredient());
            removeButton.addActionListener(e -> removeSelectedIngredient());
            saveButton.addActionListener(e -> saveDish());
            photoButton.addActionListener(e -> uploadPhoto());
            refreshButton.addActionListener(e -> loadRecipeBox());

            setLocationRelativeTo(null);
        }

        private void addIngredient() {
            tableModel.addRow(new Object[]{"New Ingredient", 0});
        }

        private void removeSelectedIngredient() {
            int selectedRow = ingredientsTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        }

        public void loadRecipeBox() {
            recipeComboBox.removeAllItems();
            recipeMap.clear();
            try{
                Connection connection = DatabaseManager.getConnection();
                String sql = "SELECT recipeID, recipeName FROM Recipe";
                PreparedStatement pstm = connection.prepareStatement(sql);
                ResultSet resultSet = pstm.executeQuery();
                int recipeID;
                String recipeName;
                while(resultSet.next()){
                    recipeID = resultSet.getInt("recipeID");
                    recipeName = resultSet.getString("recipeName");
                    recipeMap.put(recipeID, recipeName);
                    recipeComboBox.addItem(recipeName);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private void saveDish() {
            Connection connection = null;
            PreparedStatement pstm = null;
            ResultSet generatedKeys = null;
            FileInputStream fis = null;
            try {
                File imageFile = new File(photoPathField.getText());
                fis = new FileInputStream(imageFile);

                connection = DatabaseManager.getConnection();
                String sql = "INSERT INTO Dish (dishName, dishPhoto, recipeID) VALUES (?, ?, ?)";
                pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstm.setString(1, dishNameField.getText());
                pstm.setBlob(2, fis, imageFile.length());

                boolean recipeFound = false;
                for(int recipeID : recipeMap.keySet()) {
                    if(recipeMap.get(recipeID).equals(recipeComboBox.getSelectedItem())) {
                        pstm.setInt(3, recipeID);
                        recipeFound = true;
                        break;
                    }
                }

                if (!recipeFound) {
                    throw new RuntimeException("Recipe not found");
                }

                int affectedRows = pstm.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating dish failed, no rows affected.");
                }

                generatedKeys = pstm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long dishID = generatedKeys.getLong(1); // Retrieve the first field of the generated keys, which is the dish ID

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

    public class DishViewer extends JPanel {
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
                        String recipeIdStr = table.getValueAt(table.getSelectedRow(), 2).toString();
                        for(int i : recipeMap.keySet()) {
                            if(recipeMap.get(i).equals(recipeIdStr)){
                                recipeIdStr = Integer.toString(i);
                                break;
                            }
                        }
                        if (recipeIdStr.matches("\\d+")) { // Regex to check if the string contains only digits
                            openRecipeDetails(recipeIdStr);
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid Recipe ID: " + recipeIdStr, "Error", JOptionPane.ERROR_MESSAGE);
                        }
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


    public class IngredientAdder extends JPanel {
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
    }

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


        private void setDate(ActionEvent actionEvent) {
        }

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
