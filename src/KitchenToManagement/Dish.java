package KitchenToManagement;

import java.util.HashMap;

/**
 * Abstract class representing a dish, providing  dish details such as a unique identifier
 * and a list of ingredients with their quantities. This class implements
 * the IDish interface to ensure this class remains consistent with the specification.
 */
public abstract class Dish implements IDish {
    /**
     * Unique identifier for the dish, used to distinguish each dish within the system.
     */
    private int dishID;

    /**
     * The name of the dish.
     */
    private String dishName;

    /**
     * Sets the name of the dish. This method allows changing the dish's name,
     * which is useful for updating or correcting dish information.
     *
     * @param dishName The name to be assigned to the dish.
     */

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    /**
     * Retrieves the name of the dish. This method enables access to the dish's name,
     * facilitating dish identification and management.
     *
     * @return The name of the dish.
     */
    public String getDishName() {
        return dishName;
    }


    /**
     * A map of ingredients required to prepare the dish, keyed by the ingredient object with quantities as values.
     * This allows for detailed recipe management and inventory checks.
     */
    private HashMap<Ingredient, Integer> ingredientList;

    /**
     * Sets the unique identifier for this dish, used for uniquely identifying the dish
     * within the system.
     *
     * @param dishID The unique ID to be assigned to the dish.
     */
    @Override
    public void setDishID(int dishID) {
        this.dishID = dishID;
    }

    /**
     * Retrieves the ID of this dish. This method enables access to the dish's ID,
     * which makes search, update, or delete database queries simpler.
     *
     * @return The unique ID of the dish.
     */
    @Override
    public int getDishID() {
        return dishID;
    }

    /**
     * Sets the list of ingredients required to prepare the dish, along with their respective quantities.
     *
     * @param ingredientList A HashMap where each key is an ingredient object and its value is the quantity required.
     */
    @Override
    public void setIngredientList(HashMap<Ingredient, Integer> ingredientList) {
        this.ingredientList = ingredientList;
    }

    /**
     * Retrieves the list of ingredients required for the dish, along with their respective quantities.
     * This method supports recipe management and allows for inventory assessments.
     *
     * @return A HashMap where each key is an ingredient object and its value is the quantity required.
     */
    @Override
    public HashMap<Ingredient, Integer> getIngredientList() {
        return ingredientList;
    }
}
