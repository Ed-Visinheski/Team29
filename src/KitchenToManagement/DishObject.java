package KitchenToManagement;

import java.util.HashMap;

/**
 * The DishObject class represents a dish object with an extended constructor.
 * It extends the Dish class and allows for creating dish objects with additional parameters.
 */
public class DishObject extends Dish {

    /**
     * Constructs a new DishObject with the specified dish ID, dish name, and ingredient list.
     *
     * @param dishID         The ID of the dish.
     * @param dishName       The name of the dish.
     * @param ingredientList A HashMap representing the list of ingredients and their quantities.
     */
    public DishObject(int dishID, String dishName, HashMap<Ingredient, Integer> ingredientList) {
        super(dishID, dishName, ingredientList); // Call the constructor of the superclass (Dish)
    }
}
