package com.kelompok.moodflow.service;

import com.kelompok.moodflow.model.User;
import com.kelompok.moodflow.repository.UserRepository;
import com.kelompok.moodflow.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(username).orElse(null);
    }


    public User findById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }


    public void registerUser(String username, String password, String fullName) {
        // Validasi Input Kosong
        validateNotEmpty(fullName, "Nama lengkap");
        validateNotEmpty(username, "Username");
        validateNotEmpty(password, "Password");

        // Validasi Panjang Username
        if (username.length() < 4) {
            throw new IllegalArgumentException("Username minimal 4 karakter!");
        }
        if (username.length() > 20) {
            throw new IllegalArgumentException("Username maksimal 20 karakter!");
        }

        // Validasi Panjang Password (tambahan security)
        if (password.length() < 4) {
            throw new IllegalArgumentException("Password minimal 4 karakter untuk keamanan!");
        }

        // Validasi Username hanya mengandung huruf, angka, dan underscore
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username hanya boleh berisi huruf, angka, dan underscore!");
        }

        // Cek Duplikasi Username
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username '" + username + "' sudah terdaftar! Silakan gunakan username lain.");
        }

        // Enkripsi Password & Simpan
        String hashedPassword = SecurityUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword, fullName);

        User savedUser = userRepository.save(newUser);
        System.out.println("User berhasil terdaftar: " + savedUser.getUsername() + " (ID: " + savedUser.getId() + ")");
    }

    public boolean authenticate(String username, String password) {
        // Validasi input kosong
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Login gagal: Username kosong");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Login gagal: Password kosong");
            return false;
        }

        Optional<User> userOptional = userRepository.findByUsername(username.trim());

        if (userOptional.isEmpty()) {
            System.out.println("Login gagal: Username '" + username + "' tidak ditemukan");
            return false;
        }

        User user = userOptional.get();
        boolean isAuthenticated = SecurityUtil.verifyPassword(password, user.getPassword());

        if (isAuthenticated) {
            System.out.println("Login berhasil: " + user.getUsername() + " (" + user.getFullName() + ")");
        } else {
            System.out.println("Login gagal: Password salah");
        }

        return isAuthenticated;
    }

    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByUsername(username.trim()).isPresent();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public User updateProfile(Long userId, String newFullName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));

        if (newFullName != null && !newFullName.trim().isEmpty()) {
            user.setFullName(newFullName.trim());
        }

        return userRepository.save(user);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Verifikasi password lama
        if (!SecurityUtil.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }

        // Validasi password baru
        if (newPassword == null || newPassword.length() < 4) {
            throw new IllegalArgumentException("Password baru minimal 4 karakter!");
        }

        // Update password baru
        String newHashedPassword = SecurityUtil.hashPassword(newPassword);
        user.setPassword(newHashedPassword);
        userRepository.save(user);

        System.out.println("Password berhasil diubah untuk user: " + user.getUsername());
        return true;
    }


    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            System.out.println("Gagal hapus user: ID " + userId + " tidak ditemukan");
            return false;
        }

        userRepository.deleteById(userId);
        System.out.println("User dengan ID " + userId + " berhasil dihapus");
        return true;
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " tidak boleh kosong!");
        }
    }

    public String getUserInfo(Long userId) {
        User user = findById(userId);
        if (user == null) {
            return "User tidak ditemukan";
        }
        // PILAR PBO: POLYMORPHISM - Memanggil method override dari BaseEntity
        return user.getEntitySummary();
    }
}