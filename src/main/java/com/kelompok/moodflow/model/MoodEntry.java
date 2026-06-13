package com.kelompok.moodflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "mood_entries")
@Data
@EqualsAndHashCode(callSuper = true)
public class MoodEntry extends BaseEntity { // INHERITANCE

    @Column(nullable = false)
    private String moodType; // Senang, Sedih, Lelah, dll.

    private String notes;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    // PILAR PBO: POLYMORPHISM (Method Overriding)
    @Override
    public String getEntitySummary() {
        return "Mood: " + this.moodType + " dicatat pada " + this.timestamp.toLocalDate();
    }
}