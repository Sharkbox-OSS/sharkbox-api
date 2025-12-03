package dev.sharkbox.api.user;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sharkbox.api.comment.Comment;
import dev.sharkbox.api.comment.CommentService;
import dev.sharkbox.api.common.PageResponse;
import dev.sharkbox.api.security.SharkboxUser;
import dev.sharkbox.api.thread.Thread;
import dev.sharkbox.api.thread.ThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserController {

    private final ThreadService threadService;
    private final CommentService commentService;

    public UserController(ThreadService threadService, CommentService commentService) {
        this.threadService = threadService;
        this.commentService = commentService;
    }

    @GetMapping("/{username}/threads")
    @Operation(summary = "Retrieve threads by user")
    public PageResponse<Thread> retrieveThreadsByUser(@PathVariable String username, Pageable pageable,
            @AuthenticationPrincipal SharkboxUser user) {
        return PageResponse.of(threadService.retrieveThreadsByUser(username, pageable, user));
    }

    @GetMapping("/{username}/comments")
    @Operation(summary = "Retrieve comments by user")
    public PageResponse<Comment> retrieveCommentsByUser(@PathVariable String username, Pageable pageable,
            @AuthenticationPrincipal SharkboxUser user) {
        return PageResponse.of(commentService.retrieveCommentsByUser(username, pageable, user));
    }
}
