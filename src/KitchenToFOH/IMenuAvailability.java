package KitchenToFOH;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Interface defining the availability of dishes within a menu.
 * It includes methods for setting and changing the availability of dishes, retrieving available dishes,
 * dish preparation times, and handling dishes ready for serving or collected.
 */
public interface IMenuAvailability {

    /**
     * Sets the date of the menu availability.
     *
     * @param date The date to be set for the menu availability.
     */
    void setLocalDate(LocalDate date);
    /**
     * Retrieves the date of the menu availability.
     *
     * @return The date of the menu availability.
     */
    LocalDate getLocalDate();

    /**
     * Sets the availability status for multiple dishes at once.
     *
     * @param dishAvailable A HashMap with dishes as keys and their availability status as Boolean values.
     */
    void setDishAvailability(HashMap<Dish, Boolean> dishAvailable);

    /**
     * Sets or updates the availability status for the entire menu.
     *
     * @param dishAvailable A HashMap with dishes as keys and their availability status as Boolean values.
     */
    void setMenuAvailability(HashMap<Dish, Boolean> dishAvailable);

    /**
     * Retrieves a map of all dishes with their current availability status.
     *
     * @return A HashMap where each Dish is mapped to a Boolean indicating its availability.
     */
    HashMap<Dish, Boolean> getAvailableDishes();

    /**
     * Changes the availability status of a single dish.
     *
     * @param dish The dish for which the availability is to be changed.
     * @param available The new availability status of the dish (true for available, false otherwise).
     */
    void changeDishAvailability(Dish dish, boolean available);


    /**
     * Retrieves the time required to prepare a dish. Implementation is left to the concrete class.
     *
     * @return The preparation time for the dish as a String.
     */
    String getDishTimeRequired(Dish dish);

    /**
     * Retrieves a set of dishes that have been removed from the menu.
     *
     * @return A HashSet of removed dishes.
     */
    HashSet<Dish> removedDishes();

}
