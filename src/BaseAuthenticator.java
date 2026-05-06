import java.security.SecureRandom;
import java.time.Instant;

public abstract class BaseAuthenticator implements IAuthenticator {
    protected static final int TOKEN_TTL_SECONDS = 30;
    private static final SecureRandom RANDOM = new SecureRandom();
    private String lastToken = null;
    private Instant tokenGeneratedAt = null;

    @Override
    public String generateToken() {
        int otp = RANDOM.nextInt(1_000_000);
        lastToken = String.format("%06d", otp);
        tokenGeneratedAt = Instant.now();
        simulateDispatch(lastToken);
        return lastToken;
    }

    protected void simulateDispatch(String token) {
        System.out.printf("  [%s] Code: %s (valid %ds)%n", getMethodName(), token, TOKEN_TTL_SECONDS);
    }

    @Override
    public boolean verifyToken(String input) {
        if (lastToken == null || tokenGeneratedAt == null) {
            System.out.println("  [Auth] No token has been generated.");
            return false;
        }
        if (isExpired()) {
            System.out.printf("  [Auth] Token expired after %ds. Please request a new one.%n", TOKEN_TTL_SECONDS);
            invalidateToken();
            return false;
        }
        boolean match = lastToken.equals(input == null ? "" : input.trim());
        if (match) invalidateToken();
        return match;
    }

    public boolean isExpired() {
        if (tokenGeneratedAt == null) return true;
        long elapsed = Instant.now().getEpochSecond() - tokenGeneratedAt.getEpochSecond();
        return elapsed > TOKEN_TTL_SECONDS;
    }

    public long secondsRemaining() {
        if (tokenGeneratedAt == null) return 0;
        long elapsed = Instant.now().getEpochSecond() - tokenGeneratedAt.getEpochSecond();
        return Math.max(0, TOKEN_TTL_SECONDS - elapsed);
    }

    protected void invalidateToken() { lastToken = null; tokenGeneratedAt = null; }
    public void resetToken() { invalidateToken(); }
}
