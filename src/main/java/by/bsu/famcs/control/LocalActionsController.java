package by.bsu.famcs.control;

import by.bsu.famcs.entity.User;
import by.bsu.famcs.service.ExceptionHandler;
import by.bsu.famcs.service.Loader;
import by.bsu.famcs.utils.AppProperties;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class LocalActionsController {

    @FXML
    private ImageView imInit, imCommit, imAdd, imPush, imLink;

    @FXML
    private Button btnInit, btnCommit, btnAdd, btnPush, btnLink;

    @FXML
    private Label lblInitAction, lblInitDescription,
            lblCommitAction, lblCommitDescription,
            lblAddAction, lblAddDescription,
            lblPushAction, lblPushDescription,
            lblLinkAction, lblLinkDescription;

    private Loader loader = new Loader();

    public void loadActions(VBox vBox) {
        try {
            vBox.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppProperties.FXML_LOCAL_ACTIONS));
            LocalActionsController controller = new LocalActionsController();
            loader.setController(controller);
            vBox.getChildren().add(loader.load());
            controller.loadActions();
        } catch (Exception e) {
            ExceptionHandler.showException("Could not load local actions.", e);
        }
    }

    public void linkLocalAndRemoteRepositories(MouseEvent event) {
        try{
            String repositoryName = getNewRepositoryName();
            Repository repository = getRepository(event);
            Git git = new Git(repository);

            StoredConfig config = git.getRepository().getConfig();
            config.setString("remote", "origin", "url",
                    AppProperties.GITHUB_OPEN_REPOSITORY
                            .replace("login", User.getLogin())
                            .replace("repository", repositoryName));
            config.save();
        } catch (Exception e){
            ExceptionHandler.showException("Cannot link to remote repo", e);
        }
    }

    @FXML
    private void initLocalRepository(MouseEvent event) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(loader.getStage(event));

            Git.init().setDirectory(selectedDirectory).call();
        } catch (Exception e) {
            ExceptionHandler.showException("Could not init local repository.", e);
        }
    }

    @FXML
    private void commit(MouseEvent event) {
        try {
            Repository repository = getRepository(event);
            String commitMessage = getCommitMessage();

            Git git = new Git(repository);
            CommitCommand commit = git.commit().setAll(true);

            commit.setMessage(commitMessage).call();
        } catch (Exception e) {
            ExceptionHandler.showException("Exception during commit action", e);
        }
    }

    @FXML
    private void pushToRemoteRepository(MouseEvent event) {
        try {
            Repository repository = getRepository(event);
            Git git = new Git(repository);

            PushCommand pushCommand = git.push();
            pushCommand.setCredentialsProvider(User.getCredentials());
            pushCommand.call();
        } catch (Exception e) {
            ExceptionHandler.showException("Exception during push to remote repo", e);
        }
    }

    @FXML
    private void addToGit(MouseEvent event) {
        try {
            Repository repository = getRepository(event);

            Git git = new Git(repository);
            AddCommand add = git.add();
            File fileToAdd = getFile(event);
            add.addFilepattern(fileToAdd.getAbsolutePath()).call();
        } catch (Exception e) {
            ExceptionHandler.showException("Exception occurred during add command", e);
        }
    }

    private String getCommitMessage() {
        TextInputDialog dialog = new TextInputDialog("commit message");

        dialog.setTitle("Set commit message");
        dialog.setHeaderText("Enter commit message:");
        dialog.setContentText("Message:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return "";
    }

    private String getNewRepositoryName() {
        TextInputDialog dialog = new TextInputDialog("");

        dialog.setTitle("Remote repository name");
        dialog.setHeaderText("Enter remote repository name:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return "";
    }

    private Repository getRepository(MouseEvent event) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository");
        File selectedDirectory = directoryChooser.showDialog(loader.getStage(event));
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(selectedDirectory);
        return repositoryBuilder.build();
    }

    private File getFile(MouseEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select file to add");
        return directoryChooser.showDialog(loader.getStage(event));
    }

    private void loadActions() {
        Image image = new Image(AppProperties.FXML_GIT_PIC);
        imInit.setImage(image);
        imCommit.setImage(image);
        imPush.setImage(image);
        imAdd.setImage(image);
        imLink.setImage(image);
    }
}
