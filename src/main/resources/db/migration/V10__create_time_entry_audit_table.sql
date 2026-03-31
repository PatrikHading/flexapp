CREATE TABLE time_entry_audit (
    id BIGSERIAL PRIMARY KEY,
    time_entry_id BIGINT NOT NULL,
    action VARCHAR(30) NOT NULL,
    changed_by_user_id BIGINT,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details JSONB,

    CONSTRAINT fk_time_entry_audit_time_entry
        FOREIGN KEY (time_entry_id)
        REFERENCES time_entries (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_time_entry_audit_changed_by_user
        FOREIGN KEY (changed_by_user_id)
        REFERENCES users (id)
        ON DELETE SET NULL,

    CONSTRAINT chk_time_entry_audit_action
        CHECK (action IN (
            'CREATED',
            'UPDATED',
            'DELETED',
            'STATUS_CHANGED',
            'MANUAL_CREATED',
            'MANUAL_UPDATED'
        ))
);

CREATE INDEX idx_time_entry_audit_time_entry_id
    ON time_entry_audit (time_entry_id);

CREATE INDEX idx_time_entry_audit_changed_at
    ON time_entry_audit (changed_at);