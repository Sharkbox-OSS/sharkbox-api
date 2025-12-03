package dev.sharkbox.api.comment;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import dev.sharkbox.api.SharkboxApiTestBase;
import dev.sharkbox.api.box.Box;
import dev.sharkbox.api.box.BoxAccess;
import dev.sharkbox.api.thread.Thread;
import dev.sharkbox.api.thread.ThreadType;
import jakarta.persistence.EntityManager;

class CommentRepositoryTest extends SharkboxApiTestBase {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void findByThread_Id_shouldReturnComments() {
        var box = new Box();
        box.setSlug("test-box");
        box.setName("Test Box");
        box.setOwner("creator");
        box.setAccess(BoxAccess.PUBLIC);
        box.setDescription("Test Description");
        box.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(box);

        var thread = new Thread();
        thread.setTitle("Test Thread");
        thread.setContent("Test Content");
        thread.setType(ThreadType.TEXT);
        thread.setBox(box);
        thread.setUserId("creator");
        thread.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(thread);

        var comment = new Comment();
        comment.setContent("Test Comment");
        comment.setThread(thread);
        comment.setUserId("commenter");
        comment.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(comment);

        var result = commentRepository.findByThread_Id(thread.getId(), Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getThreadTitle()).isEqualTo("Test Thread");
        assertThat(result.getContent().get(0).getThreadBoxSlug()).isEqualTo("test-box");
    }

    @Test
    @Transactional
    void findByUserId_shouldReturnCommentsWithThreadDetails() {
        var box = new Box();
        box.setSlug("user-box");
        box.setName("User Box");
        box.setOwner("creator");
        box.setAccess(BoxAccess.PUBLIC);
        box.setDescription("User Description");
        box.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(box);

        var thread = new Thread();
        thread.setTitle("User Thread");
        thread.setContent("User Content");
        thread.setType(ThreadType.TEXT);
        thread.setBox(box);
        thread.setUserId("creator");
        thread.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(thread);

        var comment = new Comment();
        comment.setContent("User Comment");
        comment.setThread(thread);
        comment.setUserId("commenter");
        comment.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(comment);

        var result = commentRepository.findByUserId("commenter", Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getThreadTitle()).isEqualTo("User Thread");
        assertThat(result.getContent().get(0).getThreadBoxSlug()).isEqualTo("user-box");
    }
}
