package com.toolkit.repository;

import com.toolkit.entity.History;
import com.toolkit.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {

    Page<History> findByUserOrderByAccessedAtDesc(User user, Pageable pageable);
}