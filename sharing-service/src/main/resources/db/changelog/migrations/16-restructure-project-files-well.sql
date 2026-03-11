--liquibase formatted sql

--changeset jy-ptl:16-restructure-project-files-well

INSERT INTO permissions (name) VALUES ('UPLOAD');
INSERT INTO permissions (name) VALUES ('DOWNLOAD');
INSERT INTO permissions (name) VALUES ('VIEW');
INSERT INTO permissions (name) VALUES ('DELETE');
INSERT INTO permissions (name) VALUES ('EDIT');
INSERT INTO permissions (name) VALUES ('SHARE');

--rollback DELETE FROM permissions WHERE name IN ('UPLOAD','DOWNLOAD','VIEW','DELETE','EDIT','SHARE');
