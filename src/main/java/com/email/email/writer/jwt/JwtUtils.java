package com.email.email.writer.jwt;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

	private static final Logger logger =LoggerFactory.getLogger(JwtUtils.class);
	
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String getJwtFromHeader(HttpServletRequest request) {
    	String bearerToken=request.getHeader("Authorization");
    	logger.debug("Authorization Header : {}",bearerToken);
    	if(bearerToken!=null && bearerToken.startsWith("Bearer ") ) {
    		return bearerToken.substring(7);
    	}
    	return null;
    }
    
    // Generate JWT token
    public String generateTokenFromUsername(UserDetails userDetails) {
        String username=userDetails.getUsername();
    	return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    // Validate JWT token

    // Extract username from JWT token
    public String getUsernameFromJwtToken(String Token) {
       return Jwts.parserBuilder()
    		   .setSigningKey((SecretKey) key())
    		   .build()
    		   .parseClaimsJws(Token)
    		   .getBody()
    		   .getSubject();
    }
    
    private Key key() {
    	return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
    
    
    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            
            Jwts.parserBuilder()
                .setSigningKey(key())  // key() should return a SecretKey
                .build()
                .parseClaimsJws(authToken);  // parses and validates the token
            
            return true;
        } 
        catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT Token: {}", e.getMessage());
        } 
        catch (ExpiredJwtException e) {
            logger.error("Expired JWT Token: {}", e.getMessage());
        } 
        catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT Token: {}", e.getMessage());
        } 
        catch (IllegalArgumentException e) {
            logger.error("JWT Token string is empty: {}", e.getMessage());
        }
        return false;
    }
    
}
