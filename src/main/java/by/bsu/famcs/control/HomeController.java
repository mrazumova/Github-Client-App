package by.bsu.famcs.control;

import by.bsu.famcs.entity.User;
import by.bsu.famcs.service.ExceptionHandler;
import by.bsu.famcs.service.Loader;
import by.bsu.famcs.utils.AppProperties;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kohsuke.github.GHRepository;

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
    private VBox vbRepository, vbFollowers, vbFollowing;

    @FXML
    private Label lblRepoName, lblPrivate, lblRepoLanguage;

    @FXML
    private Label lblRepoDesc, lblRepoCreation, lblBranches;

    @FXML
    private JFXButton btnDeleteRepo, btnBrowser;

    @FXML
    private TextField searchField;

    private FollowsController followersController;

    private FollowsController followingController;

    private GHRepository cachedRepository;

    private static GHRepository selectedRepository;

    private Loader loader;

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
            GHRepository selected = cachedRepository;
            if (selected != null) try {
                selected.delete();
                clearDetails();
                User.removeRepository();
                loadRepositories(null);
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
                        .replace("repository", cachedRepository.getName()));
    }

    @FXML
    private void reloadRepositories(KeyEvent event) {
        String search = searchField.getText().trim().toLowerCase();
        try {
            loadRepositories(search);
        } catch (IOException ex) {
            ExceptionHandler.showException("Could not load repositories.", ex);
        }
    }

    @FXML
    private void createNewRepository(MouseEvent event) {
        //TODO
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loader = new Loader();
        followersController = new FollowsController();
        followingController = new FollowsController();
        clearDetails();
        try {
            loadUserInfo();
            loadRepositories( null);
            followersController.loadFollows(vbFollowers, User.getFollowers());
            followingController.loadFollows(vbFollowing, User.getFollowing());
        } catch (IOException ex) {
            ExceptionHandler.showException("Could not load user info.", ex);
        }
        initHandler();
    }

    private void loadUserInfo() throws IOException {
        String name = User.getName();
        lblName.setText(name);
        lblUsername.setText("@" + User.getLogin());
        imAvatar.setImage(User.getAvatar());
        avatarPane.setMinWidth(User.getAvatar().getRequestedWidth());
        avatarPane.setMaxWidth(User.getAvatar().getRequestedWidth());
    }

    public static void setSelectedRepository(GHRepository selectedRepository) {
        HomeController.selectedRepository = selectedRepository;
    }

    private void initHandler() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), (event) -> {
            GHRepository selected = selectedRepository;
            if (selected != null) try {
                cachedRepository = selectedRepository;
                btnDeleteRepo.setDisable(false);
                btnBrowser.setDisable(false);
                lblRepoName.setText(selected.getName());
                lblRepoLanguage.setText(selected.getLanguage());
                lblBranches.setText(String.valueOf(selected.getBranches().size()));
                String desc = selected.getDescription();
                lblRepoDesc.setText("No description is available.");
                if (desc != null && !desc.isEmpty()) {
                    lblRepoDesc.setText(desc);
                }
                lblPrivate.setText(selected.isPrivate() ? "Private" : "Public");
                String createdAt = selected.getCreatedAt().toString();
                lblRepoCreation.setText(createdAt.substring(0, 11) + createdAt.substring(24, 28));
            } catch (IOException e) {
                ExceptionHandler.showException("Error: loading repository details.", e);
            } finally {
                selectedRepository = null;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadRepositories(String filter) throws IOException {
        vbRepository.getChildren().clear();
        for (String key : User.getRepositories().keySet()) {
            GHRepository repo = User.getRepositories().get(key);
            if (repo != null) {
                if (filter != null) {
                    String name = repo.getName().toLowerCase();
                    if (!name.contains(filter)) {
                        continue;
                    }
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource(AppProperties.FXML_REPO_ITEM));
                RepositoryController controller = new RepositoryController();
                loader.setController(controller);
                vbRepository.getChildren().add(loader.load());
                controller.setRepository(repo);
            }
        }
    }

    private void clearDetails() {
        lblRepoName.setText("");
        lblRepoDesc.setText("");
        lblRepoCreation.setText("");
        lblRepoLanguage.setText("");
        lblBranches.setText("");
        btnDeleteRepo.setDisable(true);
        btnBrowser.setDisable(true);
        selectedRepository = null;
        cachedRepository = null;
    }

}
