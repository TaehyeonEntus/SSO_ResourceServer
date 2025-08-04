package entus.resourceServer.exception;

import entus.resourceServer.exception.authentication.JwtExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if(authException instanceof JwtExpiredException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"access_token_expired\", \"message\": \"access token 만료, 재 발급 요망\"}");
        } else {
            String redirectUrl = "http://localhost:9000/login?redirectUrl=" + URLEncoder.encode(request.getRequestURL().toString(), StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);
        }
    }
}
