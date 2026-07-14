package com.mosify.infrastructure.out.db.board;

import com.mosify.application.port.out.board.BoardRepository;
import com.mosify.domain.model.Board;
import com.mosify.infrastructure.out.db.board.mapper.BoardEntityConverter;
import com.mosify.infrastructure.out.db.model.BoardEntity;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BoardPersistenceAdapter implements BoardRepository {

    private final BoardJpaRepository jpaRepository;
    private final BoardEntityConverter entityConverter;

    public BoardPersistenceAdapter(BoardJpaRepository jpaRepository, BoardEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public Board save(Board board) {
        BoardEntity entity = entityConverter.toEntity(board);
        BoardEntity saved = jpaRepository.save(entity);
        return entityConverter.toDomain(saved);
    }

    @Override
    public Optional<Board> findById(UUID id) {
        return jpaRepository.findById(id).map(entityConverter::toDomain);
    }

    @Override
    public List<Board> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
