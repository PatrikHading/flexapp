ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_worked_minutes_non_negative
        CHECK (worked_minutes IS NULL OR worked_minutes >= 0);

ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_lunch_minutes_non_negative
        CHECK (lunch_minutes IS NULL OR lunch_minutes >= 0);

ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_extra_lunch_minutes_non_negative
        CHECK (extra_lunch_minutes IS NULL OR extra_lunch_minutes >= 0);