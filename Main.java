import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.util.ArrayList;
import java.util.Scanner;

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
            }
            else if (prompt.equals("exit") || prompt.equals("100")) {
                running = false;
            }
            System.out.println("\n\n");
        }

        
    }

    private static void printFunctions() {
        System.out.println("Functions: 1)list tutors | 2)primsubj | 3)secsubj | 4)tutor's courses | 5)tutor schedule | 6)tutors for a course today | 100)exit | 101)help");
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
}
