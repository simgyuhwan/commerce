package com.livecommerce.common.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider(private val jwtProperties: JwtProperties) {
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))
    }

    fun generateToken(userId: Long, role: UserRole): String {
        return Jwts.builder()
            .subject(userId.toString())
            .claim("role", role.name)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration))
            .signWith(secretKey)
            .compact()
    }
    fun validateToken(token: String): Boolean {
        return runCatching {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
            true
        }.getOrElse { false }
    }
    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val userId = claims.subject
        val role = claims.get("role", String::class.java)
        val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
        return UsernamePasswordAuthenticationToken(userId, null, authorities)
    }

    private fun getClaims(token: String) =
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
}