package dev.sharkbox.api.thread;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import dev.sharkbox.api.SharkboxApiTestBase;
import dev.sharkbox.api.box.Box;

class ThreadRepositoryTest extends SharkboxApiTestBase {

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void findByUserId_shouldReturnThreadsForGivenUser() {
        // Given
        var box = new Box();
        box.setSlug("test-box");
        box.setName("Test Box");
        box.setOwner("creator");
        box.setAccess(dev.sharkbox.api.box.BoxAccess.PUBLIC);
        box.setDescription("Test Description");
        box.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(box);

        var thread1 = new Thread();
        thread1.setTitle("Thread 1");
        thread1.setContent("Content 1");
        thread1.setType(ThreadType.TEXT);
        thread1.setBox(box);
        thread1.setUserId("user1");
        thread1.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(thread1);

        var thread2 = new Thread();
        thread2.setTitle("Thread 2");
        thread2.setContent("Content 2");
        thread2.setType(ThreadType.TEXT);
        thread2.setBox(box);
        thread2.setUserId("user2");
        thread2.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(thread2);

        entityManager.flush();

        // When
        var result = threadRepository.findByUserId("user1", Pageable.unpaged());

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo("user1");
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Thread 1");
    }
}
