package com.brieuc.cashtag.mapper;

import com.brieuc.cashtag.dto.UserDto;
import com.brieuc.cashtag.entity.user.User;

public class UserMapperImpl implements UserMapper {

      @Override
      public UserDto toDto(User user) {
            return UserDto.builder()
                  .username(user.getUsername())
                  .build();
      }

      @Override
      public User toEntity(UserDto userDto) {
            return User.builder()
                  .username(userDto.getUsername())
                  .build();
      }
      
}
