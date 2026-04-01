package com.example.flexapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "work_schedules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_work_schedules_user_id_work_date",
                        columnNames = {"user_id", "work_date"}
                )
        }
)
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

    @PositiveOrZero
    @Column(name = "paid_lunch_minutes", nullable = false)
    private Integer paidLunchMinutes;

    @PositiveOrZero
    @Column(name = "expected_work_minutes", nullable = false)
    private Integer expectedWorkMinutes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}