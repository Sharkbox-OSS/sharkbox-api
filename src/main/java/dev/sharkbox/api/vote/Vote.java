package dev.sharkbox.api.vote;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "vote", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"entity_type", "entity_id", "user_id"})
})
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String entityType;

    @NotNull
    private Long entityId;

    @NotNull
    private String userId;

    @NotNull
    private Boolean isUpvote;

    @NotNull
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsUpvote() {
        return isUpvote;
    }

    public void setIsUpvote(Boolean isUpvote) {
        this.isUpvote = isUpvote;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
