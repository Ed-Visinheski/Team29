package KitchenToFOH;

import java.time.LocalDate;

public interface KitchenToFOH {

    /**
     * Retrieves the availability of the menu that is used
     * on that current day.
     *
     * @return The availability of the menu as a MenuAvailability object.
     */

    public MenuAvailability getCurrentMenuAvailability(LocalDate date);

}
