package services.cashflow.statistics.client;

import org.springframework.stereotype.Component;

import services.cashflow.statistics.domain.Currency;
import services.cashflow.statistics.domain.ExchangeRatesContainer;

import java.util.Collections;

@Component
public class ExchangeRatesClientFallback implements ExchangeRatesClient {

    @Override
    public ExchangeRatesContainer getRates(Currency base) {
        ExchangeRatesContainer container = new ExchangeRatesContainer();
        container.setBase(Currency.getBase());
        container.setRates(Collections.emptyMap());
        return container;
    }
}
