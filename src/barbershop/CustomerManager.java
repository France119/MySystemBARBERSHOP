package barberShop; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CustomerManager {
    Scanner scanner = new Scanner(System.in);
    config dbConfig = new config();

    public void customerMenu() {
        int choice = -1;

        do {
            System.out.println("---- Customer Management ----");
            System.out.println("1. Add Customer              |");
            System.out.println("2. View Customers            |");
            System.out.println("3. Update Customer           |");
            System.out.println("4. Delete Customer           |");
            System.out.println("5. Back to Main Menu         |");
            System.out.println("------------------------------");
            System.out.print("Enter your choice:          |\n");

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
                    addCustomer();
                    break;
                case 2:
                    viewCustomers();
                    break;
                case 3:
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
            }
        } while (choice != 5);
    }

    public void addCustomer() {
        System.out.print("Enter Customer Name: ");
        String name = scanner.next().trim();
        if (name.isEmpty()) {
            System.out.println("Customer name cannot be empty.");
            return;
        }

        System.out.print("Enter Phone Number: ");
        String phone = scanner.next().trim();
        if (phone.isEmpty()) {
            System.out.println("Phone number cannot be empty.");
            return;
        }

        System.out.print("Enter Email: ");
        String email = scanner.next().trim();
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            return;
        }

        System.out.print("Enter Address: ");
        String address = scanner.next().trim();
        if (address.isEmpty()) {
            System.out.println("Address cannot be empty.");
            return;
        }

        String sql = "INSERT INTO Customer (name, phone, email, address) VALUES (?, ?, ?, ?)";
        dbConfig.addRecord(sql, name, phone, email, address);
        System.out.println("Customer added successfully.");
    }

    public void viewCustomers() {
        String sqlQuery = "SELECT * FROM Customer";
        String[] columnHeaders = {"Customer ID", "Name", "Phone"};
        String[] columnNames = {"customerId", "name", "phone"};
        dbConfig.viewRecords(sqlQuery, columnHeaders, columnNames);
    }

    public void updateCustomer() {
        int customerId = getValidIntegerInput("Enter Customer ID to update: ");
        
        if (!idExists(customerId)) {
            System.out.println("Customer ID does not exist.");
            return;
        }

        System.out.print("Enter new Customer Name (leave blank for no change): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter new Phone Number (leave blank for no change): ");
        String phone = scanner.nextLine().trim();
        System.out.print("Enter new Email (leave blank for no change): ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter new Address (leave blank for no change): ");
        String address = scanner.nextLine().trim();

        StringBuilder sql = new StringBuilder("UPDATE Customer SET ");
        boolean hasChanges = false;

        if (!name.isEmpty()) {
            sql.append("name = ?, ");
            hasChanges = true;
        }
        if (!phone.isEmpty()) {
            sql.append("phone = ?, ");
            hasChanges = true;
        }
        if (!email.isEmpty()) {
            sql.append("email = ?, ");
            hasChanges = true;
        }
        if (!address.isEmpty()) {
            sql.append("address = ?, ");
            hasChanges = true;
        }

        if (!hasChanges) {
            System.out.println("No changes made.");
            return;
        }

        
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE customerId = ?");

        try (Connection conn = dbConfig.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (!name.isEmpty()) pstmt.setString(paramIndex++, name);
            if (!phone.isEmpty()) pstmt.setString(paramIndex++, phone);
            if (!email.isEmpty()) pstmt.setString(paramIndex++, email);
            if (!address.isEmpty()) pstmt.setString(paramIndex++, address);
            pstmt.setInt(paramIndex, customerId);

            pstmt.executeUpdate();
            System.out.println("Customer updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }
    }

    public void deleteCustomer() {
        int customerId = getValidIntegerInput("Enter Customer ID to delete: ");
        
        if (!idExists(customerId)) {
            System.out.println("Customer ID does not exist.");
            return;
        }

        System.out.print("Are you sure you want to delete this customer? (yes/no): ");
        String confirmation = scanner.next().trim();
        
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String sql = "DELETE FROM Customer WHERE customerId = ?";
        dbConfig.addRecord(sql, customerId);
        System.out.println("Customer deleted successfully.");
    }

    public int getValidIntegerInput(String prompt) {
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

    public boolean idExists(int id) {
        String sql = "SELECT COUNT(*) FROM Customer WHERE customerId = ?";
        try (Connection conn = dbConfig.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            System.out.println("Error checking ID existence: " + e.getMessage());
        }
        return false; 
    }
}