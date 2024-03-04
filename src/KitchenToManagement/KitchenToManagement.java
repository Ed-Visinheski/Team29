package KitchenToManagement;

import java.util.Date;

public interface KitchenToManagement {

    /**
     * Get the menu for a specific date
     * @param date The Date value indicating which Menu object to retrieve
     * @return Menu object for the specified date
     * @apiNote This method is used to retrieve the menu for a specific date,
     * allowing for menu planning and scheduling.
     */

    public Menu getMenuOnDate(Date date);


}
