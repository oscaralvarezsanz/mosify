package com.mosify.infrastructure.out.db.board;

import com.mosify.infrastructure.out.db.model.BoardUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardUserJpaRepository extends JpaRepository<BoardUserEntity, UUID> {
    List<BoardUserEntity> findAllByBoardId(UUID boardId);
    List<BoardUserEntity> findAllByUserId(UUID userId);
    Optional<BoardUserEntity> findByBoardIdAndUserId(UUID boardId, UUID userId);
    void deleteByBoardIdAndUserId(UUID boardId, UUID userId);
    void deleteAllByBoardId(UUID boardId);
    void deleteAllByUserId(UUID userId);
}
