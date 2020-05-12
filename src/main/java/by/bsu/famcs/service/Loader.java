package by.bsu.famcs.service;

import by.bsu.famcs.utils.AppProperties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class Loader {

    public void initPage(MouseEvent event, String page) {
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();

            URL path = getClass().getResource(page);
            Parent root = FXMLLoader.load(path);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            stage.setTitle(AppProperties.APP_TITLE);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getStage(MouseEvent event) {
        Node node = (Node) event.getSource();
        return (Stage) node.getScene().getWindow();
    }

    public void openWebBrowser(String uri) {
        try {
            Desktop.getDesktop().browse(URI.create(uri));
        } catch (IOException e) {
            String title = "Error has occurred!";
            String text = AppProperties.APP_TITLE + " is unable to open a web browser.";
            new Notification(title, text);
        }
    }


}
