package by.bsu.famcs.entity;

import by.bsu.famcs.service.ExceptionHandler;
import javafx.scene.image.Image;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import java.io.IOException;
import java.util.Map;

public class User {

    private static GHUser githubUser;

    private static String name;

    private static GHPersonSet<GHUser> followers;

    private static GHPersonSet<GHUser> following;

    private static Map<String, GHRepository> repositoryMap;

    public static void setUser(GHUser ghUser) {
        githubUser = ghUser;
        setName();
        setRepositories();
        setFollowers();
        setFollowing();
    }

    public static String getName() {
        return name;
    }

    public static String getLogin() {
        return githubUser.getLogin();
    }

    public static Image getAvatar() {
        return new Image(githubUser.getAvatarUrl());
    }

    public static GHPersonSet<GHUser> getFollowers() {
        return followers;
    }

    public static GHPersonSet<GHUser> getFollowing() {
        return following;
    }

    public static Map<String, GHRepository> getRepositories(){
        return repositoryMap;
    }

    public static void removeRepository(){
        setRepositories();
    }

    private static void setName() {
        try {
            name = githubUser.getName() != null && !githubUser.getName().isEmpty() ? githubUser.getName() : githubUser.getLogin();
        } catch (IOException e) {
            ExceptionHandler.showException("Could not load user name.", e);
        }
    }

    private static void setFollowing() {
        try {
            following = githubUser.getFollows();
        } catch (IOException e) {
            ExceptionHandler.showException("Could not load following.", e);
        }
    }

    private static void setFollowers() {
        try {
            followers = githubUser.getFollowers();
        } catch (IOException e) {
            ExceptionHandler.showException("Could not load followers.", e);
        }
    }

    private static void setRepositories() {
        try {
            repositoryMap = githubUser.getRepositories();
        } catch (IOException e) {
            ExceptionHandler.showException("Could not load repositories.", e);
        }
    }
}
