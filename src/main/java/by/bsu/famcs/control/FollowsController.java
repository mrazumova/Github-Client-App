package by.bsu.famcs.control;

import by.bsu.famcs.entity.User;
import by.bsu.famcs.service.ExceptionHandler;
import by.bsu.famcs.utils.AppProperties;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private GHUser follow;

    public void loadFollows(VBox vBox, GHPersonSet<GHUser> follows) {
        try {
            vBox.getChildren().clear();
            for (GHUser following : follows) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(AppProperties.FXML_FOLLOWS_ITEM));
                FollowsController controller = new FollowsController();
                loader.setController(controller);
                vBox.getChildren().add(loader.load());
                controller.setFollows(following);
            }
        } catch (Exception e) {
            ExceptionHandler.showException("Could not load followers & following info.", e);
        }
    }

    public void loadDemo(VBox vBox) {
        try {
            vBox.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppProperties.FXML_FOLLOWS_ITEM));
            FollowsController controller = new FollowsController();
            loader.setController(controller);
            vBox.getChildren().add(loader.load());
            controller.lblName.setText("Demo follow");
            controller.lblUsername.setText("@demo_follow");
            controller.imAvatar.setImage(new Image(AppProperties.FXML_DEMO_PIC));
            controller.btnFollow.setText("Unfollow");
        } catch (Exception e) {
            ExceptionHandler.showException("Could not load followers & following info for demo user.", e);
        }
    }

    private void setFollows(GHUser follower) throws IOException {
        follow = follower;
        String name = (follower.getName() != null && !follower.getName().isEmpty() ? follower.getName() : follower.getLogin());
        lblName.setText(name);
        lblUsername.setText("@" + follower.getLogin());
        Image image = new Image(follower.getAvatarUrl());
        imAvatar.setImage(image);
        btnFollow.setText(User.getFollowing().contains(follower) ? "Unfollow" : "Follow");
    }
}
