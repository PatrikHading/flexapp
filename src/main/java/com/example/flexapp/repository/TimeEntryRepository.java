package com.example.flexapp.repository;

import com.example.flexapp.entity.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    Optional<TimeEntry> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
    List<TimeEntry> findByUserIdAndWorkDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<TimeEntry> findByUserIdOrderByWorkDateDesc(Long userId);
}
