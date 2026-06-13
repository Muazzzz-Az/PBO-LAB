package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.model.MoodEntry;
import com.kelompok.moodflow.repository.MoodRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReportController {

    @FXML private PieChart moodPieChart;

    private final MoodRepository moodRepository;

    @Autowired
    public ReportController(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    @FXML
    public void initialize() {
        // Ambil semua data mood dari database riil
        List<MoodEntry> entries = moodRepository.findAll();

        // Hitung jumlah masing-masing mood yang tersimpan
        long countExcellent = entries.stream().filter(e -> e.getMoodType().startsWith("Excellent")).count();
        long countGood = entries.stream().filter(e -> e.getMoodType().startsWith("Good")).count();
        long countNeutral = entries.stream().filter(e -> e.getMoodType().startsWith("Neutral")).count();
        long countBad = entries.stream().filter(e -> e.getMoodType().startsWith("Bad")).count();
        long countTerrible = entries.stream().filter(e -> e.getMoodType().startsWith("Terrible")).count();

        // Masukkan ke dalam PieChart JavaFX secara dinamis
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        if (countExcellent > 0) pieChartData.add(new PieChart.Data("Excellent 😁 (" + countExcellent + ")", countExcellent));
        if (countGood > 0) pieChartData.add(new PieChart.Data("Good 🙂 (" + countGood + ")", countGood));
        if (countNeutral > 0) pieChartData.add(new PieChart.Data("Neutral 😐 (" + countNeutral + ")", countNeutral));
        if (countBad > 0) pieChartData.add(new PieChart.Data("Bad 😔 (" + countBad + ")", countBad));
        if (countTerrible > 0) pieChartData.add(new PieChart.Data("Terrible 😫 (" + countTerrible + ")", countTerrible));

        // Jika data database masih kosong, beri tampilan standarnya biar grafik tidak kosong melompong
        if (pieChartData.isEmpty()) {
            pieChartData.add(new PieChart.Data("Belum ada data log mood", 1));
        }

        moodPieChart.setData(pieChartData);
    }
}