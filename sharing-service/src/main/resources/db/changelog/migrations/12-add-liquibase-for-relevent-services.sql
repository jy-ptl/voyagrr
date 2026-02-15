--liquibase formatted sql

--changeset jy-ptl:12-add-liquibase-for-relevent-services

CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    created_on TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_on TIMESTAMP WITH TIME ZONE
);

CREATE TABLE group_members (
    group_id BIGINT NOT NULL,
    user_id VARCHAR(255) NOT NULL,

    PRIMARY KEY (group_id, user_id),

    CONSTRAINT fk_group_members_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id)
        ON DELETE CASCADE
);

CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Enum constraint
ALTER TABLE permissions
ADD CONSTRAINT chk_permissions_name
CHECK (name IN ('UPLOAD','DOWNLOAD','VIEW','DELETE','EDIT','SHARE'));

CREATE TABLE media_shares (
    id BIGSERIAL PRIMARY KEY,
    file_id BIGINT,
    directory_id BIGINT,
    user_id VARCHAR(255),
    group_id BIGINT,
    created_on TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_on TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_media_share_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id)
        ON DELETE CASCADE
);

-- Either file_id or directory_id can be null not both
ALTER TABLE media_shares
ADD CONSTRAINT chk_media_share_target
CHECK (
    (file_id IS NOT NULL AND directory_id IS NULL)
    OR
    (file_id IS NULL AND directory_id IS NOT NULL)
);

CREATE TABLE media_share_permissions (
    media_share_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    PRIMARY KEY (media_share_id, permission_id),

    CONSTRAINT fk_msp_media_share
        FOREIGN KEY (media_share_id)
        REFERENCES media_shares(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_msp_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);

--rollback DROP TABLE media_share_permissions;
--rollback DROP TABLE media_shares;
--rollback DROP TABLE group_members;
--rollback DROP TABLE permissions;
--rollback DROP TABLE groups;
