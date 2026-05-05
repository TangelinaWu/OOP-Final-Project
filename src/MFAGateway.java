import java.util.Scanner;

/**
 * Session manager — bridges the User object and the chosen IAuthenticator.
 * Core authentication workflow (token expiry, failed-attempt tracking) is
 * extended by Person 2; class relationships are documented by Person 3.
 */
public class MFAGateway {

    private static final int MAX_ATTEMPTS = 3;

    private final User user;
    private IAuthenticator activeAuthenticator;
    private int failedAttempts = 0;

    public MFAGateway(User user) {
        this.user = user;
    }

    // ── Session helpers ───────────────────────────────────────────────────────

    public User getUser() { return user; }

    /** Sets the authenticator to use for the current session. */
    public void selectAuthenticator(int index) {
        activeAuthenticator = user.getAuthMethods().get(index);
    }

    public IAuthenticator getActiveAuthenticator() { return activeAuthenticator; }

    // ── Token flow (Person 2 expands these) ──────────────────────────────────

    /** Dispatches a token and returns it (for testing hooks). */
    public String dispatchToken() {
        if (activeAuthenticator == null) throw new IllegalStateException("No authenticator selected.");
        failedAttempts = 0;
        return activeAuthenticator.generateToken();
    }

    /**
     * Verifies user input. Tracks failed attempts.
     * @return true on success
     */
    public boolean verify(String input) {
        if (activeAuthenticator == null) throw new IllegalStateException("No authenticator selected.");
        boolean ok = activeAuthenticator.verifyToken(input);
        if (!ok) failedAttempts++;
        else failedAttempts = 0;
        return ok;
    }

    public int getFailedAttempts() { return failedAttempts; }

    public boolean isLocked() { return failedAttempts >= MAX_ATTEMPTS; }

    /**
     * Fallback: attempts to verify against the user's backup codes.
     * @return true if a valid backup code was supplied
     */
    public boolean verifyBackupCode(String code) {
        boolean ok = user.consumeBackupCode(code);
        if (ok) failedAttempts = 0;
        return ok;
    }

    public void resetSession() {
        failedAttempts = 0;
        activeAuthenticator = null;
    }
}
