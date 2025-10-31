package dev.sharkbox.api.thread;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.sharkbox.api.box.BoxService;
import dev.sharkbox.api.exception.EntityNotFoundException;
import dev.sharkbox.api.security.SharkboxUser;
import dev.sharkbox.api.vote.VoteForm;
import dev.sharkbox.api.vote.VoteService;
import jakarta.transaction.Transactional;

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
        // TODO permissions
        return threadRepository.findById(id).map(thread -> 
            voteService.populateVoteData(thread, "thread", user.getUserId()));
    }

    @Transactional
    public Page<Thread> retrieveThreads(String boxSlug, Pageable pageable, SharkboxUser user) {
        return boxService.retrieveBox(boxSlug).map(box -> threadRepository.findByBox(box, pageable)
            .map(thread -> voteService.populateVoteData(thread, "thread", user.getUserId()))
        ).orElseThrow();
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

    Thread updateThread(ThreadForm form, String boxSlug, Long id) {
        // TODO need to make sure we are allowed to update this thread
        return threadRepository.findById(id).map(thread -> {
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
