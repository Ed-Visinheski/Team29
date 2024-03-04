package Kitchen;

import java.util.List;

public class DishDB {
    private List<Dish> dishList;

    public DishDB(List<Dish> dishList){
        this.dishList = dishList;
    }

    public void addDish(Dish dish){
        dishList.add(dish);
    }

    public void removeDish(Dish dish){
        dishList.remove(dish);
    }

    public void updateDish(Dish dish){
        for(Dish d : dishList){
            if(d.getDishID() == dish.getDishID()){
                d.setDishName(dish.getDishName());
                d.setDishDescription(dish.getDishDescription());
                d.setDishPrice(dish.getDishPrice());
                d.setDishPreparationTime(dish.getDishPreparationTime());
            }
        }
    }

    public void sendToDatabase(){
        // Code to send the dishList to the database
    }
}
