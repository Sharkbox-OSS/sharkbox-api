package dev.sharkbox.api.vote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import dev.sharkbox.api.SharkboxApiTestBase;
import dev.sharkbox.api.WithSharkboxUser;

@Sql(scripts = "/voting-system-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class VotingSystemIntegrationTest extends SharkboxApiTestBase {

    @Test
    @WithSharkboxUser
    void testGenericVotingSystemWorksForBothThreadsAndComments() throws Exception {
        // Test that the same user can vote on both threads and comments independently
        
        // Vote up on thread
        var threadUpvote = new VoteForm();
        threadUpvote.setIsUpvote(true);
        
        this.mockMvc
            .perform(patch("/api/v1/thread/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(threadUpvote)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(true));

        // Vote down on comment (different vote type)
        var commentDownvote = new VoteForm();
        commentDownvote.setIsUpvote(false);
        
        this.mockMvc
            .perform(patch("/api/v1/comment/300/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(commentDownvote)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.downvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(false));

        // Verify both votes are maintained independently
        this.mockMvc
            .perform(get("/api/v1/thread/300"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(true));

        this.mockMvc
            .perform(get("/api/v1/comment/300"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].downvotes").value(1))
            .andExpect(jsonPath("$.content[0].userVote").value(false));
    }

    @Test
    @WithSharkboxUser
    void testVoteCountsAreAccurate() throws Exception {
        // This test verifies that vote counts are properly calculated
        // and maintained across different operations
        
        var upvote = new VoteForm();
        upvote.setIsUpvote(true);
        
        var downvote = new VoteForm();
        downvote.setIsUpvote(false);

        // Initial state
        this.mockMvc
            .perform(get("/api/v1/thread/300"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(0))
            .andExpect(jsonPath("$.downvotes").value(0));

        // Add upvote
        this.mockMvc
            .perform(patch("/api/v1/thread/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvote)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.downvotes").value(0));

        // Remove upvote
        this.mockMvc
            .perform(patch("/api/v1/thread/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvote)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(0))
            .andExpect(jsonPath("$.downvotes").value(0));

        // Add downvote
        this.mockMvc
            .perform(patch("/api/v1/thread/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(downvote)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(0))
            .andExpect(jsonPath("$.downvotes").value(1));

        // Change to upvote
        this.mockMvc
            .perform(patch("/api/v1/thread/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvote)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.downvotes").value(0));
    }
}
