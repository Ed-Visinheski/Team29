package Kitchen;

import java.util.List;
public class CourseDB {
    private List<Course> courseList;

    public CourseDB(List<Course> courseList){
        this.courseList = courseList;
    }

    public void addCourse(Course course){
        courseList.add(course);
    }

    public void removeCourse(Course course){
        courseList.remove(course);
    }

    public void updateCourse(Course course){
        for(Course c : courseList){
            if(c.getCourseID() == course.getCourseID()){
                c.setCourseName(course.getCourseName());
                c.setCourseDescription(course.getCourseDescription());
            }
        }
    }

    public void sendToDatabase(){
        // Code to send the courseList to the database
    }

}
