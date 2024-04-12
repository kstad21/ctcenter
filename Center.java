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

    public Center(String name) {
        this.name = name;
        this.listOfTutors = new ArrayList<>();
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

    public void loadApptsFromCSV(String fileName) {
        Path pathToFile = Paths.get("csv-files/" + fileName);
        int lineNum = 1;
        
        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {
            int linenum = 1;
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
                linenum++;
                line = br.readLine();
            }

        } catch (IOException e) {
            System.out.println("Error at line: " + lineNum);
        }
    }

    public Tutor findTutor(String name) {
        for (int i = 0; i < this.listOfTutors.size(); i++) {
            Tutor t = this.listOfTutors.get(i);
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

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

    public void clearAttendance() {
        for (int i = 0; i < listOfTutors.size(); i++) {
            listOfTutors.get(i).clearAttendance();
        }
    }
}
