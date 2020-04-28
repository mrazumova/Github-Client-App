package by.bsu.famcs;

import by.bsu.famcs.control.LoginController;
import by.bsu.famcs.utils.AppProperties;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL path = getClass().getResource(AppProperties.FXML_LOGIN);
        if (path != null) {
            Parent root = FXMLLoader.load(path);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            primaryStage.setTitle(AppProperties.APP_TITLE);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            System.err.println("Login Page cannot be loaded.");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
