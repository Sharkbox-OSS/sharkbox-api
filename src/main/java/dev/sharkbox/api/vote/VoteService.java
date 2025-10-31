package dev.sharkbox.api.vote;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sharkbox.api.exception.SelfVoteNotAllowedException;

@Service
public class VoteService {
    
    private final VoteRepository voteRepository;

    VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Transactional
    public VoteResult voteOnEntity(String entityType, Long entityId, Boolean isUpvote, String userId) {
        // Check if user has already voted on this entity
        Optional<Vote> existingVote = voteRepository.findByEntityTypeAndEntityIdAndUserId(entityType, entityId, userId);
        
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            
            // If voting the same way, remove the vote
            if (vote.getIsUpvote().equals(isUpvote)) {
                voteRepository.delete(vote);
                return new VoteResult(false, null, getVoteCounts(entityType, entityId));
            } else {
                // If voting differently, update the vote
                vote.setIsUpvote(isUpvote);
                voteRepository.save(vote);
                return new VoteResult(true, isUpvote, getVoteCounts(entityType, entityId));
            }
        } else {
            // Create new vote
            var newVote = new Vote();
            newVote.setEntityType(entityType);
            newVote.setEntityId(entityId);
            newVote.setUserId(userId);
            newVote.setIsUpvote(isUpvote);
            newVote.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            voteRepository.save(newVote);
            return new VoteResult(true, isUpvote, getVoteCounts(entityType, entityId));
        }
    }
    
    public VoteCounts getVoteCounts(String entityType, Long entityId) {
        var upvotes = voteRepository.countUpvotesByEntityTypeAndEntityId(entityType, entityId);
        var downvotes = voteRepository.countDownvotesByEntityTypeAndEntityId(entityType, entityId);
        return new VoteCounts(upvotes, downvotes);
    }
    
    public Boolean getUserVote(String entityType, Long entityId, String userId) {
        return voteRepository.findByEntityTypeAndEntityIdAndUserId(entityType, entityId, userId)
            .map(Vote::getIsUpvote)
            .orElse(null);
    }
    
    /**
     * Generic method to vote on any votable entity
     */
    public <T extends VotableEntity> T voteOnEntity(T entity, String entityType, VoteForm voteForm, String userId) {
        // Check if user is trying to vote on their own content
        if (entity.getUserId().equals(userId)) {
            throw new SelfVoteNotAllowedException("Users cannot vote on their own " + entityType + "s");
        }
        
        // Perform the vote operation
        voteOnEntity(entityType, entity.getId(), voteForm.getIsUpvote(), userId);
        
        // Populate vote data and return the entity
        return populateVoteData(entity, entityType, userId);
    }
    
    /**
     * Populates vote counts and user vote for any votable entity
     */
    public <T extends VotableEntity> T populateVoteData(T entity, String entityType, String userId) {
        var counts = getVoteCounts(entityType, entity.getId());
        var userVote = getUserVote(entityType, entity.getId(), userId);
        
        entity.setUpvotes(counts.getUpvotes());
        entity.setDownvotes(counts.getDownvotes());
        entity.setUserVote(userVote);
        
        return entity;
    }
    
    public static class VoteResult {
        private final boolean hasVote;
        private final Boolean isUpvote;
        private final VoteCounts counts;
        
        public VoteResult(boolean hasVote, Boolean isUpvote, VoteCounts counts) {
            this.hasVote = hasVote;
            this.isUpvote = isUpvote;
            this.counts = counts;
        }
        
        public boolean isHasVote() {
            return hasVote;
        }
        
        public Boolean getIsUpvote() {
            return isUpvote;
        }
        
        public VoteCounts getCounts() {
            return counts;
        }
    }
    
    public static class VoteCounts {
        private final Long upvotes;
        private final Long downvotes;
        
        public VoteCounts(Long upvotes, Long downvotes) {
            this.upvotes = upvotes;
            this.downvotes = downvotes;
        }
        
        public Long getUpvotes() {
            return upvotes;
        }
        
        public Long getDownvotes() {
            return downvotes;
        }
    }
}
