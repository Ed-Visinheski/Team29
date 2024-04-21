package Testing.Kitchen;

import Kitchen.LogInUI;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;


import static junit.framework.TestCase.*;

public class LogInUITest {
    private LogInUI logInUI;
    private JTextField userTextField;
    private JPasswordField passwordField;
    private JButton loginButton;

    @Before
    public void setUp() {
        logInUI = new LogInUI();
        logInUI.display = false;
        userTextField = logInUI.getUserTextField();
        passwordField = logInUI.getPasswordField();
        loginButton = logInUI.getLoginButton();
    }

    @Test
    public void testEmptyLogin() {
        // Set empty username and password
        userTextField.setText("");
        passwordField.setText("");

        // Simulate button click
        loginButton.doClick();
        assertFalse(logInUI.getAuthenticated());
    }

    @Test
    public void testValidLogin() {
        // Set valid username and password
        userTextField.setText("test");
        passwordField.setText("password");

        // Simulate button click
        loginButton.doClick();
        assertTrue(logInUI.getAuthenticated());
    }

    @Test
    public void testInvalidLogin() {
        // Set invalid username and password
        userTextField.setText("invalidUser");
        passwordField.setText("invalidPassword");

        // Simulate button click
        loginButton.doClick();
        assertFalse(logInUI.getAuthenticated());
    }
}
