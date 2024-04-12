package Kitchen.Stock;

import java.util.Date;

//Done by Ahmed and jiangyi
public class Stock {
    private int ingredientID;
    private int stockLevel;
    private int stockThreshold;
    private Date deliveryArrivalDate;

    public Stock(int ingredientID, int stockLevel, int stockThreshold, Date deliveryArrivalDate) {
        this.ingredientID = ingredientID;
        this.stockLevel = stockLevel;
        this.stockThreshold = stockThreshold;
        this.deliveryArrivalDate = deliveryArrivalDate;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void seStockLevel(int currentStockLevel) {
        this.stockLevel = currentStockLevel;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockThreshold(int stockThreshold) {
        this.stockThreshold = stockThreshold;
    }

    public int getStockThreshold() {
        return stockThreshold;
    }

    public boolean isBelowThreshold() {
        return stockLevel <= stockThreshold;
    }

    public void updateStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public Date getDeliveryArrivalDate() {
        return deliveryArrivalDate;
    }

    public void setDeliveryArrivalDate(Date deliveryArrivalDate) {
        this.deliveryArrivalDate = deliveryArrivalDate;
    }
}