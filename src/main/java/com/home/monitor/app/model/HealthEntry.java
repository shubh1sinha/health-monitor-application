package com.home.monitor.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "health_entries")
public class HealthEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private Integer systolic;
    private Integer diastolic;
    private Double sugarLevelFasting;
    private Double sugarLevelPostMeal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
