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

    // Mengambil alih 100% logika validasi dari Controller
    public void registerUser(String username, String password, String fullName) {
        // 1. Validasi Input Kosong
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Semua kolom harus diisi!");
        }

        // 2. Validasi Panjang Username
        if (username.length() < 4 || username.length() > 20) {
            throw new IllegalArgumentException("Username harus antara 4-20 karakter.");
        }

        // 3. Cek Duplikasi (Menggunakan IllegalStateException untuk membedakan tipe error di UI)
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username sudah terdaftar! Gunakan nama lain.");
        }

        // 4. Enkripsi Password & Simpan
        String hashedPassword = SecurityUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword, fullName);
        userRepository.save(newUser);
    }

    // Autentikasi Login
    public boolean authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return SecurityUtil.verifyPassword(password, userOptional.get().getPassword());
        }
        return false;
    }
}