package dev.sharkbox.api.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A wrapper class for Spring Page objects that provides a consistent JSON structure
 * while maintaining backward compatibility with existing API clients.
 * 
 * @param <T> The type of the content items
 */
public class PageResponse<T> {
    private final Page<T> page;

    public PageResponse(Page<T> page) {
        this.page = page;
    }

    @JsonProperty("content")
    public java.util.List<T> getContent() {
        return page.getContent();
    }

    @JsonProperty("pageable")
    public Pageable getPageable() {
        return page.getPageable();
    }

    @JsonProperty("totalPages")
    public int getTotalPages() {
        return page.getTotalPages();
    }

    @JsonProperty("totalElements")
    public long getTotalElements() {
        return page.getTotalElements();
    }

    @JsonProperty("size")
    public int getSize() {
        return page.getSize();
    }

    @JsonProperty("number")
    public int getNumber() {
        return page.getNumber();
    }

    @JsonProperty("numberOfElements")
    public int getNumberOfElements() {
        return page.getNumberOfElements();
    }

    @JsonProperty("first")
    public boolean isFirst() {
        return page.isFirst();
    }

    @JsonProperty("last")
    public boolean isLast() {
        return page.isLast();
    }

    @JsonProperty("empty")
    public boolean isEmpty() {
        return page.isEmpty();
    }

    /**
     * Static factory method to wrap a Page object in a PageResponse.
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page);
    }
}