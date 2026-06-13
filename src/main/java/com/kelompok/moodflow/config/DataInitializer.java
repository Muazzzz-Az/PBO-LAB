package com.kelompok.moodflow.config;

import com.kelompok.moodflow.model.User;
import com.kelompok.moodflow.repository.UserRepository;
import com.kelompok.moodflow.util.SecurityUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            // Mengecek apakah user admin sudah ada, jika belum buat baru dengan password terenkripsi
            if (userRepository.findByUsername("admin").isEmpty()) {
                String hashedPassword = SecurityUtil.hashPassword("password123");
                userRepository.save(new User("admin", hashedPassword, "Administrator"));
                System.out.println("✅ Data user 'admin' berhasil dibuat dengan keamanan enkripsi.");
            }
        };
    }
}