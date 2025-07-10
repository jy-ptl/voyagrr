package com.voyagrr.storageservice.repository;

import com.voyagrr.storageservice.dto.DirectoryFlatResponse;
import com.voyagrr.storageservice.model.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    List<Directory> findByParentDirectory(Directory directory);

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

    @Query(value = """
            WITH RECURSIVE dir_tree AS (
                SELECT
                    d.id,
                    d.name,
                    d.parent_directory_id
                FROM directories d
                WHERE d.parent_directory_id IS NULL
                  AND d.owner_id = :keycloakUserId
            
                UNION ALL
            
                SELECT
                    child.id,
                    child.name,
                    child.parent_directory_id
                FROM directories child
                INNER JOIN dir_tree dt ON child.parent_directory_id = dt.id
            )
            SELECT
                id,
                name,
                parent_directory_id
            FROM dir_tree
            """, nativeQuery = true)
    List<DirectoryFlatResponse> getAllDirectoriesRecursivelyForUserId(@Param("keycloakUserId") String keycloakUserId);


}
