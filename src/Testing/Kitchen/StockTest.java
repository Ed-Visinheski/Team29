package Testing.Kitchen;
import Kitchen.Stock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StockTest {

    private Stock stock;
    private JFrame frame;
    private JTable stockTable;
    private JTextField textIngredientID, textStockLevel, textStockThreshold, textIngredientName, textDeliveryArrivalDate;
    private Object[][] originalData;

    @Before
    public void setUp() {
        stock = new Stock();
        frame = stock.getFrame();
        stockTable = stock.getStockTable();
        textIngredientID = stock.getTextIngredientID();
        textStockLevel = stock.getTextStockLevel();
        textStockThreshold = stock.getTextStockThreshold();
        textIngredientName = stock.getTextIngredientName();
        textDeliveryArrivalDate = stock.getTextDeliveryArrivalDate();
    }

    @After
    public void restore() {
        //
    }


    @Test
    public void testInitializeUI() {
        // Check if the frame is initialized
        assertNotNull(frame);
        // Check if the layout of the frame is BorderLayout
        assertEquals(BorderLayout.class, frame.getLayout().getClass());
        // Check if the frame is visible
        assertEquals(true, frame.isVisible());
    }

    @Test
    public void testLoadStockTable() {
        // Initially, stockTable should not be null
        assertNotNull(stockTable);

        // Load the stock table
        stock.loadStockTable();

        // After loading, stockTable should have a table model set
        assertNotNull(stockTable.getModel());
    }

    @Test
    public void testSearchStock() {
        // Initially, textIngredientID should be empty
        assertEquals("", textIngredientID.getText());

        // Set ingredient ID for testing
        textIngredientID.setText("2");

        // Get the initial value of textIngredientID
        String initialText = textIngredientID.getText();

        // Simulate button click
        stock.searchStock(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "search"));

        // After search, textIngredientID should remain unchanged
        assertEquals(initialText, textIngredientID.getText());
    }

    @Test
    public void testUpdateStock() {
        // Initially, textIngredientID should be empty
        assertEquals("", textIngredientID.getText());

        // Set ingredient ID for testing
        textIngredientID.setText("1");

        // Set other fields for testing
        textStockLevel.setText("100");
        textStockThreshold.setText("50");
        textIngredientName.setText("Test Ingredient");
        textDeliveryArrivalDate.setText("2024-04-15");

        // Simulate button click
        stock.updateStock(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "update"));
    }


}

