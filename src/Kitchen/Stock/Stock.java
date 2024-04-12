package Kitchen.Stock;

import java.util.Date;

//Done by Ahmed and jiangyi
public class Stock {
    private int stockTrackingID;
    private int ingredientID;
    private int currentStockLevel;
    private int stockThreshold;

    public Stock(int stockTrackingID, int ingredientID, int currentStockLevel, int stockThreshold, Date delivery) {
        this.stockTrackingID = stockTrackingID;
        this.ingredientID = ingredientID;
        this.currentStockLevel = currentStockLevel;
        this.stockThreshold = stockThreshold;
    }

    public void setStockTrackingID(int stockTrackingID) {
        this.stockTrackingID = stockTrackingID;
    }

    public int getStockTrackingID() {
        return stockTrackingID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setCurrentStockLevel(int currentStockLevel) {
        this.currentStockLevel = currentStockLevel;
    }

    public int getCurrentStockLevel() {
        return currentStockLevel;
    }

    public void setStockThreshold(int stockThreshold) {
        this.stockThreshold = stockThreshold;
    }

    public int getStockThreshold() {
        return stockThreshold;
    }

    // Additional methods related to stock tracking could include:

    public boolean isBelowThreshold() {
        return currentStockLevel <= stockThreshold;
    }

    public void updateStockLevel(int newStockLevel) {
        this.currentStockLevel = newStockLevel;
    }

    // ... and any other methods that you feel are necessary for stock tracking.
}