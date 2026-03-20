package com.brieuc.cashtag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recurrency")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modification_date", nullable = false)
    private LocalDateTime modificationDate;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "recurrency_tag",
        joinColumns = @JoinColumn(name = "recurrency_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
