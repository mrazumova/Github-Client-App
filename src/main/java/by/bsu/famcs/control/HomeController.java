package by.bsu.famcs.control;

import by.bsu.famcs.entity.User;
import by.bsu.famcs.service.ExceptionHandler;
import by.bsu.famcs.service.Loader;
import by.bsu.famcs.utils.AppProperties;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.eclipse.jgit.api.Git;
import org.kohsuke.github.GHRepository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Label lblName, lblUsername;

    @FXML
    private ImageView imAvatar;

    @FXML
    private Pane avatarPane;

    @FXML
    private VBox vbRepository, vbFollowers, vbFollowing, vbLocalActions;

    @FXML
    private Label lblRepoName, lblPrivate, lblRepoLanguage, lblRepoDesc, lblRepoCreation, lblBranches;

    @FXML
    private JFXButton btnDeleteRepo, btnBrowser, btnClone;

    @FXML
    private TextField searchField;

    private FollowsController followersController;

    private FollowsController followingController;

    private RepositoryController repositoryController;

    private LocalActionsController localActionsController;

    private Loader loader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loader = new Loader();
        followersController = new FollowsController();
        followingController = new FollowsController();
        repositoryController = new RepositoryController();
        localActionsController = new LocalActionsController();
        clearDetails();
        loadUserInfo();
        repositoryController.loadRepositories(vbRepository, null);
        followersController.loadFollows(vbFollowers, User.getFollowers());
        followingController.loadFollows(vbFollowing, User.getFollowing());
        localActionsController.loadActions(vbLocalActions);
        showRepositoryInfo();
    }

    @FXML
    private void closeWindow(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void hideWindow(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void logOut(MouseEvent event) {
        User.setUser(null);
        loader.initPage(event, AppProperties.FXML_LOGIN);
    }

    @FXML
    private void deleteRepo(MouseEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm your action");
        alert.setContentText("Do you want to delete this repository?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            GHRepository selected = User.getSelectedRepository();
            if (selected != null) try {
                selected.delete();
                clearDetails();
                User.removeRepository();
                repositoryController.loadRepositories(vbRepository, null);
            } catch (IOException ex) {
                ExceptionHandler.showException("Error: couldn't delete this repository.", ex);
            }
        }
    }

    @FXML
    private void openRepository(MouseEvent event) {
        loader.openWebBrowser(
                AppProperties.GITHUB_OPEN_REPOSITORY
                        .replace("login", User.getLogin())
                        .replace("repository", User.getSelectedRepository().getName()));
    }

    @FXML
    private void reloadRepositories(KeyEvent event) {
        String search = searchField.getText().trim().toLowerCase();
        repositoryController.loadRepositories(vbRepository, search);
    }

    @FXML
    private void createNewRepository(MouseEvent event) {
    }

    @FXML
    private void cloneRepository(MouseEvent event) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(loader.getStage(event));

            String repositoryName = User.getSelectedRepository().getName();

            Git.cloneRepository()
                    .setURI("https://github.com/" + User.getLogin() + "/" + repositoryName + ".git")
                    .setDirectory(new File(selectedDirectory.getAbsolutePath() + "/" + repositoryName))
                    .call();
        } catch (Exception e) {
            ExceptionHandler.showException("Cannot clone repository.", e);
        }
    }

    private void showRepositoryInfo() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            try {
                GHRepository selectedRepository = User.getSelectedRepository();
                if (selectedRepository != null && !selectedRepository.getName().equals(lblRepoName.getText())) {
                    btnDeleteRepo.setDisable(false);
                    btnBrowser.setDisable(false);
                    btnClone.setDisable(false);
                    lblRepoName.setText(selectedRepository.getName());
                    lblRepoLanguage.setText(selectedRepository.getLanguage());
                    lblBranches.setText(String.valueOf(selectedRepository.getBranches().size()));
                    String desc = selectedRepository.getDescription();
                    lblRepoDesc.setText("No description is available.");
                    if (desc != null && !desc.isEmpty()) {
                        lblRepoDesc.setText(desc);
                    }
                    lblPrivate.setText(selectedRepository.isPrivate() ? "Private" : "Public");
                    String createdAt = selectedRepository.getCreatedAt().toString();
                    lblRepoCreation.setText(createdAt.substring(0, 11) + createdAt.substring(24, 28));
                }
            } catch (IOException e) {
                ExceptionHandler.showException("Cannot load repository details.", e);
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadUserInfo() {
        lblName.setText(User.getName());
        lblUsername.setText("@" + User.getLogin());
        imAvatar.setImage(User.getAvatar());
        avatarPane.setMinWidth(User.getAvatar().getRequestedWidth());
        avatarPane.setMaxWidth(User.getAvatar().getRequestedWidth());
    }

    private void clearDetails() {
        lblRepoName.setText("");
        lblRepoDesc.setText("");
        lblRepoCreation.setText("");
        lblRepoLanguage.setText("");
        lblBranches.setText("");
        btnDeleteRepo.setDisable(true);
        btnBrowser.setDisable(true);
        btnClone.setDisable(true);
        repositoryController.setSelectedRepository(null);
    }

}
