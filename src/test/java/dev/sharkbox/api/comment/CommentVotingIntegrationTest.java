package dev.sharkbox.api.comment;

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

@Sql(scripts = "/comment-voting-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class CommentVotingIntegrationTest extends SharkboxApiTestBase {

    @Test
    @WithSharkboxUser
    void testCommentVotingFlow() throws Exception {
        // First, get comments to see initial vote counts
        this.mockMvc
            .perform(get("/api/v1/comment/200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(200))
            .andExpect(jsonPath("$.content[0].upvotes").value(0))
            .andExpect(jsonPath("$.content[0].downvotes").value(0))
            .andExpect(jsonPath("$.content[0].userVote").isEmpty());

        // Vote up on comment
        var upvoteForm = new VoteForm();
        upvoteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.downvotes").value(0))
            .andExpect(jsonPath("$.userVote").value(true));

        // Vote up again (should remove vote)
        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
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
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(downvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(0))
            .andExpect(jsonPath("$.downvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(false));

        // Change to upvote
        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.downvotes").value(0))
            .andExpect(jsonPath("$.userVote").value(true));
    }

    @Test
    @WithSharkboxUser
    void testCommentVotingWithMultipleComments() throws Exception {
        var upvoteForm = new VoteForm();
        upvoteForm.setIsUpvote(true);

        // Vote on first comment
        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(true));

        // Vote on second comment (if it exists)
        this.mockMvc
            .perform(patch("/api/v1/comment/200/201")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upvoteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.upvotes").value(1))
            .andExpect(jsonPath("$.userVote").value(true));

        // Verify both comments show votes when retrieved
        this.mockMvc
            .perform(get("/api/v1/comment/200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].upvotes").value(1))
            .andExpect(jsonPath("$.content[0].userVote").value(true))
            .andExpect(jsonPath("$.content[1].upvotes").value(1))
            .andExpect(jsonPath("$.content[1].userVote").value(true));
    }
}
