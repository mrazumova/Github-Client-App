package by.bsu.famcs.control;

import by.bsu.famcs.utils.AppProperties;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    private static GHUser githubUser;

    public static GHUser getGithubUser() {
        return githubUser;
    }

    public static void setGithubUser(GHUser githubUser) {
        LoginController.githubUser = githubUser;
    }

    public static GHPersonSet<GHUser> getFollowers() throws IOException {
        return githubUser.getFollowers();
    }

    public static GHPersonSet<GHUser> getFollowing() throws IOException {
        return githubUser.getFollows();
    }

    @FXML
    private void closeWindow(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void joinGithub(ActionEvent event) {
        openWebBrowser(AppProperties.GITHUB_JOIN_URL);
    }

    @FXML
    private void signIn(MouseEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() && password.isEmpty()) {
            Notifications nf = makeNotification("Error!", "Fields cannot be empty.");
            nf.showError();
        } else {
            try {
                GitHub github = new GitHubBuilder().withPassword(username, password).build();
                githubUser = github.getMyself();
                
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                
                URL path = getClass().getResource(AppProperties.FXML_HOME);
                if (path != null) {
                    Parent root = FXMLLoader.load(path);

                    Scene scene = new Scene(root);
                    scene.setFill(Color.TRANSPARENT);
                    
                    stage.setTitle(AppProperties.APP_TITLE);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    throw new IOException("Error: loading home FXML file.");
                }
            } catch (IOException e) {
                System.err.println("Error with login: " + e.getMessage());
                Notifications nf = makeNotification("Error!", "Invalid username or password.");
                nf.showError();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void openWebBrowser(String uri) {
        try {
            Desktop.getDesktop().browse(URI.create(uri));
        } catch (IOException e) {
            String title = "Error has occurred!";
            String text = AppProperties.APP_TITLE + " is unable to open a web browser.";
            Notifications nf = makeNotification(title, text);
            nf.showError();
        }
    }

    private Notifications makeNotification(String title, String text) {
        Notifications builder = Notifications.create()
                .title(title)
                .text(text)
                .graphic(null)
                .hideAfter(Duration.seconds(3))
                .position(Pos.BOTTOM_RIGHT);
        return builder;
    }

}
