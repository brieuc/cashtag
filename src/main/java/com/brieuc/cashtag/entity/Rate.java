package com.brieuc.cashtag.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    // Always refering to CHF even if not mentionned. For CHF 1, how many CUR ? i.e. 1 CHF = 1.0722 EUR
    @Column(name = "rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal rate;
}