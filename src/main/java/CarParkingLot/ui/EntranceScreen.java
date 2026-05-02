package CarParkingLot.ui;

import CarParkingLot.database.FloorSpotDAO;
import CarParkingLot.database.TicketDAO;
import CarParkingLot.models.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EntranceScreen {
    private final Stage stage;
    private final Account account;

    public EntranceScreen(Stage stage, Account account) {
        this.stage = stage;
        this.account = account;
    }

    public void show() {
        stage.setTitle("Entrance Panel - Issue Ticket");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + UIHelper.BG + ";");
        root.setTop(topBar());

        FloorSpotDAO fsDao = new FloorSpotDAO();
        List<ParkingSpot> freeSpots = fsDao.getFreeSpots();

        if (freeSpots.isEmpty()) {
            VBox full = new VBox(16);
            full.setAlignment(Pos.CENTER);
            Label lbl = new Label("🚫 PARKING LOT IS FULL");
            lbl.setFont(Font.font("System", FontWeight.BOLD, 28));
            lbl.setTextFill(Color.RED);
            Label sub = new Label("No available spots at this time. Please come back later.");
            sub.setFont(Font.font(15));
            full.getChildren().addAll(lbl, sub);
            root.setCenter(full);
            UIHelper.applyScene(stage, new Scene(root, 900, 620), 900, 620);
            return;
        }

        // Form
        TextField licenseField = UIHelper.styledField("License Plate Number (e.g. ABC-1234)");
        licenseField.setMaxWidth(360);

        ComboBox<VehicleType> vehicleTypeBox = new ComboBox<>();
        vehicleTypeBox.getItems().addAll(VehicleType.values());
        vehicleTypeBox.setValue(VehicleType.CAR);
        vehicleTypeBox.setMaxWidth(360);
        vehicleTypeBox.setStyle("-fx-font-size:13px;");

        ComboBox<ParkingSpot> spotBox = new ComboBox<>();
        spotBox.getItems().addAll(freeSpots);
        spotBox.setValue(freeSpots.get(0));
        spotBox.setMaxWidth(360);
        spotBox.setStyle("-fx-font-size:13px;");

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        Label freeCount = new Label("Available spots: " + freeSpots.size());
        freeCount.setFont(Font.font(13));
        freeCount.setTextFill(Color.web("#43a047"));

        Button issueBtn = UIHelper.primaryButton("Issue Ticket");
        issueBtn.setMinWidth(360);
        issueBtn.setMinHeight(40);

        // Ticket receipt area
        VBox receiptBox = new VBox(8);
        receiptBox.setVisible(false);

        issueBtn.setOnAction(e -> {
            String license = licenseField.getText().trim().toUpperCase();
            ParkingSpot selectedSpot = spotBox.getValue();
            VehicleType vType = vehicleTypeBox.getValue();

            if (license.isEmpty()) { errorLabel.setText("Please enter license plate."); return; }
            if (selectedSpot == null) { errorLabel.setText("Please select a spot."); return; }

            TicketDAO ticketDAO = new TicketDAO();
            ParkingTicket ticket = ticketDAO.issueTicket(license, vType, selectedSpot.getId());

            if (ticket == null) {
                errorLabel.setText("Error issuing ticket. Vehicle may already be parked.");
                return;
            }

            errorLabel.setText("");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            receiptBox.getChildren().clear();
            receiptBox.setStyle("-fx-background-color:#e8f5e9;-fx-border-color:#43a047;-fx-border-radius:8;" +
                    "-fx-background-radius:8;-fx-padding:16;-fx-text-fill:#111;");
            receiptBox.getChildren().addAll(
                    boldLabel("✅ Ticket Issued Successfully!"),
                    infoRow("Ticket #:", ticket.getTicketNumber(), true),
                    infoRow("License:", ticket.getVehicle().getLicenseNumber(), false),
                    infoRow("Vehicle:", ticket.getVehicle().getVehicleType().name(), false),
                    infoRow("Floor:", ticket.getSpot().getFloorName(), false),
                    infoRow("Spot:", ticket.getSpot().getSpotNumber(), false),
                    infoRow("Spot Type:", ticket.getSpot().getSpotType().name(), false),
                    infoRow("Issued At:", ticket.getIssuedAt().format(fmt), false),
                    new Label("Rate: $4.00 (1st hr) | $3.50 (2nd-3rd hr) | $2.50 (4th+ hr)") {{
                        setTextFill(javafx.scene.paint.Color.web("#333"));
                        setFont(javafx.scene.text.Font.font(12));
                    }}
            );
            receiptBox.setVisible(true);

            licenseField.clear();
            // Refresh spots
            List<ParkingSpot> updated = fsDao.getFreeSpots();
            spotBox.getItems().setAll(updated);
            if (!updated.isEmpty()) spotBox.setValue(updated.get(0));
            freeCount.setText("Available spots: " + updated.size());
        });

        VBox form = UIHelper.card(
                UIHelper.titleLabel("🚗 Entrance Panel"),
                freeCount,
                UIHelper.sectionLabel("License Plate"),
                licenseField,
                UIHelper.sectionLabel("Vehicle Type"),
                vehicleTypeBox,
                UIHelper.sectionLabel("Select Parking Spot"),
                spotBox,
                errorLabel,
                issueBtn,
                receiptBox
        );
        form.setMaxWidth(420);

        VBox center = new VBox(form);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(30));
        root.setCenter(center);

        UIHelper.applyScene(stage, new Scene(root, 900, 700), 900, 700);
    }

    private HBox topBar() {
        HBox bar = new HBox(12);
        bar.setPadding(new Insets(14, 24, 14, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color:" + UIHelper.PRIMARY + ";");
        Label title = new Label("🅿 Parking Lot System");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button back = new Button("← Back");
        back.setStyle("-fx-background-color:white;-fx-text-fill:" + UIHelper.PRIMARY + ";-fx-background-radius:5;-fx-cursor:hand;");
        back.setOnAction(e -> new DashboardScreen(stage, account).show());
        bar.getChildren().addAll(title, spacer, back);
        return bar;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 14));
        l.setTextFill(Color.web("#000000"));
        l.setStyle("-fx-text-fill:#000000;");
        return l;
    }

    private HBox infoRow(String key, String value) {
        return infoRow(key, value, false);
    }

    private HBox infoRow(String key, String value, boolean copyable) {
        HBox row = new HBox(8);
        Label k = new Label(key);
        k.setFont(Font.font("System", FontWeight.BOLD, 13));
        k.setTextFill(Color.web("#000000"));
        k.setStyle("-fx-text-fill:#000000;");
        k.setMinWidth(90);
        if (copyable) {
            row.getChildren().addAll(k, UIHelper.copyableValue(value));
        } else {
            Label v = new Label(value);
            v.setFont(Font.font(13));
            v.setTextFill(Color.web("#000000"));
            v.setStyle("-fx-text-fill:#000000;");
            row.getChildren().addAll(k, v);
        }
        return row;
    }
}