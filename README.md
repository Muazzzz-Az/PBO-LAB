# 🌊 MoodFlow - Productivity meets Mood Tracking

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Framework](https://img.shields.io/badge/Framework-Spring%20Boot%203.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![GUI](https://img.shields.io/badge/GUI-JavaFX%2021-blue.svg)](https://openjfx.io/)
[![Database](https://img.shields.io/badge/Database-H2%20(In--Memory)-lightgrey.svg)](https://www.h2database.com/)

**MoodFlow** adalah aplikasi manajemen produktivitas berbasis desktop yang mengintegrasikan pelacakan tugas (*Task Management*) dengan pencatatan suasana hati (*Mood Tracking*). Aplikasi ini dirancang khusus untuk memenuhi tugas **Ujian Akhir Semester (UAS) Praktikum Pemrograman Berorientasi Objek (PBO)** dengan tema **Produktivitas**.

Aplikasi ini dibangun menggunakan **JavaFX** untuk antarmuka pengguna, **Spring Boot** sebagai core backend-nya, dan **H2 Database** sebagai media penyimpanan data *in-memory* yang efisien untuk keperluan demonstrasi.

---

## 👥 Anggota Kelompok (Katanya Makan Ayam)

| No | Nama | NIM |
|:---:|:---|:---:|
| 1 | Nailah Salmah | 241401002 | 
| 2 | Muhammad Aryadefa | 241401050 | 
| 3 | Muaz Al-Fattah | 241401053 | 
| 4 | Nabila Syahri | 241401056 |

---

## 🚀 Fitur Utama Aplikasi

1. **Authentication System (Secure Login & Register)**
   * Sistem pendaftaran akun baru dengan validasi panjang karakter input (4-20 karakter) sesuai ketentuan bisnis.
   * Autentikasi pengguna secara *real-time* yang terhubung langsung ke database H2.
2. **Interactive Dashboard Layout**
   * Antarmuka modern yang menampilkan statistik ringkas secara visual: *Total Tasks*, *Completed Tasks*, dan *Total Log Mood*.
   * Menyambut pengguna secara dinamis melalui data session login.
3. **Smart Task Management (My Tasks)**
   * Membuat, menandai selesai, dan menghapus daftar tugas kuliah atau harian.
   * **Validasi Tanggal Jauh-Hari:** Memiliki sistem proteksi kalender (`DatePicker`) yang menolak input tenggat waktu (*due date*) di masa lalu.
4. **Emotional Mood Tracker**
   * Mencatat kondisi emosional harian pengguna untuk melacak keseimbangan antara beban kerja dan kesehatan mental.
5. **Visual Analytics Report**
   * Menyediakan laporan visual berbasis grafik lingkaran (`PieChart`) yang bersumber dari akumulasi log data produktivitas dan suasana hati pengguna.

---

## 🏛️ Penerapan 4 Pilar PBO (Object-Oriented Programming)

Proyek ini sepenuhnya menerapkan prinsip dasar PBO demi menciptakan kode yang bersih, modular, dan mudah dikembangkan:
* **Encapsulation:** Mengamankan data atribut objek di kelas Model (seperti `User.java`) dengan *access modifier* `private`, yang diakses secara aman melalui metode Getter dan Setter (via Lombok `@Data`).
* **Inheritance:** Memanfaatkan pewarisan sifat bertingkat di mana seluruh *Interface Repository* kelompok kami (seperti `UserRepository`) memperluas (`extends`) sifat dan fungsionalitas dari `JpaRepository` bawaan Spring Data.
* **Abstraction:** Menyembunyikan kompleksitas detail query SQL manual dengan memanfaatkan abstraksi interface repositori dan layer *Service*.
* **Polymorphism:** Menerapkan struktur penanganan aksi yang dinamis menggunakan *method overriding* dan *lambda expressions* pada *event handling* komponen JavaFX.

---

## 🛠️ Langkah Instalasi & Cara Menjalankan Aplikasi

### Prasyarat (Dependencies & Environment)
* **Java Development Kit (JDK):** Versi 21 (Direkomendasikan Alibaba Dragonwell 21 / OpenJDK 21)
* **Build Tool:** Maven 3.x (Terintegrasi lewat `pom.xml`)
* **IDE:** IntelliJ IDEA (Sangat direkomendasikan)

### Langkah-Langkah Menjalankan Program:
1. **Clone Repository Proyek**
    [https://github.com/Muazzzz-Az/UAS-PBO_MoodFlow_Katanya-Makan-Ayam.git](https://github.com/Muazzzz-Az/UAS-PBO_MoodFlow_Katanya-Makan-Ayam.git)
2. **Buka Proyek di IntelliJ IDEA**
   * Pilih menu **File -> Open**, lalu arahkan ke folder hasil clone dan pilih file `pom.xml` sebagai proyek Maven.
   * Tunggu hingga proses unduhan seluruh dependensi selesai (*Indexing*).
3. **Konfigurasi Project SDK & Language Level**
   * Tekan kombinasi tombol `Ctrl + Alt + Shift + S` (Project Structure).
   * Pastikan bagian **Project SDK** dan **Language Level** disetel ke **Java 21**.
4. **Jalankan Aplikasi Utama (Main)**
   * Cari file `MainLauncher.java` di dalam direktori folder `src/main/java/com/kelompok/moodflow/`.
   * Klik kanan pada file tersebut, lalu pilih opsi **Run 'MainLauncher.main()'**.
5. **Mengakses Database (H2 Console)**
   Saat aplikasi sedang berjalan aktif, Anda dapat memantau isi data tabel database secara langsung melalui web browser dengan mengakses detail berikut:
     * **URL Akses:** `http://localhost:8080/h2-console`
     * **JDBC URL:** `jdbc:h2:mem:moodflowdb`
     * **Username:** `sa` *(Password silakan dikosongkan)*

---

## 📺 Link Video Presentasi (YouTube)

Tonton demonstrasi lengkap aplikasi, penjelasan alur program (*workflow*), serta pembedahan kode PBO kelompok kami pada tautan berikut:
👉 **[]**
