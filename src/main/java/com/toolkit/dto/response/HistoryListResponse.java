package com.toolkit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryListResponse {

    private List<HistoryResponse> history;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}