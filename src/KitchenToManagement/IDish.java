package KitchenToManagement;

import java.util.HashMap;

/**
 * Interface defining the dish. It specifies methods for setting and retrieving
 * a dish's unique identifier, managing its list of ingredients and their quantities, and handling information.
 */
public interface IDish {

   /**
    * Sets the unique ID for the dish, used for uniquely identifying each dish
    * within the system.
    *
    * @param dishID The unique ID to be assigned to the dish.
    */
   void setDishID(int dishID);

   /**
    * Retrieves the ID of this dish. This method enables access to the dish's ID,
    * which makes search, update, or delete database queries simpler.
    *
    * @return The unique ID of the dish.
    */
   int getDishID();

    /**
     * Sets the name of the dish. This method allows changing the dish's name,
     * which is useful for updating or correcting dish information.
     *
     * @param dishName The name to be assigned to the dish.
     */

   void setDishName(String dishName);

    /**
     * Retrieves the name of the dish. This method enables access to the dish's name,
     * facilitating dish identification and management.
     *
     * @return The name of the dish.
     */

   String getDishName();

   /**
    * Sets the list of ingredients required to prepare the dish, along with their respective quantities.
    *
    * @param ingredientList A HashMap where each key is an ingredient object and its value is the quantity required.
    */
   void setIngredientList(HashMap<Ingredient, Integer> ingredientList);

   /**
    * Retrieves the list of ingredients required for the dish, along with their respective quantities.
    * This method supports recipe management and allows for inventory assessments.
    *
    * @return A HashMap where each key is an ingredient object and its value is the quantity required.
    */
   HashMap<Ingredient, Integer> getIngredientList();

}
