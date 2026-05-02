package CarParkingLot.main;

import CarParkingLot.database.DatabaseConnection;
import CarParkingLot.ui.LoginScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseConnection.initializeDatabase();
        LoginScreen loginScreen = new LoginScreen(primaryStage);
        loginScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
