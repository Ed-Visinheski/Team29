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

public class MenuCreator extends JFrame {
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


    private void saveMenu(ActionEvent actionEvent){
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
                    }
                    catch (SQLException ex) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuCreator().setVisible(true));
    }
}


