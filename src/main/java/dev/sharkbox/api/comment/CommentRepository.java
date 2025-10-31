package dev.sharkbox.api.comment;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByThreadId(Long threadId, Pageable pageable);
}
