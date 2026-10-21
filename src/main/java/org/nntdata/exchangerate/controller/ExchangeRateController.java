package org.nntdata.exchangerate.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.persistence.PersistenceException;
import org.jboss.logging.Logger;
import org.nntdata.exchangerate.repository.ExchangeRateRepository;
import org.nntdata.exchangerate.service.ExchangeRateService;

import java.util.Map;

@Path("/api/v1/tipo-cambio")
@Produces(MediaType.APPLICATION_JSON)
public class ExchangeRateController {

    private static final Logger LOG = Logger.getLogger(ExchangeRateController.class);

    @Inject
    ExchangeRateService exchangeRateService;

    @GET
    public Response getTodayExchangeRate(@QueryParam("dni") String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error("INVALID_DNI", "El DNI es obligatorio y debe contener 8 dígitos."))
                    .build();
        }

        try {
            return Response.ok(exchangeRateService.getTodayExchangeRate(dni)).build();
        } catch (ExchangeRateService.DailyQueryLimitExceededException exception) {
            return Response.status(429)
                    .entity(error("DAILY_QUERY_LIMIT_EXCEEDED", exception.getMessage()))
                    .build();
        } catch (ExchangeRateRepository.ExternalServiceException | WebApplicationException
                 | ProcessingException exception) {
            LOG.error("Error al consultar la API externa de tipo de cambio.", exception);
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(error("EXCHANGE_RATE_PROVIDER_ERROR", "No fue posible consultar el tipo de cambio externo."))
                    .build();
        } catch (ExchangeRateRepository.DatabaseAccessException | PersistenceException exception) {
            LOG.error("Error de acceso a la base de datos.", exception);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(error("DATABASE_UNAVAILABLE", "No fue posible completar la consulta."))
                    .build();
        }
    }

    private Map<String, String> error(String code, String message) {
        return Map.of("code", code, "message", message);
    }
}
