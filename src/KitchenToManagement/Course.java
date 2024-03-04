package KitchenToManagement;

import java.util.HashSet;

/**
 * Abstract class representing a course in the menu e.g. Starter, Main... etc. Implements the ICourse interface
 * to manage course details such as its unique identifier, name, description, and the dishes included in the course.
 */
public abstract class Course implements ICourse {
    private int courseID;
    private String courseName;
    private String courseDescription;
    private HashSet<Dish> dishInCourse = new HashSet<>();

    /**
     * Sets the unique identifier for this course.
     * @param courseID The unique ID to be assigned to the course.
     */
    @Override
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    /**
     * Retrieves the unique identifier of this course.
     * @return The unique ID of the course.
     */
    @Override
    public int getCourseID() {
        return courseID;
    }

    /**
     * Sets the description for this course.
     * @param courseDescription The descriptive text to be assigned to the course.
     */
    @Override
    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    /**
     * Retrieves the description of this course.
     * @return The description of the course.
     */
    @Override
    public String getCourseDescription() {
        return courseDescription;
    }

    /**
     * Adds a dish to this course, expanding the set of dishes that are part of it.
     * @param dish The dish to be added to the course.
     */
    @Override
    public void addDishToCourse(Dish dish) {
        dishInCourse.add(dish);
    }

    /**
     * Retrieves the set of dishes that comprise this course.
     * @return A HashSet containing the dishes in the course.
     */
    @Override
    public HashSet<Dish> getDishInCourse() {
        return dishInCourse;
    }
}
