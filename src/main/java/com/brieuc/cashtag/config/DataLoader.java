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
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {
        public String getVersion() {
                return buildProperties.getVersion();
        }

    private final CurrencyRepository currencyRepository;
    private final TagRepository tagRepository;
    private final EntryRepository entryRepository;
    private final RateRepository rateRepository;
    private final BuildProperties buildProperties;

    @Override
    public void run(ApplicationArguments args) {
/*
                Currency refCurrency = Currency.builder()
                                .code("DYN")
                                .reference(true)
                                .build();
                currencyRepository.save(refCurrency);
         */
                 
        if (currencyRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        log.info("Starting data initialization...");

        // Create currencies
        Currency chf = currencyRepository.save(new Currency("CHF", Boolean.TRUE));
        Currency eur = currencyRepository.save(new Currency("EUR", Boolean.FALSE));
        log.info("Created {} currencies", 3);

        // Create tags
        Tag salaryTag = tagRepository.save(Tag.builder()
                .title("Salaire")
                .description("Revenus mensuels")
                .icon("💰")
                .build());
        Tag rentTag = tagRepository.save(Tag.builder()
                .title("Loyer")
                .description("Dépenses de logement")
                .icon("🏠")
                .build());
        Tag foodTag = tagRepository.save(Tag.builder()
                .title("Alimentation")
                .description("Courses et restaurants")
                .icon("🍽️")
                .build());
        Tag transportTag = tagRepository.save(Tag.builder()
                .title("Transport")
                .description("Déplacements et carburant")
                .icon("🚗")
                .build());
        Tag leisureTag = tagRepository.save(Tag.builder()
                .title("Loisirs")
                .description("Activités et divertissements")
                .icon("🎉")
                .build());
        log.info("Created {} tags", 5);

        // Create entries
        Entry entry1 = new Entry();
        entry1.setAccountingDate(LocalDateTime.now().minusDays(30));
        entry1.setTitle("Salaire mensuel");
        entry1.setDescription("Salaire du mois de septembre");
        entry1.setCurrency(eur);
        entry1.setTags(Set.of(salaryTag));
        entryRepository.save(entry1);

        Entry entry2 = new Entry();
        entry2.setAccountingDate(LocalDateTime.now().minusDays(28));
        entry2.setTitle("Loyer octobre");
        entry2.setDescription("Paiement du loyer");
        entry2.setCurrency(chf);
        entry2.setTags(Set.of(rentTag));
        entryRepository.save(entry2);

        Entry entry3 = new Entry();
        entry3.setAccountingDate(LocalDateTime.now().minusDays(15));
        entry3.setTitle("Courses Carrefour");
        entry3.setDescription("Courses hebdomadaires");
        entry3.setCurrency(eur);
        entry3.setTags(Set.of(foodTag));
        entryRepository.save(entry3);

        Entry entry4 = new Entry();
        entry4.setAccountingDate(LocalDateTime.now().minusDays(10));
        entry4.setTitle("Essence");
        entry4.setDescription("Plein d'essence");
        entry4.setCurrency(eur);
        entry4.setTags(Set.of(transportTag));
        entryRepository.save(entry4);

        Entry entry5 = new Entry();
        entry5.setAccountingDate(LocalDateTime.now().minusDays(5));
        entry5.setTitle("Cinéma");
        entry5.setDescription("Soirée cinéma");
        entry5.setCurrency(eur);
        entry5.setTags(Set.of(leisureTag));
        entryRepository.save(entry5);

        Entry entry6 = new Entry();
        entry6.setAccountingDate(LocalDateTime.now().minusDays(2));
        entry6.setTitle("Restaurant");
        entry6.setDescription("Dîner au restaurant");
        entry6.setCurrency(eur);
        entry6.setTags(Set.of(foodTag, leisureTag));
        entryRepository.save(entry6);

        log.info("Created {} entries", 6);

        // Create rates
        Rate rateEur = new Rate(null, eur, null, null, new BigDecimal("1.0722"));

        rateRepository.save(rateEur);
        log.info("Data initialization completed successfully!");
    }
}
