package dev.sharkbox.api.thread;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sharkbox.api.box.BoxService;
import dev.sharkbox.api.exception.EntityNotFoundException;
import dev.sharkbox.api.security.SharkboxUser;
import dev.sharkbox.api.vote.VoteForm;
import dev.sharkbox.api.vote.VoteService;

@Service
public class ThreadService {

    private final ThreadRepository threadRepository;
    private final VoteService voteService;
    private final BoxService boxService;

    ThreadService(ThreadRepository threadRepository, VoteService voteService, BoxService boxService) {
        this.threadRepository = threadRepository;
        this.voteService = voteService;
        this.boxService = boxService;
    }

    @Transactional
    public Optional<Thread> retrieveThread(Long id, SharkboxUser user) {
        return threadRepository.findById(id).map(
                thread -> user != null ? voteService.populateVoteData(thread, "thread", user.getUserId()) : thread);
    }

    @Transactional
    public Page<Thread> retrieveThreads(String boxSlug, Pageable pageable, SharkboxUser user) {
        return boxService.retrieveBox(boxSlug).map(box -> threadRepository.findByBox(box, pageable)
                .map(thread -> user != null
                        ? voteService.populateVoteData(thread, "thread", user.getUserId())
                        : thread))
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public Page<Thread> retrieveThreadsByUser(String userId, Pageable pageable, SharkboxUser user) {
        return threadRepository.findByUserId(userId, pageable)
                .map(thread -> user != null
                        ? voteService.populateVoteData(thread, "thread", user.getUserId())
                        : thread);
    }

    Thread createThread(ThreadForm form, String boxSlug, SharkboxUser user) {
        // TODO need to make sure we are allowed to create a thread in this box
        return boxService.retrieveBox(boxSlug).map(box -> {
            var thread = new Thread();
            thread.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            thread.setContent(form.getContent());
            thread.setTitle(form.getTitle());
            thread.setType(form.getType());
            thread.setBox(box);
            thread.setUserId(user.getUserId());
            return threadRepository.save(thread);
        }).orElseThrow();
    }

    Thread updateThread(ThreadForm form, String boxSlug, Long id, SharkboxUser user) {
        return threadRepository.findById(id).map(thread -> {
            if (!thread.getUserId().equals(user.getUserId())) {
                throw new AccessDeniedException("You are not the owner of this thread");
            }
            thread.setContent(form.getContent());
            thread.setTitle(form.getTitle());
            thread.setType(form.getType());
            thread.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            return threadRepository.save(thread);
        }).orElseThrow();
    }

    @Transactional
    public Thread voteOnThread(Long id, VoteForm form, SharkboxUser user) {
        var thread = threadRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Thread", id));

        return voteService.voteOnEntity(thread, "thread", form, user.getUserId());
    }

}
