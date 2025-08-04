package entus.resourceServer.exception.authentication;

public class JwtExpiredException extends JwtAuthenticationException{
    public JwtExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtExpiredException(String msg) {
        super(msg);
    }
}
