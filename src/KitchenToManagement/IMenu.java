package KitchenToManagement;

import java.time.LocalDate;
import java.util.HashSet;

/**
 * Interface defining the menu object. It specifies methods for setting and
 * retrieving the collection of dishes and courses that comprise the menu,
 * as well as managing the date the menu is applicable for.
 */
public interface IMenu {

    /**
     * Sets the collection of dishes to be included in the menu.
     *
     * @param dishSet A HashSet of Dish objects representing the dishes to be included in the menu.
     */
    void setDishSet(HashSet<Dish> dishSet);

    /**
     * Retrieves the collection of dishes included in the menu.
     *
     * @return A HashSet of Dish objects representing the dishes included in the menu.
     */
    HashSet<Dish> getDishSet();

    /**
     * Sets the date this menu is applicable for. This can be used to specify when the menu should be available,
     * allowing for menu planning and scheduling.
     *
     * @param menuDate A LocalDate object representing the date the menu is set for.
     */
    void setMenuDate(LocalDate menuDate);

    /**
     * Retrieves the date this menu is applicable for. This method allows for querying the date associated with
     * the menu, facilitating menu management and planning.
     *
     * @return A LocalDate object representing the date the menu is set for.
     */
    LocalDate getMenuDate();

    /**
     * Sets the collection of courses to be included in the menu.
     * This allows for a structured grouping of dishes into courses
     * as part of the menu.
     *
     * @param courseSet A HashSet of Course objects representing the courses to be included in the menu.
     */
    void setCourseSet(HashSet<Course> courseSet);

    /**
     * Retrieves the collection of courses included in the menu. This provides access to the structured grouping of dishes into courses,
     * enhancing the menu's organization and presentation.
     *
     * @return A HashSet of Course objects representing the courses included in the menu.
     */
    HashSet<Course> getCourseSet();
}
