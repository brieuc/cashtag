package com.brieuc.cashtag;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.repository.CurrencyRepository;
import com.brieuc.cashtag.repository.RateRepository;

@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = Replace.ANY)
@DataJpaTest
class RateRepositoryTest {
    
    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private CurrencyRepository currencyRepository;
    
    @Test
    void testFindClosestRateBefore() {
      
      Currency currencyCHF = Currency.builder()
            .code("CHF")
            .build();
      currencyRepository.save(currencyCHF);

      Currency currencyEUR = Currency.builder()
            .code("EUR")
            .build();
      currencyRepository.save(currencyEUR);

      // Given
      Rate rate1 = Rate.builder()
            .sourceCurrency(currencyEUR)
            .targetCurrency(currencyCHF)
            .valueDate(LocalDate.of(2020, 1, 1))
            .rate(BigDecimal.valueOf(1.0))
            .build();

      rateRepository.save(rate1);

      Rate rate2 = Rate.builder()
            .sourceCurrency(currencyEUR)
            .targetCurrency(currencyCHF)
            .valueDate(LocalDate.of(2020, 1, 3))
            .rate(BigDecimal.valueOf(1.1))
            .build();

      rateRepository.save(rate2);

      Rate rate3 = Rate.builder()
            .sourceCurrency(currencyEUR)
            .targetCurrency(currencyCHF)
            .valueDate(LocalDate.of(2020, 1, 5))
            .rate(BigDecimal.valueOf(1.2))
            .build();

      rateRepository.save(rate3);
       
      // When & Then
      Optional<Rate> result1 = rateRepository.findClosestRateBefore("EUR", "CHF", LocalDate.of(2020, 1, 4));
      assertTrue(result1.isPresent());
      assertTrue(result1.get().getRate().equals(BigDecimal.valueOf(1.1)));
        
      Optional<Rate> result2 = rateRepository.findClosestRateBefore("EUR", "CHF", LocalDate.of(2020, 2, 4));
      assertTrue(result2.isPresent());
      assertTrue(result2.get().getRate().equals(BigDecimal.valueOf(1.2)));
      }
}









