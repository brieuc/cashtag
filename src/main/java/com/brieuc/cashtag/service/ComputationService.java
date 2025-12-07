package com.brieuc.cashtag.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.dto.calculation.ComputationResponseDto;

@Service
public interface ComputationService {
      /*
      Compute the sum of the period entries. We could say no tag
      no amount but for the display, it's better to get an amout and
      it's more consistent with computeByCurrency. If tags is null
      then all the entries are computed.
      */
      ComputationResponseDto computeSum(ComputationRequestDto computationRequestDto);
}
