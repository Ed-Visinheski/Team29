package KitchenToManagement;

public class IngredientObject extends Ingredient {
    private int ingredientID;
    private String ingredientName;
    private int ingredientStockLevel;
    private int lowStockThreshold;

    public IngredientObject(int ingredientID, String ingredientName, int ingredientStockLevel, int lowStockThreshold) {
        this.ingredientID = ingredientID;
        this.ingredientName = ingredientName;
        this.ingredientStockLevel = ingredientStockLevel;
        this.lowStockThreshold = lowStockThreshold;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientStockLevel(int ingredientStockLevel) {
        this.ingredientStockLevel = ingredientStockLevel;
    }

    public int getIngredientStockLevel() {
        return ingredientStockLevel;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
}