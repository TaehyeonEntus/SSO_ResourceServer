package entus.resourceServer.filter.token;

import entus.resourceServer.exception.authentication.JwtAuthenticationException;
import entus.resourceServer.exception.authentication.JwtExpiredException;
import entus.resourceServer.exception.authentication.JwtSignatureException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

/**
 * 1. JWT 서명 검증
 * 2. JWT 만료 여부 검사
 * 3. 인증 정보 Security Context 등록 (접근 제한용)
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecretKey secretKey;

    public JwtAuthenticationFilter(@Value("${JWT_SECRET}") SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException, SignatureException, ExpiredJwtException {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        if (cookies != null)
            for (Cookie cookie : cookies)
                if ("access_token".equals(cookie.getName()))
                    accessToken = cookie.getValue();


        //잘못된 헤더를 보낸 사용자는 security context에 등록되지 않음, public API만 접근 가능
        if (accessToken == null || accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Jws<Claims> claimsJws;
        String jwt = accessToken;

        try {
            // 1. JWT 서명 검증
            // 2. JWT 만료 여부 검사
            claimsJws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwt);

            String userId = claimsJws.getPayload().getSubject();
            String role = claimsJws.getPayload().get("role", String.class);

            //FE에 제공 하기 위한 정보 (미 사용시 주석 처리)
            //String name = claimsJws.getPayload().get("name", String.class);
            //request.setAttribute("name", name);

            // 3. 인증 정보 Security Context 등록 (접근 제한용)
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("JWT 토큰 만료", e);
        } catch (SignatureException e) {
            throw new JwtSignatureException("JWT 서명 오류", e);
        } catch (JwtException e) {
            throw new JwtAuthenticationException("JWT 오류", e);
        }

        filterChain.doFilter(request, response);
    }
}
