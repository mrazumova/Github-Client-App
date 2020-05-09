package by.bsu.famcs.entity;

import by.bsu.famcs.service.ExceptionHandler;
import by.bsu.famcs.service.Notification;
import javafx.scene.image.Image;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.Map;

public class User {

    private static GHUser githubUser;

    private static UsernamePasswordCredentialsProvider credentialsProvider;

    private static String name;

    private static GHPersonSet<GHUser> followers;

    private static GHPersonSet<GHUser> following;

    private static Map<String, GHRepository> repositoryMap;

    private static GHRepository selectedRepository;

    public static void login(String username, String password) {
        try {
            GitHub github = new GitHubBuilder().withPassword(username, password).build();

            User.setUser(github.getMyself());
            credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
        } catch (Exception e) {
            new Notification("Error!", "Invalid username or password.");
        }
    }

    public static void setUser(GHUser ghUser) {
        githubUser = ghUser;
        if (githubUser != null) {
            setName();
            setRepositories();
            setFollowers();
            setFollowing();
        }
    }

    public static String getName() {
        return name;
    }

    public static UsernamePasswordCredentialsProvider getCredentials() {
        return credentialsProvider;
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

    public static Map<String, GHRepository> getRepositories() {
        return repositoryMap;
    }

    public static void removeRepository() {
        setRepositories();
    }

    public static void setSelectedRepository(GHRepository repository) {
        User.selectedRepository = repository;
    }

    public static GHRepository getSelectedRepository() {
        return selectedRepository;
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
