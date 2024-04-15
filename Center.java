import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Center {
    public ArrayList<Tutor> listOfTutors;
    String name;
    ArrayList<String> courses;

    public Center(String name) {
        this.name = name;
        this.listOfTutors = new ArrayList<>();
        this.courses = getCourses();
    }

    public ArrayList<String> getCourses() {
        String[] initList = {"BIBC102", "BICD100", "BILD1", "BILD2", "BILD3", "BIMM100", "BIPN100",
            "CHEM40A", "CHEM40B", "CHEM41A", "CHEM41B", "CHEM41C", "CHEM6A", "CHEM6B", "CHEM6C", "ECON1",
            "ECON3", "MATH102", "MATH109", "MATH10A", "MATH10B", "MATH10C", "MATH11", "MATH170A", "MATH18",
            "MATH2", "MATH20A", "MATH20B", "MATH20C", "MATH20D", "MATH20E", "MATH3C", "MATH4C", "PHYS1A", 
            "PHYS1B", "PHYS1C", "PHYS2A", "PHYS2B", "PHYS2C", "PSYC60"};
        ArrayList<String> courses = new ArrayList<>();
        for (int i = 0; i < initList.length; i++) {
            courses.add(initList[i]);
        }
        return courses;
    }

    public void reload(String tutorsFile, String schedFile) {
        this.listOfTutors.clear();
        this.loadTutorsFromCSV(tutorsFile);
        this.loadApptsFromCSV(schedFile);
    }

    /**
     * Load attributes for tutors at this center.
     * Name,primsubj,secsubj,coursea,courseb,coursec...
     * @param fileName
     */
    public void loadTutorsFromCSV(String fileName) {
        this.listOfTutors.clear();
        Path pathToFile = Paths.get("csv-files/" + fileName);
        int lineNum = 1;

        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {
            String line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(",");
                String name = attributes[0];
                String primSubj = attributes[1];
                String secSubj = attributes[2];
                Tutor t = new Tutor(name, primSubj, secSubj);
                for (int i = 3; i < attributes.length; i++) {
                    t.addACourse(attributes[i]);
                }
                this.listOfTutors.add(t);

                line = br.readLine();
                lineNum++;
            }
            
        } catch (IOException e) {
            System.out.println("Error at line: " + lineNum);
        }
    }

    /**
     * Load schedule from a csv file.
     * Name,Day Subject IP time-time time-time,Day Subject IP time-time time-time
     * @param fileName
     */
    public void loadApptsFromCSV(String fileName) {
        Path pathToFile = Paths.get("csv-files/" + fileName);
        int lineNum = 1;
        
        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {
            String line = br.readLine();

            while (line != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                Tutor t = findTutor(name);
                if (t != null) {
                    t.clearAttendance();
                    for (int j = 1; j < parts.length; j++) {
                        String[] info = parts[j].split(" ");
                        String day = info[0];
                        String subj = info[1];
                        Boolean IP = info[2].equals("IP");
                        for (int miniAppts = 0; miniAppts <= info.length - 4; miniAppts++) {
                            LocalTime start = LocalTime.parse(info[3 + miniAppts].split("-")[0]);
                            LocalTime end = LocalTime.parse(info[3 + miniAppts].split("-")[1]);
                            t.addSession(subj, null, day, start, end, IP);
                        }
                    }
                } else {
                    System.out.println("No tutor found for name: " + name);
                }
                lineNum++;
                line = br.readLine();
            }

        } catch (IOException e) {
            System.out.println("Error at line: " + lineNum);
        }
    }

    public void loadAttendanceFromCSV(String fileName) {
        Path pathToFile = Paths.get("csv-files/" + fileName);
        int lineNum = 1;
        
        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {
            String line = br.readLine();
            String dayOfWeek = parseDayOfWeek(line.split(" ")[5]);
            String name = "Emma Abbe";
            Tutor t = findTutor(name);
            String[] findAppts = line.split("Appointments: ");
            while (line.strip().length() > 0 || line != null) {
                if (findAppts.length > 1 && !findAppts[1].strip().contains("none   Availabilities:")) {
                    String[] findName = findAppts[0].split(" ");
                    if (findName[0].equals("Garvit")) {
                        name = "Garvit Agarwal";
                    } else if (findName[0].equals("Sean")) {
                        name = "Sean Hsu";
                    } else if (findName[0].equals("Swadhin")) {
                        name = "Swadhin Rout";
                    } else if (findName[0].equals("Sriram")) {
                        name = "Sriram Selvakumara";
                    } else {
                        if (!(lineNum == 1)) {
                            name = findName[0] + " " + findName[1];
                        }
                    }
                    t = findTutor(name);
                    String[] indiv = findAppts[1].split("2403");
                    useApptInfo(indiv, t, dayOfWeek);
                }
                line = br.readLine();
                if (line == null) {
                    break;
                }
                lineNum++;
                if (lineNum == 52) {
                    System.out.println("here");
                }
                findAppts = line.split("Appointments: ");
            }
            
        } catch (Exception e) {
            System.out.println("Error at line: " + lineNum);
        }
    }

    private void useApptInfo(String[] info, Tutor t, String dayOfWeek) {
        String time1 = "";
        String time2 = "";
        String id = "";
        String cls = "";
        for (int i = 0; i < info.length - 1; i++) {
            String[] indivSplit = info[i].split(" ");
            if (i == 0) {
                time1 = indivSplit[0];
                time2 = indivSplit[1];
                id = indivSplit[3];
            } else {
                for (int j = 0; j < indivSplit.length - 2; j++) {
                    if (indivSplit[j].equals("AM") || indivSplit[j].equals("PM")) {
                        time1 = indivSplit[j - 1];
                        time2 = indivSplit[j];
                        id = indivSplit[j + 2];
                        break;
                    }
                }
            }
            cls = indivSplit[indivSplit.length - 2];
            t.addAnAttendance(id, dayOfWeek, parseStart(time1, time2), cls);
        }
    }

    private String[] trim(String[] arr) {
        int len = 0;
        for (int i = 0; i < arr.length; i++) {
            String curr = arr[i];
            curr = curr.strip();
            if (!curr.equals("")) {
                len++;
            }
        }
        String[] toReturn = new String[len];
        for (int j = 0; j < len; j++) {
            if (!arr[j].equals("") || !(arr[j] == null)) {
                toReturn[j] = arr[j];
            }
        }
        return toReturn;
    }

    private LocalTime parseStart(String time, String ampm) {
        String parsed = "";
        if (ampm.equals("AM")) {
            parsed += "0" + time;
        } if (time.split(":")[0].equals("12")) {
            parsed = time;
        } else {
            int hour = Integer.parseInt(time.split(":")[0]);
            hour += 12;
            parsed = hour + ":" + time.split(":")[1];
        }
        return LocalTime.parse(parsed);
    }

    private String parseDayOfWeek(String date) {
        String[] unparsed = date.split("/");
        if (unparsed[0].length() == 1) {
            unparsed[0] = "0" + unparsed[0];
        }
        if (unparsed[1].length() == 1) {
            unparsed[1] = "0" + unparsed[1];
        }
        String dayOfWeek = LocalDate.parse(unparsed[2]+"-"+unparsed[0]+"-"+unparsed[1]).getDayOfWeek().toString();
        return dayOfWeek;
    }

    /**
     * Finds and returns a tutor based on their name.
     * @param name
     * @return
     */
    public Tutor findTutor(String name) {
        for (int i = 0; i < this.listOfTutors.size(); i++) {
            Tutor t = this.listOfTutors.get(i);
            if (t.name.toUpperCase().equals(name.toUpperCase())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Returns a list of tutors working on a given day.
     * @param day
     * @return
     */
    public ArrayList<Tutor> tutorsWorkingToday(String day) {
        ArrayList<Tutor> tutors = new ArrayList<>();
        for (int i = 0; i < listOfTutors.size(); i++) {
            Tutor currTutor = listOfTutors.get(i);
            for (int j = 0; j < currTutor.appointments.size(); j++) {
                if (currTutor.appointments.get(j).dayOfWeek.equals(day)) {
                    tutors.add(currTutor);
                    break;
                }
            }
        }
        return tutors;
    }

    /**
     * Returns a list of tutors that tutor for a certain primary subject.
     * @param primSubj
     * @return
     */
    public ArrayList<Tutor> tutorsForPrimSubj(String primSubj) {
        ArrayList<Tutor> tutors = new ArrayList<>();
        for (int i = 0; i < listOfTutors.size(); i++) {
            Tutor curr = listOfTutors.get(i);
            //System.out.println("LOOKING AT: " + curr.name);
            if (curr.primSubj.toLowerCase().equals(primSubj.toLowerCase())) {
                tutors.add(curr);
            }
        }
        return tutors;
    }

    /**
     * Returns a list of tutors with a certain secondary subject.
     * @param secSubj
     * @return
     */
    public ArrayList<Tutor> tutorsForSecSubj(String secSubj) {
        ArrayList<Tutor> tutors = new ArrayList<>();
        for (int i = 0; i < listOfTutors.size(); i++) {
            Tutor curr = listOfTutors.get(i);
            if (curr.secSubj != null && curr.secSubj.toLowerCase().equals(secSubj.toLowerCase())) {
                tutors.add(curr);
            }
        }
        return tutors;
    }

    /**
     * Returns a list of tutors that cover a certain course.
     * @param course
     * @return
     */
    public ArrayList<Tutor> tutorsForCourse(String course) {
        ArrayList<Tutor> tutors = new ArrayList<>();
        for (int i = 0; i < listOfTutors.size(); i++) {
            Tutor curr = listOfTutors.get(i);
            if (curr.courses.contains(course)) {
                tutors.add(curr);
            }
        }
        return tutors;
    }

    /**
     * Returns a list of tutors that tutor for a certain course on a certain day.
     * @param course
     * @param today
     * @return
     */
    public ArrayList<Tutor> tutorsForCourseToday(String course, String today) {
        ArrayList<Tutor> tutors = new ArrayList<>();
        for (int i = 0; i < listOfTutors.size(); i++) {
            Tutor curr = listOfTutors.get(i);
            ArrayList<Session> sessionsToday = curr.getSessionsForToday(today);
            if (curr.courses.contains(course) && sessionsToday.size() > 0) {
                tutors.add(curr);
            }
        }
        return tutors;
    }

    public ArrayList<Session> reschedule(String course, String today) {
        ArrayList<Session> toReturn = new ArrayList<>();
        ArrayList<Tutor> tutorsToday = tutorsForCourseToday(course, today);
        for (int i = 0; i < tutorsToday.size(); i++) {
            ArrayList<Session> tutorsSessionsToday = tutorsToday.get(i).getSessionsForToday(today);
            for (int j = 0; j < tutorsSessionsToday.size(); j++) {
                Session curr = tutorsSessionsToday.get(j);
                if (!curr.isFull()) {
                    toReturn.add(curr);
                }
            }
        }
        return toReturn;
    }

    /**
     * Returns names in our tutor database close to the given string.
     * @param name
     * @return
     */
    public String getCloseByNames(String name) {
        String toReturn = "";
        
        if (name.length() == 0) {
            return toReturn;
        }
        name = name.toLowerCase();
        for (int i = 0; i < listOfTutors.size(); i++) {
            Tutor t = listOfTutors.get(i);
            String tName = t.name.toLowerCase();
            String[] firstlast = tName.split(" ");
            if (firstlast[0].equals(name) || firstlast[1].equals(name)) {
                toReturn += t.name + " ";
                continue;
            }
            if (tName.contains(name)) {
                toReturn += t.name + " ";
                continue;
            }
        }
        return toReturn;
    }

    /**
     * Clears attendance for each tutor in this center. 
     */
    public void clearAttendance() {
        for (int i = 0; i < listOfTutors.size(); i++) {
            listOfTutors.get(i).clearAttendance();
        }
    }

    public void addCourse(String course) {
        String cls = course;
        if (course.split(" ").length > 1) {
            cls = course.split(" ")[0].toUpperCase() + course.split(" ")[1];
        }
        this.courses.add(cls);
    }
}
