package KitchenToManagement;

/**
 * The IngredientObject class represents an ingredient with additional properties
 * such as ingredient ID, name, stock level, and low stock threshold.
 */
public class IngredientObject extends Ingredient {
    private int ingredientID; // Unique identifier for the ingredient
    private String ingredientName; // Name of the ingredient
    private int ingredientStockLevel; // Current stock level of the ingredient
    private int lowStockThreshold; // Threshold indicating low stock level

    /**
     * Constructs an IngredientObject with the specified properties.
     * @param ingredientID The unique identifier for the ingredient.
     * @param ingredientName The name of the ingredient.
     * @param ingredientStockLevel The current stock level of the ingredient.
     * @param lowStockThreshold The threshold indicating low stock level.
     */
    public IngredientObject(int ingredientID, String ingredientName, int ingredientStockLevel, int lowStockThreshold) {
        this.ingredientID = ingredientID;
        this.ingredientName = ingredientName;
        this.ingredientStockLevel = ingredientStockLevel;
        this.lowStockThreshold = lowStockThreshold;
    }

    /**
     * Sets the ingredient ID.
     * @param ingredientID The ingredient ID to set.
     */
    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    /**
     * Gets the ingredient ID.
     * @return The ingredient ID.
     */
    public int getIngredientID() {
        return ingredientID;
    }

    /**
     * Sets the ingredient name.
     * @param ingredientName The ingredient name to set.
     */
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    /**
     * Gets the ingredient name.
     * @return The ingredient name.
     */
    public String getIngredientName() {
        return ingredientName;
    }

    /**
     * Sets the ingredient stock level.
     * @param ingredientStockLevel The ingredient stock level to set.
     */
    public void setIngredientStockLevel(int ingredientStockLevel) {
        this.ingredientStockLevel = ingredientStockLevel;
    }

    /**
     * Gets the ingredient stock level.
     * @return The ingredient stock level.
     */
    public int getIngredientStockLevel() {
        return ingredientStockLevel;
    }

    /**
     * Sets the low stock threshold.
     * @param lowStockThreshold The low stock threshold to set.
     */
    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    /**
     * Gets the low stock threshold.
     * @return The low stock threshold.
     */
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
}
