package dev.sharkbox.api.vote;

import jakarta.validation.constraints.NotNull;

/**
 * Generic form for voting on any votable entity (threads, comments, etc.)
 */
public class VoteForm {
    
    @NotNull
    private Boolean isUpvote;

    public Boolean getIsUpvote() {
        return isUpvote;
    }

    public void setIsUpvote(Boolean isUpvote) {
        this.isUpvote = isUpvote;
    }
}
