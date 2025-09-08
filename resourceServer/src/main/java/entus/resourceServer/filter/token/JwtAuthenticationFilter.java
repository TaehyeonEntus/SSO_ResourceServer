package entus.resourceServer.filter.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
/**
 * 1. JWT 서명 검증
 * 2. JWT 만료 여부 검사
 * 3. 인증 정보 Security Context 등록 (접근 제한용)
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecretKey secretKey;

    public JwtAuthenticationFilter(@Value("${JWT_SECRET}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        if (cookies != null)
            for (Cookie cookie : cookies)
                if ("access_token".equals(cookie.getName()))
                    accessToken = cookie.getValue();


        //익명 사용자는 public paths만 접근 가능함~!
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Jws<Claims> claimsJws = null;
        String jwt = accessToken;
        // 1. JWT 서명 검증
        // 2. JWT 만료 여부 검사
        try {
            claimsJws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwt);
        } catch (JwtException e) {
            filterChain.doFilter(request, response);
        }
        String userId = claimsJws.getPayload().getSubject();
        String role = claimsJws.getPayload().get("role", String.class);

        //FE에 제공 하기 위한 정보 (미 사용시 주석 처리)
        //String name = claimsJws.getPayload().get("name", String.class);
        //request.setAttribute("name", name);

        // 3. 인증 정보 Security Context 등록 (접근 제한용)
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
