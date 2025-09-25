package dev.sharkbox.api.thread;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.sharkbox.api.box.BoxService;
import jakarta.transaction.Transactional;

@Service
public class ThreadService {
    
    private final ThreadRepository threadRepository;
    private final BoxService boxService;

    ThreadService(ThreadRepository threadRepository, BoxService boxService) {
        this.threadRepository = threadRepository;
        this.boxService = boxService;
    }

    @Transactional
    public Optional<Thread> retrieveThread(Long id) {
        // TODO permissions
        return threadRepository.findById(id);
    }

    @Transactional
    public Page<Thread> retrieveThreads(String boxSlug, Pageable pageable) {
        return boxService.retrieveBox(boxSlug).map(box -> threadRepository.findByBox(box, pageable)).orElseThrow();
    }

    Thread createThread(ThreadForm form, String boxSlug) {
        // TODO need to make sure we are allowed to create a thread in this box
        return boxService.retrieveBox(boxSlug).map(box -> {
            var thread = new Thread();
            thread.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            thread.setContent(form.getContent());
            thread.setTitle(form.getTitle());
            thread.setType(form.getType());
            thread.setBox(box);
            // TODO user auth
            thread.setUserId(1L);
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

    Thread voteOnThread(Long id, ThreadVoteForm form) {
        // TODO implement voting
        // Make sure you can't vote more than once
        throw new NotImplementedException();
    }
}
