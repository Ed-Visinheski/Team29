package Kitchen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;

public class DishConstructionUI extends JFrame {
    private JPanel draftsPanel;
    private ArrayList<JButton> draftButtons;
    private JTextArea textArea;
    private DefaultListModel<String> draftsModel;
    private DefaultListModel<String> submittedModel;
    private DefaultListModel<String> recipesModel;
    private JList<String> draftsList;
    private JScrollPane draftsScrollPane;

    public DishConstructionUI() {
        setTitle("File Manager");
        setSize(1280, 720);
        setMinimumSize(new Dimension(1280, 720)); // Set minimum size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        //getContentPane().setBackground(new Color(255, 182, 193)); // Light pink background
        draftsModel = new DefaultListModel<>();
        submittedModel = new DefaultListModel<>();
        recipesModel = new DefaultListModel<>();
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
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setBorder(BorderFactory.createTitledBorder(title));

        JList<String> fileList = new JList<>(model);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroller = new JScrollPane(fileList);
        sectionPanel.add(listScroller);

        if ("DRAFTS".equals(title)) {
            draftsList = fileList; // Assign the draftsList reference
        }

        if (hasAddRemoveButtons) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addButton = new JButton("+");
            JButton removeButton = new JButton("-");

            // Make the add/remove buttons smaller
            addButton.setPreferredSize(new Dimension(20, 20));
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

        return sectionPanel;
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


    private void addDraft(ActionEvent e) {
        JButton newButton = new JButton("New Draft " + (draftButtons.size() + 1));
        styleButton(newButton, new Color(255, 105, 180)); // Darker pink
        draftButtons.add(newButton);
        draftsPanel.add(newButton);
        draftsPanel.revalidate();
        draftsPanel.repaint();
    }

    private void removeDraft(ActionEvent e) {
        if (draftButtons.size() > 0) {
            JButton buttonToRemove = draftButtons.remove(draftButtons.size() - 1);
            draftsPanel.remove(buttonToRemove);
            draftsPanel.revalidate();
            draftsPanel.repaint();
        }
    }

    private void createTextArea() {
        textArea = new JTextArea();
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
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
        String url = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29";
        String user = "in2033t29_d";
        String password = "m8mHWvcTuXA";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            // Drafts
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT name FROM drafts WHERE user_id = YOUR_USER_ID");
            while (rs.next()) {
                draftsModel.addElement(rs.getString("name"));
            }

            // Submitted
            rs = stmt.executeQuery("SELECT name FROM submitted WHERE user_id = YOUR_USER_ID");
            while (rs.next()) {
                submittedModel.addElement(rs.getString("name"));
            }

            // Recipes
            rs = stmt.executeQuery("SELECT name FROM recipes WHERE user_id = YOUR_USER_ID");
            while (rs.next()) {
                recipesModel.addElement(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DishConstructionUI::new);
    }
}
