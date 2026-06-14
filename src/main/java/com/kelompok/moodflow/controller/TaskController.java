package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.model.Task;
import com.kelompok.moodflow.model.User;
import com.kelompok.moodflow.repository.TaskRepository;
import com.kelompok.moodflow.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final ObservableList<Task> taskItems = FXCollections.observableArrayList();

    @Autowired
    public TaskController(TaskRepository taskRepository,
                          UserRepository userRepository,
                          SessionManager sessionManager) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        // Initialize ComboBox
        priorityCombo.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));
        priorityCombo.setValue("MEDIUM");

        // Custom Cell Factory untuk Card Design
        taskListView.setCellFactory(param -> new ListCell<Task>() {
            private final VBox card = new VBox();
            private final HBox headerRow = new HBox();
            private final Label titleLabel = new Label();
            private final Region spacer = new Region();
            private final Label priorityBadge = new Label();
            private final Label statusIcon = new Label();

            private final HBox detailsRow = new HBox();
            private final Label dueDateLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Label moodLabel = new Label();

            {
                // Setup Card Styling
                card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
                card.setPadding(new Insets(15, 20, 15, 20));
                card.setSpacing(10);

                // Header Row (Status + Title + Priority)
                headerRow.setAlignment(Pos.CENTER_LEFT);
                headerRow.setSpacing(12);
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Status Icon
                statusIcon.setStyle("-fx-font-size: 20;");

                // Title Label
                titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2D2D2D;");
                titleLabel.setWrapText(true);
                HBox.setHgrow(titleLabel, Priority.ALWAYS);

                // Priority Badge
                priorityBadge.setStyle("-fx-background-radius: 20; -fx-padding: 4 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: white;");
                priorityBadge.setAlignment(Pos.CENTER);

                headerRow.getChildren().addAll(statusIcon, titleLabel, spacer, priorityBadge);

                // Details Row
                detailsRow.setSpacing(15);
                detailsRow.setAlignment(Pos.CENTER_LEFT);

                dueDateLabel.setStyle("-fx-text-fill: #8B8B8B; -fx-font-size: 12;");
                dueDateLabel.setGraphic(createIconLabel("📅"));

                descriptionLabel.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 12;");
                descriptionLabel.setWrapText(true);
                descriptionLabel.setMaxWidth(400);

                moodLabel.setStyle("-fx-background-color: #E8F5E9; -fx-background-radius: 15; -fx-padding: 4 12; -fx-font-size: 11; -fx-text-fill: #2E7D32;");
                moodLabel.setVisible(false);

                detailsRow.getChildren().addAll(dueDateLabel, descriptionLabel);

                card.getChildren().addAll(headerRow, detailsRow);
            }

            private HBox createIconLabel(String icon) {
                HBox box = new HBox();
                box.setSpacing(5);
                box.setAlignment(Pos.CENTER_LEFT);
                Label iconLabel = new Label(icon);
                iconLabel.setStyle("-fx-font-size: 12;");
                box.getChildren().add(iconLabel);
                return box;
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Set Status Icon
                    if (task.isCompleted()) {
                        statusIcon.setText("✅");
                        card.setStyle("-fx-background-color: #F1F8E9; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
                    } else {
                        statusIcon.setText("⏳");
                        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
                    }

                    // Set Title
                    titleLabel.setText(task.getTitle());

                    // Set Priority Badge
                    String priority = task.getPriority();
                    if (priority != null) {
                        switch (priority) {
                            case "HIGH":
                                priorityBadge.setText("🔴 HIGH");
                                priorityBadge.setStyle("-fx-background-color: #FFEBEE; -fx-background-radius: 20; -fx-padding: 4 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");
                                break;
                            case "MEDIUM":
                                priorityBadge.setText("🟠 MEDIUM");
                                priorityBadge.setStyle("-fx-background-color: #FFF3E0; -fx-background-radius: 20; -fx-padding: 4 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #F57C00;");
                                break;
                            case "LOW":
                                priorityBadge.setText("🟢 LOW");
                                priorityBadge.setStyle("-fx-background-color: #E8F5E9; -fx-background-radius: 20; -fx-padding: 4 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #388E3C;");
                                break;
                            default:
                                priorityBadge.setText("📋 NORMAL");
                        }
                    }

                    // Set Due Date
                    if (task.getDueDate() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                        String dateStr = task.getDueDate().format(formatter);
                        LocalDate today = LocalDate.now();
                        LocalDate dueDate = task.getDueDate();

                        if (dueDate.isBefore(today) && !task.isCompleted()) {
                            dueDateLabel.setText("⚠️ Due: " + dateStr + " (Overdue!)");
                            dueDateLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 12; -fx-font-weight: bold;");
                        } else if (dueDate.equals(today) && !task.isCompleted()) {
                            dueDateLabel.setText("📅 Due: " + dateStr + " (Today!)");
                            dueDateLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 12; -fx-font-weight: bold;");
                        } else {
                            dueDateLabel.setText("📅 Due: " + dateStr);
                            dueDateLabel.setStyle("-fx-text-fill: #8B8B8B; -fx-font-size: 12;");
                        }
                    } else {
                        dueDateLabel.setText("📅 No due date");
                    }

                    // Set Description (if exists)
                    if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                        String desc = task.getDescription();
                        if (desc.length() > 80) {
                            desc = desc.substring(0, 80) + "...";
                        }
                        descriptionLabel.setText("📝 " + desc);
                        descriptionLabel.setVisible(true);
                    } else {
                        descriptionLabel.setVisible(false);
                    }

                    // Set Completion Mood (if task is completed)
                    if (task.isCompleted() && task.getCompletionMood() != null) {
                        if (!detailsRow.getChildren().contains(moodLabel)) {
                            detailsRow.getChildren().add(moodLabel);
                        }
                        moodLabel.setText("✨ " + task.getCompletionMood());
                        moodLabel.setVisible(true);
                    } else {
                        moodLabel.setVisible(false);
                    }

                    setGraphic(card);
                    setText(null);

                    // Hover effect
                    card.setOnMouseEntered(e -> {
                        if (!task.isCompleted()) {
                            card.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.15), 12, 0, 0, 4);");
                        }
                    });
                    card.setOnMouseExited(e -> {
                        if (!task.isCompleted()) {
                            card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
                        } else {
                            card.setStyle("-fx-background-color: #F1F8E9; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
                        }
                    });
                }
            }
        });

        loadTasks();

        addTaskBtn.setOnAction(e -> {
            taskForm.setVisible(true);
            taskForm.setManaged(true);
            titleField.requestFocus();
        });

        cancelTaskBtn.setOnAction(e -> {
            taskForm.setVisible(false);
            taskForm.setManaged(false);
            clearForm();
        });

        saveTaskBtn.setOnAction(e -> saveTask());
        markDoneBtn.setOnAction(e -> markTaskAsDone());
        deleteTaskBtn.setOnAction(e -> deleteTask());
    }

    private void loadTasks() {
        taskItems.clear();

        User currentUser = sessionManager.getCurrentUser();
        List<Task> tasks;

        if (currentUser != null) {
            tasks = taskRepository.findByUserId(currentUser.getId());
        } else {
            tasks = taskRepository.findAll();
        }

        taskItems.addAll(tasks);
        taskListView.setItems(taskItems);
    }

    private void saveTask() {
        try {
            if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Judul tugas tidak boleh kosong!");
                return;
            }

            if (dueDatePicker.getValue() != null) {
                LocalDate selectedDate = dueDatePicker.getValue();
                LocalDate today = LocalDate.now();

                if (selectedDate.isBefore(today)) {
                    showAlert(Alert.AlertType.WARNING, "Validasi Tanggal",
                            "Tidak bisa membuat tugas dengan deadline di masa lalu!");
                    return;
                }
            }

            String priority = priorityCombo.getValue();
            if (priority == null || priority.isEmpty()) {
                priority = "MEDIUM";
            }

            User currentUser = sessionManager.getCurrentUser();
            if (currentUser == null) {
                Optional<User> firstUser = userRepository.findAll().stream().findFirst();
                if (firstUser.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Tidak ada user terdaftar! Silakan registrasi dulu.");
                    return;
                }
                currentUser = firstUser.get();
            }

            Task newTask = new Task();
            newTask.setTitle(titleField.getText().trim());
            newTask.setDescription(descriptionField.getText());
            newTask.setDueDate(dueDatePicker.getValue());
            newTask.setPriority(priority);
            newTask.setCompleted(false);
            newTask.setCompletionMood(null);
            newTask.setUser(currentUser);

            taskRepository.save(newTask);

            taskForm.setVisible(false);
            taskForm.setManaged(false);
            clearForm();
            loadTasks();

            showAlert(Alert.AlertType.INFORMATION, "Berhasil!", "Task berhasil disimpan!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan task: " + e.getMessage());
        }
    }

    private void markTaskAsDone() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Tugas", "Klik dulu salah satu tugas di daftar yang mau diselesaikan!");
            return;
        }

        if (selectedTask.isCompleted()) {
            showAlert(Alert.AlertType.INFORMATION, "Sudah Selesai", "Tugas ini sudah kamu selesaikan sebelumnya!");
            return;
        }

        List<String> moodChoices = List.of(
                "😁 Sangat Puas/Senang",
                "🙂 Biasa Saja/Lega",
                "😫 Lelah/Capek Banget"
        );

        ChoiceDialog<String> dialog = new ChoiceDialog<>("😁 Sangat Puas/Senang", moodChoices);
        dialog.setTitle("Lapor Mood");
        dialog.setHeaderText("🎉 Kerja Bagus! Kamu telah menyelesaikan: " + selectedTask.getTitle());
        dialog.setContentText("Bagaimana perasaanmu setelah menyelesaikan tugas ini?");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mood -> {
            selectedTask.setCompleted(true);
            selectedTask.setCompletionMood(mood);
            taskRepository.save(selectedTask);
            loadTasks();
            showAlert(Alert.AlertType.INFORMATION, "Hebat! 🎯",
                    "Tugas diselesaikan dan mood berhasil dicatat!\n\nMood: " + mood);
        });
    }

    private void deleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Tugas", "Pilih tugas yang mau dihapus terlebih dahulu!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Tugas?");
        confirmAlert.setContentText("Apakah kamu yakin ingin menghapus tugas \"" + selectedTask.getTitle() + "\"?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            taskRepository.delete(selectedTask);
            loadTasks();
            showAlert(Alert.AlertType.INFORMATION, "Terhapus", "Tugas berhasil dihapus!");
        }
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        dueDatePicker.setValue(null);
        priorityCombo.setValue("MEDIUM");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}