package com.brieuc.cashtag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.mapper.TagMapper;
import com.brieuc.cashtag.repository.TagRepository;
import com.brieuc.cashtag.service.helper.ComputationSpecBuilder;
import com.brieuc.cashtag.service.helper.ComputeResult;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PeriodDescriptionRendererTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 1, 31, 23, 59, 59);

    @InjectMocks
    private PeriodDescriptionRenderer renderer;

    @Mock
    private ComputationSpecBuilder computationSpecBuilder;
    @Mock
    private ComputationService computationService;
    @Mock
    private EntryService entryService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private TagMapper tagMapper;

    @BeforeEach
    void setUp() {
        when(currencyService.getReferenceCurrency()).thenReturn(new Currency("CHF", true));
        when(computationSpecBuilder.from(any())).thenReturn(Specification.unrestricted());
        when(entryService.getEntries(any(), any(Pageable.class))).thenReturn(new PageImpl<Entry>(List.of()));
        when(computationService.computeSum(any(), any())).thenReturn(result("-1234.50"));
        mockTag("restaurant", 1L);
        mockTag("italie", 2L);
    }

    private ComputeResult result(String amount) {
        return new ComputeResult(new BigDecimal(amount), "CHF", 0, Map.of());
    }

    private void mockTag(String code, Long id) {
        Tag tag = Tag.builder().id(id).code(code).title(code).build();
        when(tagRepository.findByCode(code)).thenReturn(Optional.of(tag));
        when(tagMapper.toDto(tag)).thenReturn(TagDto.builder().id(id).title(code).code(code).build());
    }

    private Period period(String description, Currency currency) {
        return Period.builder().title("p").startDate(START).endDate(END).description(description).currency(currency).build();
    }

    private String swiss(String amount, String currency) {
        return String.format(Locale.forLanguageTag("de-CH"), "%,.2f %s", new BigDecimal(amount), currency);
    }

    @Test
    void nullOrBlankDescriptionIsReturnedAsIs() {
        assertNull(renderer.render(period(null, null)));
        assertEquals(" ", renderer.render(period(" ", null)));
        verifyNoInteractions(entryService, computationService);
    }

    @Test
    void simpleExpression() {
        assertEquals("J'ai dépensé \"" + swiss("-1234.50", "CHF") + "\" ce mois-ci",
                renderer.render(period("J'ai dépensé \"{{ tags: restaurant }}\" ce mois-ci", null)));
    }

    @Test
    void absExpression() {
        assertEquals("Dépensé " + swiss("1234.50", "CHF"),
                renderer.render(period("Dépensé {{ abs(tags: restaurant) }}", null)));
    }

    @Test
    void assignmentLinesDisappear() {
        String description = "{{ A = tags: restaurant }}\n{{ B = tags: italie, restaurant }}\nTotal {{ A - B }}";
        assertEquals("Total " + swiss("0.00", "CHF"), renderer.render(period(description, null)));
    }

    @Test
    void periodCurrencyIsTheTarget() {
        String rendered = renderer.render(period("{{ tags: restaurant }}", new Currency("MXN", false)));
        assertEquals(swiss("-1234.50", "MXN"), rendered);
        verify(currencyService, never()).getReferenceCurrency();
        verify(computationService).computeSum(any(), eq("MXN"));
    }

    @Test
    void referenceCurrencyWhenPeriodHasNone() {
        renderer.render(period("{{ tags: restaurant }}", null));
        verify(computationService).computeSum(any(), eq("CHF"));
    }

    @Test
    void unknownTagIsReportedInTheBlockOnly() {
        when(tagRepository.findByCode("xyz")).thenReturn(Optional.empty());
        assertEquals("[Tag inconnu : xyz] et " + swiss("-1234.50", "CHF"),
                renderer.render(period("{{ tags: xyz }} et {{ tags: restaurant }}", null)));
    }

    @Test
    void requestUsesPeriodDatesAndTags() {
        renderer.render(period("{{ tags: restaurant, !italie }}", null));
        ArgumentCaptor<ComputationRequestDto> captor = ArgumentCaptor.forClass(ComputationRequestDto.class);
        verify(computationSpecBuilder).from(captor.capture());
        ComputationRequestDto request = captor.getValue();
        assertEquals(START, request.startDate());
        assertEquals(END, request.endDate());
        assertEquals(Set.of("restaurant"), titles(request.tags()));
        assertEquals(Set.of("italie"), titles(request.excludedTags()));
        assertEquals("CHF", request.targetCurrencyCode());
    }

    private Set<String> titles(Set<TagDto> tags) {
        return tags.stream().map(TagDto::getTitle).collect(java.util.stream.Collectors.toSet());
    }
}
