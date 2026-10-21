package org.nntdata.exchangerate.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.nntdata.exchangerate.entity.ExchangeRateConsultation;
import org.nntdata.exchangerate.proxy.ExchangeRateApiProxy;
import org.nntdata.exchangerate.proxy.ExternalExchangeRate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationScoped
public class ExchangeRateRepository implements PanacheRepository<ExchangeRateConsultation> {

    private final ExchangeRateApiProxy apiProxy;

    public ExchangeRateRepository(@RestClient ExchangeRateApiProxy apiProxy) {
        this.apiProxy = apiProxy;
    }

    public long countByDniAndDate(String dni, LocalDate date) {
        try {
            return count("dni = ?1 and queryDate = ?2", dni, date);
        } catch (PersistenceException exception) {
            throw new DatabaseAccessException(exception);
        }
    }

    @Transactional
    public void save(String dni, LocalDate date, LocalDateTime queriedAt) {
        try {
            persistAndFlush(new ExchangeRateConsultation(dni, date, queriedAt));
        } catch (PersistenceException exception) {
            throw new DatabaseAccessException(exception);
        }
    }

    public ExternalExchangeRate getTodayExchangeRate() {
        try {
            ExternalExchangeRate exchangeRate = apiProxy.getTodayExchangeRate();
            if (exchangeRate == null) {
                throw new ExternalServiceException();
            }
            return exchangeRate;
        } catch (WebApplicationException | ProcessingException exception) {
            throw new ExternalServiceException(exception);
        }
    }

    public static class DatabaseAccessException extends RuntimeException {
        public DatabaseAccessException(Throwable cause) {
            super(cause);
        }
    }

    public static class ExternalServiceException extends RuntimeException {
        public ExternalServiceException() {
            super("El proveedor externo devolvió una respuesta vacía.");
        }

        public ExternalServiceException(Throwable cause) {
            super("Error al invocar el proveedor de tipo de cambio.", cause);
        }
    }
}
