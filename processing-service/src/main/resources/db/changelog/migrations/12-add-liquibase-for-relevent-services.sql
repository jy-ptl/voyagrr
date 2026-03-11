--liquibase formatted sql

--changeset jy-ptl:12-add-liquibase-for-relevent-services

CREATE TABLE file_metadata (
    id BIGSERIAL PRIMARY KEY,
    file_id BIGINT NOT NULL,
    minio_object_key VARCHAR(500) NOT NULL,
    mime_type VARCHAR(255) NOT NULL,
    created_on TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_on TIMESTAMP WITH TIME ZONE
);

-- Ensure one metadata record per file
ALTER TABLE file_metadata
ADD CONSTRAINT unique_file_metadata_file_id
UNIQUE (file_id);

--rollback DROP TABLE file_metadata;
