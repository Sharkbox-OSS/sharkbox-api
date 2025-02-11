package dev.sharkbox.api.box;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface BoxRepository extends JpaRepository<Box, Long> {
    Optional<Box> findBySlug(String slug);
}
