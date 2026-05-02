package CarParkingLot.ui;

import CarParkingLot.database.AccountDAO;
import CarParkingLot.models.Account;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class LoginScreen {
    private final Stage stage;

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Parking Lot System - Login");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:" + UIHelper.BG + ";");
        root.setPadding(new Insets(60));

        Label title = new Label("🅿 Parking Lot System");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(UIHelper.PRIMARY));

        Label subtitle = new Label("Please login to continue");
        subtitle.setFont(Font.font(14));
        subtitle.setTextFill(Color.web("#888"));

        TextField usernameField = UIHelper.styledField("Username");
        usernameField.setMaxWidth(320);
        PasswordField passwordField = UIHelper.styledPasswordField("Password");
        passwordField.setMaxWidth(320);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font(13));

        Button loginBtn = UIHelper.primaryButton("Login");
        loginBtn.setMinWidth(320);
        loginBtn.setMinHeight(42);

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            if (user.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please enter username and password.");
                return;
            }
            AccountDAO dao = new AccountDAO();
            Account account = dao.login(user, pass);
            if (account == null) {
                errorLabel.setText("Invalid username or password.");
                return;
            }
            new DashboardScreen(stage, account).show();
        });

        passwordField.setOnAction(e -> loginBtn.fire());

        VBox card = UIHelper.card(title, subtitle, usernameField, passwordField, errorLabel, loginBtn);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(380);

        root.getChildren().add(card);

        UIHelper.applyScene(stage, new Scene(root, 900, 600), 900, 600);
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.show();
    }
}
