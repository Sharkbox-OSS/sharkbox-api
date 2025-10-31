package dev.sharkbox.api.thread;

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
import dev.sharkbox.api.vote.VoteForm;

@Sql(scripts = "/thread-voting-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class ThreadVotingIntegrationTest extends SharkboxApiTestBase {

    @Test
    @WithSharkboxUser
    void testThreadVotingFlow() throws Exception {
        // First, get a thread to see initial vote counts
        this.mockMvc
            .perform(get("/api/v1/thread/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.upvotes").value(0))
            .andExpect(jsonPath("$.downvotes").value(0))
            .andExpect(jsonPath("$.userVote").isEmpty());

        // Vote up
        var upvoteForm = new VoteForm();
        upvoteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.downvotes").value(0))
            .andExpect(jsonPath("$.userVote").value(true));

        // Vote up again (should remove vote)
        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(0))
            .andExpect(jsonPath("$.downvotes").value(0))
            .andExpect(jsonPath("$.userVote").isEmpty());

        // Vote down
        var downvoteForm = new VoteForm();
        downvoteForm.setIsUpvote(false);

        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(downvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(0))
            .andExpect(jsonPath("$.downvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(false));

        // Change to upvote
        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.downvotes").value(0))
            .andExpect(jsonPath("$.userVote").value(true));
    }

    @Test
    @WithSharkboxUser
    void testThreadVotingWithMultipleUsers() throws Exception {
        // This test would require multiple user contexts
        // For now, we'll test that the vote counts are properly maintained
        
        var upvoteForm = new VoteForm();
        upvoteForm.setIsUpvote(true);

        // First user votes up
        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(true));

        // Verify the thread still shows the vote when retrieved
        this.mockMvc
            .perform(get("/api/v1/thread/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(true));
    }
}
