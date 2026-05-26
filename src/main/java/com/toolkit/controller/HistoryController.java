package com.toolkit.controller;

import com.toolkit.dto.request.HistoryRequest;
import com.toolkit.dto.response.HistoryListResponse;
import com.toolkit.dto.response.HistoryResponse;
import com.toolkit.entity.User;
import com.toolkit.repository.UserRepository;
import com.toolkit.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<HistoryListResponse> getHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(historyService.getHistory(user, page, size));
    }

    @PostMapping
    public ResponseEntity<HistoryResponse> addHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody HistoryRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        HistoryResponse response = historyService.addHistory(user, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}