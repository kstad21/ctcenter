import java.time.*;
import java.util.ArrayList;

public class Tutor {
    public String name;
    public String primSubj;
    public String secSubj;
    public ArrayList<String> courses;
    public ArrayList<Session> appointments;

    public Tutor(String n, String primSubj, String secSubj) {
        this.primSubj = primSubj;
        if (secSubj.equals("n/a")) {
            this.secSubj = null;
        } else {
            this.secSubj = secSubj.toUpperCase();
        }
        this.courses = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.name = n;
    }

    /**
     * Add a session to this tutor's list of appointments.
     * @param subject
     * @param course
     * @param dayOfWeek
     * @param start
     * @param end
     * @param IP
     */
    public void addSession(String subject, String course, String dayOfWeek, 
        LocalTime start, LocalTime end, boolean IP) {
        appointments.add(new Session(subject, course, dayOfWeek, start, end, IP, this));
    }

    public ArrayList<Session> getSessionsForToday(String day) {
        ArrayList<Session> sessions = new ArrayList<>();
        for (int i = 0; i < appointments.size(); i++) {
            Session curr = appointments.get(i);
            if (curr.getDayOfWeek().equals(day)) {
                sessions.add(curr);
            }
        }
        return sessions;
    }

    public Session findSession(String day, LocalTime start) {
        ArrayList<Session> sessionsToday = getSessionsForToday(day);
        for (int i = 0; i < sessionsToday.size(); i++) {
            Session curr = sessionsToday.get(i);
            if (curr.getStart().equals(start)) {
                return curr;
            }
        }
        return null;
    }

    public ArrayList<Session> apptsWithAttendanceForToday(String day) {
        ArrayList<Session> sessionsToday = getSessionsForToday(day);
        ArrayList<Session> toReturn = new ArrayList<>();
        for (int i = 0; i < sessionsToday.size(); i++) {
            Session curr = sessionsToday.get(i);
            if (curr.attending.size() > 0) {
                toReturn.add(curr);
            }
        }
        return toReturn;
    }

    public String getWeeklySchedule() {
        String toReturn = name + ":\n";
        for (int i = 0; i < appointments.size(); i++) {
            Session s = appointments.get(i);
            toReturn += s.toString();
            if (s.attending.size() > 0) {
                toReturn += " **Attendances: " + s.attending.toString() + "**";
            }
            if (i != appointments.size() - 1) {
                toReturn += "\n";
            }
        }
        return toReturn;
    }

    public void clearAttendance() {
        for (int i = 0; i < this.appointments.size(); i++) {
            appointments.get(i).attending.clear();
        }
    }

    public String toString() {
        String secondary = secSubj;
        if (secSubj == null) {
            secondary = "none";
        }
        return this.name + ": prim subj: " + primSubj + ", secondary subj: " + secondary + 
                ", " + courses.size() + " courses";
    }

    public void addACourse(String course) {
        this.courses.add(course);
    }

    
}
