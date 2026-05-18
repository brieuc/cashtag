package com.brieuc.cashtag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "tag_group_tag",
        joinColumns = @JoinColumn(name = "tag_group_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

}
