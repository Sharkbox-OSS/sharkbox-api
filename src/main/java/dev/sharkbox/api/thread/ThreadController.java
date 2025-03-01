package dev.sharkbox.api.thread;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    public Optional<Thread> retrieveThread(@PathVariable Long id) {
        return threadService.retrieveThread(id);
    }

    @PostMapping("/box/{slug}/thread")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Create a thread in a box")
    public Thread createThread(@RequestBody @Valid ThreadForm form, @PathVariable String slug) {
        return threadService.createThread(form, slug);
    }

    @PutMapping("/thread/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Update a thread in a box")
    public Thread updateThread(@RequestBody @Valid ThreadForm form, @PathVariable String slug, @PathVariable Long id) {
        return threadService.updateThread(form, slug, id);
    }

    // TODO this needs to be paginated
    @GetMapping("/box/{slug}/threads")
    @Operation(summary = "Retrieve threads in a box")
    List<Thread> retrieveThreads(@PathVariable String slug) {
        return threadService.retrieveThreads(slug);
    }

    @PatchMapping("/thread/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Vote on a thread")
    public Thread voteOnThread(@PathVariable Long id, @RequestBody @Valid ThreadVoteForm form) {
        return threadService.voteOnThread(id, form);
    }
}
