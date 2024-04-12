import java.time.*;
import java.util.ArrayList;

public class Session {
    Tutor tutor;
    String course;
    String subject;
    String dayOfWeek;
    LocalTime start;
    LocalTime end;
    Duration duration;
    boolean IP;
    ArrayList<String> attending;
    int capacity;

    //for creating a session from the schedule.csv file
    public Session(String subject, String course, String dayOfWeek,
        LocalTime start, LocalTime end, boolean IP, Tutor t) {
        this.subject = subject;
        this.course = course;
        this.dayOfWeek = dayOfWeek;
        this.start = start;
        this.end = end;
        this.tutor = t;
        this.duration = Duration.between(start, end);
        if (this.duration.toMinutes() >= 30) {
            this.capacity = 2;
        } else {
            this.capacity = 1;
        }
        //"PID:Class" e.g "25344:MATH10B"
        this.attending = new ArrayList<>();
    }

    /**
     * If attending length is above capacity, return true.
     * @return true or false
     */
    public boolean isFull() {
        return attending.size() >= capacity;
    }

    /**
     * PID:Course
     * @param pidAndCourse
     */
    public void addAnAttendance(String pidAndCourse) {
        attending.add(pidAndCourse);
    }

    public String toString() {
        return tutor.name + ": " + dayOfWeek + " " + subject + " " + start.toString() + "-" + end.toString() + 
                " | INPERSON: " + IP + " | Spots left: " + (this.capacity-this.attending.size());
    }

    public String getDayOfWeek() {
        return this.dayOfWeek;
    }

    public LocalTime getStart() {
        return this.start;
    }
}