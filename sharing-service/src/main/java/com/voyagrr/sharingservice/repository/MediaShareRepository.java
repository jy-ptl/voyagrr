package com.voyagrr.sharingservice.repository;

import com.voyagrr.sharingservice.model.MediaShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaShareRepository extends JpaRepository<MediaShare, Long> {

    @Query(value = """
                SELECT EXISTS (
                     SELECT 1
                     FROM media_shares
                              INNER JOIN media_share_permissions ON media_shares.id = media_share_permissions.media_share_id
                              INNER JOIN permissions ON media_share_permissions.permission_id = permissions.id
                              LEFT JOIN group_members ON group_members.group_id = media_shares.group_id AND group_members.user_id = :userId
                     WHERE media_shares.directory_id = :directoryId
                       AND (
                         (media_shares.user_id = :userId AND permissions.name = :permission)
                             OR (group_members.user_id IS NOT NULL AND permissions.name = :permission)
                         )
                 )
            """, nativeQuery = true)
    boolean hasPermission(@Param("directoryId") Long directoryId,
            @Param("userId") String userId,
            @Param("permission") String permission);

    @Query(value = """
            select * from media_shares where media_shares.directory_id in :directoryIds
            """, nativeQuery = true)
    List<MediaShare> findAllMediaSharesByDirectoryIds(@Param("directoryIds") List<Long> directoryIds);

    @Query(value = """
            select * from media_shares where media_shares.user_id in :userIds
            """, nativeQuery = true)
    List<MediaShare> findAllMediaSharesByUserIds(@Param("userIds") List<String> userIds);

    @Query(value = """
            select * from media_shares where media_shares.directory_id in :fileIds
            """, nativeQuery = true)
    List<MediaShare> findAllMediaSharesByFileIds(@Param("fileIds") List<Long> fileIds);

    @Query(value = """
            select * from media_shares where media_shares.directory_id in :groupIds
            """, nativeQuery = true)
    List<MediaShare> findAllMediaSharesByGroupIds(@Param("groupIds") List<Long> groupIds);

    @Query(value = """
            SELECT ms.directory_id, p.name
            FROM media_shares ms
                     INNER JOIN media_share_permissions msp ON ms.id = msp.media_share_id
                     INNER JOIN permissions p ON msp.permission_id = p.id
                     LEFT JOIN group_members gm ON gm.group_id = ms.group_id AND gm.user_id = :userId
            WHERE ms.directory_id IN :directoryIds
              AND (
                ms.user_id = :userId
                OR gm.user_id IS NOT NULL
              )
            """, nativeQuery = true)
    List<Object[]> findDirectoryPermissions(@Param("directoryIds") List<Long> directoryIds,
            @Param("userId") String userId);

    @Query(value = """
            SELECT ms.file_id, p.name
            FROM media_shares ms
                     INNER JOIN media_share_permissions msp ON ms.id = msp.media_share_id
                     INNER JOIN permissions p ON msp.permission_id = p.id
                     LEFT JOIN group_members gm ON gm.group_id = ms.group_id AND gm.user_id = :userId
            WHERE ms.file_id IN :fileIds
              AND (
                ms.user_id = :userId
                OR gm.user_id IS NOT NULL
              )
            """, nativeQuery = true)
    List<Object[]> findFilePermissions(@Param("fileIds") List<Long> fileIds,
            @Param("userId") String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM media_shares ms
                         JOIN media_share_permissions msp ON ms.id = msp.media_share_id
                         JOIN permissions p ON msp.permission_id = p.id
                         LEFT JOIN group_members gm ON gm.group_id = ms.group_id AND gm.user_id = :userId
                WHERE ms.directory_id IN (:directoryIds)
                  AND p.name = :permission
                  AND (
                    ms.user_id = :userId
                    OR gm.user_id IS NOT NULL
                  )
            )
            """, nativeQuery = true)
    boolean existsByUserIdAndDirectoryIdInAndPermission(@Param("userId") String keycloakUserId,
            @Param("directoryIds") List<Long> directoryIds,
            @Param("permission") String permission);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM media_shares ms
                         JOIN media_share_permissions msp ON ms.id = msp.media_share_id
                         JOIN permissions p ON msp.permission_id = p.id
                         LEFT JOIN group_members gm ON gm.group_id = ms.group_id AND gm.user_id = :userId
                WHERE ms.file_id = :fileId
                  AND p.name = :permission
                  AND (
                    ms.user_id = :userId
                    OR gm.user_id IS NOT NULL
                  )
            )
            """, nativeQuery = true)
    boolean existsByUserIdAndFileIdAndPermission(@Param("userId") String keycloakUserId,
            @Param("fileId") long fileId,
            @Param("permission") String permission);

}
