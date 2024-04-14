package Kitchen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LogInUI extends JFrame implements ActionListener {

    private Container container;
    private JLabel userLabel;
    private JTextField userTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LogInUI() {
        setTitle("Login Page");
        setBounds(10, 10, 370, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        container = getContentPane();
        container.setLayout(null);

        userLabel = new JLabel("Username");
        userLabel.setBounds(50, 150, 100, 30);
        container.add(userLabel);

        userTextField = new JTextField();
        userTextField.setBounds(150, 150, 150, 30);
        container.add(userTextField);

        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(50, 220, 100, 30);
        container.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 220, 150, 30);
        container.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(50, 300, 100, 30);
        loginButton.addActionListener(this);
        container.add(loginButton);
    }




    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String userName = userTextField.getText();
            String password = String.valueOf(passwordField.getPassword());
            // Simple login success logic, replace with real authentication
            if (!userName.trim().isEmpty() && !password.trim().isEmpty()) {
                UserAuthentication userAuthentication = new UserAuthentication();
                boolean isAuthenticated = userAuthentication.checkUserCredentials(userName, password);
                if(isAuthenticated) {
                    int chefID = userAuthentication.getChefID();
                    // Dispose the current window
                    this.dispose();
                    // Open the FileRetrievalUI
                    EventQueue.invokeLater(() -> {
                        try {
                            DishConstructionUI frame = new DishConstructionUI(chefID);
                            frame.setVisible(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Login Failed");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Login Failed");
            }
        }
    }



    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LogInUI frame = new LogInUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
