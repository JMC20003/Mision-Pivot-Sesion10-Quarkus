package org.nntdata.exchangerate.proxy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/tipo-cambio/today.json")
@RegisterRestClient(configKey = "api-proxy")
@Produces(MediaType.APPLICATION_JSON)
public interface ExchangeRateApiProxy {

    @GET
    ExternalExchangeRate getTodayExchangeRate();
}
