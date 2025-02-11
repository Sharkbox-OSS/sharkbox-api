package dev.sharkbox.api.box;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/box")
@Tag(name = "Box")
public class BoxController {

    private final BoxService boxService;

    BoxController(final BoxService boxService) {
        this.boxService = boxService;
    }
    
    @GetMapping
    @Operation(summary = "Retrieve all boxes")
    public List<Box> retrieveBoxes() {
        return boxService.retrieveBoxes();
    }

    @PostMapping
    @Operation(summary = "Create a box")
    public Box createBox(@RequestBody @Valid BoxForm form) {
        return boxService.createBox(form);
    }

    @PutMapping("/{slug}")
    @Operation(summary = "Update a box")
    public Box putMethodName(@PathVariable String slug, @RequestBody @Valid BoxForm form) {
        return boxService.updateBox(slug, form);
    }
}
