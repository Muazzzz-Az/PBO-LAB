package com.kelompok.moodflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends BaseEntity { // INHERITANCE

    @Column(nullable = false)
    private String title;

    private String description;
    
    private LocalDate dueDate;
    
    private String priority; // HIGH, MEDIUM, LOW
    
    private boolean isCompleted = false;
    
    private String completionMood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // PILAR PBO: POLYMORPHISM (Method Overriding)
    @Override
    public String getEntitySummary() {
        return "Task: " + this.title + " | Status: " + (this.isCompleted ? "Done" : "Pending");
    }
}