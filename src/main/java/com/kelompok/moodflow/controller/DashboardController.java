package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.model.Task;
import com.kelompok.moodflow.repository.MoodRepository;
import com.kelompok.moodflow.repository.TaskRepository;
import com.kelompok.moodflow.repository.UserRepository;
import com.kelompok.moodflow.controller.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class DashboardController {

    @FXML private Label userLabel;
    @FXML private Button logoutButton;
    @FXML private Button dashboardBtn;
    @FXML private Button tasksBtn;
    @FXML private Button moodBtn;
    @FXML private Button reportBtn;
    @FXML private StackPane contentArea;

    @FXML private VBox totalTasksCard;
    @FXML private VBox completedTasksCard;
    @FXML private VBox totalMoodsCard;

    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label avgMoodLabel;

    private Node homeView;
    private final ConfigurableApplicationContext springContext;
    private final TaskRepository taskRepository;
    private final MoodRepository moodRepository;
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    @Autowired
    public DashboardController(ConfigurableApplicationContext springContext,
                               TaskRepository taskRepository,
                               MoodRepository moodRepository,
                               UserRepository userRepository,
                               SessionManager sessionManager) {
        this.springContext = springContext;
        this.taskRepository = taskRepository;
        this.moodRepository = moodRepository;
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        Scene scene = dashboardBtn.getScene();
        if (scene != null) {
            String cssPath = getClass().getResource("/css/style.css").toExternalForm();
            if (cssPath != null && !scene.getStylesheets().contains(cssPath)) {
                scene.getStylesheets().add(cssPath);
                System.out.println("CSS loaded: " + cssPath);
            }
        }

        String username = sessionManager.getCurrentUser() != null ?
                sessionManager.getCurrentUser().getUsername() : "User";
        userLabel.setText("✨ " + username + " ✨");
        userLabel.setStyle("-fx-text-fill: white;");

        logoutButton.setOnAction(e -> handleLogout());

        if (!contentArea.getChildren().isEmpty()) {
            homeView = contentArea.getChildren().get(0);
        }

        dashboardBtn.setOnAction(e -> { showHome(); setActiveButton(dashboardBtn); });
        tasksBtn.setOnAction(e -> { loadPage("/fxml/task.fxml"); setActiveButton(tasksBtn); });
        moodBtn.setOnAction(e -> { loadPage("/fxml/mood.fxml"); setActiveButton(moodBtn); });
        reportBtn.setOnAction(e -> { loadPage("/fxml/report.fxml"); setActiveButton(reportBtn); });

        if (totalTasksCard != null) totalTasksCard.setOnMouseClicked(this::navigateToTasks);
        if (completedTasksCard != null) completedTasksCard.setOnMouseClicked(this::navigateToCompletedTasks);
        if (totalMoodsCard != null) totalMoodsCard.setOnMouseClicked(this::navigateToMood);

        setActiveButton(dashboardBtn);
        refreshDashboardStats();
    }

    private void navigateToTasks(MouseEvent event) {
        loadPage("/fxml/task.fxml");
        setActiveButton(tasksBtn);
    }

    private void navigateToCompletedTasks(MouseEvent event) {
        loadPage("/fxml/task.fxml");
        setActiveButton(tasksBtn);
    }

    private void navigateToMood(MouseEvent event) {
        loadPage("/fxml/mood.fxml");
        setActiveButton(moodBtn);
    }

    private void setActiveButton(Button activeBtn) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: black; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20; -fx-background-radius: 12;";
        String activeStyle = "-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20; -fx-background-radius: 12;";

        dashboardBtn.setStyle(defaultStyle);
        tasksBtn.setStyle(defaultStyle);
        moodBtn.setStyle(defaultStyle);
        reportBtn.setStyle(defaultStyle);

        activeBtn.setStyle(activeStyle);
    }

    private void showHome() {
        contentArea.getChildren().clear();
        if (homeView != null) {
            contentArea.getChildren().add(homeView);
            refreshDashboardStats();
        }
    }

    private void refreshDashboardStats() {
        Long userId = sessionManager.getCurrentUser() != null ?
                sessionManager.getCurrentUser().getId() : null;

        List<Task> tasks;
        if (userId != null) {
            tasks = taskRepository.findByUserId(userId);
        } else {
            tasks = taskRepository.findAll();
        }

        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(Task::isCompleted).count();
        long totalMoods = moodRepository.count();

        totalTasksLabel.setText(String.valueOf(totalTasks));
        completedTasksLabel.setText(String.valueOf(completedTasks));
        avgMoodLabel.setText(String.valueOf(totalMoods));
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat halaman: " + e.getMessage());
        }
    }

    private void handleLogout() {
        sessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("MoodFlow - Login");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}