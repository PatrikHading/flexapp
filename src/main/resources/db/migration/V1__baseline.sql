CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL,
    token_version INTEGER
);

ALTER TABLE users
    ADD CONSTRAINT uk_users_email UNIQUE (email);

 CREATE TABLE work_schedules (
     id BIGSERIAL PRIMARY KEY,
     work_date DATE NOT NULL,
     planned_start_time TIME NOT NULL,
     planned_end_time TIME NOT NULL,
     paid_lunch_minutes INTEGER NOT NULL,
     expected_work_minutes INTEGER NOT NULL,
     user_id BIGINT NOT NULL,
     created_at TIMESTAMP NOT NULL
);

ALTER TABLE work_schedules
    ADD CONSTRAINT uk_work_schedules_user_id_work_date
        UNIQUE (user_id, work_date);

ALTER TABLE work_schedules
    ADD CONSTRAINT fk_work_schedules_user_id
        FOREIGN KEY (user_id) REFERENCES users(id);

CREATE TABLE time_entries (
    id BIGSERIAL PRIMARY KEY,
    work_date DATE NOT NULL,
    check_in_time TIMESTAMP,
    lunch_out_time TIMESTAMP,
    lunch_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    worked_minutes INTEGER,
    lunch_minutes INTEGER,
    extra_lunch_minutes INTEGER,
    flex_minutes INTEGER,
    manual_entry BOOLEAN NOT NULL,
    comment VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

ALTER TABLE time_entries
    ADD CONSTRAINT uk_time_entries_user_id_work_date
        UNIQUE (user_id, work_date);

ALTER TABLE time_entries
    ADD CONSTRAINT fk_time_entries_user_id
        FOREIGN KEY (user_id) REFERENCES users(id);
