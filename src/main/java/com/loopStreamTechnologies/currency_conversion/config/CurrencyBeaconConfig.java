package com.loopStreamTechnologies.currency_conversion.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "currencybeacon.api")
public class CurrencyBeaconConfig {
    private String key;
}
