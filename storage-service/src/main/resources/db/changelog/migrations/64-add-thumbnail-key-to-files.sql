-- liquibase formatted sql

-- changeset jy-ptl:64-add-thumbnail-key-to-files
ALTER TABLE files ADD COLUMN thumbnail_key VARCHAR(255);
