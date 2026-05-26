package com.toolkit.service;

import com.toolkit.dto.request.FavoriteRequest;
import com.toolkit.dto.response.FavoriteListResponse;
import com.toolkit.dto.response.FavoriteResponse;
import com.toolkit.entity.Favorite;
import com.toolkit.entity.User;
import com.toolkit.exception.ApiException;
import com.toolkit.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteListResponse getFavorites(User user) {
        List<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user);

        List<FavoriteResponse> responses = favorites.stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());

        return FavoriteListResponse.builder()
                .favorites(responses)
                .build();
    }

    @Transactional
    public FavoriteResponse addFavorite(User user, FavoriteRequest request) {
        if (favoriteRepository.existsByUserAndToolId(user, request.getToolId())) {
            throw new ApiException("Tool already in favorites", HttpStatus.CONFLICT);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .toolId(request.getToolId())
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);

        return toFavoriteResponse(savedFavorite);
    }

    @Transactional
    public void removeFavorite(User user, String toolId) {
        favoriteRepository.deleteByUserAndToolId(user, toolId);
    }

    private FavoriteResponse toFavoriteResponse(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .toolId(favorite.getToolId())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}