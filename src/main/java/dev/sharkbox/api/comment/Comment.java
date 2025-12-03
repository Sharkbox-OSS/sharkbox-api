package dev.sharkbox.api.comment;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.sharkbox.api.thread.Thread;
import dev.sharkbox.api.vote.VotableEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "comment")
public class Comment extends VotableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    private Long parentId;

    @NotNull
    private String userId;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    @JsonIgnore
    private Thread thread;

    @NotEmpty
    private String content;

    @NotNull
    private OffsetDateTime createdAt;

    @Nullable
    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @JsonProperty("threadId")
    public Long getThreadId() {
        return thread != null ? thread.getId() : null;
    }

    @JsonProperty("threadTitle")
    public String getThreadTitle() {
        return thread != null ? thread.getTitle() : null;
    }

    @JsonProperty("threadBoxSlug")
    public String getThreadBoxSlug() {
        return thread != null && thread.getBox() != null ? thread.getBox().getSlug() : null;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
