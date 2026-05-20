package com.agenda.interactiva.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Utilidad para el manejo de JSON Web Tokens (JWT).
 * 
 * ¿Qué es un JWT?
 * Es un estándar que define una forma compacta y segura de transmitir información entre
 * el frontend y el backend como un objeto JSON. Esta información puede ser verificada y
 * es confiable porque está firmada digitalmente con una clave secreta.
 * 
 * Esta clase proporciona funciones para:
 * 1. Generar nuevos tokens firmados tras el inicio de sesión.
 * 2. Extraer el nombre de usuario de un token recibido.
 * 3. Validar si el token es legítimo y no ha expirado.
 */
@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final long jwtExpiration;

    // Inyectamos la clave secreta y el tiempo de expiración desde application.properties
    public JwtUtil(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration) {
        
        // Decodificamos el secreto o bien lo convertimos directamente a bytes.
        // Usamos bytes UTF-8 para inicializar la clave HMAC de forma robusta.
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * Genera un nuevo Token JWT firmado para un usuario específico.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // Asignamos el nombre de usuario como sujeto del token
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de expiración (24h)
                .signWith(signingKey) // Firma digital utilizando la clave secreta
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) contenido en el token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un Claim (información payload) específico del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae y parsea todo el conjunto de Claims (datos) del token usando la clave de firma
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey) // Verifica la firma digital del token recibido
                .build()
                .parseSignedClaims(token) // Parsea el token firmado
                .getPayload(); // Devuelve el contenido (payload)
    }

    // Comprueba si el token ya expiró
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida si el token es válido comparando el nombre de usuario y asegurando que no ha expirado.
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
