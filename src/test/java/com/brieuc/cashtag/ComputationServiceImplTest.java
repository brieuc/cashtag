package com.brieuc.cashtag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.mapper.EntrySpecificationMapperImpl;
import com.brieuc.cashtag.service.ComputationServiceImpl;
import com.brieuc.cashtag.service.CurrencyServiceImpl;
import com.brieuc.cashtag.service.EntryServiceImpl;
import com.brieuc.cashtag.service.RateServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ComputationServiceImplTest {
      
      @InjectMocks
      private ComputationServiceImpl computationService;

      @Mock
      private EntryServiceImpl entryService;

      @Mock
      private CurrencyServiceImpl currencyService;

      @Mock
      private RateServiceImpl rateService;

      @Spy
      private EntrySpecificationMapperImpl entrySpecificationMapper;


      @Test
      void ShouldComputeWithSameCurrencyBetweenEntryAndTargetCurrency() {

            Entry entry = Entry.builder()
                  .accountingDate(LocalDateTime.now())
                  .title("Entry Test Title")
                  .description("Entry Test Description")
                  .amount(BigDecimal.valueOf(1000.10))
                  .currency(new Currency("CHF", true))
                  .build();

            Page<Entry> page = new PageImpl<>(List.of(entry));
            when(entryService.getEntries(any(), any())).thenReturn(page);

            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 12, 31),
                        null, "CHF");

            ComputationResponseDto computationResponseDto = computationService.computeSum(computationRequestDto);
            assertEquals(1, computationResponseDto.numberOfEntries());
            assertEquals(1000.1, computationResponseDto.totalAmount().doubleValue());
      }

      @Test
      void ShouldReturnTheSameCurrencyBetweenRequestAndResponse() {
                  Entry entry = Entry.builder()
                  .accountingDate(LocalDateTime.now())
                  .title("Entry Test Title")
                  .description("Entry Test Description")
                  .amount(BigDecimal.valueOf(1000.10))
                  .currency(new Currency("CHF", true))
                  .build();

            Page<Entry> page = new PageImpl<>(List.of(entry));
            when(entryService.getEntries(any(), any())).thenReturn(page);

            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 12, 31),
                        null, "CHF");

            ComputationResponseDto computationResponseDto = computationService.computeSum(computationRequestDto);
            assertEquals(computationRequestDto.targetCurrencyCode(), computationResponseDto.targetCurrencyCode());
      }

      @Test
      void ShouldConvertEntriesWhenInDifferentCurrencyThanTargetCurrency() {
                  Entry entry = Entry.builder()
                  .accountingDate(LocalDateTime.now())
                  .amount(BigDecimal.valueOf(1000))
                  .currency(new Currency("EUR", false))
                  .build();

            Page<Entry> page = new PageImpl<>(List.of(entry));
            when(entryService.getEntries(any(), any())).thenReturn(page);
            when(currencyService.getReferenceCurrency()).thenReturn(new Currency("CHF", true));
            when(rateService.getRateByCurrenciesAndDate(any(), any(), any()))
                  .thenReturn(new Rate(null, null, null, null, BigDecimal.valueOf(0.5)));
            

            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 12, 31),
                        null, "CHF");

            ComputationResponseDto computationResponseDto = computationService.computeSum(computationRequestDto);
            assertEquals(1000 / 0.5, computationResponseDto.totalAmount().doubleValue());
      }

      @Test
      void ShouldThrowIfTargetCurrencyNotMatchEntryCurrencyAndIsNotReferenceCurrency() {
                  Entry entry = Entry.builder()
                  .accountingDate(LocalDateTime.now())
                  .amount(BigDecimal.valueOf(1000))
                  .currency(new Currency("EUR", false))
                  .build();

            Page<Entry> page = new PageImpl<>(List.of(entry));
            when(entryService.getEntries(any(), any())).thenReturn(page);
            when(currencyService.getReferenceCurrency()).thenReturn(new Currency("CHF", true));

            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 12, 31),
                        null, "USD");

            assertThrows(EntityNotFoundException.class, () -> computationService.computeSum(computationRequestDto));

      }

      @Test
      void ShouldComputeIfTargetCurrencyIsNotReferenceCurrencyAndEntryCurrencyNotTargetCurrencyButAtLeastReferenceCurrency() {
                  Entry entry = Entry.builder()
                  .accountingDate(LocalDateTime.now())
                  .amount(BigDecimal.valueOf(1000))
                  .currency(new Currency("CHF", false))
                  .build();

            Page<Entry> page = new PageImpl<>(List.of(entry));
            when(entryService.getEntries(any(), any())).thenReturn(page);
            when(currencyService.getReferenceCurrency()).thenReturn(new Currency("CHF", true));
            when(rateService.getRateByCurrenciesAndDate(any(), any(), any()))
                  .thenReturn(new Rate(null, null, null, null, BigDecimal.valueOf(0.5)));

            
            ComputationRequestDto computationRequestDto = new ComputationRequestDto(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 12, 31),
                        null, "EUR");

            ComputationResponseDto computationResponseDto = computationService.computeSum(computationRequestDto);
            assertEquals(1000 / (1 / 0.5), computationResponseDto.totalAmount().doubleValue());

      }
}
