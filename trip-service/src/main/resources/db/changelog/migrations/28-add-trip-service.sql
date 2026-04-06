--liquibase formatted sql

--changeset jy-ptl:28-add-trip-service

CREATE TABLE trip (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id VARCHAR(100) NOT NULL,
    directory_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_on TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_on TIMESTAMP WITH TIME ZONE
);

ALTER TABLE trip
ADD CONSTRAINT chk_trip_visibility
CHECK (visibility IN ('PRIVATE', 'SHARED'));

ALTER TABLE trip
ADD CONSTRAINT chk_trip_status
CHECK (status IN ('PLANNED', 'ONGOING', 'COMPLETED'));

--rollback DROP TABLE trip;
