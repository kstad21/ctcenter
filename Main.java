import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        Center CT = new Center("Content Tutoring Center");
        CT.reload("tutors.csv", "schedule.csv");
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        System.out.println("\n\nWelcome to schedule-search! This program helps us keep track of our tutors and their appointments.\n\n");
        while (running) {
            printFunctions();
            System.out.println("What would you like to do? Type either the number (e.g 5) or the command (e.g tutor schedule).");
            String prompt = sc.nextLine().strip().toLowerCase();

            if (prompt.equals("1") || prompt.toLowerCase().equals("list tutors")) {
                printTutors(CT);
            }
            else if (prompt.equals("2") || prompt.equals("primsubj")) {
                System.out.println("Please input subject. For chem, type either CHEM(GEN) or CHEM(O).");
                String subj = sc.nextLine().strip();
                ArrayList<Tutor> tutors = CT.tutorsForPrimSubj(subj);
                if (tutors.size() == 0) {
                    System.out.println("Looks like you've input an invalid subject or a subject we don't have tutors for right now.");
                } else {
                    for (int i = 0; i < tutors.size(); i++) {
                        System.out.println(tutors.get(i).toString());
                    }
                }
            }
            else if (prompt.equals("3") || prompt.equals("secsubj")) {
                System.out.println("Please input subject. For chem, type either CHEM(GEN) or CHEM(O).");
                String subj = sc.nextLine().strip();
                ArrayList<Tutor> tutors = CT.tutorsForPrimSubj(subj);
                if (tutors.size() == 0) {
                    System.out.println("Looks like you've input an invalid subject or a subject we don't have tutors for right now.");
                } else {
                    for (int i = 0; i < tutors.size(); i++) {
                        System.out.println(tutors.get(i).toString());
                    }
                }
            }
            else if (prompt.equals("4") || prompt.equals("tutor's courses")) {
                System.out.println("Tutor name: <First Last>");
                String name = getName(CT, sc);
                ArrayList<String> courses = CT.findTutor(name).courses;
                for (int i = 0; i < courses.size(); i++) {
                    System.out.print("| " + courses.get(i) + " |");
                }
            }
            else if (prompt.equals("5") || prompt.equals("tutor schedule")) {
                System.out.println("Tutor name: <First Last>");
                String name = getName(CT, sc);
                ArrayList<String> courses = CT.findTutor(name).courses;
                for (int i = 0; i < courses.size(); i++) {
                    System.out.print("| " + courses.get(i) + " |");
                }
                System.out.println(CT.findTutor(name).getWeeklySchedule());
            } 
            else if (prompt.equals("6") || prompt.equals("tutors for a course today")) {
                //VALIDATION?
                System.out.println("Enter a day (e.g Monday)");
                String day = sc.nextLine().strip();
                System.out.println("Enter a course");
                String course = sc.nextLine().strip().toUpperCase();
                ArrayList<Tutor> tutors = CT.tutorsForCourseToday(course, day);
                for (int i = 0; i < tutors.size(); i++) {
                    ArrayList<Session> apptsToday = tutors.get(i).getSessionsForToday(day);
                    for (int j = 0; j < apptsToday.size(); j++) {
                        System.out.println(apptsToday.get(j).toString());
                    }
                }
            }
            else if (prompt.equals("10") || prompt.equals("load attendance")) {
                collectAttendance(CT);
            }
            else if (prompt.equals("exit") || prompt.equals("100")) {
                running = false;
            }
            System.out.println("\n\n");
        }

        
    }

    private static void printFunctions() {
        System.out.println("Functions: 1)list tutors | 2)primsubj | 3)secsubj | 4)tutor's courses | 5)tutor schedule | 6)tutors for a course today | 10)upload attendance | 100)exit | 101)help");
    }

    private static void printTutors(Center ct) {
        for (int i = 0; i < ct.listOfTutors.size(); i++) {
            System.out.println(ct.listOfTutors.get(i).toString());
        }
        System.out.println("\n\n");
    }

    private static String getName(Center CT, Scanner sc) {
        System.out.println("Enter tutor name:");
        String name = sc.nextLine();
        Tutor t = CT.findTutor(name);
        while (t == null) {
            System.out.println("It appears we didn't find a tutor in the system with that name. Check spelling, etc.");
            String possible = CT.getCloseByNames(name);
            if (possible.length() != 0) {
                System.out.println("Did you mean: " + possible);
            }
            System.out.println("Input tutor name here: ");
            name = sc.nextLine();
            t = CT.findTutor(name);
        }
        return name;
    }

    private static void collectAttendance(Center CT) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("csv-files/appts.csv")));
            String attendance = JOptionPane.showInputDialog("copy and paste RedRock report for today.");
            attendance = attendance.replace("    ","\n");
            attendance = getRidOfDelete(attendance);

            out.write(attendance);
            out.close();

            CT.clearAttendance();
            CT.loadAttendanceFromCSV("appts.csv");
        } catch (IOException e) {
            System.out.println("Looks like the information you updated is in the wrong format! Make sure you copy and pased the RedRock report exactly.");
        }
    }

    private static String getRidOfDelete(String attendance) {
        String[] splits = attendance.split(" ");
        String toReturn = "";
        for (int i = 0; i < splits.length; i++) {
            if (splits[i].contains("\n") && splits[i].contains("Appointments:")) {
                //System.out.println("." + splits[i] + ".");
                String[] pts = splits[i].split("Appointments:");
                splits[i] = pts[0].strip() + " Appointments:"; 
                splits[i].replace("\n", "");
            }
            
            if (!(splits[i].equals("-") || splits[i].equals("DELETED") 
                    || splits[i].equals("Missed"))) {
                toReturn += splits[i];
            }
            
            if (!(i == splits.length - 1)) {
                toReturn += " ";
            }
        }
        return toReturn;
    }
}
