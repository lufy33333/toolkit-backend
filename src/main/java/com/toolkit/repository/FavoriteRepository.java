package com.toolkit.repository;

import com.toolkit.entity.Favorite;
import com.toolkit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    List<Favorite> findByUserOrderByCreatedAtDesc(User user);

    Optional<Favorite> findByUserAndToolId(User user, String toolId);

    boolean existsByUserAndToolId(User user, String toolId);

    void deleteByUserAndToolId(User user, String toolId);
}