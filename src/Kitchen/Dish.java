package Kitchen;

public class Dish implements KitchenToFOH.IDish{
    private int dishID;
    private String timeRequired;

    public Dish(int dishID, String timeRequired){
        this.dishID = dishID;
        this.timeRequired = timeRequired;
    }


    @Override
    public void setDishID(int dishID) {

    }

    @Override
    public int getDishID() {
        return 0;
    }

    @Override
    public void setTimeRequired(String time) {

    }

    @Override
    public String getTimeRequired() {
        return null;
    }
}
