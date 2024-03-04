package KitchenToManagement;

import java.time.LocalDate;
import java.util.HashSet;

/**
 * Abstract class representing the menu.
 * This class implements the IMenu interface, providing a structure for managing
 * a collection of dishes and courses, as well as specifying the date the menu is applicable.
 * It allows for the organisation of menu items into both individual dishes and grouped courses,
 * facilitating detailed menu planning and scheduling.
 */
public abstract class Menu implements IMenu {
    /**
     * A collection of dishes that are included in the menu.
     */
    private HashSet<Dish> dishSet;

    /**
     * A HashSet of courses, each of which can contain multiple dishes.
     */
    private HashSet<Course> courseSet;

    /**
     * The date on which the menu is set to be.
     */
    private LocalDate menuDate;

    /**
     * Sets the collection of dishes to be included in this menu.
     *
     * @param dishSet A HashSet of Dish objects representing the dishes to be included in the menu.
     */
    @Override
    public void setDishSet(HashSet<Dish> dishSet) {
        this.dishSet = dishSet;
    }

    /**
     * Retrieves the collection of dishes included in this menu.
     *
     * @return A HashSet of Dish objects representing the dishes included in the menu.
     */
    @Override
    public HashSet<Dish> getDishSet() {
        return dishSet;
    }

    /**
     * Sets the date this menu is applicable for. This can be used to specify when the menu should be available,
     * allowing for menu planning and scheduling.
     *
     * @param menuDate A LocalDate object representing the date the menu is set for.
     */
    @Override
    public void setMenuDate(LocalDate menuDate) {
        this.menuDate = menuDate;
    }

    /**
     * Retrieves the date this menu is applicable for. This method allows for querying the date associated with
     * the menu.
     *
     * @return A LocalDate object representing the date the menu is set for.
     */
    @Override
    public LocalDate getMenuDate() {
        return menuDate;
    }

    /**
     * Sets the collection of courses to be included in this menu. This enables the menu
     * to offer a structured grouping of dishes into courses.
     *
     * @param courseSet A HashSet of Course objects representing the courses to be included in the menu.
     */
    @Override
    public void setCourseSet(HashSet<Course> courseSet) {
        this.courseSet = courseSet;
    }

    /**
     * Retrieves the collection of courses included in this menu.
     * This method provides access to the courses that are part of the menu.
     *
     * @return A HashSet of Course objects representing the courses included in the menu.
     */
    @Override
    public HashSet<Course> getCourseSet() {
        return courseSet;
    }

}
