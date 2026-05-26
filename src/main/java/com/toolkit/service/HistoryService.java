package com.toolkit.service;

import com.toolkit.dto.request.HistoryRequest;
import com.toolkit.dto.response.HistoryListResponse;
import com.toolkit.dto.response.HistoryResponse;
import com.toolkit.entity.History;
import com.toolkit.entity.User;
import com.toolkit.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryListResponse getHistory(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<History> historyPage = historyRepository.findByUserOrderByAccessedAtDesc(user, pageable);

        List<HistoryResponse> responses = historyPage.getContent().stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());

        return HistoryListResponse.builder()
                .history(responses)
                .page(page)
                .size(size)
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .build();
    }

    @Transactional
    public HistoryResponse addHistory(User user, HistoryRequest request) {
        History history = History.builder()
                .user(user)
                .toolId(request.getToolId())
                .build();

        History savedHistory = historyRepository.save(history);

        return toHistoryResponse(savedHistory);
    }

    private HistoryResponse toHistoryResponse(History history) {
        return HistoryResponse.builder()
                .id(history.getId())
                .toolId(history.getToolId())
                .accessedAt(history.getAccessedAt())
                .build();
    }
}