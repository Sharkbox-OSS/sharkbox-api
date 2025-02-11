package dev.sharkbox.api.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByThreadId(Long threadId);
}
