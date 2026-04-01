package com.example.flexapp.repository;

import com.example.flexapp.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    Optional<WorkSchedule> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
    List<WorkSchedule> findAllByUserIdOrderByWorkDateDesc(Long userId);
}