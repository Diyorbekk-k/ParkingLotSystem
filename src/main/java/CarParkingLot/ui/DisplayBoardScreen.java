package CarParkingLot.ui;

import CarParkingLot.database.FloorSpotDAO;
import CarParkingLot.models.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DisplayBoardScreen {
    private final Stage stage;
    private final Account account;

    public DisplayBoardScreen(Stage stage, Account account) {
        this.stage = stage;
        this.account = account;
    }

    public void show() {
        stage.setTitle("Parking Display Board");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#111;");
        root.setTop(topBar());

        FloorSpotDAO dao = new FloorSpotDAO();
        List<ParkingFloor> floors = dao.getAllFloors();
        List<ParkingSpot> allSpots = dao.getAllSpots();

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        Label header = new Label("🅿 PARKING AVAILABILITY");
        header.setFont(Font.font("System", FontWeight.BOLD, 28));
        header.setTextFill(Color.web("#FFD700"));

        int totalFree = (int) allSpots.stream().filter(ParkingSpot::isFree).count();
        int total = allSpots.size();

        Label totalLabel = new Label("TOTAL AVAILABLE: " + totalFree + " / " + total);
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        totalLabel.setTextFill(totalFree > 0 ? Color.web("#00e676") : Color.RED);

        content.getChildren().addAll(header, totalLabel);

        if (totalFree == 0) {
            Label full = new Label("⛔ PARKING LOT FULL");
            full.setFont(Font.font("System", FontWeight.BOLD, 24));
            full.setTextFill(Color.RED);
            content.getChildren().add(full);
        }

        Map<Integer, List<ParkingSpot>> byFloor = allSpots.stream()
                .collect(Collectors.groupingBy(ParkingSpot::getFloorId));

        for (ParkingFloor floor : floors) {
            List<ParkingSpot> floorSpots = byFloor.getOrDefault(floor.getId(), List.of());
            VBox floorCard = new VBox(10);
            floorCard.setPadding(new Insets(16));
            floorCard.setStyle("-fx-background-color:#1e1e1e;-fx-border-color:#FFD700;-fx-border-radius:8;" +
                    "-fx-background-radius:8;");
            floorCard.setMaxWidth(700);

            Label floorName = new Label("🏢 " + floor.getName().toUpperCase());
            floorName.setFont(Font.font("System", FontWeight.BOLD, 18));
            floorName.setTextFill(Color.web("#FFD700"));

            GridPane spotGrid = new GridPane();
            spotGrid.setHgap(10);
            spotGrid.setVgap(6);

            Map<ParkingSpotType, Long> freeByType = floorSpots.stream()
                    .filter(ParkingSpot::isFree)
                    .collect(Collectors.groupingBy(ParkingSpot::getSpotType, Collectors.counting()));
            Map<ParkingSpotType, Long> totalByType = floorSpots.stream()
                    .collect(Collectors.groupingBy(ParkingSpot::getSpotType, Collectors.counting()));

            int col = 0;
            for (ParkingSpotType type : ParkingSpotType.values()) {
                long free = freeByType.getOrDefault(type, 0L);
                long tot = totalByType.getOrDefault(type, 0L);
                if (tot == 0) continue;

                VBox typeBox = new VBox(4);
                typeBox.setAlignment(Pos.CENTER);
                typeBox.setPadding(new Insets(10, 16, 10, 16));
                String bg = free > 0 ? "#1b5e20" : "#b71c1c";
                typeBox.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:6;");

                Label typeLbl = new Label(type.name());
                typeLbl.setFont(Font.font("System", FontWeight.BOLD, 12));
                typeLbl.setTextFill(Color.WHITE);

                Label countLbl = new Label(free + "/" + tot);
                countLbl.setFont(Font.font("System", FontWeight.BOLD, 20));
                countLbl.setTextFill(Color.WHITE);

                typeBox.getChildren().addAll(typeLbl, countLbl);
                spotGrid.add(typeBox, col++, 0);
            }

            floorCard.getChildren().addAll(floorName, spotGrid);
            content.getChildren().add(floorCard);
        }

        if (floors.isEmpty()) {
            Label noFloors = new Label("No floors configured yet.");
            noFloors.setTextFill(Color.WHITE);
            content.getChildren().add(noFloors);
        }

        Button refreshBtn = UIHelper.primaryButton("🔄 Refresh");
        refreshBtn.setOnAction(e -> new DisplayBoardScreen(stage, account).show());
        content.getChildren().add(refreshBtn);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:#111;-fx-background:#111;");
        root.setCenter(scroll);

        UIHelper.applyScene(stage, new Scene(root, 900, 650), 900, 650);
    }

    private HBox topBar() {
        HBox bar = new HBox(12);
        bar.setPadding(new Insets(14, 24, 14, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color:#000;");
        Label title = new Label("🅿 Parking Display Board");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#FFD700"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button back = new Button("← Back");
        back.setStyle("-fx-background-color:#FFD700;-fx-text-fill:black;-fx-background-radius:5;-fx-cursor:hand;");
        back.setOnAction(e -> new DashboardScreen(stage, account).show());
        bar.getChildren().addAll(title, spacer, back);
        return bar;
    }
}
