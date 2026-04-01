package com.example.flexapp.entity;

import com.example.flexapp.enums.TimeEntryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "time_entries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_time_entries_user_id_work_date",
                        columnNames = {"user_id", "work_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "lunch_out_time")
    private LocalDateTime lunchOutTime;

    @Column(name = "lunch_in_time")
    private LocalDateTime lunchInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @PositiveOrZero
    @Column(name = "worked_minutes")
    private Integer workedMinutes;

    @PositiveOrZero
    @Column(name = "lunch_minutes")
    private Integer lunchMinutes;

    @PositiveOrZero
    @Column(name = "extra_lunch_minutes")
    private Integer extraLunchMinutes;

    @Column(name = "flex_minutes")
    private Integer flexMinutes;

    @Column(name = "manual_entry", nullable = false)
    private boolean manualEntry = false;

    @Column(length = 500)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TimeEntryStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}