package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.model.Task;
import com.kelompok.moodflow.repository.TaskRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {

    @FXML private Button addTaskBtn;
    @FXML private VBox taskForm;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private Button saveTaskBtn;
    @FXML private Button cancelTaskBtn;

    @FXML private Button markDoneBtn;
    @FXML private Button deleteTaskBtn;
    @FXML private ListView<Task> taskListView;

    private final TaskRepository taskRepository;
    private final ObservableList<Task> taskItems = FXCollections.observableArrayList();

    @Autowired
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @FXML
    public void initialize() {
        // Mengatur format tampilan list (Membedakan yang belum dan sudah selesai)
        taskListView.setCellFactory(param -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    String status = task.isCompleted() ? "✅ [Selesai] " : "⏳ [Belum] ";
                    String moodInfo = task.getCompletionMood() != null ? " | Mood: " + task.getCompletionMood() : "";
                    setText(status + task.getTitle() + " (Prioritas: " + task.getPriority() + ")" + moodInfo);
                }
            }
        });

        loadTasks();

        addTaskBtn.setOnAction(e -> {
            taskForm.setVisible(true);
            taskForm.setManaged(true);
        });

        cancelTaskBtn.setOnAction(e -> {
            taskForm.setVisible(false);
            taskForm.setManaged(false);
            clearForm();
        });

        saveTaskBtn.setOnAction(e -> saveTask());

        // Aksi Tombol Fitur Utama Kita
        markDoneBtn.setOnAction(e -> markTaskAsDone());
        deleteTaskBtn.setOnAction(e -> deleteTask());
    }

    private void loadTasks() {
        taskItems.clear();
        List<Task> tasks = taskRepository.findAll();
        taskItems.addAll(tasks);
        taskListView.setItems(taskItems);
    }

    private void saveTask() {
        // Validasi Sederhana
        if (titleField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Judul tugas tidak boleh kosong ya!");
            return;
        }

        if (dueDatePicker.getValue() != null) {
            java.time.LocalDate tanggalDipilih = dueDatePicker.getValue();
            java.time.LocalDate hariIni = java.time.LocalDate.now();

            if (tanggalDipilih.isBefore(hariIni)) {
                showAlert(Alert.AlertType.WARNING, "Validasi Tanggal Gagal", "Ups! Kamu tidak bisa memilih tanggal deadline di masa lalu.");
                return; // Menghentikan proses ke bawah agar data tidak tersimpan
            }
        }

        Task newTask = new Task();
        newTask.setTitle(titleField.getText());
        newTask.setDescription(descriptionField.getText());
        newTask.setDueDate(dueDatePicker.getValue());
        newTask.setPriority(priorityCombo.getValue() != null ? priorityCombo.getValue() : "MEDIUM");
        newTask.setCompleted(false);

        taskRepository.save(newTask); // Simpan ke Database

        taskForm.setVisible(false);
        taskForm.setManaged(false);
        clearForm();
        loadTasks();
    }

    private void markTaskAsDone() {
        // Ambil tugas yang sedang diklik user di list
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Tugas", "Klik dulu salah satu tugas di daftar yang mau diselesaikan!");
            return;
        }

        if (selectedTask.isCompleted()) {
            showAlert(Alert.AlertType.INFORMATION, "Sudah Selesai", "Tugas ini sudah kamu selesaikan sebelumnya!");
            return;
        }

        // Memunculkan Pop-up pilihan Mood
        List<String> moodChoices = List.of("😁 Sangat Puas/Senang", "🙂 Biasa Saja/Lega", "😫 Lelah/Capek Banget");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("😁 Sangat Puas/Senang", moodChoices);
        dialog.setTitle("Lapor Mood");
        dialog.setHeaderText("Kerja Bagus! Kamu telah menyelesaikan: " + selectedTask.getTitle());
        dialog.setContentText("Bagaimana perasaanmu setelah menyelesaikan tugas ini?");

        // Jika user memilih mood dan klik OK
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mood -> {
            selectedTask.setCompleted(true);
            selectedTask.setCompletionMood(mood);
            taskRepository.save(selectedTask); // Update ke Database

            loadTasks(); // Refresh list
            showAlert(Alert.AlertType.INFORMATION, "Hebat!", "Tugas diselesaikan dan mood berhasil dicatat!");
        });
    }

    private void deleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskRepository.delete(selectedTask);
            loadTasks();
        } else {
            showAlert(Alert.AlertType.WARNING, "Pilih Tugas", "Pilih tugas yang mau dihapus terlebih dahulu!");
        }
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        dueDatePicker.setValue(null);
        priorityCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}