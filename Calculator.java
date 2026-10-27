
import java.util.Scanner;

public class Calculator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Java Calculator ===");

        while (true) {
            System.out.println("\n1. Addition (+)");
            System.out.println("2. Subtraction (-)");
            System.out.println("3. Multiplication (*)");
            System.out.println("4. Division (/)");
            System.out.println("5. Exit");

            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            if (choice == 5) {
                System.out.println("Calculator closed!");
                break;
            }

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice!");
                continue;
            }

            System.out.print("Enter first number: ");
            double a = sc.nextDouble();

            System.out.print("Enter second number: ");
            double b = sc.nextDouble();

            switch (choice) {
                case 1:
                    System.out.println("Result: " + (a + b));
                    break;

                case 2:
                    System.out.println("Result: " + (a - b));
                    break;

                case 3:
                    System.out.println("Result: " + (a * b));
                    break;

                case 4:
                    if (b == 0) {
                        System.out.println(
                            "Error: Cannot divide by zero!"
                        );
                    } else {
                        System.out.println("Result: " + (a / b));
                    }
                    break;
            }
        }

        sc.close();
    }
}
