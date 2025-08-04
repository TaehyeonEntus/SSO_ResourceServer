package entus.resourceServer.exception.authentication;

public class JwtSignatureException extends JwtAuthenticationException{
    public JwtSignatureException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtSignatureException(String msg) {
        super(msg);
    }
}
