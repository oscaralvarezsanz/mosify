package com.mosify.application.service;

import com.mosify.application.port.in.category.CategoryCreatePort;
import com.mosify.application.port.in.category.CategoryDeletePort;
import com.mosify.application.port.in.category.CategoryGetAllPort;
import com.mosify.application.port.in.category.CategoryGetByIdPort;
import com.mosify.application.port.out.board.BoardRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
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

    public CategoryService(CategoryRepository categoryRepository, 
                           BoardRepository boardRepository,
                           TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public Category createCategory(Category category) {
        if (category.getBoardId() == null || boardRepository.findById(category.getBoardId()).isEmpty()) {
            throw new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Board not found with id: " + category.getBoardId());
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        // Verify category exists
        categoryRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + id));

        // Delete tasks in this category
        taskRepository.deleteAllByCategoryId(id);

        // Delete category
        categoryRepository.deleteById(id);
    }
}
