package com.mosify.infrastructure.out.db.category;

import com.mosify.infrastructure.out.db.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
}
