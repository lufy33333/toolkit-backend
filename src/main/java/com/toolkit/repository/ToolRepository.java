package com.toolkit.repository;

import com.toolkit.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ToolRepository extends JpaRepository<Tool, UUID> {

    Optional<Tool> findByPath(String path);

    boolean existsByPath(String path);
}