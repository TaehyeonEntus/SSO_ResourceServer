package entus.resourceServer.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 미인증 상태 요청 예외 핸들링
 * 서블릿까지 안들어가서 리턴 타입 명시해줘야함
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        //헤더
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        //바디
        Map<String, String> body = new HashMap<>();
        body.put("error", "access_token_error");
        body.put("message", "인증되지 않은 사용자");

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}