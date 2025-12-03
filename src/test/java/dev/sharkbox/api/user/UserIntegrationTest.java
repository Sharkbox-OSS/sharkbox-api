package dev.sharkbox.api.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import dev.sharkbox.api.WithSharkboxUser;
import dev.sharkbox.api.SharkboxApiTestBase;

class UserIntegrationTest extends SharkboxApiTestBase {

    @Test
    @WithSharkboxUser(username = "quantas", userId = "quantas")
    void retrieveThreadsByUser_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/user/quantas/threads")
                .param("page", "0")
                .param("size", "20")
                .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());
    }

    @Test
    @WithSharkboxUser(username = "quantas", userId = "quantas")
    void retrieveCommentsByUser_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/user/quantas/comments")
                .param("page", "0")
                .param("size", "20")
                .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());
        // Note: We can't easily assert content here without setting up complex data,
        // but CommentRepositoryTest verifies the mapping.
    }
}
