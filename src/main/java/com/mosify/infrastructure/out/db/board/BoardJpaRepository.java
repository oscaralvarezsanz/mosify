package com.mosify.infrastructure.out.db.board;

import com.mosify.infrastructure.out.db.model.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BoardJpaRepository extends JpaRepository<BoardEntity, UUID> {
}
