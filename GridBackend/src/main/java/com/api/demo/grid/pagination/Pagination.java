package com.api.demo.grid.pagination;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;

import java.util.List;

public class Pagination<T> {

    public Page<T> convertToPage(List<T> data, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        int total = data.size();
        int start = Math.toIntExact(pageRequest.getOffset());
        int end = Math.min((start + pageRequest.getPageSize()), total);

        if (start > end)
            return new PageImpl<>(new ArrayList<>(), pageRequest, total);

        return new PageImpl<>(data.subList(start, end), pageRequest, total);
    }


}