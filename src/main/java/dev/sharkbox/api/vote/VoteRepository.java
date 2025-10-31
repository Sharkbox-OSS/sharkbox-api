package dev.sharkbox.api.vote;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByEntityTypeAndEntityIdAndUserId(String entityType, Long entityId, String userId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.entityType = :entityType AND v.entityId = :entityId AND v.isUpvote = true")
    Long countUpvotesByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.entityType = :entityType AND v.entityId = :entityId AND v.isUpvote = false")
    Long countDownvotesByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    void deleteByEntityTypeAndEntityIdAndUserId(String entityType, Long entityId, String userId);
}
