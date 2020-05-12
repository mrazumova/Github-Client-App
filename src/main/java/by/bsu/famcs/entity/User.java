package by.bsu.famcs.entity;

import by.bsu.famcs.service.ExceptionHandler;
import by.bsu.famcs.service.Notification;
import by.bsu.famcs.utils.AppProperties;
import javafx.scene.image.Image;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.*;

import java.util.HashMap;
import java.util.Map;

public class User {

    private static String name;

    private static String login;

    private static Image avatar;

    private static boolean isDemoUser;

    private static UsernamePasswordCredentialsProvider credentialsProvider;

    private static GHPersonSet<GHUser> followers;

    private static GHPersonSet<GHUser> following;

    private static Map<String, GHRepository> repositoryMap;

    private static GHRepository selectedRepository;

    public static void login(String username, String password) {
        try {
            GitHub github = new GitHubBuilder().withPassword(username, password).build();

            User.setUser(github.getMyself());
            setCredentialsProvider(username, password);
        } catch (Exception e) {
            new Notification("Error!", "Invalid username or password.");
        }
    }

    public static void setUser(GHUser ghUser) {
        try {
            if (ghUser != null) {
                setName(ghUser.getName() != null && !ghUser.getName().isEmpty() ? ghUser.getName() : ghUser.getLogin());
                setLogin(ghUser.getLogin());
                setIsDemoUser(false);
                setRepositories(ghUser.getRepositories());
                setFollowers(ghUser.getFollowers());
                setFollowing(ghUser.getFollows());
                setAvatar(new Image(ghUser.getAvatarUrl()));
            }
        } catch (Exception e) {
            ExceptionHandler.showException("Could not load user info.", e);
        }
    }

    public static void setDemoUser() {
        setIsDemoUser(true);
        setName("Demo user");
        setLogin("demouser");
        setRepositories(new HashMap<>());
        setFollowers(new GHPersonSet<>());
        setFollowing(new GHPersonSet<>());
        setAvatar(new Image(AppProperties.FXML_DEMO_PIC));
        setCredentialsProvider("", "");
    }

    public static void removeRepository(GHRepository selected) {
        repositoryMap.remove(selected);
        setRepositories(repositoryMap);
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        User.login = login;
    }

    public static Image getAvatar() {
        return avatar;
    }

    public static void setAvatar(Image avatar) {
        User.avatar = avatar;
    }

    public static boolean isDemoUser() {
        return isDemoUser;
    }

    public static void setIsDemoUser(boolean isDemoUser) {
        User.isDemoUser = isDemoUser;
    }

    public static UsernamePasswordCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public static void setCredentialsProvider(String usr, String psw) {
        credentialsProvider = new UsernamePasswordCredentialsProvider(usr, psw);
    }

    public static GHPersonSet<GHUser> getFollowers() {
        return followers;
    }

    public static void setFollowers(GHPersonSet<GHUser> followers) {
        User.followers = followers;
    }

    public static GHPersonSet<GHUser> getFollowing() {
        return following;
    }

    public static void setFollowing(GHPersonSet<GHUser> following) {
        User.following = following;
    }

    public static Map<String, GHRepository> getRepositories() {
        return repositoryMap;
    }

    public static void setRepositories(Map<String, GHRepository> repositoryMap) {
        User.repositoryMap = repositoryMap;
    }

    public static GHRepository getSelectedRepository() {
        return selectedRepository;
    }

    public static void setSelectedRepository(GHRepository selectedRepository) {
        User.selectedRepository = selectedRepository;
    }
}
