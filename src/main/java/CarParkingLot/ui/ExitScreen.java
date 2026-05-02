package CarParkingLot.ui;

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

public class ExitScreen {
    private final Stage stage;
    private final Account account;
    private ParkingTicket currentTicket;

    public ExitScreen(Stage stage, Account account) {
        this.stage = stage;
        this.account = account;
    }

    public void show() {
        stage.setTitle("Exit Panel");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + UIHelper.BG + ";");
        root.setTop(topBar());

        TextField ticketField = UIHelper.styledField("Enter Ticket Number (e.g. TKT-XXXXXXXX)");
        ticketField.setMaxWidth(360);

        Button scanBtn = UIHelper.primaryButton("Scan Ticket");
        scanBtn.setMinWidth(360);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        VBox ticketInfoBox = new VBox(8);
        ticketInfoBox.setVisible(false);

        VBox paymentBox = new VBox(12);
        paymentBox.setVisible(false);

        VBox successBox = new VBox(10);
        successBox.setVisible(false);

        scanBtn.setOnAction(e -> {
            String number = ticketField.getText().trim().toUpperCase();
            if (number.isEmpty()) { errorLabel.setText("Please enter ticket number."); return; }

            TicketDAO dao = new TicketDAO();
            currentTicket = dao.getTicketByNumber(number);

            if (currentTicket == null) {
                errorLabel.setText("Ticket not found.");
                ticketInfoBox.setVisible(false);
                paymentBox.setVisible(false);
                return;
            }

            errorLabel.setText("");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            double fee = currentTicket.calculateFee();

            ticketInfoBox.getChildren().clear();
            ticketInfoBox.setStyle("-fx-background-color:#e3f2fd;-fx-border-color:#1a73e8;" +
                    "-fx-border-radius:8;-fx-background-radius:8;-fx-padding:14;-fx-text-fill:#111;");

            if (currentTicket.getStatus() == TicketStatus.PAID) {
                ticketInfoBox.getChildren().addAll(
                        boldLabel("✅ Ticket Already Paid"),
                        infoRow("Ticket #:", currentTicket.getTicketNumber(), true),
                        infoRow("Paid At:", currentTicket.getPaidAt().format(fmt)),
                        infoRow("Amount Paid:", "$" + String.format("%.2f", currentTicket.getPaidAmount()))
                );
                ticketInfoBox.setVisible(true);
                paymentBox.setVisible(false);
                successBox.setVisible(false);
                return;
            }

            ticketInfoBox.getChildren().addAll(
                    boldLabel("🎫 Ticket Details"),
                    infoRow("Ticket #:", currentTicket.getTicketNumber(), true),
                    infoRow("License:", currentTicket.getVehicle().getLicenseNumber()),
                    infoRow("Vehicle:", currentTicket.getVehicle().getVehicleType().name()),
                    infoRow("Floor:", currentTicket.getSpot().getFloorName()),
                    infoRow("Spot:", currentTicket.getSpot().getSpotNumber()),
                    infoRow("Issued At:", currentTicket.getIssuedAt().format(fmt)),
                    boldLabel("💰 Amount Due: $" + String.format("%.2f", fee))
            );
            ticketInfoBox.setVisible(true);

            // Payment form
            paymentBox.getChildren().clear();
            ToggleGroup payGroup = new ToggleGroup();
            RadioButton cashRb = new RadioButton("Cash");
            RadioButton cardRb = new RadioButton("Credit Card");
            cashRb.setToggleGroup(payGroup);
            cardRb.setToggleGroup(payGroup);
            cashRb.setSelected(true);

            TextField cashField = UIHelper.styledField("Cash Amount Tendered ($)");
            cashField.setMaxWidth(300);
            TextField cardNameField = UIHelper.styledField("Name on Card");
            cardNameField.setMaxWidth(300);
            cardNameField.setVisible(false);
            cashField.setVisible(true);

            payGroup.selectedToggleProperty().addListener((obs, o, n) -> {
                boolean isCash = ((RadioButton) n) == cashRb;
                cashField.setVisible(isCash);
                cardNameField.setVisible(!isCash);
            });

            Label payError = new Label("");
            payError.setTextFill(Color.RED);

            Button payBtn = UIHelper.successButton("💳 Process Payment");
            payBtn.setMinWidth(300);

            double finalFee = fee;
            payBtn.setOnAction(ev -> {
                String payType = cashRb.isSelected() ? "CASH" : "CREDIT_CARD";
                double cashTendered = 0;
                String nameOnCard = "";

                if (cashRb.isSelected()) {
                    try {
                        cashTendered = Double.parseDouble(cashField.getText().trim());
                        if (cashTendered < finalFee) {
                            payError.setText("Insufficient cash. Amount due: $" + String.format("%.2f", finalFee));
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        payError.setText("Invalid cash amount.");
                        return;
                    }
                } else {
                    nameOnCard = cardNameField.getText().trim();
                    if (nameOnCard.isEmpty()) { payError.setText("Please enter name on card."); return; }
                }

                TicketDAO tDao = new TicketDAO();
                boolean success = tDao.payTicket(currentTicket.getId(), finalFee, payType, nameOnCard, cashTendered);

                if (success) {
                    paymentBox.setVisible(false);
                    ticketInfoBox.setVisible(false);
                    successBox.getChildren().clear();
                    successBox.setStyle("-fx-background-color:#e8f5e9;-fx-border-color:#43a047;" +
                            "-fx-border-radius:8;-fx-background-radius:8;-fx-padding:16;-fx-text-fill:#111;");
                    double change = cashTendered - finalFee;
                    successBox.getChildren().addAll(
                            boldLabel("✅ Payment Successful! Gate Opening..."),
                            infoRow("Ticket #:", currentTicket.getTicketNumber(), true),
                            infoRow("Amount Paid:", "$" + String.format("%.2f", finalFee)),
                            infoRow("Payment Type:", payType),
                            cashRb.isSelected() ? infoRow("Change:", "$" + String.format("%.2f", Math.max(0, change))) : new Label("")
                    );
                    successBox.setVisible(true);
                    ticketField.clear();
                } else {
                    payError.setText("Payment processing failed. Please try again.");
                }
            });

            paymentBox.getChildren().addAll(
                    sectionLabel("Select Payment Method"),
                    new HBox(16, cashRb, cardRb),
                    cashField, cardNameField,
                    payError, payBtn
            );
            paymentBox.setStyle("-fx-background-color:#f1f8ff;-fx-border-color:#90caf9;" +
                    "-fx-border-radius:8;-fx-background-radius:8;-fx-padding:14;-fx-text-fill:#111;");
            cashRb.setStyle("-fx-text-fill:#111;-fx-font-size:13px;");
            cardRb.setStyle("-fx-text-fill:#111;-fx-font-size:13px;");
            paymentBox.setVisible(true);
            successBox.setVisible(false);
        });

        VBox form = UIHelper.card(
                UIHelper.titleLabel("🏁 Exit Panel"),
                CarParkingLot.ui.UIHelper.sectionLabel("Scan / Enter Ticket Number"),
                ticketField,
                errorLabel,
                scanBtn,
                ticketInfoBox,
                paymentBox,
                successBox
        );
        form.setMaxWidth(440);

        VBox center = new VBox(form);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(30));

        ScrollPane scroll = new ScrollPane(center);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:transparent;");

        root.setCenter(scroll);
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

    private Label sectionLabel(String text) {
        return CarParkingLot.ui.UIHelper.sectionLabel(text);
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
        k.setMinWidth(100);
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