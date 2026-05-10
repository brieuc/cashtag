package com.brieuc.cashtag.mapper;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.EntryDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.service.helper.SimulatedEntry;

@Service
public interface EntryMapper {
      Entry toEntity(EntryDto entryDto);
      EntryDto tDto(Entry entry);
      EntryDto toDto(SimulatedEntry simulatedEntry);

}
