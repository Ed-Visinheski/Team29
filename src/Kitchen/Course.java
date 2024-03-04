package Kitchen;

public class Course extends KitchenToManagement.Course{
    private int courseID;
    private String courseName;
    private String courseDescription;

    public Course(int courseID, String courseName, String courseDescription) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseDescription() {
        return courseDescription;
    }
}
