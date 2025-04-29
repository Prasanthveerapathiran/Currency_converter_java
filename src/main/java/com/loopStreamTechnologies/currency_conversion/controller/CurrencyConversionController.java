package com.loopStreamTechnologies.currency_conversion.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopStreamTechnologies.currency_conversion.service.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "http://localhost:5173")
public class CurrencyConversionController {

    private final CurrencyConversionService conversionService;

    @Autowired
    public CurrencyConversionController(CurrencyConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/convert")
    public ResponseEntity<Double> convertCurrency(
            @RequestParam String source,
            @RequestParam String target,
            @RequestParam int amount
    ) {
        double result = conversionService.convert(source, target, amount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/latest")
    public ResponseEntity<JsonNode> getLatestRates(
            @RequestParam(defaultValue = "USD") String base
    ) {
        JsonNode rates = conversionService.getLatestRates(base);
        return ResponseEntity.ok(rates);
    }
}
