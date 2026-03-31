package com.example.flexapp.repository;

import com.example.flexapp.entity.TimeEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    Optional<TimeEntry> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
    List<TimeEntry> findByUserIdAndWorkDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    Page<TimeEntry> findByUserId(Long userId, Pageable pageable);
    List<TimeEntry> findByUserIdOrderByWorkDateDesc(Long userId);
}