package Kitchen;
import javax.swing.*;

public class Stock {
    // Main Stock Management
    private JPanel StockPanel;
    private JButton addButton, updateButton, saveButton, deleteButton, searchButton;
    private JTextField textIngredientID, textStockLevel, textStockThreshold, textDeliveryArrivalDate, textSearch;
    private JScrollPane TableScrollPane; private JTable stockTable;

    // Menu sidebar
    private JPanel MenuPanel; private JLabel logo;
    private JButton dashboardButton, menuManagementButton, inventoryManagementButton, ordersAndServicesButton, settingsButton, signInButton, signOutButton;

}
