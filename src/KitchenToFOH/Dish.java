package KitchenToFOH;

/**
 * Abstract class representing a dish. Implements the IDish interface to manage dish details,
 * including a unique identifier, list of ingredients with quantities information.
 */
public abstract class Dish implements IDish {
    /**
     * Unique identifier for the dish.
     */
    private int dishID;

    private String timeRequired;

    /**
     * Sets the unique identifier for this dish.
     *
     * @param dishID The unique ID to be assigned to the dish.
     */
    @Override
    public void setDishID(int dishID) {
        this.dishID = dishID;
    }

    /**
     * Retrieves the unique identifier of this dish.
     *
     * @return The unique ID of the dish.
     */
    @Override
    public int getDishID() {
        return dishID;
    }


    /**
     * Sets the time required to prepare this dish.
     * This method allows setting or updating the preparation time for a dish,
     * which is crucial for accurate menu planning and kitchen operations.
     *
     * @param time The preparation time to be set for the dish.
     */
    @Override
    public void setTimeRequired(String time) {
        this.timeRequired = time;
    }

    /**
     * Retrieves the time required to prepare this dish.
     * This method enables access to the preparation time of the dish,
     * facilitating scheduling and operational efficiency in culinary preparations.
     *
     * @return The time required to prepare the dish as a String.
     */
    @Override
    public String getTimeRequired() {
        return timeRequired;
    }
}
