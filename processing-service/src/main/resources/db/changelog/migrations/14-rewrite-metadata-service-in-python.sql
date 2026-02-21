--changeset jy-ptl:14-rewrite-metadata-service-in-python

ALTER TABLE file_metadata
ADD COLUMN metadata JSONB;

--rollback ALTER TABLE file_metadata DROP COLUMN metadata;
