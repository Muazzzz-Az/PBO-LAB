package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.model.Task;
import com.kelompok.moodflow.repository.MoodRepository;
import com.kelompok.moodflow.repository.TaskRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
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

    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label avgMoodLabel; // Kita gunakan untuk total log mood

    private Node homeView;
    private final ConfigurableApplicationContext springContext;
    private final TaskRepository taskRepository;
    private final MoodRepository moodRepository;

    @Autowired
    public DashboardController(ConfigurableApplicationContext springContext, TaskRepository taskRepository, MoodRepository moodRepository) {
        this.springContext = springContext;
        this.taskRepository = taskRepository;
        this.moodRepository = moodRepository;
    }

    @FXML
    public void initialize() {
        userLabel.setText("Hello, admin!");
        logoutButton.setOnAction(e -> handleLogout());

        if (!contentArea.getChildren().isEmpty()) {
            homeView = contentArea.getChildren().get(0);
        }

        // Atur event klik tombol beserta indikator warnanya
        dashboardBtn.setOnAction(e -> { showHome(); setActiveButton(dashboardBtn); });
        tasksBtn.setOnAction(e -> { loadPage("/fxml/task.fxml"); setActiveButton(tasksBtn); });
        moodBtn.setOnAction(e -> { loadPage("/fxml/mood.fxml"); setActiveButton(moodBtn); });
        reportBtn.setOnAction(e -> { showReportFeature(); setActiveButton(reportBtn); });

        // Set tombol dashboard sebagai aktif pertama kali & hitung angka awal
        setActiveButton(dashboardBtn);
        refreshDashboardStats();
    }

    // --- FUNGSI BARU: MENGUBAH WARNA TOMBOL AKTIF ---
    private void setActiveButton(Button activeBtn) {
        // 1. Kembalikan semua tombol ke gaya default (transparan)
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #333; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20; -fx-cursor: hand;";
        dashboardBtn.setStyle(defaultStyle);
        tasksBtn.setStyle(defaultStyle);
        moodBtn.setStyle(defaultStyle);
        reportBtn.setStyle(defaultStyle);

        // 2. Beri warna ungu pada tombol yang baru saja diklik
        activeBtn.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }
    // --------------------------------------------------

    private void showHome() {
        contentArea.getChildren().clear();
        if (homeView != null) {
            contentArea.getChildren().add(homeView);
            // Paksa hitung ulang angka statistik SETIAP KALI kembali ke dashboard
            refreshDashboardStats();
        }
    }

    private void refreshDashboardStats() {
        List<Task> tasks = taskRepository.findAll();
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
            showAlert(Alert.AlertType.ERROR, "System Error", "Gagal memuat halaman: " + e.getMessage());
        }
    }

    private void showReportFeature() {
        long t = taskRepository.count();
        long c = taskRepository.findAll().stream().filter(Task::isCompleted).count();
        long m = moodRepository.count();

        String report = "Laporan Produktivitas Kamu:\n\n" +
                "✅ Total Tugas Dibuat: " + t + "\n" +
                "🏆 Tugas Selesai: " + c + "\n" +
                "😊 Total Log Mood: " + m + "\n\n" +
                "Teruskan kerja bagusmu untuk menjaga keseimbangan produktivitas dan mood!";

        showAlert(Alert.AlertType.INFORMATION, "📈 Productivity Report", report);
        loadPage("/fxml/report.fxml");
    }

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
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