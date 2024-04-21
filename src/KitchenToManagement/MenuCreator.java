package KitchenToManagement;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * The MenuCreator class represents a GUI application for creating and managing menus.
 */
public class MenuCreator extends JFrame {
    // GUI components
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
     * Creates a new instance of the MenuCreator class.
     */
    public MenuCreator() {
        // Frame setup
        setTitle("Menu Creator");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabbed pane setup
        tabbedPane = new JTabbedPane();

        // Create menu panel setup
        createMenuPanel = new JPanel(new BorderLayout());
        setupCreateMenuPanel();  // This method will contain your existing setup code for creating menus

        // View menu panel setup
        viewMenuPanel = new JPanel(new BorderLayout());
        setupViewMenuPanel();  // This method will be defined to view saved menus

        // Adding tabs to the tabbed pane
        tabbedPane.addTab("Create Menu", createMenuPanel);
        tabbedPane.addTab("View Menus", viewMenuPanel);

        // Adding the tabbed pane to the frame
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Sets up the view menu panel.
     * This method will be defined to view saved menus.
     */
    private void setupViewMenuPanel() {
        // Panel setup
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
     * Deletes the selected menu from the database and updates the UI.
     * @param table The table containing the menus.
     */
    private void deleteSelectedMenu(JTable table) {
        // Get the selected row index
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the menu ID from the selected row
        int menuId = (Integer) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this menu?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete the menu from the database
            try (Connection conn = getDatabaseConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Menu WHERE menuId = ?")) {
                stmt.setInt(1, menuId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    // Remove the row from the table if deletion is successful
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Menu deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Clear details panel if the deleted menu was displayed
                    detailsPanel.removeAll();
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
     * Displays the details of the selected menu.
     * @param menuId The ID of the selected menu.
     */
    private void displayMenuDetails(int menuId) {
        detailsPanel.removeAll(); // Clear previous contents

        // Fetch course details from the database
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

        // Refresh the details panel
        detailsPanel.revalidate();
        detailsPanel.repaint();
        splitPane.setRightComponent(new JScrollPane(detailsPanel));  // Ensure the updated panel is visible
        detailsPanel.revalidate();
        detailsPanel.repaint();
        splitPane.revalidate();
    }

    /**
     * Loads menus into the table.
     * @param table The table to load menus into.
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

        // Fetch menus from the database and populate the table
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

        // Set the model to the table and add a mouse listener to handle double-click events
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
     * Sets up the create menu panel.
     * This method will contain your existing setup code for creating menus.
     */
    private void setupCreateMenuPanel() {
        // Top panel setup
        JPanel topPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        addComponentsToTopPanel(topPanel);

        // Courses panel setup
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        mainScrollPane = new JScrollPane(coursesPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setPreferredSize(new Dimension(780, 500));

        // Add components to create menu panel
        createMenuPanel.add(topPanel, BorderLayout.NORTH);
        createMenuPanel.add(mainScrollPane, BorderLayout.CENTER);

        // Load dishes from the database
        loadDishes();
    }

    /**
     * Adds components to the top panel of the create menu panel.
     * @param panel The panel to add components to.
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

        // Add components to the top panel
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
     * Saves the menu to the database.
     * @param actionEvent The action event.
     */
    private void saveMenu(ActionEvent actionEvent){
        // Get input values
        String menuName = menuNameField.getText();
        String description = descriptionArea.getText();
        String preparationTime = preparationTimeField.getText();
        String date = dateField.getText();

        // Validate input
        if (menuName.isEmpty() || description.isEmpty() || preparationTime.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save menu to the database
        try (Connection conn = getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Menu (menuName, menuDescription, menuDate, menuStatus, preparationTime ) VALUES (?, ?, ?, ?, ?)",
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
     * Saves the courses of the menu to the database.
     * @param menuID The ID of the menu.
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
     * Saves the dishes of the course to the database.
     * @param coursePanel The panel representing the course.
     * @param courseID The ID of the course.
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
     * @param actionEvent The action event.
     */
    private void setDate(ActionEvent actionEvent) {
        // Method implementation for setting the date can be added here
    }

    /**
     * Loads dishes from the database.
     */
    private void loadDishes() {
        try (Connection conn = getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dishID, dishName FROM Dish")) {
            while (rs.next()) {
                dishMap.put(rs.getInt("dishID"), rs.getString("dishName"));
                dishesModel.addElement(rs.getString("dishName"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading dishes: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a course to the menu.
     * @param e The action event.
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
     * Adds a dish to the course.
     * @param coursePanel The panel representing the course.
     * @param dishDropdown The dropdown containing dish options.
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
     * Establishes a database connection.
     * @return The database connection.
     * @throws SQLException If a database access error occurs.
     */
    private Connection getDatabaseConnection() throws SQLException {
        return Kitchen.DatabaseManager.getConnection();  // Assuming Kitchen.DatabaseManager is correctly set up
    }

    /**
     * @deprecated
     * The main method to start the application.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuCreator().setVisible(true));
    }
}
