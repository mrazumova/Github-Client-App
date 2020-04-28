package by.bsu.famcs.control;

import by.bsu.famcs.utils.AppProperties;
import by.bsu.famcs.service.ExceptionHandler;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController implements Initializable {

    private FollowsController followersController;
    private FollowsController followingController;

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

    private GHRepository cachedRepository;

    private static GHRepository selectedRepository;

    public Map<String, GHRepository> repositoryMap;

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
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            URL path = getClass().getResource(AppProperties.FXML_LOGIN);
            if (path != null) {
                Parent root = FXMLLoader.load(path);

                LoginController.setGithubUser(null);
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);

                stage.setTitle(AppProperties.APP_TITLE);
                stage.setScene(scene);
                stage.show();
            } else {
                throw new IOException("Error loading login page.");
            }
        } catch (IOException e) {
            ExceptionHandler.showException("There was an error trying to log out.", e);
        }
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
                repositoryMap = null;
                loadRepositories(null);
            } catch (IOException ex) {
                ExceptionHandler.showException("Error: couldn't delete this repository.", ex);
                Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void openRepo(MouseEvent event) {
        String uri = "https://github.com/" + LoginController.getGithubUser().getLogin() + "/" + this.cachedRepository.getName();
        try {
            Desktop.getDesktop().browse(URI.create(uri));
        } catch (IOException e) {
            ExceptionHandler.showException("Error: couldn't open selected repository.", e);
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    void reloadRepositories(KeyEvent event) {
        String search = searchField.getText().trim().toLowerCase();
        try {
            loadRepositories(search);
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void createNewRepository(MouseEvent event) {
        //TODO
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        followersController = new FollowsController();
        followingController = new FollowsController();
        clearDetails();
        try {
            loadUserInfo();
            loadRepositories( null);
            followersController.loadFollows(vbFollowers, LoginController.getFollowers());
            followingController.loadFollows(vbFollowing, LoginController.getFollowing());
        } catch (IOException ex) {
            ExceptionHandler.showException("Could not load user info.", ex);
        }
        initHandler();
    }

    private void loadUserInfo() throws IOException {
        GHPerson user = LoginController.getGithubUser();
        String name = (user.getName() != null && !user.getName().isEmpty() ? user.getName() : "No Name Available");
        lblName.setText(name);
        lblUsername.setText("@" + user.getLogin());
        Image image = new Image(user.getAvatarUrl());
        imAvatar.setImage(image);
        avatarPane.setMinWidth(image.getRequestedWidth());
        avatarPane.setMaxWidth(image.getRequestedWidth());
    }

    public static GHRepository getSelectedRepository() {
        return selectedRepository;
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
        if (repositoryMap == null) {
            repositoryMap = new HashMap<>(LoginController.getGithubUser().getRepositories());
        }
        vbRepository.getChildren().clear();
        for (String key : repositoryMap.keySet()) {
            GHRepository repo = repositoryMap.get(key);
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
