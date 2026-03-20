package com.example.flexapp.repository;

import com.example.flexapp.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    Optional<WorkSchedule> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
    List<WorkSchedule> findByUserIdAndWorkDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
