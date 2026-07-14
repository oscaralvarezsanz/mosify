package com.mosify.application.service;

import com.mosify.application.port.in.category.CategoryCreatePort;
import com.mosify.application.port.in.category.CategoryDeletePort;
import com.mosify.application.port.in.category.CategoryGetAllPort;
import com.mosify.application.port.in.category.CategoryGetByIdPort;
import com.mosify.application.port.out.board.BoardRepository;
import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.BoardUser;
import com.mosify.domain.model.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService implements CategoryCreatePort, CategoryGetByIdPort, CategoryGetAllPort, CategoryDeletePort {

    private final CategoryRepository categoryRepository;
    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;
    private final BoardUserRepository boardUserRepository;

    public CategoryService(CategoryRepository categoryRepository, 
                           BoardRepository boardRepository,
                           TaskRepository taskRepository,
                           BoardUserRepository boardUserRepository) {
        this.categoryRepository = categoryRepository;
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
        this.boardUserRepository = boardUserRepository;
    }

    private void checkMembership(UUID boardId, UUID userId) {
        boolean hasMembers = !boardUserRepository.findAllByBoardId(boardId).isEmpty();
        if (!hasMembers) {
            return;
        }
        boolean isMember = boardUserRepository.findByBoardIdAndUserId(boardId, userId).isPresent();
        if (!isMember) {
            throw new MosifyException(ErrorCode.FORBIDDEN, "Access denied. User is not a member of this board.");
        }
    }

    @Override
    public Category createCategory(Category category, UUID userId) {
        if (category.getBoardId() == null || boardRepository.findById(category.getBoardId()).isEmpty()) {
            throw new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Board not found with id: " + category.getBoardId());
        }
        checkMembership(category.getBoardId(), userId);
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(UUID id, UUID userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + id));
        checkMembership(category.getBoardId(), userId);
        return category;
    }

    @Override
    public List<Category> getAllCategories(UUID userId) {
        List<BoardUser> memberships = boardUserRepository.findAllByUserId(userId);
        List<UUID> memberBoardIds = memberships.stream().map(BoardUser::getBoardId).toList();
        if (memberBoardIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return categoryRepository.findAllByBoardIdIn(memberBoardIds);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id, UUID userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + id));
        checkMembership(category.getBoardId(), userId);

        taskRepository.deleteAllByCategoryId(id);
        categoryRepository.deleteById(id);
    }
}
