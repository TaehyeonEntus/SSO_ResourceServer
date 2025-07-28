package entus.resourceServer.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ExpiredJwtException.class)
    //access token 만료
    public ResponseEntity<?> handleExpiredJwt() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "access_token_expired"));
    }

    //유효하지 않은 서명, 에러가 반복되면 secret key에 문제가 있을 수 있음, 관리자 연락
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid_signature"));
    }
}
