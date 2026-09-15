package com.brieuc.cashtag.config;

import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.entity.Currency;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Rate;
import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagGroup;
import com.brieuc.cashtag.entity.user.Role;
import com.brieuc.cashtag.entity.user.User;
import com.brieuc.cashtag.mapper.EntrySpecificationMapper;
import com.brieuc.cashtag.repository.CurrencyRepository;
import com.brieuc.cashtag.repository.EntryRepository;
import com.brieuc.cashtag.repository.RateRepository;
import com.brieuc.cashtag.repository.TagRepository;
import com.brieuc.cashtag.repository.UserRepository;
import com.brieuc.cashtag.service.EntryService;
import com.brieuc.cashtag.service.TagGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.info.BuildProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final TagGroupService tagGroupService;
    private final EntryService entryService;
    private final EntrySpecificationMapper entrySpecificationMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (this.userRepository.count() == 0) {
            User user = User.builder()
                            .username("brieuc")
                            .password(passwordEncoder.encode("PasswdThunder1982fr_"))
                            .role(Role.ADMIN)
                            .build();
            this.userRepository.save(user);
        }
        List<TagGroup> tagGroups = tagGroupService.getTagGroups(List.of());
        if (tagGroups.isEmpty()) {
                
                EntrySpecificationDto entrySpecificationDto = EntrySpecificationDto.builder()
                                                                  .startDate(LocalDateTime.of(2026, 2, 1, 0, 0))
                                                                  .endDate(LocalDateTime.of(2026, 2, 28, 0, 0))
                                                                  .build();                                                           

                Specification<Entry> specification = entrySpecificationMapper.toEntity(entrySpecificationDto);
                List<Entry> entries = entryService.getEntries(specification, Pageable.unpaged()).getContent();
                tagGroupService.resetTagGroupsAndTitleSuggestions(entries);
        }
    }
}
