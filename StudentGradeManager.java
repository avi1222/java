
import java.util.ArrayList;
import java.util.Scanner;

public class StudentGradeManager {

    static class Student {
        String name;
        double[] marks;

        Student(String name, double[] marks) {
            this.name = name;
            this.marks = marks;
        }

        double calculatePercentage() {
            double total = 0;

            for (double mark : marks) {
                total += mark;
            }

            return total / marks.length;
        }

        String calculateGrade() {
            double percentage = calculatePercentage();

            if (percentage >= 90) return "A+";
            else if (percentage >= 80) return "A";
            else if (percentage >= 70) return "B";
            else if (percentage >= 60) return "C";
            else if (percentage >= 40) return "D";
            else return "F";
        }

        void display() {
            System.out.println("\nName: " + name);
            System.out.println("Percentage: "
                    + String.format("%.2f", calculatePercentage())
                    + "%");
            System.out.println("Grade: " + calculateGrade());
        }
    }

    static ArrayList<Student> students = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    static void addStudent() {
        System.out.print("Enter student name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        double[] marks = new double[5];

        for (int i = 0; i < marks.length; i++) {
            while (true) {
                System.out.print("Enter marks for subject "
                        + (i + 1) + " (0-100): ");

                if (sc.hasNextDouble()) {
                    double mark = sc.nextDouble();
                    sc.nextLine();

                    if (mark >= 0 && mark <= 100) {
                        marks[i] = mark;
                        break;
                    }
                } else {
                    sc.nextLine();
                }

                System.out.println("Invalid marks. Enter a number from 0 to 100.");
            }
        }

        students.add(new Student(name, marks));
        System.out.println("Student added successfully!");
    }

    static void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("No students added yet.");
            return;
        }

        for (Student student : students) {
            student.display();
        }
    }

    static void searchStudent() {
        System.out.print("Enter student name to search: ");
        String name = sc.nextLine().trim();

        for (Student student : students) {
            if (student.name.equalsIgnoreCase(name)) {
                student.display();
                return;
            }
        }

        System.out.println("Student not found.");
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Student Grade Management ===");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            if (!sc.hasNextInt()) {
                System.out.println("Please enter a valid menu number.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    displayStudents();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    System.out.println("Exiting program. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
