package com.brieuc.cashtag.config;

import com.brieuc.cashtag.dto.EntrySpecificationDto;
import com.brieuc.cashtag.entity.Entry;

import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.entity.TagGroup;
import com.brieuc.cashtag.entity.user.Role;
import com.brieuc.cashtag.entity.user.User;
import com.brieuc.cashtag.mapper.EntrySpecificationMapper;
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
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final BuildProperties buildProperties;
    private final TagGroupService tagGroupService;
    private final EntryService entryService;
    private final EntrySpecificationMapper entrySpecificationMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

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
        fixTagCodesSpaces();
        initTagCodes();
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

    /**
     * Sets the code of the tags that have none, from their lowercase title
     * (surrounding spaces removed, inner spaces replaced by a hyphen).
     * If the code is already used by another tag, a number (1, 2, ...) is appended.
     */
    private void initTagCodes() {
        List<Tag> tags = tagRepository.findAll();
        Set<String> usedCodes = tags.stream()
                                    .map(Tag::getCode)
                                    .filter(code -> code != null)
                                    .map(DataLoader::collationKey)
                                    .collect(Collectors.toCollection(HashSet::new));
        List<Tag> tagsToUpdate = tags.stream().filter(tag -> tag.getCode() == null).toList();
        for (Tag tag : tagsToUpdate) {
            tag.setCode(uniqueCode(toCode(tag.getTitle()), usedCodes));
        }
        if (!tagsToUpdate.isEmpty()) {
            tagRepository.saveAll(tagsToUpdate);
            log.info("Code initialized for {} tags", tagsToUpdate.size());
        }
    }

    /**
     * Fixes the codes already set that contain spaces: surrounding spaces are removed
     * and inner spaces are replaced by a hyphen. Codes without spaces are left untouched,
     * so once fixed this does nothing anymore.
     */
    private void fixTagCodesSpaces() {
        List<Tag> tags = tagRepository.findAll();
        Set<String> usedCodes = tags.stream()
                                    .map(Tag::getCode)
                                    .filter(code -> code != null)
                                    .map(DataLoader::collationKey)
                                    .collect(Collectors.toCollection(HashSet::new));
        List<Tag> tagsToUpdate = tags.stream()
                                     .filter(tag -> tag.getCode() != null && !tag.getCode().equals(toCode(tag.getCode())))
                                     .toList();
        for (Tag tag : tagsToUpdate) {
            // free its own key first: "bettina " and "bettina" are the same for the database
            usedCodes.remove(collationKey(tag.getCode()));
            tag.setCode(uniqueCode(toCode(tag.getCode()), usedCodes));
        }
        if (!tagsToUpdate.isEmpty()) {
            tagRepository.saveAll(tagsToUpdate);
            log.info("Spaces fixed in the code of {} tags", tagsToUpdate.size());
        }
    }

    /** Lowercase, surrounding spaces removed, inner spaces replaced by a hyphen. */
    private static String toCode(String text) {
        return text.trim().replaceAll("\\s+", "-").toLowerCase(Locale.ROOT);
    }

    /** Appends 1, 2, ... to the code until it is not used, and registers it in usedCodes. */
    private static String uniqueCode(String baseCode, Set<String> usedCodes) {
        String code = baseCode;
        int suffix = 1;
        while (!usedCodes.add(collationKey(code))) {
            code = baseCode + suffix++;
        }
        return code;
    }

    /**
     * Key used to detect duplicates the way the database unique constraint does:
     * case, accents and trailing spaces are ignored (utf8mb4_unicode_ci).
     */
    private static String collationKey(String code) {
        return Normalizer.normalize(code, Normalizer.Form.NFD)
                         .replaceAll("\\p{M}", "")
                         .toLowerCase(Locale.ROOT)
                         .stripTrailing();
    }

    public String getVersion() {
        return buildProperties.getVersion();
    }

}
