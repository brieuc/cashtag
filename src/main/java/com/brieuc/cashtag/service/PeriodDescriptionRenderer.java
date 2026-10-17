package com.brieuc.cashtag.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.mapper.TagMapper;
import com.brieuc.cashtag.repository.TagRepository;
import com.brieuc.cashtag.service.helper.ComputationSpecBuilder;
import com.brieuc.cashtag.service.template.Evaluator;
import com.brieuc.cashtag.service.template.Expr;
import com.brieuc.cashtag.service.template.Lexer;
import com.brieuc.cashtag.service.template.Parser;
import com.brieuc.cashtag.service.template.Statement;
import com.brieuc.cashtag.service.template.TemplateException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Renders a period description by replacing the {{ ... }} blocks with computed amounts.
 * Amounts are computed on the period dates with the same rules as the computation endpoint.
 * Tags are referenced by their code.
 */
@Service
@RequiredArgsConstructor
public class PeriodDescriptionRenderer {

    // group(1): block content, group(2): optional line break right after the block
    private static final Pattern BLOCK = Pattern.compile("\\{\\{(.*?)\\}\\}(\\R)?", Pattern.DOTALL);
    private static final Locale SWISS = Locale.forLanguageTag("de-CH");

    private final ComputationSpecBuilder computationSpecBuilder;
    private final ComputationService computationService;
    private final EntryService entryService;
    private final CurrencyService currencyService;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public String render(Period period) {
        String text = period.getDescription();
        if (text == null || text.isBlank()) {
            return text;
        }

        String target = period.getCurrency() != null
                ? period.getCurrency().getCode()
                : currencyService.getReferenceCurrency().getCode();
        Evaluator evaluator = new Evaluator(q -> sum(q, period, target));

        Matcher m = BLOCK.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String lineBreak = m.group(2) != null ? m.group(2) : "";
            String replacement;
            try {
                Statement st = new Parser(new Lexer(m.group(1)).tokenize()).parseStatement();
                // assignment: empty string and the following line break is dropped
                replacement = evaluator.execute(st)
                        .map(v -> String.format(SWISS, "%,.2f %s", v, target) + lineBreak)
                        .orElse("");
            } catch (TemplateException ex) {
                replacement = "[" + ex.getMessage() + "]" + lineBreak;
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private BigDecimal sum(Expr.TagQuery q, Period period, String target) {
        ComputationRequestDto request = new ComputationRequestDto(
                period.getStartDate(),
                period.getEndDate(),
                toTagDtos(q.included()),
                toTagDtos(q.excluded()),
                null,   // currencies: no filter
                null,   // searchText: no filter
                target);
        List<Entry> entries = entryService
                .getEntries(computationSpecBuilder.from(request), Pageable.unpaged())
                .getContent();
        try {
            return computationService.computeSum(entries, target).totalAmount();
        } catch (EntityNotFoundException ex) {
            throw new TemplateException("Conversion impossible : " + ex.getMessage());
        }
    }

    private Set<TagDto> toTagDtos(List<String> codes) {
        return codes.stream()
                .map(c -> tagRepository.findByCode(c)
                        .orElseThrow(() -> new TemplateException("Tag inconnu : " + c)))
                .map(tagMapper::toDto)
                .collect(Collectors.toSet());
    }
}
