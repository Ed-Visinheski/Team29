package Kitchen;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BaseGUI extends JFrame {
    private JPanel BaseGUIPanel;
    private JPanel MenuPanel;
    private JLabel logo;
    private JButton dashboardButton;
    private JButton menuManagementButton;
    private JButton StockManagementButton;
    private JButton WasteManagementButton;
    private JButton ordersAndServicesButton;
    private JButton settingsButton;
    private JButton signOutButton;

    public BaseGUI(){
        ImageIcon img = new ImageIcon("src/Kitchen/Img/Lancaster.jpeg");
        setIconImage(img.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }
}
