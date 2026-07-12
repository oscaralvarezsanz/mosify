package com.mosify.infrastructure.out.db.model;

import com.mosify.domain.model.TaskFrequency;
import com.mosify.domain.model.TaskType;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskFrequency frequency;

    @Column(name = "points_value", nullable = false)
    private Integer pointsValue;

    @Column(nullable = false)
    private Boolean active;
}
