package com.loopStreamTechnologies.currency_conversion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopStreamTechnologies.currency_conversion.Repository.ConversionRateRepository;
import com.loopStreamTechnologies.currency_conversion.entities.ConversionRate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    private final ConversionRateRepository repository;
    private final RestTemplate restTemplate;

    @Value("${currencybeacon.api.key}")
    private String apiKey;

    public double convert(String source, String target, int amount) {
        Optional<ConversionRate> recentRateOpt = repository
                .findTopBySourceCurrencyAndTargetCurrencyOrderByTimestampDesc(source, target);

        if (recentRateOpt.isPresent()) {
            ConversionRate rate = recentRateOpt.get();
            if (rate.getTimestamp().isAfter(LocalDateTime.now().minusHours(1))) {
                return amount * rate.getRate();
            }
        }

        String url = String.format(
                "https://api.currencybeacon.com/v1/convert?from=%s&to=%s&amount=%d&api_key=%s",
                source, target, amount, apiKey
        );

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode body = response.getBody();

        if (response.getStatusCode().is2xxSuccessful() && body != null && body.has("response")) {
            JsonNode responseNode = body.get("response");

            if (responseNode.has("value")) {
                double value = responseNode.get("value").asDouble();
                double rate = value / amount;

                ConversionRate newRate = new ConversionRate(null, source, target, rate, LocalDateTime.now());
                repository.save(newRate);

                return value;
            } else {
                throw new RuntimeException("Missing 'value' field in API response");
            }
        } else {
            throw new RuntimeException("Currency conversion failed or unexpected response format");
        }
    }
    public JsonNode getLatestRates(String baseCurrency) {
        String url = String.format("https://api.currencybeacon.com/v1/latest?api_key=%s&base=%s", apiKey, baseCurrency);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch latest exchange rates");
        }
    }

}
