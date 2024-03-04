package KitchenToFOH;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Abstract class that implements the IServer interface, managing orders and dishes within those orders.
 * It allows for adding, retrieving, and removing dishes from an order.
 */
public abstract class Server implements IServer {

    /**
     * Stores the current order ID. This could be used to track or identify a specific order.
     */
    private int orderID;


    /**
     * Indicates whether a dish is ready to be served. Initially set to false.
     */
    private boolean dishReadyToServe = false;

    /**
     * Maps each order ID to a set of dishes that are included in that order.
     */
    private HashMap<Integer, HashSet<Dish>> order;

    /**
     * Adds a set of dishes to the current order. If the order already exists, it updates the existing set of dishes.
     *
     * @param dish The set of dishes to be added to the current order.
     */
    public void addDishToOrder(HashSet<Dish> dish) {
        order.put(orderID, dish);
    }

    /**
     * Retrieves the set of dishes included in the current order.
     *
     * @return A HashSet of Dish objects representing the dishes in the current order.
     */
    public HashSet<Dish> getDishesInOrder(){
        return order.get(orderID);
    }

    /**
     * Removes a specific dish from the current order.
     *
     * @param dish The Dish object to be removed from the order.
     */
    public void removeDishFromOrder(Dish dish){
        order.get(orderID).remove(dish);
    }


    /**
     * Retrieves the time required to prepare a specified dish.
     * This method provides a way to access the preparation time of a dish,
     * which is essential for planning and scheduling in a culinary setting.
     *
     * @param dish The dish for which the preparation time is required.
     * @return The time required to prepare the dish as a String.
     */
    public String getDishTime(Dish dish){
        return dish.getTimeRequired();
    }

    /**
     * Marks a dish as ready to be served to the customer.
     */
    @Override
    public void callServerToPass(){
        this.dishReadyToServe = true;
    }

    /**
     * Marks a dish as no longer ready to be served, indicating it has been collected.
     */
    @Override
    public void dishCollected() {
        this.dishReadyToServe = false;
    }
}
