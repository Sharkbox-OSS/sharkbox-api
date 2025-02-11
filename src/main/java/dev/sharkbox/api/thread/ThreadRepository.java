package dev.sharkbox.api.thread;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import dev.sharkbox.api.box.Box;


interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByBox(Box box);
}
