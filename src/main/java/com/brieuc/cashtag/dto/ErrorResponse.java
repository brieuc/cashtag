package com.brieuc.cashtag.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

      private String message;
      private LocalDateTime timestamp;

      public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = LocalDateTime.now();
      }
}
