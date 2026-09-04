package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.UserDto;
import com.brieuc.cashtag.entity.user.User;

public interface UserMapper {
      UserDto toDto(User user);
      User toEntity(UserDto userDto);
}
