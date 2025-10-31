package dev.sharkbox.api.vote;

import jakarta.persistence.Transient;

/**
 * Abstract base class for entities that support voting.
 * Provides common vote-related fields and methods.
 */
public abstract class VotableEntity {
    
    private Long upvotes = 0L;
    private Long downvotes = 0L;
    
    // Transient field to show user's current vote (not persisted to database)
    @Transient
    private Boolean userVote; // null = no vote, true = upvote, false = downvote
    
    public abstract Long getId();
    public abstract String getUserId();
    
    // Vote count getters and setters
    public Long getUpvotes() {
        return upvotes;
    }
    
    public void setUpvotes(Long upvotes) {
        this.upvotes = upvotes;
    }
    
    public Long getDownvotes() {
        return downvotes;
    }
    
    public void setDownvotes(Long downvotes) {
        this.downvotes = downvotes;
    }
    
    public Boolean getUserVote() {
        return userVote;
    }
    
    public void setUserVote(Boolean userVote) {
        this.userVote = userVote;
    }
}
