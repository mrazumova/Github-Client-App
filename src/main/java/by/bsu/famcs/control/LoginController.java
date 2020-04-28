package by.bsu.famcs.control;

import by.bsu.famcs.entity.User;
import by.bsu.famcs.service.Loader;
import by.bsu.famcs.service.Notification;
import by.bsu.famcs.utils.AppProperties;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    private Loader loader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loader = new Loader();
    }

    @FXML
    private void signIn(MouseEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() && password.isEmpty()) {
            new Notification("Error!", "Fields cannot be empty.");
        } else {
            try {
                GitHub github = new GitHubBuilder().withPassword(username, password).build();
                User.setUser(github.getMyself());
                loader.initPage(event, AppProperties.FXML_HOME);
            } catch (IOException e) {
                new Notification("Error!", "Invalid username or password.");
            }
        }
    }

    @FXML
    private void joinGithub(ActionEvent event) {
        loader.openWebBrowser(AppProperties.GITHUB_JOIN_URL);
    }

    @FXML
    private void closeWindow(MouseEvent event) {
        System.exit(0);
    }

}
