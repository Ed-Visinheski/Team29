package Kitchen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class UI {
    private List<Dish> dishList;
    private List<Menu> menuList;
    private List<Ingredient> ingredientList;
    private List<Course> courseList;


    public UI (String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);// Make sure you have this line at the beginning of your UI method or constructor
        dishList = new ArrayList<>();
        menuList = new ArrayList<>();
        ingredientList = new ArrayList<>();
        courseList = new ArrayList<>();
        System.out.println("Kitchen UI -- Version 1.0.0");
        System.out.println("Please select one of the options:");
        while (true) {
            System.out.println("1. Dish");
            System.out.println("2. Menu");
            System.out.println("3. Ingredient");
            System.out.println("4. Course");
            System.out.println("q. Quit");
            String option = scanner.nextLine(); // Read the whole line of input

            if (option.equals("q")) {
                System.out.println("Quit");
                break; // Exit the loop
            }

            switch (option) {
                case "1":
                    while (true) {
                        System.out.println("1. Add Dish");
                        System.out.println("2. Remove Dish");
                        System.out.println("3. View Dishes");
                        System.out.println("q. Back");
                        String DishOption = scanner.nextLine(); // Read the whole line of input
                        if (DishOption.equals("q")) {
                            System.out.println("Back");
                            break; // Exit the loop, return to the main menu
                        }
                        switch (DishOption) {
                            case "1":
                                System.out.println("Add Dish");
                                AddDish(scanner);
                                break; // Break after each case to avoid fall-through
                            case "2":
                                System.out.println("Remove Dish");
                                RemoveDish(scanner);
                                break;
                            case "3":
                                System.out.println("View Dishes");
                                ViewDishes();
                                break;
                            default:
                                System.out.println("Invalid option");
                        }
                        // Removed the extra AddDish(scanner); call here
                    }
                    break;
                case "2":
                    while (true) {
                        System.out.println("1. Add Menu");
                        System.out.println("2. Remove Menu");
                        System.out.println("3. View Menu");
                        System.out.println("q. Back");
                        String MenuOption = scanner.nextLine(); // Read the whole line of input

                        if (MenuOption.equals("q")) {
                            System.out.println("Back");
                            break; // Exit the loop
                        }
                        switch (MenuOption) {
                            case "1":
                                System.out.println("Add Menu");
                                AddMenu(scanner);
                                break; // Remember to break after each case to avoid fall-through
                            case "2":
                                System.out.println("Remove Menu");
                                RemoveMenu(scanner);
                                break;
                            case "3":
                                System.out.println("View Menu");
                                ViewMenu();
                                break;
                            default:
                                System.out.println("Invalid option");
                        }
                    }
                    break;
                case "3":
                    while (true) {
                        System.out.println("1. Add Ingredient");
                        System.out.println("2. Remove Ingredient");
                        System.out.println("3. View Ingredient");
                        System.out.println("q. Back");
                        String IngredientOption = scanner.nextLine(); // Read the whole line of input

                        if (IngredientOption.equals("q")) {
                            System.out.println("Back");
                            break; // Exit the loop
                        }
                        switch (IngredientOption) {
                            case "1":
                                System.out.println("Add Ingredient");
                                AddIngredient(scanner);
                                break; // Remember to break after each case to avoid fall-through
                            case "2":
                                System.out.println("Remove Ingredient");
                                RemoveIngredient(scanner);
                                break;
                            case "3":
                                System.out.println("View Ingredient");
                                ViewIngredient();
                                break;
                            default:
                                System.out.println("Invalid option");
                        }
                    }
                    break;
                case "4":
                    while (true) {
                        System.out.println("1. Add Course");
                        System.out.println("2. Remove Course");
                        System.out.println("3. View Course");
                        System.out.println("q. Back");
                        String CourseOption = scanner.nextLine(); // Read the whole line of input

                        if (CourseOption.equals("q")) {
                            System.out.println("Back");
                            break; // Exit the loop
                        }
                        switch (CourseOption) {
                            case "1":
                                System.out.println("Add Course");
                                AddCourse(scanner);
                                break; // Remember to break after each case to avoid fall-through
                            case "2":
                                System.out.println("Remove Course");
                                RemoveCourse(scanner);
                                break;
                            case "3":
                                System.out.println("View Course");
                                ViewCourse();
                                break;
                            default:
                                System.out.println("Invalid option");
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void AddDish(Scanner scanner) {
        System.out.println("Adding Dish");
        System.out.println("Enter Dish ID:");
        int dishID = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter Dish Name:");
        String dishName = scanner.nextLine();
        System.out.println("Enter Dish Description:");
        String dishDescription = scanner.nextLine();
        System.out.println("Enter Dish Price:");
        int dishPrice = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter Dish Preparation Time:");
        int dishPreparationTime = Integer.parseInt(scanner.nextLine());
        Dish dish = new Dish(dishID, dishName, dishDescription, dishPrice, dishPreparationTime);
        dishList.add(dish);
        System.out.println("Dish added successfully!");
    }

    private void ViewDishes() {
        System.out.println("Viewing Dishes");
        for (Dish dish : dishList) {
            System.out.println("Dish ID: " + dish.getDishID());
            System.out.println("Dish Name: " + dish.getDishName());
            System.out.println("Dish Description: " + dish.getDishDescription());
            System.out.println("Dish Price: " + dish.getDishPrice());
            System.out.println("Dish Preparation Time: " + dish.getDishPreparationTime());
            System.out.println("==================================");
        }
    }

    private void RemoveDish(Scanner scanner) {
        System.out.println("Removing Dish");
        System.out.println("Enter Dish ID:");
        int dishID = Integer.parseInt(scanner.nextLine());
        for (Dish dish : dishList) {
            if (dish.getDishID() == dishID) {
                dishList.remove(dish);
                System.out.println("Dish removed successfully!");
                return;
            }
        }
        System.out.println("Dish not found!");
    }

    private void AddMenu(Scanner scanner) {
        System.out.println("Adding Menu");
        System.out.println("Enter Menu ID:");
        int menuID = Integer.parseInt(scanner.nextLine()); // Read the next line as an integer
        System.out.println("Enter Menu Name:");
        String menuName = scanner.nextLine(); // Read the next line as a string
        System.out.println("Enter Menu Description:");
        String menuDescription = scanner.nextLine(); // Read the next line as a string
        System.out.println("Enter Menu Price:");
        int menuPrice = Integer.parseInt(scanner.nextLine()); // Read the next line as an integer
        System.out.println("Enter Menu Preparation Time:");
        int menuPreparationTime = Integer.parseInt(scanner.nextLine()); // Read the next line as an integer
        Menu menu = new Menu(menuID, menuName, menuDescription, menuPrice, menuPreparationTime);
        menuList.add(menu);
        System.out.println("Menu added successfully!");
    }

    private void ViewMenu() {
        System.out.println("Viewing Menu");
        for(Menu menu: menuList){
            System.out.println("Menu ID: " + menu.getMenuID());
            System.out.println("Menu Name: " + menu.getMenuName());
            System.out.println("Menu Description: " + menu.getMenuDescription());
            System.out.println("Menu Price: " + menu.getMenuPrice());
            System.out.println("Menu Preparation Time: " + menu.getMenuPreparationTime());
            System.out.println("==================================");
        }
    }

    private void RemoveMenu(Scanner scanner) {
        System.out.println("Removing Menu");
        System.out.println("Enter Menu ID:");
        int menuID = Integer.parseInt(scanner.nextLine());
        for(Menu menu: menuList){
            if(menu.getMenuID() == menuID){
                menuList.remove(menu);
                System.out.println("Menu removed successfully!");
                return;
            }
        }
        System.out.println("Menu not found!");
    }

    private void AddIngredient(Scanner scanner) {
        System.out.println("Adding Ingredient");
        System.out.println("Enter Ingredient ID:");
        int ingredientID = Integer.parseInt(scanner.nextLine()); // Read the next line as an integer
        System.out.println("Enter Ingredient Name:");
        String ingredientName = scanner.nextLine(); // Read the next line as a string
        System.out.println("Enter Ingredient Stock Level:");
        int ingredientStockLevel = Integer.parseInt(scanner.nextLine()); // Read the next line as an integer
        System.out.println("Enter Low Stock Threshold:");
        int ingredientLowStockThreshold = Integer.parseInt(scanner.nextLine()); // Read the next line as an integer
        // Assuming Ingredient constructor exists and works as expected
        Ingredient ingredient = new Ingredient(ingredientID, ingredientName, ingredientStockLevel, ingredientLowStockThreshold);
        ingredientList.add(ingredient);
        System.out.println("Ingredient added successfully!");
    }

    private void RemoveIngredient(Scanner scanner) {
        System.out.println("Removing Ingredient");
        System.out.println("Enter Ingredient ID:");
        int ingredientID = Integer.parseInt(scanner.nextLine());
        for(Ingredient ingredient: ingredientList){
            if(ingredient.getIngredientID() == ingredientID){
                ingredientList.remove(ingredient);
                System.out.println("Ingredient removed successfully!");
                return;
            }
        }
        System.out.println("Ingredient not found!");
    }

    private void ViewIngredient() {
        System.out.println("Viewing Ingredient");
        for(Ingredient ingredient: ingredientList){
            System.out.println("Ingredient ID: " + ingredient.getIngredientID());
            System.out.println("Ingredient Name: " + ingredient.getIngredientName());
            System.out.println("Ingredient Stock Level: " + ingredient.getIngredientStockLevel());
            System.out.println("Low Stock Threshold: " + ingredient.getLowStockThreshold());
            System.out.println("==================================");
        }
    }

    private void AddCourse(Scanner scanner) {
        System.out.println("Adding Course");
        System.out.println("Enter Course ID:");
        int courseID = Integer.parseInt(scanner.nextLine()); // Parse the next line as an integer
        System.out.println("Enter Course Name:");
        String courseName = scanner.nextLine(); // Read the next line as a string
        System.out.println("Enter Course Description:");
        String courseDescription = scanner.nextLine(); // Read the next line as a string
        // Assuming Course constructor exists and works as expected
        Course course = new Course(courseID, courseName, courseDescription);
        courseList.add(course);
        System.out.println("Course added successfully!");
    }

    private void RemoveCourse(Scanner scanner) {
        System.out.println("Removing Course");
        System.out.println("Enter Course ID:");
        int courseID = Integer.parseInt(scanner.nextLine());
        for(Course course: courseList){
            if(course.getCourseID() == courseID){
                courseList.remove(course);
                System.out.println("Course removed successfully!");
                return;
            }
        }
    }

    private void ViewCourse() {
        System.out.println("Viewing Course");
        for(Course course: courseList){
            System.out.println("Course ID: " + course.getCourseID());
            System.out.println("Course Name: " + course.getCourseName());
            System.out.println("Course Description: " + course.getCourseDescription());
            System.out.println("==================================");
        }
    }


    public static void main(String[] args) throws IOException {
        new UI(args);
    }
}
