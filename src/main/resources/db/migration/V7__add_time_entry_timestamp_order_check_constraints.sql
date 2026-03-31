ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_lunch_out_not_before_check_in
        CHECK (
            lunch_out_time IS NULL
                OR check_in_time IS NULL
                OR lunch_out_time >= check_in_time
            );

ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_lunch_in_not_before_lunch_out
        CHECK (
            lunch_in_time IS NULL
                OR lunch_out_time IS NULL
                OR lunch_in_time >= lunch_out_time
            );

ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_check_out_not_before_check_in
        CHECK (
            check_out_time IS NULL
                OR check_in_time IS NULL
                OR check_out_time >= check_in_time
            );

ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_check_out_not_before_lunch_in
        CHECK (
            check_out_time IS NULL
                OR lunch_in_time IS NULL
                OR check_out_time >= lunch_in_time
            );