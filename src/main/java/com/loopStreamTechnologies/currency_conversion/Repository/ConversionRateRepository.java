package com.loopStreamTechnologies.currency_conversion.Repository;

import com.loopStreamTechnologies.currency_conversion.entities.ConversionRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversionRateRepository extends JpaRepository<ConversionRate, Long> {
    Optional<ConversionRate> findTopBySourceCurrencyAndTargetCurrencyOrderByTimestampDesc(String source, String target);
}
