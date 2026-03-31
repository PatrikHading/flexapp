package com.example.flexapp.entity;

import com.example.flexapp.enums.TimeEntryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "worked_minutes")
    private Integer workedMinutes;

    @Column(name = "lunch_minutes")
    private Integer lunchMinutes;

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}