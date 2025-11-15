package dev.sharkbox.api.comment;

import org.springframework.data.domain.Pageable;
import dev.sharkbox.api.common.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import dev.sharkbox.api.security.SharkboxUser;
import dev.sharkbox.api.vote.VoteForm;

@RestController
@RequestMapping("/api/v1/comment")
@Tag(name = "Comment")
public class CommentController {
    
    private final CommentService commentService;

    CommentController(final CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{threadId}")
    @Operation(summary = "Retrieve comments in a thread")
    public PageResponse<Comment> retrieveComments(@PathVariable Long threadId, Pageable pageable, @AuthenticationPrincipal SharkboxUser user) {
        return PageResponse.of(commentService.retrieveComments(threadId, pageable, user));
    }

    @PostMapping("/{threadId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Create a comment in a thread")
    public Comment createComment(@RequestBody @Valid CommentForm commentForm, @PathVariable Long threadId, @AuthenticationPrincipal SharkboxUser user) {
        return commentService.createComment(commentForm, threadId, user);
    }

    @PutMapping("/{threadId}/{commentId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Update a comment in a thread")
    public Comment updateComment(@RequestBody @Valid CommentForm commentForm, @PathVariable Long threadId, @PathVariable Long commentId, @AuthenticationPrincipal SharkboxUser user) {
        return commentService.updateComment(commentForm, threadId, commentId, user);
    }

    @PatchMapping("/{threadId}/{commentId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Vote on a comment")
    public Comment voteOnComment(@PathVariable Long threadId, @PathVariable Long commentId, @RequestBody @Valid VoteForm voteForm, @AuthenticationPrincipal SharkboxUser user) {
        return commentService.voteOnComment(threadId, commentId, voteForm, user);
    }
}
