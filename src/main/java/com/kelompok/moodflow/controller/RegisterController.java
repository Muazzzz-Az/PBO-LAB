package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.model.User;
import com.kelompok.moodflow.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final ConfigurableApplicationContext springContext;

    @Autowired
    public RegisterController(UserRepository userRepository, ConfigurableApplicationContext springContext) {
        this.userRepository = userRepository;
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

        // 1. Validasi Input Kosong
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Semua kolom harus diisi!");
            return;
        }

        // 2. Validasi Panjang Username (Sesuai anotasi @Size di model User temanmu)
        if (username.length() < 4 || username.length() > 20) {
            showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Username harus antara 4-20 karakter.");
            return;
        }

        // 3. Cek Apakah Username Sudah Terpakai
        if (userRepository.findByUsername(username).isPresent()) {
            showAlert(Alert.AlertType.WARNING, "Registrasi Gagal", "Username sudah terdaftar! Gunakan nama lain.");
            return;
        }

        // 4. Simpan User Baru ke Database
        User newUser = new User(username, password, fullName);
        userRepository.save(newUser);

        showAlert(Alert.AlertType.INFORMATION, "Registrasi Sukses", "Akun berhasil dibuat! Silakan login.");
        openLoginPage();
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