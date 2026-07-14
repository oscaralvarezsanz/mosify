package com.mosify.infrastructure.out.db.board;

import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.domain.model.BoardUser;
import com.mosify.infrastructure.out.db.board.mapper.BoardUserEntityConverter;
import com.mosify.infrastructure.out.db.model.BoardUserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BoardUserPersistenceAdapter implements BoardUserRepository {

    private final BoardUserJpaRepository jpaRepository;
    private final BoardUserEntityConverter entityConverter;

    public BoardUserPersistenceAdapter(BoardUserJpaRepository jpaRepository, BoardUserEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public BoardUser save(BoardUser boardUser) {
        BoardUserEntity entity = entityConverter.toEntity(boardUser);
        BoardUserEntity saved = jpaRepository.save(entity);
        return entityConverter.toDomain(saved);
    }

    @Override
    public List<BoardUser> findAllByBoardId(UUID boardId) {
        return jpaRepository.findAllByBoardId(boardId).stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    @Override
    public List<BoardUser> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByUserId(userId).stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    @Override
    public Optional<BoardUser> findByBoardIdAndUserId(UUID boardId, UUID userId) {
        return jpaRepository.findByBoardIdAndUserId(boardId, userId).map(entityConverter::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByBoardIdAndUserId(UUID boardId, UUID userId) {
        jpaRepository.deleteByBoardIdAndUserId(boardId, userId);
    }

    @Override
    @Transactional
    public void deleteAllByBoardId(UUID boardId) {
        jpaRepository.deleteAllByBoardId(boardId);
    }

    @Override
    @Transactional
    public void deleteAllByUserId(UUID userId) {
        jpaRepository.deleteAllByUserId(userId);
    }
}
