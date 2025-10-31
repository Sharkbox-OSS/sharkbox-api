package dev.sharkbox.api.comment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class CommentVotingTest extends SharkboxApiTestBase {

    @Test
    void testVoteOnCommentNotAuthenticated() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithSharkboxUser
    void testVoteOnCommentUpvote() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(200))
            .andExpect(jsonPath("$.upvotes").exists())
            .andExpect(jsonPath("$.downvotes").exists())
            .andExpect(jsonPath("$.userVote").value(true));
    }

    @Test
    @WithSharkboxUser
    void testVoteOnCommentDownvote() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(false);

        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(200))
            .andExpect(jsonPath("$.upvotes").exists())
            .andExpect(jsonPath("$.downvotes").exists())
            .andExpect(jsonPath("$.userVote").value(false));
    }

    @Test
    @WithSharkboxUser
    void testVoteOnCommentWithInvalidId() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/comment/200/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithSharkboxUser
    void testVoteOnCommentWithNullVote() throws Exception {
        var voteForm = new VoteForm();
        // isUpvote is null

        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithSharkboxUser
    void testVoteOnCommentToggleVote() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        // First vote - upvote
        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userVote").value(true));

        // Second vote - same upvote (should remove vote)
        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userVote").isEmpty());

        // Third vote - downvote (should create new vote)
        voteForm.setIsUpvote(false);
        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userVote").value(false));
    }

    @Test
    @WithSharkboxUser
    void testVoteOnCommentChangeVote() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        // First vote - upvote
        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userVote").value(true));

        // Change vote to downvote
        voteForm.setIsUpvote(false);
        this.mockMvc
            .perform(patch("/api/v1/comment/200/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userVote").value(false));
    }

    @Test
    @WithSharkboxUser
    void testVoteOnOwnCommentNotAllowed() throws Exception {
        // First create a comment owned by the test user
        var commentForm = new CommentForm();
        commentForm.setContent("This is my own comment");

        var response = this.mockMvc
            .perform(post("/api/v1/comment/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(commentForm)))
            .andExpect(status().isCreated())
            .andReturn();

        // Extract the created comment ID from the response
        var responseBody = response.getResponse().getContentAsString();
        var jsonNode = this.objectMapper.readTree(responseBody);
        var commentId = jsonNode.get("id").asLong();

        // Now try to vote on the comment we just created (should fail)
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/comment/200/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").value("Users cannot vote on their own comments"));
    }
}
