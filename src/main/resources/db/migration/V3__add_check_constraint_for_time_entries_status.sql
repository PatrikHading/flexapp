UPDATE time_entries
SET status = UPPER(TRIM(status))
WHERE status IS NOT NULL
  AND status <> UPPER(TRIM(status));

ALTER TABLE time_entries
    ADD CONSTRAINT chk_time_entries_status
        CHECK (status IN ('OPEN', 'LUNCH', 'COMPLETED', 'MANUAL', 'EDITED'));