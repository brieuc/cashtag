package com.brieuc.cashtag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tag_group_title_suggestion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagGroupTitleSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tag_group_id", nullable = false)
    private TagGroup tagGroup;

    @Column(nullable = false)
    private String title;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;
}
