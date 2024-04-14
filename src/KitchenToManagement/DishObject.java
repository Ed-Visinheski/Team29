package KitchenToManagement;

import java.util.HashMap;

public class DishObject extends Dish {

    public DishObject(int dishID, String dishName, HashMap<Ingredient, Integer> ingredientList) {
        super(dishID, dishName, ingredientList);
    }

}
