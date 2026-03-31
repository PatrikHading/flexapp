ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_lunch_out_requires_check_in
        CHECK (
            lunch_out_time IS NULL
                OR check_in_time IS NOT NULL
            );

ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_lunch_in_requires_lunch_out
        CHECK (
            lunch_in_time IS NULL
                OR lunch_out_time IS NOT NULL
            );

ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_check_out_requires_check_in
        CHECK (
            check_out_time IS NULL
                OR check_in_time IS NOT NULL
            );