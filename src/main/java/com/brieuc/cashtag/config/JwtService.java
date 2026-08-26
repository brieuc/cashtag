package com.brieuc.cashtag.config;

import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

      private static final String SECRET_KEY = "2620f15e41c80a2b98709e1b358d08ca1a54ad39315c2a46ecb2853a009ad350";

      public String extractUsername(String jwt) {
            return extractClaim(jwt, Claims::getSubject);
      }

      private Date extractExpiration(String jwt) {
            return extractClaim(jwt, Claims::getExpiration);
      }

      public String generateToken(UserDetails userDetails) {
            return generateToken(new HashMap<>(), userDetails);
      }

      public String generateToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {

            return Jwts
                        .builder()
                        .claims(extraClaims)
                        .subject(userDetails.getUsername())
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7)))
                        .signWith(getSigningKey(), Jwts.SIG.HS256)
                        .compact();
      }

      public boolean isTokenValid(String jwt, UserDetails userDetails) {
            final String username = extractUsername(jwt);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
      }

      private boolean isTokenExpired(String jwt) {
            return extractExpiration(jwt).before(new Date(System.currentTimeMillis()));
      }

      public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(jwt);
            return claimsResolver.apply(claims);
      }

      private Claims extractAllClaims(String jwt) {
            SecretKey key = getSigningKey();
            JwtParser jwtParser = Jwts.parser().verifyWith(key).build();
            return jwtParser.parseSignedClaims(jwt).getPayload();
      }

      private SecretKey getSigningKey() {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
      }
}
