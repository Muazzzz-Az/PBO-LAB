package com.kelompok.moodflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // Wajib ditambahkan agar Lombok mengenali properti dari BaseEntity
public class User extends BaseEntity { // PILAR PBO 1: INHERITANCE (Pewarisan)

    // Atribut 'id' dihapus karena sudah diwarisi (Inheritance) secara otomatis dari BaseEntity

    // PILAR PBO 2: ENCAPSULATION (Atribut private, validasi ketat)
    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 4, max = 20, message = "Username harus antara 4-20 karakter")
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    private String password; 

    private String fullName;

    public User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    // PILAR PBO 3 & 4: ABSTRACTION & POLYMORPHISM (Method Overriding)
    @Override
    public String getEntitySummary() {
        return "User Profile: " + this.fullName + " (@" + this.username + ")";
    }
}