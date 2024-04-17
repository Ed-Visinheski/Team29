package KitchenToManagement;

import java.time.LocalDate;
import java.util.Date;

/**
 * Interface for the KitchenToManagement class, which is responsible for retrieving menu information from the database.
 */

public interface KitchenToManagement {

    /**
     * Get the menu for a specific date
     * @param date The Date value indicating which Menu object to retrieve
     * @return Menu object for the specified date
     * @apiNote This method is used to retrieve the menu for a specific date,
     * allowing for menu planning and scheduling.
     */

    public Menu getMenuOnDate(LocalDate date);


}
