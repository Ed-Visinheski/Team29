package Kitchen;

import java.time.LocalDate;

public class Menu extends KitchenToManagement.Menu{
    private int menuID;
    private String menuName;
    private String menuDescription;
    private LocalDate menuDate;
    private int menuPreparationTime;

    public Menu(int menuID, String menuName, String menuDescription, int menuPreparationTime, LocalDate menuDate) {
        this.menuID = menuID;
        this.menuName = menuName;
        this.menuDescription = menuDescription;
        this.menuPreparationTime = menuPreparationTime;
        this.menuDate = menuDate;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }
    public LocalDate getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(LocalDate menuDate) {
        this.menuDate = menuDate;
    }

    public int getMenuID() {
        return menuID;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuDescription(String menuDescription) {
        this.menuDescription = menuDescription;
    }

    public String getMenuDescription() {
        return menuDescription;
    }

    public void setMenuPreparationTime(int menuPreparationTime) {
        this.menuPreparationTime = menuPreparationTime;
    }

    public int getMenuPreparationTime() {
        return menuPreparationTime;
    }
}
