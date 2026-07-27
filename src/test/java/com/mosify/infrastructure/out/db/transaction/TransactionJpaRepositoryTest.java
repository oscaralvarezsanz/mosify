package com.mosify.infrastructure.out.db.transaction;

import com.mosify.infrastructure.out.db.model.CategoryEntity;
import com.mosify.infrastructure.out.db.model.TaskEntity;
import com.mosify.infrastructure.out.db.model.TransactionEntity;
import com.mosify.domain.model.TaskType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TransactionJpaRepositoryTest {

    @Autowired
    private TransactionJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void shouldFindAllTransactionsByBoardId() {
        UUID boardIdA = UUID.randomUUID();
        UUID boardIdB = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CategoryEntity categoryA = CategoryEntity.builder()
                .userId(userId)
                .boardId(boardIdA)
                .name("Cat A")
                .build();
        categoryA = entityManager.persist(categoryA);

        TaskEntity taskA = TaskEntity.builder()
                .title("Task A")
                .categoryId(categoryA.getId())
                .type(TaskType.RECURRENT)
                .pointsValue(10)
                .active(true)
                .build();
        taskA = entityManager.persist(taskA);

        TransactionEntity txA = TransactionEntity.builder()
                .userId(userId)
                .taskId(taskA.getId())
                .pointsAffected(10)
                .createdAt(LocalDateTime.now())
                .build();
        txA = entityManager.persist(txA);

        CategoryEntity categoryB = CategoryEntity.builder()
                .userId(userId)
                .boardId(boardIdB)
                .name("Cat B")
                .build();
        categoryB = entityManager.persist(categoryB);

        TaskEntity taskB = TaskEntity.builder()
                .title("Task B")
                .categoryId(categoryB.getId())
                .type(TaskType.RECURRENT)
                .pointsValue(20)
                .active(true)
                .build();
        taskB = entityManager.persist(taskB);

        TransactionEntity txB = TransactionEntity.builder()
                .userId(userId)
                .taskId(taskB.getId())
                .pointsAffected(20)
                .createdAt(LocalDateTime.now())
                .build();
        txB = entityManager.persist(txB);

        List<TransactionEntity> resultA = repository.findAllByBoardId(boardIdA);
        assertThat(resultA).hasSize(1);
        assertThat(resultA.get(0).getId()).isEqualTo(txA.getId());

        List<TransactionEntity> resultB = repository.findAllByBoardId(boardIdB);
        assertThat(resultB).hasSize(1);
        assertThat(resultB.get(0).getId()).isEqualTo(txB.getId());
    }
}
