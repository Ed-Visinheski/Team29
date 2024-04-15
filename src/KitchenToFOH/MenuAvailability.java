package KitchenToFOH;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Abstract class that implements the IMenuAvailability interface to manage dish availability within a menu.
 * This class keeps track of which dishes are available and whether they are ready to be served.
 */
public class MenuAvailability implements IMenuAvailability {
    /**
     * Stores the availability status (true for available, false otherwise) for each dish.
     */
    private HashMap<Dish, Boolean> dishAvailability;


    /**
     * Stores the date of the menu availability.
     */
    private LocalDate date;

    public MenuAvailability(LocalDate date, HashMap<Dish, Boolean> dishAvailability){
        this.date = date;
        this.dishAvailability = dishAvailability;
    }



    /**
     * Sets the date of the menu availability.
     * @param date
     */
    public void setLocalDate(LocalDate date){
        this.date = date;
    }

    /**
     * Retrieves the date of the menu availability.
     * @return
     */

    public LocalDate getLocalDate(){
        return date;
    }

    @Override
    public void setDishAvailability(HashMap<Dish, Boolean> dishAvailable) {
       for(Dish dish : dishAvailable.keySet()){
           dishAvailability.put(dish, dishAvailable.get(dish));
       }
    }


    /**
     * Sets the availability of dishes in the menu.
     *
     * @param dishAvailable A HashMap containing dishes and their availability status.
     */
    @Override
    public void setMenuAvailability(HashMap<Dish, Boolean> dishAvailable){
        this.dishAvailability = dishAvailable;
    }

    /**
     * Retrieves a map of dishes and their availability status.
     *
     * @return A HashMap of dishes and their corresponding availability.
     */
    @Override
    public HashMap<Dish, Boolean> getAvailableDishes(){
        return dishAvailability;
    }

    /**
     * Changes the availability status of a specific dish.
     *
     * @param dish The dish whose availability is to be changed.
     * @param available The new availability status of the dish (true for available, false otherwise).
     */
    @Override
    public void changeDishAvailability(Dish dish, boolean available){
        dishAvailability.put(dish, available);
    }


    /**
     * Retrieves the time required to prepare a dish. Implementation is specific to subclasses.
     *
     * @return The preparation time for the dish as a String.
     */
    @Override
    public String getDishTimeRequired(Dish dish){
        return dish.getTimeRequired();
    }

    /**
     * Retrieves a set of dishes that have been removed from the menu. Implementation is specific to subclasses.
     *
     * @return A HashSet of removed dishes.
     */

    @Override
    public HashSet<Dish> removedDishes(){
        HashSet<Dish> removedDishes = new HashSet<>();
        for (Dish dish : dishAvailability.keySet()) {
            if (!dishAvailability.get(dish)) {
                removedDishes.add(dish);
            }
        }
        return removedDishes;
    }
}
