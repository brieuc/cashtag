package com.brieuc.cashtag.config;

import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.repository.CurrencyRepository;
import com.brieuc.cashtag.repository.EntryRepository;
import com.brieuc.cashtag.repository.RateRepository;
import com.brieuc.cashtag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final CurrencyRepository currencyRepository;
    private final TagRepository tagRepository;
    private final EntryRepository entryRepository;
    private final RateRepository rateRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (currencyRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        log.info("Starting data initialization...");

        // Create currencies
        Currency eur = currencyRepository.save(new Currency("EUR"));
        Currency usd = currencyRepository.save(new Currency("USD"));
        Currency gbp = currencyRepository.save(new Currency("GBP"));
        log.info("Created {} currencies", 3);

        // Create tags
        Tag salaryTag = tagRepository.save(new Tag(null, "Salaire", "Revenus mensuels", "💰"));
        Tag rentTag = tagRepository.save(new Tag(null, "Loyer", "Dépenses de logement", "🏠"));
        Tag foodTag = tagRepository.save(new Tag(null, "Alimentation", "Courses et restaurants", "🍽️"));
        Tag transportTag = tagRepository.save(new Tag(null, "Transport", "Déplacements et carburant", "🚗"));
        Tag leisureTag = tagRepository.save(new Tag(null, "Loisirs", "Activités et divertissements", "🎉"));
        log.info("Created {} tags", 5);

        // Create entries
        Entry entry1 = new Entry();
        entry1.setAccountingDate(LocalDate.now().minusDays(30));
        entry1.setTitle("Salaire mensuel");
        entry1.setDescription("Salaire du mois de septembre");
        entry1.setCurrency(eur);
        entry1.setTags(Set.of(salaryTag));
        entryRepository.save(entry1);

        Entry entry2 = new Entry();
        entry2.setAccountingDate(LocalDate.now().minusDays(28));
        entry2.setTitle("Loyer octobre");
        entry2.setDescription("Paiement du loyer");
        entry2.setCurrency(eur);
        entry2.setTags(Set.of(rentTag));
        entryRepository.save(entry2);

        Entry entry3 = new Entry();
        entry3.setAccountingDate(LocalDate.now().minusDays(15));
        entry3.setTitle("Courses Carrefour");
        entry3.setDescription("Courses hebdomadaires");
        entry3.setCurrency(eur);
        entry3.setTags(Set.of(foodTag));
        entryRepository.save(entry3);

        Entry entry4 = new Entry();
        entry4.setAccountingDate(LocalDate.now().minusDays(10));
        entry4.setTitle("Essence");
        entry4.setDescription("Plein d'essence");
        entry4.setCurrency(eur);
        entry4.setTags(Set.of(transportTag));
        entryRepository.save(entry4);

        Entry entry5 = new Entry();
        entry5.setAccountingDate(LocalDate.now().minusDays(5));
        entry5.setTitle("Cinéma");
        entry5.setDescription("Soirée cinéma");
        entry5.setCurrency(eur);
        entry5.setTags(Set.of(leisureTag));
        entryRepository.save(entry5);

        Entry entry6 = new Entry();
        entry6.setAccountingDate(LocalDate.now().minusDays(2));
        entry6.setTitle("Restaurant");
        entry6.setDescription("Dîner au restaurant");
        entry6.setCurrency(eur);
        entry6.setTags(Set.of(foodTag, leisureTag));
        entryRepository.save(entry6);

        log.info("Created {} entries", 6);

        // Create rates
        Rate rateUsd1 = new Rate(null, usd, LocalDate.now().minusDays(30), new BigDecimal("1.0850"));
        Rate rateUsd2 = new Rate(null, usd, LocalDate.now().minusDays(15), new BigDecimal("1.0920"));
        Rate rateUsd3 = new Rate(null, usd, LocalDate.now(), new BigDecimal("1.0875"));

        Rate rateGbp1 = new Rate(null, gbp, LocalDate.now().minusDays(30), new BigDecimal("0.8650"));
        Rate rateGbp2 = new Rate(null, gbp, LocalDate.now().minusDays(15), new BigDecimal("0.8590"));
        Rate rateGbp3 = new Rate(null, gbp, LocalDate.now(), new BigDecimal("0.8625"));

        rateRepository.save(rateUsd1);
        rateRepository.save(rateUsd2);
        rateRepository.save(rateUsd3);
        rateRepository.save(rateGbp1);
        rateRepository.save(rateGbp2);
        rateRepository.save(rateGbp3);

        log.info("Created {} rates", 6);
        log.info("Data initialization completed successfully!");
    }
}
