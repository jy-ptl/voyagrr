package com.voyagrr.storageservice.repository;

import com.voyagrr.storageservice.model.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    @Query(value = """
                WITH RECURSIVE dir_path AS (
                    SELECT id, parent_directory_id, 1 AS level
                    FROM directories
                    WHERE id = :directoryId
            
                    UNION ALL
            
                    SELECT d.id, d.parent_directory_id, dp.level + 1
                    FROM directories d
                    JOIN dir_path dp ON dp.parent_directory_id = d.id
                )
                SELECT string_agg('dir_' || id, '/' ORDER BY level DESC) AS full_path
                FROM dir_path;
            """, nativeQuery = true)
    String buildMinioObjectPathFromDirectoryId(Long directoryId);
}
