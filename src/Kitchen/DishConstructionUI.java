package Kitchen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DishConstructionUI extends JFrame {
    private JPanel draftsPanel;
    private ArrayList<JButton> draftButtons;
    private HashMap<String, String> fileMap;
    private JTextArea textArea;
    private DefaultListModel<String> draftsModel;
    private DefaultListModel<String> submittedModel;
    private DefaultListModel<String> recipesModel;
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
        fileMap = new HashMap<>();
        draftsModel = new DefaultListModel<>();
        submittedModel = new DefaultListModel<>();
        recipesModel = new DefaultListModel<>();
        retrieveDataAndPopulateLists();
        // Create the menu header with file name and buttons
        createMenuHeader();
        // Create the file directory with DRAFTS, SUBMITTED, and RECIPES sections
        createFileDirectory();

        // Create the text area
        createTextArea();

        setVisible(true);
    }


    private void createMenuHeader() {
        JPanel headerPanel = new JPanel();
        JTextField fileNameField = new JTextField("----FILE NAME----", 20);
        JButton saveButton = new JButton("SAVE");
        JButton deleteButton = new JButton("DELETE");
        JButton submitButton = new JButton("SUBMIT");
        styleButton(saveButton, Color.BLUE);
        styleButton(deleteButton, Color.RED);
        styleButton(submitButton, Color.GREEN);

        headerPanel.add(fileNameField);
        headerPanel.add(saveButton);
        headerPanel.add(submitButton);
        headerPanel.add(deleteButton);
        submitButton.addActionListener(e -> submitDraft());

        saveButton.addActionListener(e -> {
            String selectedRecipe = draftsList.getSelectedValue();
            if (selectedRecipe != null) {
                updateRecipeContent(selectedRecipe, textArea.getText(), false);
            } else {
                JOptionPane.showMessageDialog(this, "No recipe selected for update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        submitButton.addActionListener(e -> {
            String selectedRecipe = draftsList.getSelectedValue();
            if (selectedRecipe != null) {
                updateRecipeContent(selectedRecipe, textArea.getText(), true);
            } else {
                JOptionPane.showMessageDialog(this, "No recipe selected for update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createFileDirectory() {
        JPanel directoryPanel = new JPanel();
        directoryPanel.setLayout(new BoxLayout(directoryPanel, BoxLayout.Y_AXIS));
        directoryPanel.setBackground(new Color(255, 182, 193)); // Light pink background

        // Create sections
        directoryPanel.add(createSectionPanel("DRAFTS", true, draftsModel));
        directoryPanel.add(createSectionPanel("SUBMITTED", false, submittedModel));
        directoryPanel.add(createSectionPanel("RECIPES", false, recipesModel));

        JScrollPane directoryScrollPane = new JScrollPane(directoryPanel);
        directoryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(directoryScrollPane, BorderLayout.WEST);
    }

    private JPanel createSectionPanel(String title, boolean hasAddRemoveButtons, DefaultListModel<String> model) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBorder(BorderFactory.createTitledBorder(title));

        JList<String> fileList = new JList<>(model);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroller = new JScrollPane(fileList);
        sectionPanel.add(listScroller);

        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (draftsList != null && draftsList != fileList) draftsList.clearSelection();
                if (submittedList != null && submittedList != fileList) submittedList.clearSelection();
                if (recipesList != null && recipesList != fileList) recipesList.clearSelection();

                String selectedFileName = fileList.getSelectedValue();
                if (selectedFileName != null) {
                    loadContentFromDatabase(selectedFileName); // Load and display content from the database
                } else {
                    textArea.setText(""); // Clear the text area if no file is selected
                }
            }
        });

        if (hasAddRemoveButtons) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addButton = new JButton("+");
            addButton.setPreferredSize(new Dimension(20, 20));
            JButton removeButton = new JButton("-");
            removeButton.setPreferredSize(new Dimension(20, 20));

            addButton.addActionListener(e -> model.addElement("New Draft " + (model.getSize() + 1)));
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

        if ("DRAFTS".equals(title)) {
            draftsList = fileList;
        } else if ("SUBMITTED".equals(title)) {
            submittedList = fileList;
        } else if ("RECIPES".equals(title)) {
            recipesList = fileList;
        }

        return sectionPanel;
    }

    private void loadContentFromDatabase(String recipeName) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("SELECT recipeFile FROM Recipe WHERE recipeName = ? AND chefID = ?")) {
            pstmt.setString(1, recipeName);
            pstmt.setInt(2, this.chefID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Blob recipeBlob = rs.getBlob("recipeFile");
                    String content = new String(recipeBlob.getBytes(1, (int) recipeBlob.length()), StandardCharsets.UTF_8);
                    textArea.setText(content);
                } else {
                    textArea.setText("No content available."); // Handle case where no content is found
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load recipe content: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            textArea.setText("");
        }
    }






    // Call this method when the "SUBMIT" button is clicked
    private void submitDraft() {
        int selectedIndex = draftsList.getSelectedIndex();
        if (selectedIndex != -1) {
            // Move the selected item from drafts to submitted
            String selectedItem = draftsModel.get(selectedIndex);
            submittedModel.addElement(selectedItem);
            draftsModel.remove(selectedIndex);
        }
    }


    private void createTextArea() {
        textArea = new JTextArea();
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        textAreaScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(textAreaScrollPane, BorderLayout.CENTER);
        // Display the content of the first recipe (for testing
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Recipe WHERE chefID = " + chefID+ " AND recipeStatus IN ('DRAFT', 'SUBMITTED', 'RECIPE')");
            while (resultSet.next()) {
                String dishName = resultSet.getString("recipeName");
                String status = resultSet.getString("recipeStatus");
                Blob recipeFile = resultSet.getBlob("recipeFile");
                String recipeContent;
                if(recipeFile == null) {
                    recipeContent = "";
                }
                else{
                    recipeContent = new String(recipeFile.getBytes(1, (int) recipeFile.length()), StandardCharsets.UTF_8);
                }
                if ("DRAFT".equals(status)) {
                    System.out.println("Adding draft: " + dishName);
                    draftsModel.addElement(dishName);
                } else if ("SUBMITTED".equals(status)) {
                    System.out.println("Adding submitted: " + dishName);
                    submittedModel.addElement(dishName);

                } else if ("RECIPE".equals(status)) {
                    System.out.println("Adding recipe: " + dishName);
                    recipesModel.addElement(dishName);
                }
                fileMap.put(dishName, recipeContent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRecipeContent(String recipeName, String newContent, boolean isSubmitted) {
        try{
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Recipe SET recipeFile = ?, recipeStatus = ? WHERE recipeName = ? AND chefID = ?");

            pstmt.setBytes(1, newContent.getBytes(StandardCharsets.UTF_8));
            pstmt.setString(2, isSubmitted ? "SUBMITTED" : "DRAFT");
            pstmt.setString(3, recipeName);
            pstmt.setInt(4, this.chefID);

            int updatedRows = pstmt.executeUpdate();
            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Recipe updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update the recipe. Make sure the recipe exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating the recipe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
