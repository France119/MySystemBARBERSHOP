
package barberShop; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ReportManager {
     Scanner scanner = new Scanner(System.in);
    config dbConfig = new config(); 

    public void generateReports() {
        System.out.println("---- Reports ----");
        System.out.println("1. Individual Customer Report  |");
        System.out.println("2. Back to Main Menu           |");
        System.out.println("-------------------------------");
        System.out.print("Enter your choice:           |\n");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                generateIndividualCustomerReport();
                break;
            case 2:
                
                break;
        }
    }

    private void generateIndividualCustomerReport() {
         int customerId = getValidIntegerInput("Enter Customer ID for the report: ");

        String customerSql = "SELECT name, phone FROM Customer WHERE customerId = ?";
        String appointmentSql = "SELECT appointmentDate, appointmentTime FROM Appointment WHERE customerId = ?";

        try (Connection conn = dbConfig.connectDB();
             PreparedStatement customerStmt = conn.prepareStatement(customerSql);
             PreparedStatement appointmentStmt = conn.prepareStatement(appointmentSql)) {
             
           
            customerStmt.setInt(1, customerId);
            ResultSet customerRs = customerStmt.executeQuery();

            if (customerRs.next()) {
                String name = customerRs.getString("name");
                String phone = customerRs.getString("phone");

                System.out.println("----- Customer Report -----");
                System.out.printf("Customer ID: %d\n", customerId);
                System.out.printf("Name: %s\n", name);
                System.out.printf("Phone: %s\n", phone);
                System.out.println("---------------------------");
                System.out.println("Appointments:");

                
                appointmentStmt.setInt(1, customerId);
                ResultSet appointmentRs = appointmentStmt.executeQuery();

                boolean hasAppointments = false;
                while (appointmentRs.next()) {
                    String date = appointmentRs.getString("appointmentDate");
                    String time = appointmentRs.getString("appointmentTime");
                    System.out.printf("- Date: %s, Time: %s\n", date, time);
                    hasAppointments = true;
                }

                if (!hasAppointments) {
                    System.out.println("No appointments found for this customer.");
                }
            } else {
                System.out.println("Customer not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private int getValidIntegerInput(String prompt) {
        int value = -1;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine(); 
                return value;
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
        }
    }
}
