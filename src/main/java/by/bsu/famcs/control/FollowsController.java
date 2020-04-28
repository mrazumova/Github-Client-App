package by.bsu.famcs.control;

import by.bsu.famcs.entity.User;
import by.bsu.famcs.utils.AppProperties;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHUser;

import java.io.IOException;

public class FollowsController {

    @FXML
    private ImageView imAvatar;

    @FXML
    private Label lblName, lblUsername;

    @FXML
    private Button btnFollow;

    public void loadFollows(VBox vBox, GHPersonSet<GHUser> follows) throws IOException {
        vBox.getChildren().clear();
        for (GHUser following : follows){
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppProperties.FXML_FOLLOWS_ITEM));
            loader.setController(this);
            vBox.getChildren().add(loader.load());
            setFollows(following);
        }
    }

    private void setFollows(GHUser follower) throws IOException {
        String name = (follower.getName() != null && !follower.getName().isEmpty() ? follower.getName() : follower.getLogin());
        lblName.setText(name);
        lblUsername.setText("@" + follower.getLogin());
        Image image = new Image(follower.getAvatarUrl());
        imAvatar.setImage(image);
        btnFollow.setText(User.getFollowers().contains(follower) ? "Unfollow" : "Follow");
    }

    @FXML
    private void followUser(MouseEvent event) {
        //TODO
    }
}
