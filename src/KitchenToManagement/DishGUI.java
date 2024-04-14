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

public class DishGUI extends JFrame {
    private JTextField dishNameField;
    private JTextField photoPathField; // Text field to display photo file name
    private JTable ingredientsTable;
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

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addIngredient());
        removeButton.addActionListener(e -> removeSelectedIngredient());
        saveButton.addActionListener(e -> saveDish());
        photoButton.addActionListener(e -> uploadPhoto());

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
                    String ingredient = (String) tableModel.getValueAt(0, 0);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DishGUI().setVisible(true));
    }
}
