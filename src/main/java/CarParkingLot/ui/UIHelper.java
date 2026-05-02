package CarParkingLot.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class UIHelper {

    public static final String PRIMARY = "#1a73e8";
    public static final String DANGER  = "#e53935";
    public static final String SUCCESS = "#43a047";
    public static final String BG      = "#f5f7fa";
    public static final String CARD    = "#ffffff";

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;-fx-font-size:13px;" +
                "-fx-padding:8 20;-fx-background-radius:6;-fx-cursor:hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:#1558b0;-fx-text-fill:white;" +
                "-fx-font-size:13px;-fx-padding:8 20;-fx-background-radius:6;-fx-cursor:hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;" +
                "-fx-font-size:13px;-fx-padding:8 20;-fx-background-radius:6;-fx-cursor:hand;"));
        return btn;
    }

    public static Button dangerButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color:" + DANGER + ";-fx-text-fill:white;-fx-font-size:13px;" +
                "-fx-padding:8 20;-fx-background-radius:6;-fx-cursor:hand;");
        return btn;
    }

    public static Button successButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color:" + SUCCESS + ";-fx-text-fill:white;-fx-font-size:13px;" +
                "-fx-padding:8 20;-fx-background-radius:6;-fx-cursor:hand;");
        return btn;
    }

    public static Label titleLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web("#000000"));
        lbl.setStyle("-fx-text-fill:#000000;");
        return lbl;
    }

    public static Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 15));
        lbl.setTextFill(Color.web("#000000"));
        lbl.setStyle("-fx-text-fill:#000000;");
        return lbl;
    }

    public static TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding:8;-fx-border-color:#ccc;-fx-border-radius:5;-fx-background-radius:5;-fx-font-size:13px;");
        return tf;
    }

    public static PasswordField styledPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle("-fx-padding:8;-fx-border-color:#ccc;-fx-border-radius:5;-fx-background-radius:5;-fx-font-size:13px;");
        return pf;
    }

    public static VBox card(javafx.scene.Node... children) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color:white;-fx-background-radius:10;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,2);");
        box.getChildren().addAll(children);
        return box;
    }

    public static void applyScene(javafx.stage.Stage stage, javafx.scene.Scene scene,
                                  double defaultWidth, double defaultHeight) {
        boolean hasSize = stage.getScene() != null
                && stage.getWidth() > 0 && stage.getHeight() > 0;
        stage.setScene(scene);
        if (!hasSize) {
            stage.setWidth(defaultWidth);
            stage.setHeight(defaultHeight);
        }
    }

    public static TextField copyableValue(String value) {
        TextField tf = new TextField(value);
        tf.setEditable(false);
        tf.setFont(Font.font("System", 13));
        tf.setBackground(javafx.scene.layout.Background.EMPTY);
        tf.setBorder(javafx.scene.layout.Border.EMPTY);
        tf.setPadding(new Insets(0));
        tf.setStyle("-fx-text-fill:#111;-fx-highlight-fill:#1a73e8;-fx-highlight-text-fill:white;");
        return tf;
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}