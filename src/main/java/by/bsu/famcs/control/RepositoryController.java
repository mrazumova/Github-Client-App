package by.bsu.famcs.control;

import by.bsu.famcs.service.ExceptionHandler;
import by.bsu.famcs.utils.AppProperties;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RepositoryController implements Initializable {

    @FXML
    private Label lblRepoName, lblRepoDesc, lblFork;

    @FXML
    private OctIconView icon;

    private GHRepository repository;

    private Map<String, GHRepository> repositoryMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            repositoryMap = new HashMap<>(LoginController.getGithubUser().getRepositories());
        } catch (IOException e) {
            ExceptionHandler.showException("Could not load repositories.", e);
        }
    }

    public void setRepository(GHRepository repo) {
        repository = repo;
        if (repo.isFork()) {
            icon.setIcon(OctIcon.REPO_FORKED);
            icon.setFill(Paint.valueOf("#6A737D"));
            lblFork.setText("Forked");
        } else if (repo.isPrivate()) {
            icon.setIcon(OctIcon.LOCK);
            icon.setFill(Paint.valueOf("#DBAB09"));
            lblFork.setText("Private");
        } else {
            icon.setIcon(OctIcon.REPO);
            icon.setFill(Paint.valueOf("#6A737D"));
            lblFork.setText("Public");
        }

        lblRepoName.setText(repo.getName());
        lblRepoDesc.setText("There are no description.");
        String desc = repo.getDescription();
        if (desc != null && !desc.isEmpty()) {
            lblRepoDesc.setText(desc);
        }
    }

    public Map<String, GHRepository> getRepositoryMap() {
        return repositoryMap;
    }

    public void setRepositoryMap(Map<String, GHRepository> repositoryMap) {
        this.repositoryMap = repositoryMap;
    }

    public void loadRepositories(VBox vBox, String filter) throws IOException {
        vBox.getChildren().clear();
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
                vBox.getChildren().add(loader.load());
                controller.setRepository(repo);
            }
        }
    }

    @FXML
    public void setSelectedRepository(ActionEvent event) {
        HomeController.setSelectedRepository(repository);
    }
}
