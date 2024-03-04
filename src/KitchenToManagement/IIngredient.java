package KitchenToManagement;

/**
 * Interface defining the ingredient object. It specifies methods for setting and retrieving
 * the name, stock level, and low stock threshold of ingredients. This interface is designed to ensure that
 * ingredient-related information can be managed in a consistent manner across culinary or restaurant management applications.
 */
public interface IIngredient {

    void setIngredientID(int ingredientID);

    int getIngredientID();

    /**
     * Sets the name of the ingredient. This method allows changing the ingredient's name,
     * which is useful for updating or correcting ingredient information.
     *
     * @param ingredientName The name to be assigned to the ingredient.
     */
    void setIngredientName(String ingredientName);

    /**
     * Retrieves the name of the ingredient. This method enables access to the ingredient's name,
     * facilitating ingredient identification and management.
     *
     * @return The name of the ingredient.
     */
    String getIngredientName();

    /**
     * Sets the stock level of the ingredient. This method is crucial for inventory management,
     * allowing the tracking of ingredient availability.
     *
     * @param ingredientStockLevel The stock level to be assigned to the ingredient.
     */
    void setIngredientStockLevel(int ingredientStockLevel);

    /**
     * Retrieves the stock level of the ingredient. This method provides access to the current stock level,
     * supporting inventory management and restocking decisions.
     *
     * @return The current stock level of the ingredient.
     */
    int getIngredientStockLevel();

    /**
     * Sets the low stock threshold for the ingredient. This threshold indicates the stock level at which
     * the ingredient is considered to be running low, triggering restocking actions.
     *
     * @param lowStockThreshold The threshold value below which the stock level is considered low.
     */
    void setLowStockThreshold(int lowStockThreshold);

    /**
     * Retrieves the low stock threshold for the ingredient. This method allows access to the threshold value,
     * aiding in inventory management and alerting when stock levels approach critical lows.
     *
     * @return The threshold value below which the stock level of the ingredient is considered low.
     */
    int getLowStockThreshold();
}
