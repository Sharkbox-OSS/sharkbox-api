package dev.sharkbox.api.box;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BoxForm {
    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    @NotBlank
    private String description;

    @NotNull
    private BoxAccess access;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BoxAccess getAccess() {
        return access;
    }

    public void setAccess(BoxAccess access) {
        this.access = access;
    }
}
