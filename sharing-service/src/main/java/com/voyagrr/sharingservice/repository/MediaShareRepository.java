package com.voyagrr.sharingservice.repository;

import com.voyagrr.sharingservice.model.MediaShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

}
