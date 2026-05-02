package CarParkingLot.ui;

import CarParkingLot.database.*;
import CarParkingLot.models.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;

public class AdminScreen {
    private final Stage stage;
    private final Account account;
    private final FloorSpotDAO fsDao = new FloorSpotDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public AdminScreen(Stage stage, Account account) {
        this.stage = stage;
        this.account = account;
    }

    public void show() {
        stage.setTitle("Admin Panel");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + UIHelper.BG + ";");
        root.setTop(topBar());

        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-font-size:13px;");
        tabs.getTabs().addAll(
                new Tab("🏢 Floors", floorsTab()),
                new Tab("🅿 Spots", spotsTab()),
                new Tab("👤 Accounts", accountsTab())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));

        root.setCenter(tabs);
        UIHelper.applyScene(stage, new Scene(root, 950, 680), 950, 680);
    }

    // ─── FLOORS TAB ──────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private VBox floorsTab() {
        TableView<ParkingFloor> table = new TableView<>();
        TableColumn<ParkingFloor, String> idCol = col("ID", 60);
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<ParkingFloor, String> nameCol = col("Floor Name", 300);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        table.getColumns().addAll(idCol, nameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refreshFloors(table);

        TextField nameField = UIHelper.styledField("Floor name (e.g. Floor 1)");
        nameField.setMaxWidth(280);
        Button addBtn = UIHelper.primaryButton("Add Floor");
        Button delBtn = UIHelper.dangerButton("Delete Selected");

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { UIHelper.showAlert("Error", "Enter floor name.", Alert.AlertType.ERROR); return; }
            if (fsDao.addFloor(name)) { nameField.clear(); refreshFloors(table); }
            else UIHelper.showAlert("Error", "Could not add floor.", Alert.AlertType.ERROR);
        });

        delBtn.setOnAction(e -> {
            ParkingFloor selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { UIHelper.showAlert("Error", "Select a floor to delete.", Alert.AlertType.WARNING); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete floor '" + selected.getName() + "' and all its spots?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) { fsDao.deleteFloor(selected.getId()); refreshFloors(table); }
            });
        });

        VBox box = new VBox(14, UIHelper.sectionLabel("Manage Floors"),
                new HBox(10, nameField, addBtn, delBtn), table);
        box.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    private void refreshFloors(TableView<ParkingFloor> table) {
        table.setItems(FXCollections.observableArrayList(fsDao.getAllFloors()));
    }

    // ─── SPOTS TAB ───────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private VBox spotsTab() {
        TableView<ParkingSpot> table = new TableView<>();
        TableColumn<ParkingSpot, String> idCol = col("ID", 50);
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<ParkingSpot, String> numCol = col("Spot #", 100);
        numCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpotNumber()));
        TableColumn<ParkingSpot, String> floorCol = col("Floor", 120);
        floorCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFloorName()));
        TableColumn<ParkingSpot, String> typeCol = col("Type", 120);
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpotType().name()));
        TableColumn<ParkingSpot, String> freeCol = col("Status", 90);
        freeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isFree() ? "Free" : "Occupied"));
        freeCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Free".equals(item) ? "-fx-text-fill:#43a047;-fx-font-weight:bold;" : "-fx-text-fill:#e53935;-fx-font-weight:bold;");
            }
        });
        table.getColumns().addAll(idCol, numCol, floorCol, typeCol, freeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refreshSpots(table);

        List<ParkingFloor> floors = fsDao.getAllFloors();
        ComboBox<ParkingFloor> floorBox = new ComboBox<>(FXCollections.observableArrayList(floors));
        floorBox.setPromptText("Select Floor");
        floorBox.setStyle("-fx-font-size:13px;");
        if (!floors.isEmpty()) floorBox.setValue(floors.get(0));

        TextField spotNumField = UIHelper.styledField("Spot number (e.g. A-01)");
        spotNumField.setMaxWidth(160);
        ComboBox<ParkingSpotType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(ParkingSpotType.values());
        typeBox.setValue(ParkingSpotType.COMPACT);
        typeBox.setStyle("-fx-font-size:13px;");

        Button addBtn = UIHelper.primaryButton("Add Spot");
        Button delBtn = UIHelper.dangerButton("Delete Selected");

        addBtn.setOnAction(e -> {
            if (floorBox.getValue() == null) { UIHelper.showAlert("Error", "Select a floor.", Alert.AlertType.ERROR); return; }
            String num = spotNumField.getText().trim();
            if (num.isEmpty()) { UIHelper.showAlert("Error", "Enter spot number.", Alert.AlertType.ERROR); return; }
            ParkingSpot spot = new ParkingSpot(num, floorBox.getValue().getId(), typeBox.getValue());
            if (fsDao.addSpot(spot)) { spotNumField.clear(); refreshSpots(table); }
            else UIHelper.showAlert("Error", "Could not add spot. Number may already exist.", Alert.AlertType.ERROR);
        });

        delBtn.setOnAction(e -> {
            ParkingSpot selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { UIHelper.showAlert("Error", "Select a spot.", Alert.AlertType.WARNING); return; }
            if (!selected.isFree()) { UIHelper.showAlert("Error", "Cannot delete an occupied spot.", Alert.AlertType.ERROR); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete spot " + selected.getSpotNumber() + "?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> { if (btn == ButtonType.YES) { fsDao.deleteSpot(selected.getId()); refreshSpots(table); }});
        });

        HBox form = new HBox(10, floorBox, spotNumField, typeBox, addBtn, delBtn);
        form.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(14, UIHelper.sectionLabel("Manage Parking Spots"), form, table);
        box.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    private void refreshSpots(TableView<ParkingSpot> table) {
        table.setItems(FXCollections.observableArrayList(fsDao.getAllSpots()));
    }

    // ─── ACCOUNTS TAB ────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private VBox accountsTab() {
        TableView<Account> table = new TableView<>();
        TableColumn<Account, String> idCol = col("ID", 50);
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<Account, String> userCol = col("Username", 120);
        userCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        TableColumn<Account, String> nameCol = col("Name", 150);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<Account, String> roleCol = col("Role", 110);
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        TableColumn<Account, String> emailCol = col("Email", 180);
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        TableColumn<Account, String> phoneCol = col("Phone", 120);
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        table.getColumns().addAll(idCol, userCol, nameCol, roleCol, emailCol, phoneCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refreshAccounts(table);

        TextField userField = UIHelper.styledField("Username");
        userField.setMaxWidth(140);
        PasswordField passField = UIHelper.styledPasswordField("Password");
        passField.setMaxWidth(120);
        TextField nameField2 = UIHelper.styledField("Full Name");
        nameField2.setMaxWidth(150);
        TextField emailField = UIHelper.styledField("Email");
        emailField.setMaxWidth(160);
        TextField phoneField = UIHelper.styledField("Phone");
        phoneField.setMaxWidth(120);
        ComboBox<String> roleBox = new ComboBox<>(FXCollections.observableArrayList("ADMIN", "ATTENDANT"));
        roleBox.setValue("ATTENDANT");
        roleBox.setStyle("-fx-font-size:13px;");

        Button addBtn = UIHelper.primaryButton("Add Account");
        Button delBtn = UIHelper.dangerButton("Delete Selected");

        addBtn.setOnAction(e -> {
            if (userField.getText().isEmpty() || passField.getText().isEmpty() || nameField2.getText().isEmpty()) {
                UIHelper.showAlert("Error", "Username, password and name are required.", Alert.AlertType.ERROR);
                return;
            }
            Account a = new Account(userField.getText().trim(), passField.getText().trim(),
                    roleBox.getValue(), nameField2.getText().trim(),
                    emailField.getText().trim(), phoneField.getText().trim());
            if (accountDAO.addAccount(a)) {
                userField.clear(); passField.clear(); nameField2.clear(); emailField.clear(); phoneField.clear();
                refreshAccounts(table);
            } else UIHelper.showAlert("Error", "Could not add account. Username may already exist.", Alert.AlertType.ERROR);
        });

        delBtn.setOnAction(e -> {
            Account selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { UIHelper.showAlert("Error", "Select an account.", Alert.AlertType.WARNING); return; }
            if (selected.getUsername().equals("admin")) { UIHelper.showAlert("Error", "Cannot delete the main admin account.", Alert.AlertType.ERROR); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete account '" + selected.getUsername() + "'?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> { if (btn == ButtonType.YES) { accountDAO.deleteAccount(selected.getId()); refreshAccounts(table); }});
        });

        HBox form = new HBox(8, userField, passField, nameField2, roleBox, emailField, phoneField, addBtn, delBtn);
        form.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(14, UIHelper.sectionLabel("Manage Accounts"), form, table);
        box.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    private void refreshAccounts(TableView<Account> table) {
        table.setItems(FXCollections.observableArrayList(accountDAO.getAllAccounts()));
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
        Label title = new Label("🅿 Parking Lot System — Admin Panel");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button back = new Button("← Back");
        back.setStyle("-fx-background-color:white;-fx-text-fill:" + UIHelper.PRIMARY + ";-fx-background-radius:5;-fx-cursor:hand;");
        back.setOnAction(e -> new CarParkingLot.ui.DashboardScreen(stage, account).show());
        bar.getChildren().addAll(title, spacer, back);
        return bar;
    }
}
