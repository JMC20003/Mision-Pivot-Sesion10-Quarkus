package org.nntdata.exchangerate.proxy;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExternalExchangeRate {

    private String fecha;
    private BigDecimal sunat;
    private BigDecimal compra;
    private BigDecimal venta;
}
