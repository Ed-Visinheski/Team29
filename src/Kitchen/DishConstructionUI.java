package Kitchen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DishConstructionUI extends JFrame {
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

    public DishConstructionUI(int chefID) {
        setTitle("File Manager");
        this.chefID = chefID;
        setSize(1280, 720);
        setMinimumSize(new Dimension(1280, 720)); // Set minimum size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        //getContentPane().setBackground(new Color(255, 182, 193)); // Light pink background

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

        // Define button actions after they are instantiated
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
        if (selectedList == draftsList) {
            saveButton.setEnabled(true);
            submitButton.setEnabled(true);
            deleteButton.setEnabled(true);
            saveButton.setVisible(true);
            submitButton.setVisible(true);
            deleteButton.setVisible(true);
            textArea.setEditable(true);
            fileNameField.setEditable(true);
        } else {
            saveButton.setVisible(false);
            submitButton.setVisible(false);
            deleteButton.setVisible(false);
            textArea.setEditable(false);
            fileNameField.setEditable(false);
        }
        this.revalidate();
        this.repaint();
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
}
