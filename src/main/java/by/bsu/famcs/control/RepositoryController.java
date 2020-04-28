package by.bsu.famcs.control;

import by.bsu.famcs.entity.User;
import by.bsu.famcs.service.ExceptionHandler;
import by.bsu.famcs.utils.AppProperties;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.kohsuke.github.GHRepository;

import java.util.Map;

public class RepositoryController {

    @FXML
    private Label lblRepoName, lblRepoDesc, lblFork;

    @FXML
    private OctIconView icon;

    private GHRepository repository;

    @FXML
    public void setSelectedRepository(MouseEvent event) {
        User.setSelectedRepository(repository);
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

    public void loadRepositories(VBox vBox, String filter) {
        try {
            Map<String, GHRepository> repositoryMap = User.getRepositories();
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
                    RepositoryController repositoryController = new RepositoryController();
                    loader.setController(repositoryController);
                    vBox.getChildren().add(loader.load());
                    repositoryController.setRepository(repo);
                }
            }
        } catch (Exception e) {
            ExceptionHandler.showException("Could not load repositories.", e);
        }
    }
}
