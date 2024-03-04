package KitchenToFOH;

import java.util.HashSet;

/**
 * Interface defining the actions a server can perform in respect to managing orders.
 * It provides methods for adding dishes to an order, retrieving dishes in an order, removing dishes from an order.
 */
public interface IServer {

    /**
     * Adds a set of dishes to an order. Implementations should specify how the dishes are added
     * and associated with a specific order.
     *
     * @param dish The set of dishes to be added to the order.
     */
    void addDishToOrder(HashSet<Dish> dish);

    /**
     * Retrieves the set of dishes currently in the order. Implementations should return the dishes
     * associated with the current order context.
     *
     * @return A HashSet containing the dishes in the current order.
     */
    HashSet<Dish> getDishesInOrder();

    /**
     * Removes a specific dish from the order. Implementations should handle the removal of the dish
     * from the current order context.
     *
     * @param dish The dish to be removed from the order.
     */
    void removeDishFromOrder(Dish dish);


    /**
     * Signals that a dish is ready to be served. The specific implementation of this action is left to the concrete class.
     */
    void callServerToPass();

    /**
     * Indicates that a dish has been collected for serving. The specific implementation is left to the concrete class.
     */
    void dishCollected();

    /**
     * Retrieves the time required to prepare a specified dish.
     * This method provides a way to access the preparation time of a dish,
     * which is essential for planning and scheduling in a culinary setting.
     *
     * @param dish The dish for which the preparation time is required.
     * @return The time required to prepare the dish as a String.
     */
    String getDishTime(Dish dish);
}
