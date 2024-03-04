package KitchenToFOH;

/**
 * Interface defining the dish object.
 * It specifies methods for setting and retrieving dish identifiers, managing ingredients and their quantities,
 * and handling information associated with the dish.
 */
public interface IDish {

    /**
     * Sets the unique identifier for the dish.
     *
     * @param dishID The unique ID to be assigned to the dish.
     */
    void setDishID(int dishID);

    /**
     * Retrieves the unique identifier of the dish.
     *
     * @return The unique ID of the dish.
     */
    int getDishID();

    /**
     * Sets the time required to prepare the dish.
     * Implementations should define how this time is stored and used within the context of the dish.
     *
     * @param time The preparation time to be set for the dish.
     */
    void setTimeRequired(String time);

    /**
     * Retrieves the time required to prepare the dish.
     * Implementations should provide access to the stored preparation time for the dish.
     *
     * @return The preparation time of the dish as a String.
     */
    String getTimeRequired();
}
