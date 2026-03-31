package com.example.flexapp.repository;

import com.example.flexapp.entity.TimeEntryAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeEntryAuditRepository extends JpaRepository<TimeEntryAudit, Long> {
}