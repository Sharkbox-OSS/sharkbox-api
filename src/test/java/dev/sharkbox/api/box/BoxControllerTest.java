package dev.sharkbox.api.box;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import dev.sharkbox.api.SharkboxApiTestBase;
import dev.sharkbox.api.WithSharkboxUser;

class BoxControllerTest extends SharkboxApiTestBase {

    @Test
    void testBoxCreateNotAuth() throws Exception {
        var boxForm = new BoxForm();
        boxForm.setAccess(BoxAccess.PUBLIC);
        boxForm.setSlug("test");
        boxForm.setName("Test");
        boxForm.setDescription("Test");

        this.mockMvc
            .perform(post("/api/v1/box")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(boxForm)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithSharkboxUser
    void testBoxCreateAuthorized() throws Exception {
        var boxForm = new BoxForm();
        boxForm.setAccess(BoxAccess.PUBLIC);
        boxForm.setSlug("test");
        boxForm.setName("Test");
        boxForm.setDescription("Test");

        this.mockMvc
            .perform(post("/api/v1/box")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(boxForm)))
            .andExpect(status().isCreated());
    }
}
