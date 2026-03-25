--changeset jy-ptl:49-restructure-project

ALTER TABLE file_metadata
DROP COLUMN mime_type;

--rollback ALTER TABLE file_metadata ADD COLUMN mime_type VARCHAR(255);
