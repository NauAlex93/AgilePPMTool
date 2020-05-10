package ru.managementtool.ppmtool.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.managementtool.ppmtool.domain.User;
import ru.managementtool.ppmtool.exceptions.ProjectException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ru.managementtool.ppmtool.security.SecurityConstants.EXPIRATION_TIME;
import static ru.managementtool.ppmtool.security.SecurityConstants.SECRET;

@Component
public class JwtTokenProvider {
    public String generateToken(Authentication authentication){
        User user = (User)authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());

        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String,Object> claims = new HashMap<>();
        claims.put("id", (Long.toString(user.getId())));
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFullName());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
            throw new ProjectException("Invalid JWT Signature");
        }catch (MalformedJwtException ex){
            throw new ProjectException("Invalid JWT Token");
        }catch (ExpiredJwtException ex){
            throw new ProjectException("Expired JWT token");
        }catch (UnsupportedJwtException ex){
            throw new ProjectException("Unsupported JWT token");
        }catch (IllegalArgumentException ex){
            throw new ProjectException("JWT claims string is empty");
        }
    }

    public Long getUserIdFromJWT(String token){
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        String id = (String)claims.get("id");

        return Long.parseLong(id);
    }
}