package CarParkingLot.ui;

import CarParkingLot.database.TicketDAO;
import CarParkingLot.models.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketsScreen {
    private final Stage stage;
    private final Account account;

    public TicketsScreen(Stage stage, Account account) {
        this.stage = stage;
        this.account = account;
    }

    @SuppressWarnings("unchecked")
    public void show() {
        stage.setTitle("All Tickets");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + UIHelper.BG + ";");
        root.setTop(topBar());

        TicketDAO dao = new TicketDAO();
        List<ParkingTicket> tickets = dao.getAllTickets();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        TableView<ParkingTicket> table = new TableView<>();
        table.setStyle("-fx-font-size:13px;");

        TableColumn<ParkingTicket, String> numCol = col("Ticket #", 160);
        numCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTicketNumber()));
        numCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                HBox box = new HBox(8);
                TextField tf = new TextField(item);
                tf.setEditable(false);
                tf.setStyle("-fx-font-size:12px;-fx-control-inner-background:#f5f5f5;-fx-border-color:#ddd;-fx-border-radius:3;");
                tf.setPrefWidth(140);
                Button copyBtn = new Button("📋");
                copyBtn.setStyle("-fx-font-size:11px;-fx-padding:4 8;-fx-background-color:#1a73e8;-fx-text-fill:white;-fx-background-radius:3;");
                copyBtn.setOnAction(e -> {
                    javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(item);
                    clipboard.setContent(content);
                });
                box.getChildren().addAll(tf, copyBtn);
                setGraphic(box);
                setText(null);
            }
        });

        TableColumn<ParkingTicket, String> licCol = col("License", 110);
        licCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getVehicle() != null ? c.getValue().getVehicle().getLicenseNumber() : ""));
        licCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) { setText(null); setGraphic(null); return; }
                HBox box = new HBox(8);
                TextField tf = new TextField(item);
                tf.setEditable(false);
                tf.setStyle("-fx-font-size:12px;-fx-control-inner-background:#f5f5f5;-fx-border-color:#ddd;-fx-border-radius:3;");
                tf.setPrefWidth(85);
                Button copyBtn = new Button("📋");
                copyBtn.setStyle("-fx-font-size:11px;-fx-padding:4 8;-fx-background-color:#1a73e8;-fx-text-fill:white;-fx-background-radius:3;");
                copyBtn.setOnAction(e -> {
                    javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(item);
                    clipboard.setContent(content);
                });
                box.getChildren().addAll(tf, copyBtn);
                setGraphic(box);
                setText(null);
            }
        });

        TableColumn<ParkingTicket, String> typeCol = col("Vehicle", 100);
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getVehicle() != null ? c.getValue().getVehicle().getVehicleType().name() : ""));

        TableColumn<ParkingTicket, String> floorCol = col("Floor", 90);
        floorCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSpot() != null ? c.getValue().getSpot().getFloorName() : ""));

        TableColumn<ParkingTicket, String> spotCol = col("Spot", 90);
        spotCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSpot() != null ? c.getValue().getSpot().getSpotNumber() : ""));

        TableColumn<ParkingTicket, String> issuedCol = col("Issued At", 140);
        issuedCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getIssuedAt() != null ? c.getValue().getIssuedAt().format(fmt) : ""));

        TableColumn<ParkingTicket, String> statusCol = col("Status", 90);
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                if ("PAID".equals(item)) setStyle("-fx-text-fill:#43a047;-fx-font-weight:bold;");
                else if ("ACTIVE".equals(item)) setStyle("-fx-text-fill:#1a73e8;-fx-font-weight:bold;");
                else setStyle("-fx-text-fill:#e53935;-fx-font-weight:bold;");
            }
        });

        TableColumn<ParkingTicket, String> feeCol = col("Fee ($)", 80);
        feeCol.setCellValueFactory(c -> {
            ParkingTicket t = c.getValue();
            if (t.getStatus() == TicketStatus.PAID)
                return new SimpleStringProperty(String.format("%.2f", t.getPaidAmount()));
            return new SimpleStringProperty(String.format("%.2f", t.calculateFee()));
        });

        table.getColumns().addAll(numCol, licCol, typeCol, floorCol, spotCol, issuedCol, statusCol, feeCol);
        table.setItems(FXCollections.observableArrayList(tickets));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label countLbl = new Label("Total tickets: " + tickets.size());
        countLbl.setFont(Font.font(13));
        countLbl.setTextFill(Color.web("#666"));

        VBox content = new VBox(12, UIHelper.titleLabel("🎫 All Parking Tickets"), countLbl, table);
        content.setPadding(new Insets(24));
        VBox.setVgrow(table, Priority.ALWAYS);

        root.setCenter(content);
        UIHelper.applyScene(stage, new Scene(root, 950, 650), 950, 650);
    }

    private <T> TableColumn<T, String> col(String title, double width) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        return col;
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
}