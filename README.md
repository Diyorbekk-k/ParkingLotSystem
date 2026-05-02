# Parking Lot System

A desktop-based Parking Lot Management System built with Java, JavaFX, and MySQL. Developed as a university group project.


## 🛠 Tech Stack

- **Java 21** — core language
- **JavaFX 21** — desktop UI
- **MySQL / MariaDB** — database (via XAMPP)
- **JDBC** — Java to MySQL connection
- **Maven** — dependency management

---

## ⚙️ Prerequisites

Before running the project, make sure you have the following installed:

- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [JDK 21+](https://adoptium.net/)
- [XAMPP](https://www.apachefriends.org/) — for MySQL database

---

## 🚀 How to Run

> ⚠️ **Important:** This project must be run through the Maven plugin, NOT the green Run button in IntelliJ. Running via the Run button will cause a `JavaFX runtime components are missing` error.

### Step 1 — Start MySQL

Open XAMPP Control Panel and click **Start** next to **MySQL**. Make sure it shows green (Running) on port 3306.

### Step 2 — Clone the repository

```bash
git clone https://github.com/Diyorbekk-k/ParkingLotSystem.git
```

Open the project in IntelliJ via **File → New → Project from Version Control**.

### Step 3 — Load Maven dependencies

When IntelliJ opens the project, look for a popup saying **"Maven projects need to be imported"** and click **Load**. Or go to the Maven panel on the right side and click the refresh icon.

Wait for all dependencies to download (JavaFX and MySQL connector).

### Step 4 — Run the project

In IntelliJ, open the **Maven** panel on the right side:

```
Maven → Plugins → javafx → javafx:run
```

Double-click **javafx:run** to launch the application.

---

## 🔐 Default Login

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Admin |

The database and all tables are created automatically on first launch. No manual SQL setup needed.

---

## 📋 Features

- **Login system** with Admin and Attendant roles
- **Entrance Panel** — issue parking tickets to incoming vehicles
- **Exit Panel** — scan ticket, calculate fee, process payment (cash or credit card)
- **Display Board** — real-time availability per floor and spot type
- **Ticket History** — view all tickets with status and fees
- **Admin Panel**
  - Add / remove parking floors
  - Add / remove parking spots (Handicapped, Compact, Large, Motorcycle, Electric)
  - Add / remove staff accounts

---

## 💰 Parking Fee Structure

| Hour | Rate |
|------|------|
| 1st hour | $4.00 |
| 2nd and 3rd hour | $3.50 each |
| 4th hour onwards | $2.50 each |

---

## 🗄️ Database

The application connects to MySQL on `localhost:3306` with username `root` and no password — the default XAMPP setup. The database `parking_lot` and all tables are created automatically on first run.

If you need to inspect the database, open **phpMyAdmin** at `http://localhost/phpmyadmin` while XAMPP is running.

---

## ⚠️ Known Issues

- The app must be run via Maven (`javafx:run`), not the IntelliJ Run button
- phpMyAdmin may require config fixes on some XAMPP installations — the Java app works independently of phpMyAdmin as long as MySQL is running
- Window must be resized manually on first launch if it appears too small

---

## 📁 Project Structure

```
src/main/java/CarParkingLot/
├── main/
│   └── Main.java                  # Entry point
├── models/
│   ├── Account.java
│   ├── ParkingFloor.java
│   ├── ParkingSpot.java
│   ├── ParkingSpotType.java
│   ├── ParkingTicket.java
│   ├── Payment.java
│   ├── TicketStatus.java
│   ├── Vehicle.java
│   └── VehicleType.java
├── database/
│   ├── DatabaseConnection.java    # Auto-creates DB and tables
│   ├── AccountDAO.java
│   ├── FloorSpotDAO.java
│   └── TicketDAO.java
└── ui/
    ├── UIHelper.java
    ├── LoginScreen.java
    ├── DashboardScreen.java
    ├── EntranceScreen.java
    ├── ExitScreen.java
    ├── DisplayBoardScreen.java
    ├── TicketsScreen.java
    └── AdminScreen.java
```
