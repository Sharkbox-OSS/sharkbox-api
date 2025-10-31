package dev.sharkbox.api.vote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    private VoteService voteService;

    @BeforeEach
    void setUp() {
        voteService = new VoteService(voteRepository);
    }

    @Test
    void testVoteOnEntity_NewVote() {
        // Given
        String entityType = "thread";
        Long entityId = 1L;
        Boolean isUpvote = true;
        String userId = "test-user-uuid";

        when(voteRepository.findByEntityTypeAndEntityIdAndUserId(entityType, entityId, userId))
            .thenReturn(Optional.empty());
        when(voteRepository.countUpvotesByEntityTypeAndEntityId(entityType, entityId))
            .thenReturn(1L);
        when(voteRepository.countDownvotesByEntityTypeAndEntityId(entityType, entityId))
            .thenReturn(0L);

        // When
        VoteService.VoteResult result = voteService.voteOnEntity(entityType, entityId, isUpvote, userId);

        // Then
        assertTrue(result.isHasVote());
        assertEquals(true, result.getIsUpvote());
        assertEquals(1, result.getCounts().getUpvotes());
        assertEquals(0, result.getCounts().getDownvotes());

        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    void testVoteOnEntity_RemoveVote() {
        // Given
        String entityType = "thread";
        Long entityId = 1L;
        Boolean isUpvote = true;
        String userId = "test-user-uuid";

        Vote existingVote = new Vote();
        existingVote.setId(1L);
        existingVote.setEntityType(entityType);
        existingVote.setEntityId(entityId);
        existingVote.setUserId(userId);
        existingVote.setIsUpvote(true);
        existingVote.setCreatedAt(OffsetDateTime.now());

        when(voteRepository.findByEntityTypeAndEntityIdAndUserId(entityType, entityId, userId))
            .thenReturn(Optional.of(existingVote));
        when(voteRepository.countUpvotesByEntityTypeAndEntityId(entityType, entityId))
            .thenReturn(0L);
        when(voteRepository.countDownvotesByEntityTypeAndEntityId(entityType, entityId))
            .thenReturn(0L);

        // When
        VoteService.VoteResult result = voteService.voteOnEntity(entityType, entityId, isUpvote, userId);

        // Then
        assertFalse(result.isHasVote());
        assertNull(result.getIsUpvote());
        assertEquals(0, result.getCounts().getUpvotes());
        assertEquals(0, result.getCounts().getDownvotes());

        verify(voteRepository).delete(existingVote);
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void testVoteOnEntity_ChangeVote() {
        // Given
        String entityType = "thread";
        Long entityId = 1L;
        Boolean isUpvote = false; // changing from upvote to downvote
        String userId = "test-user-uuid";

        Vote existingVote = new Vote();
        existingVote.setId(1L);
        existingVote.setEntityType(entityType);
        existingVote.setEntityId(entityId);
        existingVote.setUserId(userId);
        existingVote.setIsUpvote(true); // existing upvote
        existingVote.setCreatedAt(OffsetDateTime.now());

        when(voteRepository.findByEntityTypeAndEntityIdAndUserId(entityType, entityId, userId))
            .thenReturn(Optional.of(existingVote));
        when(voteRepository.countUpvotesByEntityTypeAndEntityId(entityType, entityId))
            .thenReturn(0L);
        when(voteRepository.countDownvotesByEntityTypeAndEntityId(entityType, entityId))
            .thenReturn(1L);

        // When
        VoteService.VoteResult result = voteService.voteOnEntity(entityType, entityId, isUpvote, userId);

        // Then
        assertTrue(result.isHasVote());
        assertEquals(false, result.getIsUpvote());
        assertEquals(0, result.getCounts().getUpvotes());
        assertEquals(1, result.getCounts().getDownvotes());

        verify(voteRepository).save(existingVote);
        verify(voteRepository, never()).delete(any(Vote.class));
    }

    @Test
    void testGetVoteCounts() {
        // Given
        String entityType = "comment";
        Long entityId = 2L;

        when(voteRepository.countUpvotesByEntityTypeAndEntityId(entityType, entityId))
            .thenReturn(5L);
        when(voteRepository.countDownvotesByEntityTypeAndEntityId(entityType, entityId))
            .thenReturn(2L);

        // When
        VoteService.VoteCounts counts = voteService.getVoteCounts(entityType, entityId);

        // Then
        assertEquals(5, counts.getUpvotes());
        assertEquals(2, counts.getDownvotes());
    }

    @Test
    void testGetUserVote_UserHasVoted() {
        // Given
        String entityType = "thread";
        Long entityId = 1L;
        String userId = "test-user-uuid";

        Vote userVote = new Vote();
        userVote.setIsUpvote(false);

        when(voteRepository.findByEntityTypeAndEntityIdAndUserId(entityType, entityId, userId))
            .thenReturn(Optional.of(userVote));

        // When
        Boolean result = voteService.getUserVote(entityType, entityId, userId);

        // Then
        assertEquals(false, result);
    }

    @Test
    void testGetUserVote_UserHasNotVoted() {
        // Given
        String entityType = "thread";
        Long entityId = 1L;
        String userId = "test-user-uuid";

        when(voteRepository.findByEntityTypeAndEntityIdAndUserId(entityType, entityId, userId))
            .thenReturn(Optional.empty());

        // When
        Boolean result = voteService.getUserVote(entityType, entityId, userId);

        // Then
        assertNull(result);
    }

    @Test
    void testVoteCountsConstructor() {
        // Given
        Long upvotes = 10L;
        Long downvotes = 3L;

        // When
        VoteService.VoteCounts counts = new VoteService.VoteCounts(upvotes, downvotes);

        // Then
        assertEquals(upvotes, counts.getUpvotes());
        assertEquals(downvotes, counts.getDownvotes());
    }

    @Test
    void testVoteResultConstructor() {
        // Given
        boolean hasVote = true;
        Boolean isUpvote = true;
        VoteService.VoteCounts counts = new VoteService.VoteCounts(1L, 0L);

        // When
        VoteService.VoteResult result = new VoteService.VoteResult(hasVote, isUpvote, counts);

        // Then
        assertEquals(hasVote, result.isHasVote());
        assertEquals(isUpvote, result.getIsUpvote());
        assertEquals(counts, result.getCounts());
    }
}
