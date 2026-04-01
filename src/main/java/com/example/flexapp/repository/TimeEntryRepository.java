package com.example.flexapp.repository;

import com.example.flexapp.entity.TimeEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    Optional<TimeEntry> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
    List<TimeEntry> findByUserIdAndWorkDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    Page<TimeEntry> findByUserId(Long userId, Pageable pageable);
    List<TimeEntry> findByUserIdOrderByWorkDateDesc(Long userId);

    @Query("SELECT COALESCE(SUM(t.flexMinutes), 0) FROM TimeEntry t WHERE t.user.id = :userId")
    Long sumFlexMinutesByUserId(@Param("userId") Long userId);
}