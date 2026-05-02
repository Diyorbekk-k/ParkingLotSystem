package CarParkingLot.ui;

import CarParkingLot.database.FloorSpotDAO;
import CarParkingLot.database.TicketDAO;
import CarParkingLot.models.Account;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class DashboardScreen {
    private final Stage stage;
    private final Account account;

    public DashboardScreen(Stage stage, Account account) {
        this.stage = stage;
        this.account = account;
    }

    public void show() {
        stage.setTitle("Parking Lot System - Dashboard");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + UIHelper.BG + ";");

        // ── TOP NAV ──
        HBox nav = new HBox(16);
        nav.setPadding(new Insets(14, 24, 14, 24));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle("-fx-background-color:" + UIHelper.PRIMARY + ";");

        Label logo = new Label("🅿 Parking Lot System");
        logo.setFont(Font.font("System", FontWeight.BOLD, 18));
        logo.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label("👤 " + account.getName() + " (" + account.getRole() + ")");
        userInfo.setTextFill(Color.WHITE);
        userInfo.setFont(Font.font(13));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color:white;-fx-text-fill:" + UIHelper.PRIMARY + ";-fx-background-radius:5;-fx-cursor:hand;");
        logoutBtn.setOnAction(e -> new CarParkingLot.ui.LoginScreen(stage).show());

        nav.getChildren().addAll(logo, spacer, userInfo, logoutBtn);
        root.setTop(nav);

        // ── STATS ROW ──
        FloorSpotDAO fsDao = new FloorSpotDAO();
        TicketDAO ticketDAO = new TicketDAO();
        int total = fsDao.getTotalSpots();
        int free = fsDao.getFreeSpotCount();
        int occupied = total - free;
        int activeTickets = ticketDAO.getAllActiveTickets().size();

        HBox stats = new HBox(16);
        stats.setPadding(new Insets(20, 24, 0, 24));
        stats.getChildren().addAll(
                statCard("Total Spots", String.valueOf(total), "#1a73e8"),
                statCard("Free Spots", String.valueOf(free), "#43a047"),
                statCard("Occupied", String.valueOf(occupied), "#e53935"),
                statCard("Active Tickets", String.valueOf(activeTickets), "#fb8c00")
        );

        // ── MENU GRID ──
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPadding(new Insets(20, 24, 24, 24));

        Button entranceBtn = menuButton("🚗  Issue Ticket\n(Entrance)", UIHelper.PRIMARY);
        Button exitBtn     = menuButton("🏁  Process Exit\n(Exit Panel)", UIHelper.SUCCESS);
        Button ticketsBtn  = menuButton("🎫  View All Tickets", "#6200ea");
        Button displayBtn  = menuButton("📋  Display Board\n(Available Spots)", "#0097a7");

        entranceBtn.setOnAction(e -> new CarParkingLot.ui.EntranceScreen(stage, account).show());
        exitBtn.setOnAction(e -> new CarParkingLot.ui.ExitScreen(stage, account).show());
        ticketsBtn.setOnAction(e -> new CarParkingLot.ui.TicketsScreen(stage, account).show());
        displayBtn.setOnAction(e -> new CarParkingLot.ui.DisplayBoardScreen(stage, account).show());

        grid.add(entranceBtn, 0, 0);
        grid.add(exitBtn,     1, 0);
        grid.add(ticketsBtn,  0, 1);
        grid.add(displayBtn,  1, 1);

        if (account.isAdmin()) {
            Button adminBtn = menuButton("Admin Panel\n(Floors, Spots, Accounts)", "#37474f");
            adminBtn.setOnAction(e -> new AdminScreen(stage, account).show());
            grid.add(adminBtn, 0, 2, 2, 1);
            adminBtn.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(adminBtn, Priority.ALWAYS);
        }

        VBox center = new VBox(0, stats, grid);
        root.setCenter(center);

        UIHelper.applyScene(stage, new Scene(root, 900, 620), 900, 620);
    }

    private VBox statCard(String label, String value, String color) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16, 24, 16, 24));
        box.setStyle("-fx-background-color:white;-fx-background-radius:10;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.07),8,0,0,2);");
        box.setPrefWidth(180);

        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 30));
        val.setTextFill(Color.web(color));

        Label lbl = new Label(label);
        lbl.setFont(Font.font(13));
        lbl.setTextFill(Color.web("#666"));

        box.getChildren().addAll(val, lbl);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private Button menuButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color:" + color + ";-fx-text-fill:white;-fx-font-size:14px;" +
                "-fx-padding:24 32;-fx-background-radius:10;-fx-cursor:hand;-fx-alignment:center;");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMaxHeight(Double.MAX_VALUE);
        btn.setMinHeight(100);
        GridPane.setHgrow(btn, Priority.ALWAYS);
        GridPane.setVgrow(btn, Priority.ALWAYS);
        return btn;
    }
}