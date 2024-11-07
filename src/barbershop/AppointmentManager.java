package barberShop; // Ensure this matches your other classes

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AppointmentManager {
    Scanner scanner = new Scanner(System.in);
    config dbConfig = new config();

    public void appointmentMenu() {
        int choice = -1;

        do {
            System.out.println("---- Appointment Management ----");
            System.out.println("1. Add Appointment             |");
            System.out.println("2. View Appointments           |");
            System.out.println("3. Update Appointment          |");
            System.out.println("4. Delete Appointment          |");
            System.out.println("5. Back to Main Menu           |");
            System.out.println("-------------------------------");
            System.out.print("Enter your choice:           |\n");

            while (true) {
                try {
                    choice = scanner.nextInt();
                    if (choice < 1 || choice > 5) {
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
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
                    addAppointment();
                    break;
                case 2:
                    viewAppointments();
                    break;
                case 3:
                    updateAppointment();
                    break;
                case 4:
                    deleteAppointment();
                    break;
            }
        } while (choice != 5);
    }

    private void addAppointment() {
        int customerId = getValidIntegerInput("Enter Customer ID: ");
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String date = scanner.next().trim();
        System.out.print("Enter Appointment Time (HH:MM:SS): ");
        String time = scanner.next().trim();
        System.out.print("Enter Service Type: ");
        String serviceType = scanner.next().trim();
        System.out.print("Enter Style of your haircut: ");
        String style = scanner.next().trim();
        System.out.print("Enter Note: ");
        String notes = scanner.next().trim();

        String sql = "INSERT INTO Appointment (customerId, appointmentDate, appointmentTime, serviceType, style, notes) VALUES (?, ?, ?, ?, ?, ?)";
        dbConfig.addRecord(sql, customerId, date, time, serviceType, style, notes);
        System.out.println("Appointment added successfully.");
    }

    private void viewAppointments() {
        String sqlQuery = "SELECT a.appointmentId, a.customerId, c.name, a.appointmentDate, a.appointmentTime, a.serviceType, a.style, a.notes " +
                          "FROM Appointment a " +
                          "JOIN Customer c ON a.customerId = c.customerId"; 

        String[] columnHeaders = {"Appointment ID", "Customer ID", "Name", "Date", "Time", "Service Type", "Style", "Note"};
        String[] columnNames = {"appointmentId", "customerId", "name", "appointmentDate", "appointmentTime", "serviceType", "style", "notes"};
        
        dbConfig.viewRecords(sqlQuery, columnHeaders, columnNames);
    }

    private void updateAppointment() {
        int appointmentId = getValidIntegerInput("Enter Appointment ID to update: ");
        
        if (!appointmentExists(appointmentId)) {
            System.out.println("Appointment ID does not exist.");
            return;
        }

        
        System.out.print("Enter new Appointment Date (YYYY-MM-DD) (leave blank for no change): ");
        String date = scanner.nextLine().trim();
        System.out.print("Enter new Appointment Time (HH:MM:SS) (leave blank for no change): ");
        String time = scanner.nextLine().trim();
        System.out.print("Enter new Service Type (leave blank for no change): ");
        String serviceType = scanner.nextLine().trim();
        System.out.print("Enter new Style (leave blank for no change): ");
        String style = scanner.nextLine().trim();
        System.out.print("Enter new Note (leave blank for no change): ");
        String notes = scanner.nextLine().trim();

        StringBuilder sql = new StringBuilder("UPDATE Appointment SET ");
        boolean hasChanges = false;

        if (!date.isEmpty()) {
            sql.append("appointmentDate = ?, ");
            hasChanges = true;
        }
        if (!time.isEmpty()) {
            sql.append("appointmentTime = ?, ");
            hasChanges = true;
        }
        if (!serviceType.isEmpty()) {
            sql.append("serviceType = ?, ");
            hasChanges = true;
        }
        if (!style.isEmpty()) {
            sql.append("style = ?, ");
            hasChanges = true;
        }
        if (!notes.isEmpty()) {
            sql.append("notes = ?, ");
            hasChanges = true;
        }

        if (!hasChanges) {
            System.out.println("No changes made.");
            return;
        }

        
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE appointmentId = ?");

        try (Connection conn = dbConfig.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (!date.isEmpty()) pstmt.setString(paramIndex++, date);
            if (!time.isEmpty()) pstmt.setString(paramIndex++, time);
            if (!serviceType.isEmpty()) pstmt.setString(paramIndex++, serviceType);
            if (!style.isEmpty()) pstmt.setString(paramIndex++, style);
            if (!notes.isEmpty()) pstmt.setString(paramIndex++, notes);
            pstmt.setInt(paramIndex, appointmentId);

            pstmt.executeUpdate();
            System.out.println("Appointment updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating appointment: " + e.getMessage());
        }
    }

    private void deleteAppointment() {
        int appointmentId = getValidIntegerInput("Enter Appointment ID to delete: ");
        
        if (!appointmentExists(appointmentId)) {
            System.out.println("Appointment ID does not exist.");
            return;
        }

        System.out.print("Are you sure you want to delete this appointment? (yes/no): ");
        String confirmation = scanner.next().trim();
        
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String sql = "DELETE FROM Appointment WHERE appointmentId = ?";
        dbConfig.addRecord(sql, appointmentId);
        System.out.println("Appointment deleted successfully.");
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

    private boolean appointmentExists(int appointmentId) {
        String sql = "SELECT COUNT(*) FROM Appointment WHERE appointmentId = ?";
        try (Connection conn = dbConfig.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            System.out.println("Error checking appointment existence: " + e.getMessage());
        }
        return false; 
    }
}