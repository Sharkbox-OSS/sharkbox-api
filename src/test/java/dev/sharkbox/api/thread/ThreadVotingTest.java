package dev.sharkbox.api.thread;

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

@Sql(scripts = "/thread-voting-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class ThreadVotingTest extends SharkboxApiTestBase {

    @Test
    void testVoteOnThreadNotAuthenticated() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithSharkboxUser
    void testVoteOnThreadUpvote() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.upvotes").exists())
            .andExpect(jsonPath("$.downvotes").exists())
            .andExpect(jsonPath("$.userVote").value(true));
    }

    @Test
    @WithSharkboxUser
    void testVoteOnThreadDownvote() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(false);

        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.upvotes").exists())
            .andExpect(jsonPath("$.downvotes").exists())
            .andExpect(jsonPath("$.userVote").value(false));
    }

    @Test
    @WithSharkboxUser
    void testVoteOnThreadWithInvalidId() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/thread/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithSharkboxUser
    void testVoteOnThreadWithNullVote() throws Exception {
        var voteForm = new VoteForm();
        // isUpvote is null

        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithSharkboxUser
    void testVoteOnThreadToggleVote() throws Exception {
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        // First vote - upvote
        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userVote").value(true));

        // Second vote - same upvote (should remove vote)
        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userVote").isEmpty());

        // Third vote - downvote (should create new vote)
        voteForm.setIsUpvote(false);
        this.mockMvc
            .perform(patch("/api/v1/thread/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userVote").value(false));
    }

    @Test
    @WithSharkboxUser
    void testVoteOnOwnThreadNotAllowed() throws Exception {
        // First create a thread owned by the test user
        var threadForm = new ThreadForm();
        threadForm.setTitle("My Own Thread");
        threadForm.setContent("This is my own thread content");
        threadForm.setType(ThreadType.TEXT);

        var response = this.mockMvc
            .perform(post("/api/v1/box/thread-test-box/thread")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(threadForm)))
            .andExpect(status().isCreated())
            .andReturn();

        // Extract the created thread ID from the response
        var responseBody = response.getResponse().getContentAsString();
        var jsonNode = this.objectMapper.readTree(responseBody);
        var threadId = jsonNode.get("id").asLong();

        // Now try to vote on the thread we just created (should fail)
        var voteForm = new VoteForm();
        voteForm.setIsUpvote(true);

        this.mockMvc
            .perform(patch("/api/v1/thread/" + threadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(voteForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").value("Users cannot vote on their own threads"));
    }
}
