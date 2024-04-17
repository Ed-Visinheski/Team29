package Kitchen;

import KitchenToManagement.KitchenManagementApp;
import KitchenToManagement.KitchenToManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * The LogInUI class represents the user interface for the login page.
 */
public class LogInUI extends JFrame implements ActionListener {

    private Container container;
    private JLabel userLabel;
    private JTextField userTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;

    /**
     * Constructs a new LogInUI object.
     * This method initializes the login page UI components.
     */
    public LogInUI() {
        setTitle("Login Page"); // Set the title of the window
        setBounds(10, 10, 370, 600); // Set the size and position of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Specify the close operation
        setResizable(false); // Make the window not resizable
        setLocationRelativeTo(null); // Center the window on the screen
        container = getContentPane(); // Get the content pane of the frame
        container.setLayout(null); // Set layout to null (absolute positioning)

        // Create and configure the username label
        userLabel = new JLabel("Username");
        userLabel.setBounds(50, 150, 100, 30); // Set position and size
        container.add(userLabel); // Add the label to the container

        // Create and configure the username text field
        userTextField = new JTextField();
        userTextField.setBounds(150, 150, 150, 30); // Set position and size
        container.add(userTextField); // Add the text field to the container

        // Create and configure the password label
        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(50, 220, 100, 30); // Set position and size
        container.add(passwordLabel); // Add the label to the container

        // Create and configure the password field
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 220, 150, 30); // Set position and size
        container.add(passwordField); // Add the password field to the container

        // Create and configure the login button
        loginButton = new JButton("Login");
        loginButton.setBounds(50, 300, 100, 30); // Set position and size
        loginButton.addActionListener(this); // Add action listener to handle button click
        container.add(loginButton); // Add the button to the container
    }

    /**
     * Handles action events for the login button.
     *
     * @param e The action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) { // Check if the login button is clicked
            String userName = userTextField.getText(); // Get the entered username
            String password = String.valueOf(passwordField.getPassword()); // Get the entered password
            // Simple login success logic, replace with real authentication
            if (!userName.trim().isEmpty() && !password.trim().isEmpty()) { // Check if username and password are not empty
                UserAuthentication userAuthentication = new UserAuthentication(); // Create a UserAuthentication object
                boolean isAuthenticated = userAuthentication.checkUserCredentials(userName, password); // Check user credentials
                if(isAuthenticated) { // If authentication succeeds
                    int chefID = userAuthentication.getChefID(); // Get the chef ID
                    // Dispose the current window
                    this.dispose();
                    // Open the KitchenManagementApp window
                    EventQueue.invokeLater(() -> {
                        try {
                            KitchenManagementApp frame = new KitchenManagementApp(); // Create a new KitchenManagementApp object
                            frame.setVisible(true); // Make the window visible
                        } catch (Exception ex) {
                            ex.printStackTrace(); // Print stack trace if an exception occurs
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Login Failed"); // Show login failed message
                }
            } else {
                JOptionPane.showMessageDialog(this, "Login Failed"); // Show login failed message
            }
        }
    }

    /**
     * The main method of the application.
     * It creates an instance of LogInUI and makes it visible.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LogInUI frame = new LogInUI(); // Create a new LogInUI object
                frame.setVisible(true); // Make the window visible
            } catch (Exception e) {
                e.printStackTrace(); // Print stack trace if an exception occurs
            }
        });
    }
}