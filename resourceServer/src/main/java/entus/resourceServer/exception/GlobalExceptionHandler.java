package entus.resourceServer.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 *  비즈니스 로직에서 터지는 예외 핸들링
 */

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({AccessTokenException.class})
    public ResponseEntity<Map<String, String>> handleAccessTokenException() {
        Map<String, String> body = new HashMap<>();
        body.put("error", "access_token_error");
        body.put("message", "Access token is invalid or expired.");
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(body);
    }
}