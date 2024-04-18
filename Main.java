import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Main {
    public static String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SUNDAY"};
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
                while (tutors.size() == 0) {
                    System.out.println("Looks like you've input an invalid subject or a subject we don't have tutors for right now. Please enter the subject again: (e.g MATH10C or PSYC60):");
                    subj = sc.nextLine().strip();
                    tutors = CT.tutorsForPrimSubj(subj);
                } 
                for (int i = 0; i < tutors.size(); i++) {
                    System.out.println(tutors.get(i).toString());
                }
                
            }
            else if (prompt.equals("3") || prompt.equals("secsubj")) {
                System.out.println("Please input subject. For chem, type either CHEM(GEN) or CHEM(O).");
                String subj = sc.nextLine().strip();
                ArrayList<Tutor> tutors = CT.tutorsForSecSubj(subj);
                while (tutors.size() == 0) {
                    System.out.println("Looks like you've input an invalid subject or a subject we don't have tutors for right now. Please enter the subject again: (MATH, PHYSICS, PSYC, CHEM(GEN), CHEM(O), BIO):");
                    subj = sc.nextLine().strip();
                    tutors = CT.tutorsForSecSubj(subj);
                } 
                for (int i = 0; i < tutors.size(); i++) {
                    System.out.println(tutors.get(i).toString());
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
                System.out.println("Enter a day (e.g Monday)");
                String day = sc.nextLine().strip();
                while (!validDay(day)) {
                    System.out.println("Looks like you haven't entered a valid day. Try again: ");
                    day = sc.nextLine().strip();
                }
                System.out.println("Enter a course");
                String course = sc.nextLine().strip().toUpperCase();
                while (!CT.courses.contains(course)) {
                    System.out.println("Looks like the course you entered doesn't match any in our CT Center. Make sure to use the format like MATH10C or PSYC60:");
                    course = sc.nextLine().strip().toUpperCase();
                }
                ArrayList<Tutor> tutors = CT.tutorsForCourseToday(course, day);
                for (int i = 0; i < tutors.size(); i++) {
                    System.out.println(tutors.get(i).toString());
                }
            } else if (prompt.equals("7") || prompt.equals("reschedule")) {
                System.out.println("Enter a day (e.g Monday)");
                String day = sc.nextLine().strip();
                while (!validDay(day)) {
                    System.out.println("Looks like you haven't entered a valid day. Try again: ");
                    day = sc.nextLine().strip();
                }
                System.out.println("Enter a course");
                String course = sc.nextLine().strip().toUpperCase();
                while (!CT.courses.contains(course)) {
                    System.out.println("Looks like the course you entered doesn't match any in our CT Center. Make sure to use the format like MATH10C or PSYC60:");
                    course = sc.nextLine().strip().toUpperCase();
                }
                ArrayList<Session> sessions = CT.reschedule(course, day);
                for (int i = 0; i < sessions.size(); i++) {
                    System.out.println(sessions.get(i).toString());
                }
            }
            else if (prompt.equals("10") || prompt.equals("load attendance")) {
                collectAttendance(CT);
            }
            else if (prompt.contains("help") || prompt.contains("101")) {
                String[] command = prompt.split(" ");
                if (command.length <= 1) {
                    System.out.println("Make sure to include the command you're looking for help on, like typing 'help 1' or 'help list tutors' if you're looking for guidance on the 1/list tutors command.");
                } else {
                    System.out.println(getHelp(prompt.split(" ")[1]));
                }
            }
            else if (prompt.equals("exit") || prompt.equals("100")) {
                running = false;
            }
            System.out.println("\n\n");
        }

        
    }

    private static boolean validDay(String day) {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equals(day.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private static void printFunctions() {
        System.out.println("Functions: 1)list tutors | 2)primsubj | 3)secsubj | 4)tutor's courses | 5)tutor schedule | 6)tutors for a course today | 7)reschedule | 10)upload attendance | 100)exit | 101)help");
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

    public static String  getHelp(String command) {
        String toReturn = "This command ";
        if (command.equals("")) {
            toReturn = "Make sure to include the command you're looking for help on, like typing 'help 1' or 'help list tutors' if you're looking for guidance on the 1/list tutors command.";
        }
        else if (command.equals("1") || command.equals("list tutors")) {
            toReturn += "prints out a list of all the tutors in the CT center! You'll see their names, primary subject, secondary subject, and the amount of courses they offer.";
        }
        else if (command.equals("2") || command.equals("primsubj")) {
            toReturn += "will prompt you for a primary subject, like MATH, BIO, CHEM(GEN), CHEM(O), PSYC, PHYSICS. Make sure to type exactly the subject as listed.";
        }
        else if (command.equals("3") || command.equals("secsubj")) {
            toReturn += "will prompt you for a secondary subject, like MATH, CHEM(GEN), CHEM(O), PSYC, PHYSICS. Make sure to type exactly the subject as listed.";
        }
        else if (command.equals("4") || command.equals("tutor's courses")) {
            toReturn += "will prompt you for a tutor. Try to make sure you type the full name with correct capitalization, but there should be some safeguarding there. For example, type in Connor Gilcrest, not connor gilcrest or Connor or connor. Then the program will print a list of the tutor's courses.";
        }
        else if (command.equals("5") || command.equals("tutor schedule")) {
            toReturn += "will prompt you for a tutor. Try to make sure you type the full name with correct capitalization, but there should be some safeguarding there. For example, type in Connor Gilcrest, not connor gilcrest or Connor or connor. Then the program will print the tutor's schedule, including attendances IF attendances have been uploaded.";
        }
        else if (command.equals("6") || command.equals("tutors for a course today")) {
            toReturn += "will prompt you for a day (make sure to type Monday, Tuesday, Wednesday, Thursday and so on). Finally, you will be prompted for a course (make sure to type in the format like MATH10C, where all alphabetic letters are capitalized and there is no space between the subject and the ID.). After all that, the program will print a list of all the tutors' appointments that are today for a certain course.";
        } 
        else if (command.equals("7") || command.equals("reschedule")) {
            toReturn += "will prompt you for a day and course. Then the program will give you a list of still-open sessions (take note if you want to upload attendance first to see which sessions are full!), of which you can go to RedRock and schedule the student for!";
        }
        else if (command.equals("10") || command.equals("load attendance")) {
            toReturn += "will cause a window to pop up. Copy and paste the RedRock report for the day you want to load attendances for and press <enter>. The program will go through the report and check if tutors have appointments, then take note of those appointments. Once those appointments are loaded, you can use the 5/tutor schedule command to check if a certain tutor has correctly had their appointments uploaded. Attendances and/or pospective attendances will be recorded and displayed as PID:Course.";
        }

        return toReturn;
    }
}
