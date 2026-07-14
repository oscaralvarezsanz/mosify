package com.mosify.application.port.in.board;

import com.mosify.domain.model.Category;
import java.util.List;
import java.util.UUID;

public interface BoardGetCategoriesPort {
    List<Category> getBoardCategories(UUID boardId, UUID userId);
}
