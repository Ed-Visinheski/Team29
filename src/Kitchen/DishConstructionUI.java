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

    // Constructors

    /**
     * Constructs a new DishConstructionUI object with the specified chef ID.
     *
     * @param chefID The ID of the chef using the interface.
     */
    public DishConstructionUI(int chefID) {
        // Set the title of the window to "File Manager"
        setTitle("File Manager");

        // Set the chef ID
        this.chefID = chefID;

        // Set the size of the window to 1280x720 pixels
        setSize(1280, 720);

        // Set the minimum size of the window to 1280x720 pixels
        setMinimumSize(new Dimension(1280, 720));

        // Set the default close operation to exit the application when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the layout of the content pane to BorderLayout
        getContentPane().setLayout(new BorderLayout());

        // Initialize the components that will use the buttons
        createMenuHeader(); // This should instantiate the buttons

        // Set the initial states of the buttons to disabled
        saveButton.setEnabled(false);
        submitButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Initialize data structures
        fileMap = new HashMap<>();
        draftsModel = new DefaultListModel<>();
        submittedModel = new DefaultListModel<>();
        recipesModel = new DefaultListModel<>();

        // Retrieve data from the database and populate lists
        retrieveDataAndPopulateLists();

        // Create the file directory with DRAFTS, SUBMITTED, and RECIPES sections
        createFileDirectory();

        // Create the text area
        createTextArea();

        // Make the window visible
        setVisible(true);
    }





    /**
     * Creates the menu header panel with text field and buttons.
     */
    private void createMenuHeader() {
        // Create a new panel to hold the header components
        JPanel headerPanel = new JPanel();

        // Create a text field for file name input with initial empty string and width of 20 columns
        fileNameField = new JTextField("", 20);

        // Create buttons for save, delete, and submit actions
        saveButton = new JButton("SAVE");
        deleteButton = new JButton("DELETE");
        submitButton = new JButton("SUBMIT");

        // Add the text field and buttons to the header panel
        headerPanel.add(fileNameField);
        headerPanel.add(saveButton);
        headerPanel.add(submitButton);
        headerPanel.add(deleteButton);

        // Define button actions after they are instantiated
        defineButtonActions();

        // Add the header panel to the top of the content pane using BorderLayout
        add(headerPanel, BorderLayout.NORTH);
    }


    /**
     * Defines actions for buttons in the menu header.
     */
    private void defineButtonActions() {
        // Action listener for the save button
        saveButton.addActionListener(e -> {
            // Get the name of the selected recipe from the drafts list
            String selectedRecipeName = draftsList.getSelectedValue();

            // Retrieve the ID of the selected recipe
            Integer recipeId = getSelectedRecipeId(); // This method needs to retrieve the ID based on the selected name

            // Get the new recipe name from the text field
            String newRecipeName = fileNameField.getText();

            // Check if the recipe ID and new name are valid
            if (recipeId != null && !newRecipeName.isEmpty()) {
                // Only update if there is no name conflict or if it's the same record
                if (!checkIfRecipeNameExists(newRecipeName, recipeId)) {
                    // Update the name and content of the selected recipe
                    updateRecipeNameAndContent(recipeId, newRecipeName, textArea.getText());
                }
            } else {
                // Show an error message if no recipe is selected or the recipe name is empty
                JOptionPane.showMessageDialog(this, "No recipe selected or recipe name is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action listener for the delete button
        deleteButton.addActionListener(e -> {
            // Get the index of the selected recipe in the drafts list
            int selectedIndex = draftsList.getSelectedIndex();
            if (selectedIndex != -1) {
                // Get the selected recipe name
                String selectedRecipe = draftsModel.get(selectedIndex);

                // Get the ID of the selected recipe
                int recipeId = getSelectedRecipeId();

                // Attempt to delete the recipe from the database
                try (Connection connection = DatabaseManager.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Recipe WHERE recipeID = ?")) {
                    pstmt.setInt(1, recipeId);

                    // Execute the delete query and get the number of deleted rows
                    int deletedRows = pstmt.executeUpdate();
                    if (deletedRows > 0) {
                        // Show a success message and remove the recipe from the drafts list
                        JOptionPane.showMessageDialog(this, "Recipe deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        draftsModel.remove(selectedIndex);
                    } else {
                        // Show an error message if the recipe deletion failed
                        JOptionPane.showMessageDialog(this, "Failed to delete the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    // Show an error message if an SQL exception occurs
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for the submit button
        submitButton.addActionListener(e -> {
            // Call the submitDraft method to handle the submission of a draft
            submitDraft();
        });

        // Add other button listeners if needed
    }


    /**
     * Checks if a recipe name already exists in the database.
     *
     * @param newRecipeName   The new recipe name to check.
     * @param currentRecipeId The ID of the current recipe (if exists).
     * @return True if the recipe name already exists, false otherwise.
     */
    private boolean checkIfRecipeNameExists(String newRecipeName, Integer currentRecipeId) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("SELECT recipeID FROM Recipe WHERE recipeName = ?")) {
            // Set the new recipe name as a parameter in the SQL query
            pstmt.setString(1, newRecipeName);
            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if the query returned any results
                if (rs.next()) {
                    // If a recipe with the same name exists...
                    int existingRecipeId = rs.getInt("recipeID");
                    // Check if it's the same recipe being edited or a different one
                    if (existingRecipeId != currentRecipeId) {
                        // Show an error message indicating the name conflict
                        JOptionPane.showMessageDialog(this, "Recipe name already exists. Please choose a different name.", "Name Conflict", JOptionPane.ERROR_MESSAGE);
                        return true; // Return true to indicate that the name exists
                    }
                }
            }
        } catch (SQLException ex) {
            // Handle any SQL exceptions
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking for recipe name: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; // Return false if no name conflict was found
    }



    /**
     * Updates the name and content of a recipe in the database.
     *
     * @param recipeId The ID of the recipe to update.
     * @param newName  The new name of the recipe.
     * @param content  The new content of the recipe.
     */
    private void updateRecipeNameAndContent(int recipeId, String newName, String content) {
        // Check if content is empty
        if (content == null || content.isEmpty()) {
            // Show an error message if the content is empty
            JOptionPane.showMessageDialog(this, "Content is empty, nothing to save.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Always update both name and content to ensure changes are committed even if the name remains the same
        String sql = "UPDATE Recipe SET recipeName = ?, recipeFile = ? WHERE recipeID = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Set the parameters for the SQL query
            pstmt.setString(1, newName);
            pstmt.setBytes(2, content.getBytes(StandardCharsets.UTF_8));
            pstmt.setInt(3, recipeId);

            // Execute the update query
            int updatedRows = pstmt.executeUpdate();
            if (updatedRows > 0) {
                // Show a success message if the update was successful
                JOptionPane.showMessageDialog(this, "Recipe updated successfully.", "Update Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the UI to reflect the changes
                refreshUIAfterRecipeUpdate(recipeId, newName);
            } else {
                // Show an error message if no changes were made
                JOptionPane.showMessageDialog(this, "No changes were made. Ensure the recipe ID is correct.", "Update Failure", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            // Handle any SQL exceptions
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating the recipe: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Refreshes the UI after updating a recipe's name.
     *
     * @param recipeId The ID of the updated recipe.
     * @param newName  The new name of the recipe.
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

        if (oldName != null) {
            // Update the map with the new name
            recipeIdMap.remove(oldName);
            recipeIdMap.put(newName, recipeId);

            // Update the UI components (list models)
            updateListModel(draftsModel, oldName, newName);
            updateListModel(submittedModel, oldName, newName);
            updateListModel(recipesModel, oldName, newName);
        } else {
            // Show an error message if the old recipe name is not found
            JOptionPane.showMessageDialog(this, "Failed to find the old recipe name for updating.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Updates the list model with a new name.
     *
     * @param model   The list model to update.
     * @param oldName The old name to replace.
     * @param newName The new name to set.
     */
    private void updateListModel(DefaultListModel<String> model, String oldName, String newName) {
        int index = model.indexOf(oldName);
        if (index != -1) {
            // If the old name is found in the list model, replace it with the new name
            model.set(index, newName);
        }
    }



    /**
     * Creates the file directory panel with separate sections for drafts, submitted recipes, and recipes.
     * This method sets up a panel containing three sections vertically arranged using a box layout:
     * - DRAFTS: Displays a list of draft recipes with buttons to add and remove drafts.
     * - SUBMITTED: Displays a list of submitted recipes.
     * - RECIPES: Displays a list of finalized recipes.
     * Each section is created using the createSectionPanel method, which accepts parameters
     * for section title, whether to include add/remove buttons, and the associated list model.
     * The directory panel is then wrapped in a scroll pane and added to the west side of the frame.
     */
    private void createFileDirectory() {
        // Create a panel to hold the directory sections
        JPanel directoryPanel = new JPanel();
        directoryPanel.setLayout(new BoxLayout(directoryPanel, BoxLayout.Y_AXIS)); // Use a vertical box layout
        directoryPanel.setBackground(new Color(255, 182, 193)); // Set background color

        // Add sections for drafts, submitted recipes, and finalized recipes
        directoryPanel.add(createSectionPanel("DRAFTS", true, draftsModel)); // Include add/remove buttons for drafts
        directoryPanel.add(createSectionPanel("SUBMITTED", false, submittedModel)); // No add/remove buttons for submitted recipes
        directoryPanel.add(createSectionPanel("RECIPES", false, recipesModel)); // No add/remove buttons for finalized recipes

        // Create a scroll pane to accommodate the directory panel
        JScrollPane directoryScrollPane = new JScrollPane(directoryPanel);
        directoryScrollPane.setPreferredSize(new Dimension(200, getHeight())); // Set preferred width of the scroll pane
        directoryScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Set empty border to improve appearance

        // Add the scroll pane containing the directory panel to the west side of the frame
        add(directoryScrollPane, BorderLayout.WEST);
    }



    /**
     * Creates a panel for a specific section in the file directory.
     * This method sets up a JPanel with a titled border displaying the specified title.
     * It then creates a JList component with the provided DefaultListModel to display items.
     * The list is wrapped in a JScrollPane and added to the center of the panel.
     * Depending on the value of 'hasAddRemoveButtons', this method may also add buttons
     * for adding and removing items to/from the list, specifically for the "DRAFTS" section.
     * Additionally, it assigns the appropriate list reference (draftsList, submittedList, or recipesList)
     * based on the title of the section. A list selection listener is set up to handle item selection.
     * Finally, if 'hasAddRemoveButtons' is true, a button panel is created and added to the bottom
     * of the section panel to accommodate add and remove buttons.
     *
     * @param title               The title of the section.
     * @param hasAddRemoveButtons A boolean indicating whether to include add/remove buttons for the section.
     * @param model               The DefaultListModel containing the data to be displayed in the list.
     * @return A JPanel representing the section panel.
     */
    private JPanel createSectionPanel(String title, boolean hasAddRemoveButtons, DefaultListModel<String> model) {
        // Create a panel for the section with a titled border
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBorder(BorderFactory.createTitledBorder(title));

        // Create a JList component to display items from the provided model
        JList<String> fileList = new JList<>(model);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single selection
        JScrollPane listScroller = new JScrollPane(fileList); // Wrap the list in a scroll pane
        sectionPanel.add(listScroller, BorderLayout.CENTER); // Add the list to the center of the panel

        // Assign the correct list reference based on the section title
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

        // Set up a list selection listener to handle item selection
        setupListSelectionListener(fileList);

        // Add add/remove buttons if this is the "DRAFTS" section
        if (hasAddRemoveButtons) {
            JPanel buttonPanel = createButtonPanel(fileList); // Create a button panel
            sectionPanel.add(buttonPanel, BorderLayout.SOUTH); // Add the button panel to the bottom of the section panel
        }

        return sectionPanel; // Return the section panel
    }


    /**
     * Sets up a list selection listener for the specified JList component.
     * When an item in the list is selected, this listener responds by:
     * - Clearing selections in other lists (if any).
     * - Updating the file name field with the selected recipe name.
     * - Loading the content of the selected recipe from the database.
     * - Updating the states of buttons based on the selection.
     * If no item is selected, it clears the text area, resets the file name field,
     * and disables relevant buttons.
     *
     * @param list The JList component for which the selection listener is being set up.
     */
    private void setupListSelectionListener(JList<String> list) {
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Ensure the event is not in the process of adjusting values
                clearOtherListSelections(list); // Clear selections in other lists
                String selectedRecipeName = list.getSelectedValue(); // Get the selected recipe name
                if (selectedRecipeName != null) { // If an item is selected
                    fileNameField.setText(selectedRecipeName); // Update the file name field
                    loadContentFromDatabase(selectedRecipeName); // Load content of the selected recipe from the database
                    updateButtonStates(list); // Update button states based on the selection
                } else {
                    textArea.setText(""); // Clear text area when no selection
                    fileNameField.setText(""); // Reset file name field
                    disableButtons(); // Disable buttons if nothing is selected
                }
            }
        });
    }


    /**
     * Creates a panel containing buttons for adding and removing items from the list.
     * The panel includes buttons for adding and removing items from the list displayed
     * in the specified JList component. When the "+" button is clicked, a new draft
     * item is added to the database and displayed in the list. When the "-" button is
     * clicked, the selected draft item is removed from the database and from the list.
     *
     * @param list The JList component for which the buttons are created.
     * @return A JPanel containing buttons for adding and removing items from the list.
     */
    private JPanel createButtonPanel(JList<String> list) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Create a panel for buttons
        JButton addButton = new JButton("+"); // Create a button for adding items
        addButton.setPreferredSize(new Dimension(20, 20)); // Set preferred size for the button
        JButton removeButton = new JButton("-"); // Create a button for removing items
        removeButton.setPreferredSize(new Dimension(20, 20)); // Set preferred size for the button

        // Add action listener for the "+" button
        addButton.addActionListener(e -> {
            // Generate a new draft name and add it to the database and list
            String newDraftName = "New Draft " + (draftsModel.getSize() + 1);
            addNewDraftToDatabase(newDraftName, list);
        });

        // Add action listener for the "-" button
        removeButton.addActionListener(e -> {
            int selectedIndex = list.getSelectedIndex(); // Get the index of the selected item
            if (selectedIndex != -1) { // If an item is selected
                // Remove the selected draft from the database and list
                removeDraftFromDatabase(list.getModel().getElementAt(selectedIndex), selectedIndex, list);
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        return buttonPanel; // Return the panel containing the buttons
    }



    /**
     * Adds a new draft to the database with the specified name.
     * This method inserts a new record into the Recipe table in the database,
     * representing a new draft recipe. The draft is associated with the current chef ID.
     * Upon successful insertion, the method retrieves the generated recipe ID
     * and updates the recipe ID map and the list model to reflect the changes.
     *
     * @param draftName The name of the new draft recipe to be added.
     * @param list      The JList component representing the list where the new draft will be displayed.
     */
    private void addNewDraftToDatabase(String draftName, JList<String> list) {
        String sql = "INSERT INTO Recipe (recipeName, chefID, recipeStatus, recipeFile) VALUES (?, ?, 'DRAFT', '')";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, draftName); // Set the draft name in the prepared statement
            pstmt.setInt(2, chefID); // Set the current chef ID in the prepared statement
            int insertedRows = pstmt.executeUpdate(); // Execute the SQL query to insert the new draft

            if (insertedRows > 0) { // If insertion was successful
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) { // If generated keys are available
                        int newRecipeId = generatedKeys.getInt(1); // Get the generated recipe ID
                        System.out.println("New draft added with ID: " + newRecipeId); // Print the ID to console
                        JOptionPane.showMessageDialog(this, "New draft added successfully with ID: " + newRecipeId, "Success", JOptionPane.INFORMATION_MESSAGE); // Show success message
                        recipeIdMap.put(draftName, newRecipeId); // Update the recipe ID map with the new draft
                        ((DefaultListModel<String>) list.getModel()).addElement(draftName); // Add the draft to the list model
                    } else {
                        throw new SQLException("Creating draft failed, no ID obtained."); // Throw exception if no ID obtained
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add new draft.", "Error", JOptionPane.ERROR_MESSAGE); // Show error message if insertion failed
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Print stack trace for SQL exception
            JOptionPane.showMessageDialog(this, "Error adding new draft: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Show error message
        }
    }


    /**
     * Removes a draft recipe from the database and the corresponding list model.
     * This method deletes the draft recipe from the Recipe table in the database
     * based on the selected recipe ID. Upon successful deletion, it removes the
     * recipe from the list model to reflect the changes in the UI.
     *
     * @param recipeName The name of the draft recipe to be removed.
     * @param index      The index of the selected item in the list model.
     * @param list       The JList component representing the list where the draft is displayed.
     */
    private void removeDraftFromDatabase(String recipeName, int index, JList<String> list) {
        int recipeId = getSelectedRecipeId(); // Get the ID of the selected recipe
        String sql = "DELETE FROM Recipe WHERE recipeID = ?"; // SQL query to delete recipe by ID
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId); // Set the recipe ID in the prepared statement
            int deletedRows = pstmt.executeUpdate(); // Execute the SQL query to delete the recipe

            if (deletedRows > 0) { // If deletion was successful
                JOptionPane.showMessageDialog(this, "Recipe deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE); // Show success message
                ((DefaultListModel<String>) list.getModel()).remove(index); // Remove the recipe from the list model
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the recipe.", "Error", JOptionPane.ERROR_MESSAGE); // Show error message if deletion failed
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Print stack trace for SQL exception
            JOptionPane.showMessageDialog(this, "Error deleting the recipe.", "Error", JOptionPane.ERROR_MESSAGE); // Show error message
        }
    }



    /**
     * Updates the states and visibility of buttons based on the selected list.
     * This method enables or disables the save, submit, and delete buttons and sets
     * the visibility of these buttons and the editability of the text area and file name field.
     *
     * @param selectedList The JList component representing the currently selected list.
     */
    private void updateButtonStates(JList<String> selectedList) {
        if (selectedList == draftsList) { // If the selected list is the drafts list
            saveButton.setEnabled(true); // Enable the save button
            submitButton.setEnabled(true); // Enable the submit button
            deleteButton.setEnabled(true); // Enable the delete button
            saveButton.setVisible(true); // Make the save button visible
            submitButton.setVisible(true); // Make the submit button visible
            deleteButton.setVisible(true); // Make the delete button visible
            textArea.setEditable(true); // Make the text area editable
            fileNameField.setEditable(true); // Make the file name field editable
        } else { // If the selected list is not the drafts list
            saveButton.setVisible(false); // Hide the save button
            submitButton.setVisible(false); // Hide the submit button
            deleteButton.setVisible(false); // Hide the delete button
            textArea.setEditable(false); // Make the text area non-editable
            fileNameField.setEditable(false); // Make the file name field non-editable
        }
        this.revalidate(); // Revalidate the container to reflect changes in component layout
        this.repaint(); // Repaint the container to ensure visual updates
    }


    /**
     * Disables and hides the save, submit, and delete buttons, and sets the text area
     * and file name field to be non-editable.
     * This method is called when there is no item selected in the list, to prevent
     * interaction with these buttons and fields.
     */
    private void disableButtons() {
        saveButton.setVisible(false); // Hide the save button
        submitButton.setVisible(false); // Hide the submit button
        deleteButton.setVisible(false); // Hide the delete button
        textArea.setEditable(false); // Make the text area non-editable
        fileNameField.setEditable(false); // Make the file name field non-editable
    }



    /**
     * Clears the selection of other lists when a selection is made in an active list.
     * This method is called to ensure that only one item can be selected across all lists.
     *
     * @param activeList The list in which a selection has been made.
     */
    private void clearOtherListSelections(JList<String> activeList) {
        // Clear the selection of draftsList if it's not the activeList and if draftsList is not null
        if (activeList != draftsList && draftsList != null) {
            draftsList.clearSelection();
        }
        // Clear the selection of submittedList if it's not the activeList and if submittedList is not null
        if (activeList != submittedList && submittedList != null) {
            submittedList.clearSelection();
        }
        // Clear the selection of recipesList if it's not the activeList and if recipesList is not null
        if (activeList != recipesList && recipesList != null) {
            recipesList.clearSelection();
        }
    }


    /**
     * Adds buttons for adding and removing items if required for the specified section.
     *
     * @param title        The title of the section.
     * @param sectionPanel The panel representing the section.
     * @param model        The list model associated with the section.
     * @param fileList     The JList displaying the items in the section.
     */
    private void addButtonsIfRequired(String title, JPanel sectionPanel, DefaultListModel<String> model, JList<String> fileList) {
        // If the section is not "DRAFTS", return without adding buttons
        if (!"DRAFTS".equals(title)) {
            return;
        }

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create the "+" button
        JButton addButton = new JButton("+");
        addButton.setPreferredSize(new Dimension(20, 20));
        addButton.addActionListener(e -> {
            // Generate a new draft name and add it to the database and the list model
            String newDraftName = "New Draft " + (draftsModel.getSize() + 1);
            addNewDraftToDatabase(newDraftName);
            model.addElement(newDraftName);
        });

        // Create the "-" button
        JButton removeButton = new JButton("-");
        removeButton.setPreferredSize(new Dimension(20, 20));
        removeButton.addActionListener(e -> {
            // Remove the selected item from the list model
            int selectedIndex = fileList.getSelectedIndex();
            if (selectedIndex != -1) {
                model.remove(selectedIndex);
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        // Add the button panel to the section panel
        sectionPanel.add(buttonPanel, BorderLayout.SOUTH);
    }


    /**
     * Adds a new draft to the database with the specified draft name.
     *
     * @param draftName The name of the new draft to add.
     */
    private void addNewDraftToDatabase(String draftName) {
        // SQL statement to insert a new draft into the Recipe table
        String sql = "INSERT INTO Recipe (recipeName, chefID, recipeStatus, recipeFile) VALUES (?, ?, 'DRAFT', '')";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters for the prepared statement
            pstmt.setString(1, draftName);  // Set the draft name
            pstmt.setInt(2, chefID);  // Set the chef ID

            // Execute the SQL statement and get the number of rows affected
            int insertedRows = pstmt.executeUpdate();

            // If insertion was successful, retrieve the generated keys
            if (insertedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    // If a key is generated, retrieve the new recipe ID
                    if (generatedKeys.next()) {
                        int newRecipeId = generatedKeys.getInt(1);  // Get the generated key (new recipe ID)
                        System.out.println("New draft added with ID: " + newRecipeId);
                        // Display success message
                        JOptionPane.showMessageDialog(this, "New draft added successfully with ID: " + newRecipeId, "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Store the new recipe ID in the map and update the list model
                        recipeIdMap.put(draftName, newRecipeId);  // Store the new recipe ID in the map
                        draftsModel.addElement(draftName);  // Add the draft name to the list model
                    } else {
                        // If no key is generated, throw an exception
                        throw new SQLException("Creating draft failed, no ID obtained.");
                    }
                }
            } else {
                // If insertion failed, display an error message
                JOptionPane.showMessageDialog(this, "Failed to add new draft.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            // If an SQL exception occurs, print the stack trace and display an error message
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding new draft: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    /**
     * Loads the content of the recipe from the database into the text area based on the specified recipe name.
     *
     * @param recipeName The name of the recipe for which to load the content.
     */
    private void loadContentFromDatabase(String recipeName) {
        // Check if the recipe name is null or empty
        if (recipeName == null || recipeName.isEmpty()) {
            // If the recipe name is null or empty, display a message in the text area
            textArea.setText("No recipe selected.");
            return;
        }

        // SQL query to retrieve the recipe file content from the Recipe table
        String sql = "SELECT recipeFile FROM Recipe WHERE recipeName = ? AND chefID = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Set parameters for the prepared statement
            pstmt.setString(1, recipeName);  // Set the recipe name
            pstmt.setInt(2, chefID);  // Set the chef ID

            // Execute the SQL query and process the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // If the result set contains data
                if (rs.next()) {
                    // Retrieve the recipe file content from the result set
                    Blob recipeBlob = rs.getBlob("recipeFile");
                    // If the recipe file content is not null and not empty
                    if (recipeBlob != null && recipeBlob.length() > 0) {
                        // Read the bytes from the blob and convert them to a string using UTF-8 encoding
                        byte[] bytes = recipeBlob.getBytes(1, (int) recipeBlob.length());
                        String content = new String(bytes, StandardCharsets.UTF_8);
                        // Set the content in the text area
                        textArea.setText(content);
                    } else {
                        // If the recipe file content is null or empty, display a message in the text area
                        textArea.setText("No content available for this recipe.");
                    }
                } else {
                    // If no data is found for the specified recipe name and chef ID, display a message in the text area
                    textArea.setText("Recipe details not found in the database.");
                }
            }
        } catch (SQLException ex) {
            // If an SQL exception occurs, print the stack trace, display an error message, and set a message in the text area
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            textArea.setText("Failed to load recipe content.");
        }
    }



    // Call this method when the "SUBMIT" button is clicked

    /**
     * Submits the selected draft recipe for approval.
     * Retrieves the selected recipe's ID, checks if the recipe name already exists in the database,
     * and if not, proceeds to submit the draft.
     * If submission is successful, the draft is moved to the submitted list, and its content is loaded
     * into the text area for further viewing or editing.
     */
    private void submitDraft() {
        // Get the ID of the selected recipe
        int recipeId = getSelectedRecipeId();

        // Check if the recipe name already exists in the database
        if (!checkIfRecipeNameExists(fileNameField.getText(), recipeId)) {
            // Get the index of the selected item in the drafts list
            int selectedIndex = draftsList.getSelectedIndex();

            // If an item is selected in the drafts list
            if (selectedIndex != -1) {
                // Get the name of the selected item
                String selectedItem = draftsModel.getElementAt(selectedIndex);

                // Capture the content of the text area before it might get cleared or changed
                String content = textArea.getText();

                // Submit the draft by updating its content in the database
                boolean updateSuccess = updateRecipeContent(selectedItem, content, true);

                // If the update operation is successful
                if (updateSuccess) {
                    // Add the selected item to the submitted list model
                    submittedModel.addElement(selectedItem);

                    // Remove the selected item from the drafts list model
                    draftsModel.remove(selectedIndex);

                    // After moving, select the same item in the submitted list to load its content
                    submittedList.setSelectedValue(selectedItem, true);
                    loadContentFromDatabase(selectedItem);
                }
            }
        }
    }




    /**
     * Creates the text area component for displaying and editing recipe content.
     * Configures the text area with specific properties such as background color, border,
     * line wrapping, and scroll pane settings.
     */
    private void createTextArea() {
        // Create a new JTextArea component
        textArea = new JTextArea();

        // Set the background color of the text area to white
        textArea.setBackground(Color.WHITE);

        // Set the border of the text area to a black line border
        textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable line wrapping in the text area
        textArea.setLineWrap(true);

        // Enable word wrapping in the text area
        textArea.setWrapStyleWord(true);

        // Create a scroll pane to contain the text area, allowing scrolling if the content exceeds the viewable area
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);

        // Set the preferred size of the scroll pane (example size: 300x400 pixels)
        textAreaScrollPane.setPreferredSize(new Dimension(300, 400));

        // Set an empty border for the scroll pane to provide spacing around the text area
        textAreaScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add the scroll pane containing the text area to the center of the frame's layout
        add(textAreaScrollPane, BorderLayout.CENTER);
    }


    /**
     * Styles a JButton component with the specified background color and text color.
     * Configures the button to have a solid background, white text, and no border.
     *
     * @param button The JButton component to style.
     * @param color The background color to apply to the button.
     */
    private void styleButton(JButton button, Color color) {
        // Set the background color of the button
        button.setBackground(color);

        // Set the text color of the button to white
        button.setForeground(Color.WHITE);

        // Make the button opaque to display the background color
        button.setOpaque(true);

        // Remove the painted border around the button
        button.setBorderPainted(false);
    }


    // Other methods including button panel creation, database operations, UI updates, etc.

    /**
     * Retrieves data from the database based on the chef's ID and populates the lists accordingly.
     * The method fetches recipe data from the database and categorizes them into drafts, submitted, and recipes.
     * It populates the respective DefaultListModel objects (draftsModel, submittedModel, and recipesModel)
     * with the fetched recipe names and updates the recipeIdMap with the corresponding recipe IDs.
     */
    private void retrieveDataAndPopulateLists() {
        try {
            // Print a message indicating the start of data retrieval
            System.out.println("Retrieving data for chefID: " + chefID);

            // Establish a connection to the database
            Connection connection = DatabaseManager.getConnection();

            // Create a statement to execute SQL queries
            Statement statement = connection.createStatement();

            // Execute a SQL query to select recipe data based on chefID or recipe status
            ResultSet resultSet = statement.executeQuery("SELECT recipeID, recipeName, recipeStatus FROM Recipe WHERE chefID = " + chefID + " OR recipeStatus = 'RECIPE'");

            // Iterate through the result set and process each recipe record
            while (resultSet.next()) {
                // Retrieve recipe attributes from the result set
                int id = resultSet.getInt("recipeID");
                String name = resultSet.getString("recipeName");
                String status = resultSet.getString("recipeStatus");

                // Add the recipe name and ID to the recipeIdMap
                recipeIdMap.put(name, id);

                // Categorize the recipe based on its status and add it to the appropriate list model
                if ("DRAFT".equals(status)) {
                    draftsModel.addElement(name);
                } else if ("SUBMITTED".equals(status)) {
                    submittedModel.addElement(name);
                } else if ("RECIPE".equals(status)) {
                    recipesModel.addElement(name);
                }
            }
        } catch (SQLException e) {
            // Print the stack trace if an SQL exception occurs
            e.printStackTrace();
        }
    }


    /**
     * Retrieves the ID of the selected recipe.
     *
     * @return The ID of the selected recipe, or null if no recipe is selected.
     */
    private Integer getSelectedRecipeId() {
        // Retrieve the name of the selected recipe from the drafts list
        String selectedRecipeName = draftsList.getSelectedValue();

        // Check if the selected recipe name exists in the recipe ID map
        if (selectedRecipeName != null && recipeIdMap.containsKey(selectedRecipeName)) {
            // Return the ID corresponding to the selected recipe name
            return recipeIdMap.get(selectedRecipeName);
        }

        // Return null if no recipe is selected or the selected recipe name is not found in the map
        return null;
    }


    /**
     * Updates the content of a recipe in the database.
     *
     * @param recipeName The name of the recipe to update.
     * @param newContent The new content of the recipe.
     * @param isSubmitted A boolean indicating whether the recipe is submitted (true) or not (false).
     * @return True if the update was successful, false otherwise.
     */
    private boolean updateRecipeContent(String recipeName, String newContent, boolean isSubmitted) {
        try {
            // Establish a connection to the database
            Connection connection = DatabaseManager.getConnection();

            // Prepare a SQL statement to update the recipe content and status
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Recipe SET recipeFile = ?, recipeStatus = ? WHERE recipeName = ? AND chefID = ?");

            // Set parameters for the prepared statement
            pstmt.setBytes(1, newContent.getBytes(StandardCharsets.UTF_8));  // Set the new content as bytes
            pstmt.setString(2, isSubmitted ? "SUBMITTED" : "DRAFT");  // Set the recipe status based on the 'isSubmitted' flag
            pstmt.setString(3, recipeName);  // Set the recipe name
            pstmt.setInt(4, chefID);  // Set the chef ID

            // Execute the update statement and get the number of updated rows
            int updatedRows = pstmt.executeUpdate();

            // Check if the update was successful
            if (updatedRows > 0) {
                // Display a success message if the update was successful
                JOptionPane.showMessageDialog(this, "Recipe updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;  // Return true to indicate a successful update
            } else {
                // Display an error message if the update failed
                JOptionPane.showMessageDialog(this, "Failed to update the recipe. Make sure the recipe exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;  // Return false to indicate a failed update
            }
        } catch (SQLException ex) {
            // Handle any SQL exceptions that may occur during the update process
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;  // Return false to indicate a failed update
        }
    }

}
