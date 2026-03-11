--liquibase formatted sql

--changeset jy-ptl:18-add-analysis-service

-- Drop existing check constraint
ALTER TABLE files
DROP CONSTRAINT IF EXISTS chk_files_encoding_status;

-- Recreate constraint with IN_ANALYSIS included
ALTER TABLE files
ADD CONSTRAINT chk_files_encoding_status
CHECK (encoding_status IN (
    'PENDING',
    'PROCESSING',
    'IN_ANALYSIS',
    'COMPLETED',
    'FAILED'
));

--rollback ALTER TABLE files DROP CONSTRAINT chk_files_encoding_status;
--rollback ALTER TABLE files ADD CONSTRAINT chk_files_encoding_status
--rollback CHECK (encoding_status IN ('PENDING','PROCESSING','COMPLETED','FAILED'));
