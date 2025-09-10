package com.hp.hospin.hospital.domain.type;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements
) {
    public int totalPages() {
        return (int) Math.ceil((double) totalElements / pageSize);
    }
}
