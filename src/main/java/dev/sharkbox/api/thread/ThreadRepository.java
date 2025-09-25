package dev.sharkbox.api.thread;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.sharkbox.api.box.Box;


interface ThreadRepository extends JpaRepository<Thread, Long> {
    Page<Thread> findByBox(Box box, Pageable pageable);
}
