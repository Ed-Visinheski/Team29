package Kitchen;

import java.util.List;
/**
 * Manages the list of courses and provides operations to add, remove, update, and send courses to the database.
 */
public class CourseDB {

    // Fields

    /** The list of courses managed by this class. */
    private List<Course> courseList;

    // Constructor

    /**
     * Constructs a new CourseDB object with the given list of courses.
     *
     * @param courseList The list of courses to manage.
     */
    public CourseDB(List<Course> courseList){
        this.courseList = courseList;
    }

    // Methods

    /**
     * Adds a course to the list of managed courses.
     *
     * @param course The course to add.
     */
    public void addCourse(Course course){
        courseList.add(course);
    }

    /**
     * Removes a course from the list of managed courses.
     *
     * @param course The course to remove.
     */
    public void removeCourse(Course course){
        courseList.remove(course);
    }

    /**
     * Updates the details of a course in the list of managed courses.
     *
     * @param course The updated course details.
     */
    public void updateCourse(Course course){
        for(Course c : courseList){
            if(c.getCourseID() == course.getCourseID()){
                c.setCourseName(course.getCourseName());
                c.setCourseDescription(course.getCourseDescription());
            }
        }
    }

    /**
     * Sends the list of courses to the database.
     * Note: Implementation of this method is missing.
     */
    public void sendToDatabase(){
        // Code to send the courseList to the database
    }

}
