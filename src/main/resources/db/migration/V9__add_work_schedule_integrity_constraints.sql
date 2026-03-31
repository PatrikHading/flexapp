ALTER TABLE work_schedules
    ADD CONSTRAINT chk_work_schedules_paid_lunch_minutes_non_negative
        CHECK (paid_lunch_minutes >= 0);

ALTER TABLE work_schedules
    ADD CONSTRAINT chk_work_schedules_expected_work_minutes_non_negative
        CHECK (expected_work_minutes >= 0);

ALTER TABLE work_schedules
    ADD CONSTRAINT chk_work_schedules_end_not_before_start
        CHECK (planned_end_time >= planned_start_time);