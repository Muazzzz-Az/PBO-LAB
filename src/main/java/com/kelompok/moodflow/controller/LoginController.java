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

    @FXML private TextField emailField; 
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
        loginButton.setOnAction(e -> handleLogin());
        registerLink.setOnAction(event -> openRegisterPage());
    }

    private void handleLogin() {
        String username = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Pastikan semua kolom telah diisi!");
            return;
        }

        if (userService.authenticate(username, password)) {
            // FUNGSI DIKEMBALIKAN: Pop-up notifikasi sukses
            showAlert(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang kembali!");
            openDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau Password salah.");
        }
    }

    private void openRegisterPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MoodFlow - Register");
        } catch (Exception ex) {
            ex.printStackTrace();
            // FUNGSI DIKEMBALIKAN: Detail spesifik error message
            showAlert(Alert.AlertType.ERROR, "Error System", "Gagal membuka halaman register: " + ex.getMessage());
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MoodFlow - Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error System", "Gagal memuat halaman dashboard.");
        }
    }

    // CLEAN CODE: Modularisasi method alert agar tidak duplikat
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}