package org.nntdata.exchangerate.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.nntdata.exchangerate.proxy.ExternalExchangeRate;
import org.nntdata.exchangerate.repository.ExchangeRateRepository;

import java.time.LocalDateTime;

@ApplicationScoped
public class ExchangeRateService {

    @Inject
    ExchangeRateRepository repository;

    @ConfigProperty(name = "exchange-rate.daily-query-limit", defaultValue = "10")
    int dailyQueryLimit;

    public ExternalExchangeRate getTodayExchangeRate(String dni) {
        LocalDateTime requestedAt = LocalDateTime.now();
        if (repository.countByDniAndDate(dni, requestedAt.toLocalDate()) >= dailyQueryLimit) {
            throw new DailyQueryLimitExceededException();
        }

        ExternalExchangeRate exchangeRate = repository.getTodayExchangeRate();
        repository.save(dni, requestedAt.toLocalDate(), requestedAt);
        return exchangeRate;
    }

    public static class DailyQueryLimitExceededException extends RuntimeException {
        public DailyQueryLimitExceededException() {
            super("Se superó el límite diario de consultas de tipo de cambio para este DNI.");
        }
    }
}
