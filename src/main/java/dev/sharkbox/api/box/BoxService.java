package dev.sharkbox.api.box;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.sharkbox.api.security.SharkboxUser;

@Service
public class BoxService {
    
    private final BoxRepository boxRepository;

    BoxService(final BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    List<Box> retrieveBoxes() {
        return boxRepository.findAll();
    }

    public Optional<Box> retrieveBox(Long id) {
        // TODO verify access
        return boxRepository.findById(id);
    }

    public Optional<Box> retrieveBox(String slug) {
        // TODO verify access
        return boxRepository.findBySlug(slug);
    }

    Box createBox(BoxForm form, SharkboxUser user) {
        var box = new Box();
        box.setName(form.getName());
        box.setSlug(form.getSlug().toLowerCase());
        box.setDescription(form.getDescription());
        box.setAccess(form.getAccess());
        box.setCreatedAt(OffsetDateTime.now((ZoneOffset.UTC)));
        box.setOwner(user.getUsername());
        // TODO throw 400 if slug already exists
        return boxRepository.save(box);
    }

    Box updateBox(String slug, BoxForm form) {
        var box = boxRepository.findBySlug(slug).orElseThrow();
        box.setName(form.getName());
        box.setDescription(form.getDescription());
        box.setAccess(form.getAccess());
        box.setUpdatedAt(OffsetDateTime.now((ZoneOffset.UTC)));
        return boxRepository.save(box);
    }
}
