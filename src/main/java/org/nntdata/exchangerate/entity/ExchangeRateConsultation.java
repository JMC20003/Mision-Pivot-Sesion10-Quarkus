package org.nntdata.exchangerate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rate_consultation")
public class ExchangeRateConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String dni;

    @Column(name = "query_date", nullable = false)
    private LocalDate queryDate;

    @Column(name = "queried_at", nullable = false)
    private LocalDateTime queriedAt;

    protected ExchangeRateConsultation() {
    }

    public ExchangeRateConsultation(String dni, LocalDate queryDate, LocalDateTime queriedAt) {
        this.dni = dni;
        this.queryDate = queryDate;
        this.queriedAt = queriedAt;
    }
}
