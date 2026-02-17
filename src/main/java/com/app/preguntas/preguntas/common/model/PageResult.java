package com.app.preguntas.preguntas.common.model;

import java.util.Collections;
import java.util.List;

/**
 * Generic pagination result for in-memory stores.
 */
public class PageResult<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PageResult(List<T> allItems, int page, int size) {
        this.size = Math.max(size, 1);
        this.totalElements = allItems.size();
        this.totalPages = (int) Math.ceil((double) totalElements / this.size);
        this.page = Math.max(0, Math.min(page, Math.max(totalPages - 1, 0)));

        int fromIndex = this.page * this.size;
        if (fromIndex >= allItems.size()) {
            this.content = Collections.emptyList();
        } else {
            int toIndex = Math.min(fromIndex + this.size, allItems.size());
            this.content = allItems.subList(fromIndex, toIndex);
        }
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPrevious() {
        return page > 0;
    }

    public boolean isHasNext() {
        return page < totalPages - 1;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    /** 1-based page number for display */
    public int getDisplayPage() {
        return page + 1;
    }
}
