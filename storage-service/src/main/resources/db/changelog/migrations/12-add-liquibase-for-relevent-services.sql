--liquibase formatted sql

--changeset jy-ptl:12-add-liquibase-for-relevent-services

CREATE TABLE directories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    parent_directory_id BIGINT,
    created_on TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_on TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_directory_parent
        FOREIGN KEY (parent_directory_id)
        REFERENCES directories(id)
        ON DELETE SET NULL
);

-- Prevent duplicate folder names under same parent for same owner
ALTER TABLE directories
ADD CONSTRAINT unique_directory_name_per_parent
UNIQUE (name, parent_directory_id, owner_id);

CREATE TABLE files (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    directory_id BIGINT NOT NULL,
    minio_object_key VARCHAR(500),
    mime_type VARCHAR(255),
    encoding_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_on TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_on TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_file_directory
        FOREIGN KEY (directory_id)
        REFERENCES directories(id)
        ON DELETE CASCADE
);

-- Prevent duplicate file names in same directory
ALTER TABLE files
ADD CONSTRAINT unique_file_name_per_directory
UNIQUE (name, directory_id);

-- Enum safety constraint
ALTER TABLE files
ADD CONSTRAINT chk_files_encoding_status
CHECK (encoding_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'));

--rollback DROP TABLE files;
--rollback DROP TABLE directories;
