package com.example.facture.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "OD3k7etca8CTffsTYxI6xC8C8hMMnWryHnlaLt7StJTEYIkdyk114c8XgRaE3xJKgIFjqBP/eYdVkDPpGhiJBodQVO3Pw5K8IAj722Q4QGtEf/Vsd6X68mFKzLma3hTiYa7ADR4Sctr79BGIcLAgByARZAjKl3vjgI+mCo8aHHLL5Jg7hFCCFr1qXtzIFbcrrECFXMRJMPgrbwCvaTWRDZWSOrR2QwNb9/OHwaJBVN4Eq7NWxNDURPvmSv4+7FkPHmOZIOBGNqPtHigWBS9kyJ/geqsFQxZZlaD3kGdpJ/H9ikEAXNm6sZ9bmgnB36dQ8yIRmNBbw3HT/mSlhTOMnzjPtm7hBIod324umXk37TU=\n";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims ,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
  public String generateToken(UserDetails userDetails) {
       return generateToken(new HashMap<>(), userDetails);
  }


public String generateToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails

){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 *24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
}
public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
}

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
//                .parseClaimsJwt(token)
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
