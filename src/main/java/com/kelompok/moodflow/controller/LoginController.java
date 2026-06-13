package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    @FXML private TextField emailField; // Menggunakan emailField sesuai id di FXML kamu
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    private final UserService userService;
    private final ConfigurableApplicationContext springContext;

    @Autowired
    public LoginController(UserService userService, ConfigurableApplicationContext springContext) {
        this.userService = userService;
        this.springContext = springContext;
    }

    @FXML
    public void initialize() {
        // Memastikan tombol login merespons saat diklik
        loginButton.setOnAction(e -> handleLogin());

        registerLink.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
                loader.setControllerFactory(springContext::getBean);
                Parent root = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("MoodFlow - Register");
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorAlert("Error System", "Gagal membuka halaman register: " + ex.getMessage());
            }
        });
    }

    private void handleLogin() {
        // Mengambil input dari pengguna
        String username = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorAlert("Validasi Gagal", "Pastikan semua kolom telah diisi!");
            return;
        }

        // Mengecek ke database melalui UserService
        if (userService.authenticate(username, password)) {
            showInfoAlert("Login Berhasil", "Selamat datang kembali!");
            openDashboard();
        } else {
            showErrorAlert("Login Gagal", "Username atau Password salah.");
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            // Penting: Biarkan Spring yang membuat controller untuk Dashboard
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MoodFlow - Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error System", "Gagal memuat halaman dashboard.");
        }
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}