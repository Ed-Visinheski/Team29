package Kitchen;

public class Menu extends KitchenToManagement.Menu{
    private int menuID;
    private String menuName;
    private String menuDescription;
    private int menuPrice;
    private int menuPreparationTime;

    public Menu(int menuID, String menuName, String menuDescription, int menuPrice, int menuPreparationTime) {
        this.menuID = menuID;
        this.menuName = menuName;
        this.menuDescription = menuDescription;
        this.menuPrice = menuPrice;
        this.menuPreparationTime = menuPreparationTime;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
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

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPreparationTime(int menuPreparationTime) {
        this.menuPreparationTime = menuPreparationTime;
    }

    public int getMenuPreparationTime() {
        return menuPreparationTime;
    }
}
