--liquibase formatted sql

--changeset jy-ptl:72-add-directory-type
ALTER TABLE directories ADD COLUMN type SMALLINT NOT NULL DEFAULT 2;

--rollback ALTER TABLE directories DROP COLUMN type;
