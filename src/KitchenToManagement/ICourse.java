package KitchenToManagement;

import java.util.HashSet;

/**
 * Interface defining the course object.
 * It specifies methods for setting and retrieving course identifiers and descriptions,
 * as well as managing the dishes that comprise the course.
 */
public interface ICourse {

    /**
     * Sets the unique identifier for the course.
     * @param courseID The unique ID to be assigned to the course.
     */
    void setCourseID(int courseID);

    /**
     * Retrieves the unique identifier of the course.
     * @return The unique ID of the course.
     */
    int getCourseID();

    /**
     * Sets the description for the course. This can include details about what the course covers,
     * its objectives, or any other relevant information.
     * @param courseDescription The descriptive text to be assigned to the course.
     */
    void setCourseDescription(String courseDescription);

    /**
     * Retrieves the description of the course.
     * @return The description of the course.
     */
    String getCourseDescription();

    /**
     * Adds a dish to the course. This method allows for the compilation of dishes that are part of this course.
     * @param dish The dish to be added to the course.
     */
    void addDishToCourse(Dish dish);

    /**
     * Retrieves the set of dishes that comprise the course.
     * @return A HashSet containing the dishes in the course.
     */
    HashSet<Dish> getDishInCourse();
}
