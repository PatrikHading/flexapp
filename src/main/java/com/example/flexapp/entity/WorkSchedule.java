package com.example.flexapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

@Entity
@Table(name = "work_schedules")
@Getter
@Setter
@NoArgsConstructor
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "planned_start_time", nullable = false)
    private LocalTime plannedStartTime;

    @Column(name = "planned_end_time", nullable = false)
    private LocalTime plannedEndTime;

    @Column(name = "paid_lunch_minutes", nullable = false)
    private Integer paidLunchMinutes;

    @Column(name = "expected_work_minutes", nullable = false)
    private Integer expectedWorkMinutes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
