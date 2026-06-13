package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerButton;
    @FXML private Hyperlink loginLink;

    private final UserService userService;
    private final ConfigurableApplicationContext springContext;

    @Autowired
    public RegisterController(UserService userService, ConfigurableApplicationContext springContext) {
        this.userService = userService;
        this.springContext = springContext;
    }

    @FXML
    public void initialize() {
        registerButton.setOnAction(e -> handleRegister());
        loginLink.setOnAction(e -> openLoginPage());
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        try {
            // Controller murni bertugas memanggil Service
            userService.registerUser(username, password, fullName);
            
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Sukses", "Akun berhasil dibuat! Silakan login.");
            openLoginPage();
            
        } catch (IllegalArgumentException e) {
            // Menangkap error validasi (Kolom kosong / panjang karakter)
            showAlert(Alert.AlertType.ERROR, "Validasi Gagal", e.getMessage());
        } catch (IllegalStateException e) {
            // Menangkap error duplikasi (Username sudah terpakai) persis seperti kodemu
            showAlert(Alert.AlertType.WARNING, "Registrasi Gagal", e.getMessage());
        }
    }

    private void openLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MoodFlow - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error System", "Gagal memuat halaman login.");
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