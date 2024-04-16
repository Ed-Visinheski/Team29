package Kitchen;

/**
 * Represents a dish in the kitchen.
 */
public class Dish implements KitchenToFOH.IDish {

    // Fields

    /** The unique identifier of the dish. */
    private int dishID;

    /** The time required to prepare the dish. */
    private String timeRequired;

    // Constructor

    /**
     * Constructs a new Dish object with the specified dish ID and time required.
     *
     * @param dishID       The unique identifier of the dish.
     * @param timeRequired The time required to prepare the dish.
     */
    public Dish(int dishID, String timeRequired) {
        this.dishID = dishID;
        this.timeRequired = timeRequired;
    }

    // Implemented methods from the IDish interface

    @Override
    public void setDishID(int dishID) {
        // Method not implemented
    }

    @Override
    public int getDishID() {
        return 0; // Method not implemented
    }

    @Override
    public void setTimeRequired(String time) {
        // Method not implemented
    }

    @Override
    public String getTimeRequired() {
        return null; // Method not implemented
    }
}
