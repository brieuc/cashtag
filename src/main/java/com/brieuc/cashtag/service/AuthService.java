package com.brieuc.cashtag.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.brieuc.cashtag.config.JwtService;
import com.brieuc.cashtag.controller.AuthenticationRequest;
import com.brieuc.cashtag.controller.AuthenticationResponse;
import com.brieuc.cashtag.entity.user.User;
import com.brieuc.cashtag.repository.UserRepository;

@Service
public class AuthService {

      private final AuthenticationManager authenticationManager;
      private final UserRepository userRepository;
      private final JwtService jwtService;

      @Autowired
      public AuthService(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
            this.userRepository = userRepository;
            this.jwtService = jwtService;
            this.authenticationManager = authenticationManager;
      }

      public AuthenticationResponse login(AuthenticationRequest authRqst) {
            User user = this.userRepository.findByUsername(authRqst.getUsername()).orElseThrow();
            authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(authRqst.getUsername(), authRqst.getPassword())
            );
            String token = jwtService.generateToken(user);
            return AuthenticationResponse.builder().token(token).build();
      }
}
