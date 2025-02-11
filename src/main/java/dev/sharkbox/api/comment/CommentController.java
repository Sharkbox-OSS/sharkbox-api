package dev.sharkbox.api.comment;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/comment")
@Tag(name = "Comment")
public class CommentController {
    
    private final CommentService commentService;

    CommentController(final CommentService commentService) {
        this.commentService = commentService;
    }

    // TODO this needs to be paginated
    @GetMapping("/{threadId}")
    @Operation(summary = "Retrieve comments in a thread")
    public List<Comment> retrieveComments(@PathVariable Long threadId) {
        return commentService.retrieveComments(threadId);
    }

    @PostMapping("/{threadId}")
    @Operation(summary = "Create a comment in a thread")
    public Comment createComment(@RequestBody @Valid CommentForm commentForm, @PathVariable Long threadId) {
        return commentService.createComment(commentForm, threadId);
    }

    @PutMapping("/{threadId}/{commentId}")
    @Operation(summary = "Update a comment in a thread")
    public Comment updateComment(@RequestBody @Valid CommentForm commentForm, @PathVariable Long threadId, @PathVariable Long commentId) {
        return commentService.updateComment(commentForm, threadId, commentId);
    }

    @PatchMapping("/{threadId}/{commentId}")
    @Operation(summary = "Vote on a comment")
    public Comment voteOnComment(@PathVariable Long threadId, @PathVariable Long commentId, @RequestBody @Valid CommentVoteForm commentVoteForm) {
        return commentService.voteOnComment(threadId, commentId, commentVoteForm);
    }
}
