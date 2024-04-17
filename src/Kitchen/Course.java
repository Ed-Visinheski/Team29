package Kitchen;

/**
 * Represents a course in the kitchen management system.
 * Extends the Course class from the KitchenToManagement package.
 */
public class Course extends KitchenToManagement.Course {

    // Fields

    /** The unique identifier for the course. */
    private int courseID;

    /** The name of the course. */
    private String courseName;

    /** A brief description of the course. */
    private String courseDescription;

    // Constructors

    /**
     * Constructs a new Course object with the given details.
     *
     * @param courseID The unique identifier for the course.
     * @param courseName The name of the course.
     * @param courseDescription A brief description of the course.
     */
    public Course(int courseID, String courseName, String courseDescription) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }

    // Getters and Setters

    /**
     * Sets the unique identifier for the course.
     *
     * @param courseID The unique identifier for the course.
     */
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    /**
     * Retrieves the unique identifier for the course.
     *
     * @return The course ID.
     */
    public int getCourseID() {
        return courseID;
    }

    /**
     * Sets the name of the course.
     *
     * @param courseName The name of the course.
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Retrieves the name of the course.
     *
     * @return The course name.
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Sets the description of the course.
     *
     * @param courseDescription A brief description of the course.
     */
    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    /**
     * Retrieves the description of the course.
     *
     * @return The course description.
     */
    public String getCourseDescription() {
        return courseDescription;
    }
}
