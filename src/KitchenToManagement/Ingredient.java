package KitchenToManagement;

/**
 * Abstract class representing an ingredient. Implements the IIngredient interface
 * to manage ingredient details, including its name, stock level, and low stock threshold.
 */
public abstract class Ingredient implements IIngredient {
    /**
     * The name of the ingredient.
     */
    private String ingredientName;

    /**
     * The unique identifier for the ingredient.
     */
    private int ingredientID;

    /**
     * The current stock level of the ingredient.
     */
    private int ingredientStockLevel;

    /**
     * The threshold below which the stock level of the ingredient is considered low.
     */
    private int lowStockThreshold;

    /**
     * Sets the unique identifier for this ingredient.
     *
     * @param ingredientID The unique ID to be assigned to the ingredient.
     */

    @Override
    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }
    /**
     * Retrieves the ID of this ingredient. This method enables access to the ingredient's ID,
     * which makes search, update, or delete database queries simpler.
     *
     * @return The unique ID of the ingredient.
     */

    @Override
    public int getIngredientID() {
        return ingredientID;
    }

    /**
     * Sets the name of this ingredient.
     *
     * @param ingredientName The name to be assigned to the ingredient.
     */
    @Override
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    /**
     * Retrieves the name of this ingredient.
     *
     * @return The name of the ingredient.
     */
    @Override
    public String getIngredientName() {
        return ingredientName;
    }

    /**
     * Sets the stock level of this ingredient.
     *
     * @param ingredientStockLevel The stock level to be assigned to the ingredient.
     */
    @Override
    public void setIngredientStockLevel(int ingredientStockLevel) {
        this.ingredientStockLevel = ingredientStockLevel;
    }

    /**
     * Retrieves the stock level of this ingredient.
     *
     * @return The current stock level of the ingredient.
     */
    @Override
    public int getIngredientStockLevel() {
        return ingredientStockLevel;
    }

    /**
     * Sets the low stock threshold for this ingredient.
     *
     * @param lowStockThreshold The threshold value below which the stock level is considered low.
     */
    @Override
    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    /**
     * Retrieves the low stock threshold for this ingredient.
     *
     * @return The threshold value below which the stock level of the ingredient is considered low.
     */
    @Override
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
}
