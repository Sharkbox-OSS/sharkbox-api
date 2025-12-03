package dev.sharkbox.api.thread;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Optional;

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

import dev.sharkbox.api.security.SharkboxUser;
import dev.sharkbox.api.vote.VoteForm;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Thread")
public class ThreadController {

    private ThreadService threadService;

    ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping("/thread/{id}")
    @Operation(summary = "Retrieve a thread by ID")
    public Optional<Thread> retrieveThread(@PathVariable Long id, @AuthenticationPrincipal SharkboxUser user) {
        return threadService.retrieveThread(id, user);
    }

    @PostMapping("/box/{slug}/thread")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Create a thread in a box")
    public Thread createThread(@RequestBody @Valid ThreadForm form, @PathVariable String slug,
            @AuthenticationPrincipal SharkboxUser user) {
        return threadService.createThread(form, slug, user);
    }

    @PutMapping("/thread/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Update a thread")
    public Thread updateThread(@RequestBody @Valid ThreadForm form, @PathVariable Long id,
            @AuthenticationPrincipal SharkboxUser user) {
        return threadService.updateThread(form, null, id, user);
    }

    @GetMapping("/box/{slug}/threads")
    @Operation(summary = "Retrieve threads in a box")
    public PageResponse<Thread> retrieveThreads(@PathVariable String slug, Pageable pageable,
            @AuthenticationPrincipal SharkboxUser user) {
        return PageResponse.of(threadService.retrieveThreads(slug, pageable, user));
    }

    @PatchMapping("/thread/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Vote on a thread")
    public Thread voteOnThread(@PathVariable Long id, @RequestBody @Valid VoteForm form,
            @AuthenticationPrincipal SharkboxUser user) {
        return threadService.voteOnThread(id, form, user);
    }
}
