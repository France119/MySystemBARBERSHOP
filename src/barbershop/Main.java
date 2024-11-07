package barberShop;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MainMenu menu = new MainMenu();
        menu.displayMenu(scanner); 
        scanner.close(); 
    }
}

class MainMenu {
    public void displayMenu(Scanner scanner) {
        int choice = -1;

        do {
            System.out.println("--------- Main Menu ---------");
            System.out.println("1. Customer Management        |");
            System.out.println("2. Appointment Management     |");
            System.out.println("3. Reports                    |");
            System.out.println("4. Exit                       |");
            System.out.println("------------------------------");
            System.out.print("Enter your choice:           |\n");

            while (true) {
                try {
                    choice = scanner.nextInt();
                    if (choice < 1 || choice > 4) {
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                    } else {
                        break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter an integer.");
                    scanner.next(); 
                }
            }

            switch (choice) {
                case 1:
                    CustomerManager customerManager = new CustomerManager(); 
                    customerManager.customerMenu(); 
                    break;
                case 2:
                    AppointmentManager appointmentManager = new AppointmentManager(); 
                    appointmentManager.appointmentMenu(); 
                    break;
                case 3:
                    ReportManager reportManager = new ReportManager(); 
                    reportManager.generateReports(); 
                    break;
            }
        } while (choice != 4);
    }
}