package Kitchen;

import java.time.LocalDate;

/**
 * The Menu class represents a menu in the kitchen.
 * It extends the Menu class from the KitchenToManagement package.
 */
public class Menu extends KitchenToManagement.Menu {

    private int menuID; // The ID of the menu
    private String menuName; // The name of the menu
    private String menuDescription; // The description of the menu
    private LocalDate menuDate; // The date of the menu
    private int menuPreparationTime; // The preparation time of the menu

    /**
     * Constructs a new Menu object with the specified attributes.
     *
     * @param menuID             The ID of the menu.
     * @param menuName           The name of the menu.
     * @param menuDescription    The description of the menu.
     * @param menuPreparationTime The preparation time of the menu.
     * @param menuDate           The date of the menu.
     */
    public Menu(int menuID, String menuName, String menuDescription, int menuPreparationTime, LocalDate menuDate) {
        this.menuID = menuID;
        this.menuName = menuName;
        this.menuDescription = menuDescription;
        this.menuPreparationTime = menuPreparationTime;
        this.menuDate = menuDate;
    }

    /**
     * Sets the ID of the menu.
     *
     * @param menuID The ID of the menu.
     */
    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }

    /**
     * Gets the date of the menu.
     *
     * @return The date of the menu.
     */
    public LocalDate getMenuDate() {
        return menuDate;
    }

    /**
     * Sets the date of the menu.
     *
     * @param menuDate The date of the menu.
     */
    public void setMenuDate(LocalDate menuDate) {
        this.menuDate = menuDate;
    }

    /**
     * Gets the ID of the menu.
     *
     * @return The ID of the menu.
     */
    public int getMenuID() {
        return menuID;
    }

    /**
     * Sets the name of the menu.
     *
     * @param menuName The name of the menu.
     */
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    /**
     * Gets the name of the menu.
     *
     * @return The name of the menu.
     */
    public String getMenuName() {
        return menuName;
    }

    /**
     * Sets the description of the menu.
     *
     * @param menuDescription The description of the menu.
     */
    public void setMenuDescription(String menuDescription) {
        this.menuDescription = menuDescription;
    }

    /**
     * Gets the description of the menu.
     *
     * @return The description of the menu.
     */
    public String getMenuDescription() {
        return menuDescription;
    }

    /**
     * Sets the preparation time of the menu.
     *
     * @param menuPreparationTime The preparation time of the menu.
     */
    public void setMenuPreparationTime(int menuPreparationTime) {
        this.menuPreparationTime = menuPreparationTime;
    }

    /**
     * Gets the preparation time of the menu.
     *
     * @return The preparation time of the menu.
     */
    public int getMenuPreparationTime() {
        return menuPreparationTime;
    }
}

