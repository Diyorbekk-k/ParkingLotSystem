package CarParkingLot.database;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/parking_lot";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        String createDB = "CREATE DATABASE IF NOT EXISTS parking_lot";
        String useDB = "USE parking_lot";

        String accounts = """
            CREATE TABLE IF NOT EXISTS accounts (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL,
                role ENUM('ADMIN','ATTENDANT') NOT NULL,
                name VARCHAR(100),
                email VARCHAR(100),
                phone VARCHAR(20)
            )
        """;

        String floors = """
            CREATE TABLE IF NOT EXISTS parking_floors (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(50) NOT NULL
            )
        """;

        String spots = """
            CREATE TABLE IF NOT EXISTS parking_spots (
                id INT AUTO_INCREMENT PRIMARY KEY,
                spot_number VARCHAR(20) NOT NULL,
                floor_id INT,
                spot_type ENUM('HANDICAPPED','COMPACT','LARGE','MOTORCYCLE','ELECTRIC') NOT NULL,
                is_free BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (floor_id) REFERENCES parking_floors(id) ON DELETE CASCADE
            )
        """;

        String vehicles = """
            CREATE TABLE IF NOT EXISTS vehicles (
                id INT AUTO_INCREMENT PRIMARY KEY,
                license_number VARCHAR(20) UNIQUE NOT NULL,
                vehicle_type ENUM('CAR','TRUCK','VAN','MOTORCYCLE','ELECTRIC') NOT NULL
            )
        """;

        String tickets = """
            CREATE TABLE IF NOT EXISTS parking_tickets (
                id INT AUTO_INCREMENT PRIMARY KEY,
                ticket_number VARCHAR(50) UNIQUE NOT NULL,
                issued_at DATETIME NOT NULL,
                paid_at DATETIME,
                paid_amount DOUBLE DEFAULT 0,
                status ENUM('ACTIVE','PAID','LOST') DEFAULT 'ACTIVE',
                vehicle_id INT,
                spot_id INT,
                FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
                FOREIGN KEY (spot_id) REFERENCES parking_spots(id)
            )
        """;

        String payments = """
            CREATE TABLE IF NOT EXISTS payments (
                id INT AUTO_INCREMENT PRIMARY KEY,
                ticket_id INT NOT NULL,
                amount DOUBLE NOT NULL,
                payment_type ENUM('CASH','CREDIT_CARD') NOT NULL,
                created_at DATETIME NOT NULL,
                name_on_card VARCHAR(100),
                cash_tendered DOUBLE,
                FOREIGN KEY (ticket_id) REFERENCES parking_tickets(id)
            )
        """;

        String rates = """
            CREATE TABLE IF NOT EXISTS parking_rates (
                id INT AUTO_INCREMENT PRIMARY KEY,
                hour_number INT NOT NULL,
                rate DOUBLE NOT NULL
            )
        """;

        String insertRates = """
            INSERT IGNORE INTO parking_rates (hour_number, rate) VALUES
            (1, 4.0), (2, 3.5), (3, 3.5), (4, 2.5)
        """;

        String insertAdmin = """
            INSERT IGNORE INTO accounts (username, password, role, name, email, phone)
            VALUES ('admin', 'admin123', 'ADMIN', 'Administrator', 'admin@parking.com', '0000000000')
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createDB);
            stmt.execute(useDB);
            stmt.execute(accounts);
            stmt.execute(floors);
            stmt.execute(spots);
            stmt.execute(vehicles);
            stmt.execute(tickets);
            stmt.execute(payments);
            stmt.execute(rates);
            stmt.execute(insertRates);
            stmt.execute(insertAdmin);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
}
