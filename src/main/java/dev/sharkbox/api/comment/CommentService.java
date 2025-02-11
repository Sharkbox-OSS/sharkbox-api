package dev.sharkbox.api.comment;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import dev.sharkbox.api.thread.ThreadService;

@Service
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final ThreadService threadService;

    CommentService(CommentRepository commentRepository, ThreadService threadService) {
        this.commentRepository = commentRepository;
        this.threadService = threadService;
    }

    public List<Comment> retrieveComments(Long threadId) {
        return commentRepository.findByThreadId(threadId);
    }

    public Comment createComment(CommentForm commentForm, Long threadId) {
        return threadService.retrieveThread(threadId)
            .map(thread -> {
                Comment comment = new Comment();
                comment.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                comment.setThreadId(threadId);
                // TODO parent checking?
                comment.setParentId(commentForm.getParentId());
                comment.setContent(commentForm.getContent());
                // TODO user authentication
                comment.setUserId(1L);
                return commentRepository.save(comment);
            })
            .orElseThrow();
    }

    public Comment updateComment(CommentForm commentForm, Long threadId, Long commentId) {
        // TODO user authentication
        return commentRepository.findById(commentId)
            .map(comment -> {
                comment.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                // TODO parent checking?
                comment.setParentId(commentForm.getParentId());
                comment.setContent(commentForm.getContent());
                return commentRepository.save(comment);
            })
            .orElseThrow();
    }

    Comment voteOnComment(Long threadId, Long commentId, CommentVoteForm commentVoteForm) {
        // TODO implement voting
        // Make sure you can't vote more than once
        throw new NotImplementedException();
    }
}
