package by.bsu.famcs;

import by.bsu.famcs.utils.AppProperties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = initLoader();

        primaryStage.setTitle(AppProperties.APP_TITLE);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Scene initLoader() throws IOException {
        URL path = getClass().getResource(AppProperties.FXML_LOGIN);
        Parent root = FXMLLoader.load(path);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
