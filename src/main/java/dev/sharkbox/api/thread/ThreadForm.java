package dev.sharkbox.api.thread;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ThreadForm {
    @NotNull
    private String title;

    @NotNull
    private ThreadType type;

    @NotBlank
    private String content;

    private Long boxId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ThreadType getType() {
        return type;
    }

    public void setType(ThreadType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getBoxId() {
        return boxId;
    }

    public void setBoxId(Long boxId) {
        this.boxId = boxId;
    }
}
