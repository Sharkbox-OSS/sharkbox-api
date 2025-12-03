package dev.sharkbox.api.comment;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sharkbox.api.exception.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import dev.sharkbox.api.security.SharkboxUser;
import dev.sharkbox.api.thread.ThreadService;
import dev.sharkbox.api.vote.VoteForm;
import dev.sharkbox.api.vote.VoteService;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ThreadService threadService;
    private final VoteService voteService;

    CommentService(CommentRepository commentRepository, ThreadService threadService, VoteService voteService) {
        this.commentRepository = commentRepository;
        this.threadService = threadService;
        this.voteService = voteService;
    }

    @Transactional(readOnly = true)
    public Page<Comment> retrieveComments(Long threadId, Pageable pageable, SharkboxUser user) {
        Page<Comment> comments = commentRepository.findByThread_Id(threadId, pageable);
        return comments.map(
                comment -> user != null ? voteService.populateVoteData(comment, "comment", user.getUserId()) : comment);
    }

    @Transactional(readOnly = true)
    public Page<Comment> retrieveCommentsByUser(String userId, Pageable pageable, SharkboxUser user) {
        Page<Comment> comments = commentRepository.findByUserId(userId, pageable);
        return comments.map(
                comment -> user != null ? voteService.populateVoteData(comment, "comment", user.getUserId()) : comment);
    }

    public Comment createComment(CommentForm commentForm, Long threadId, SharkboxUser user) {
        return threadService.retrieveThread(threadId, user)
                .map(thread -> {
                    Comment comment = new Comment();
                    comment.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                    comment.setThread(thread);
                    // TODO parent checking?
                    comment.setParentId(commentForm.getParentId());
                    comment.setContent(commentForm.getContent());
                    comment.setUserId(user.getUserId());
                    return commentRepository.save(comment);
                })
                .orElseThrow();
    }

    // TODO permissions checking
    public Comment updateComment(CommentForm commentForm, Long threadId, Long commentId, SharkboxUser user) {
        return commentRepository.findById(commentId)
                .map(comment -> {
                    if (!comment.getUserId().equals(user.getUserId())) {
                        throw new AccessDeniedException("You are not the owner of this comment");
                    }
                    comment.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                    // TODO parent checking?
                    comment.setParentId(commentForm.getParentId());
                    comment.setContent(commentForm.getContent());
                    return commentRepository.save(comment);
                })
                .orElseThrow();
    }

    @Transactional
    public Comment voteOnComment(Long threadId, Long commentId, VoteForm voteForm, SharkboxUser user) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment", commentId));

        return voteService.voteOnEntity(comment, "comment", voteForm, user.getUserId());
    }

}
