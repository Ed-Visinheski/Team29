package Kitchen;
//Done by Ahmed
public class StockTracking {
    private int stockTrackingID;
    private int ingredientID;
    private int currentStockLevel;
    private int minimumRequiredStock;

    public StockTracking(int stockTrackingID, int ingredientID, int currentStockLevel, int minimumRequiredStock) {
        this.stockTrackingID = stockTrackingID;
        this.ingredientID = ingredientID;
        this.currentStockLevel = currentStockLevel;
        this.minimumRequiredStock = minimumRequiredStock;
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

    public void setMinimumRequiredStock(int minimumRequiredStock) {
        this.minimumRequiredStock = minimumRequiredStock;
    }

    public int getMinimumRequiredStock() {
        return minimumRequiredStock;
    }

    // Additional methods related to stock tracking could include:

    public boolean isStockBelowThreshold() {
        return currentStockLevel <= minimumRequiredStock;
    }

    public void updateStockLevel(int newStockLevel) {
        this.currentStockLevel = newStockLevel;
    }

    // ... and any other methods that you feel are necessary for stock tracking.
}