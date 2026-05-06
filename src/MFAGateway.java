import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MFAGateway {
    public static final int MAX_FAILED_ATTEMPTS = 3;
    private static final DateTimeFormatter LOG_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private User user;
    private IAuthenticator activeAuthenticator;
    private int failedAttempts;
    private boolean locked;

    public MFAGateway(User user) {
        if (user == null) throw new IllegalArgumentException("User must not be null");
        this.user = user;
        this.failedAttempts = 0;
        this.locked = false;
        log("Session initiated for user: " + user.getUsername());
    }

    public boolean selectAuthenticator(int index) {
        IAuthenticator selected = user.getAuthMethod(index);
        if (selected == null) { System.out.println("  [Gateway] Invalid method selection."); return false; }
        activeAuthenticator = selected;
        log("Auth method selected: " + activeAuthenticator.getMethodName());
        return true;
    }

    public String dispatchToken() {
        if (locked) { System.out.println("  [Gateway] Account locked. Use a backup recovery code."); return null; }
        if (activeAuthenticator == null) { System.out.println("  [Gateway] No authentication method selected."); return null; }
        String token = activeAuthenticator.generateToken();
        log("Token dispatched via " + activeAuthenticator.getMethodName());
        return token;
    }

    public boolean verify(String input) {
        if (locked) { System.out.println("  [Gateway] Account locked. Use a backup recovery code."); return false; }
        if (activeAuthenticator == null) { System.out.println("  [Gateway] No authentication method selected."); return false; }
        boolean success = activeAuthenticator.verifyToken(input);
        if (success) {
            failedAttempts = 0;
            log("Verification SUCCESS via " + activeAuthenticator.getMethodName());
        } else {
            failedAttempts++;
            log("Verification FAILED (attempt " + failedAttempts + " of " + MAX_FAILED_ATTEMPTS + ")");
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                locked = true;
                log("ACCOUNT LOCKED — max failed attempts reached");
            }
        }
        return success;
    }

    public boolean verifyBackupCode(String code) {
        if (user.consumeBackupCode(code)) {
            failedAttempts = 0;
            locked = false;
            if (activeAuthenticator instanceof BaseAuthenticator ba) ba.resetToken();
            log("Recovery via backup code. Account unlocked.");
            return true;
        }
        log("Backup code attempt FAILED");
        return false;
    }

    public void resetSession() {
        if (activeAuthenticator instanceof BaseAuthenticator ba) ba.resetToken();
        activeAuthenticator = null;
        failedAttempts = 0;
        locked = false;
        log("Session reset.");
    }

    public boolean isLocked() { return locked; }
    public int getFailedAttempts() { return failedAttempts; }
    public IAuthenticator getActiveAuthenticator() { return activeAuthenticator; }
    public User getUser() { return user; }

    private void log(String event) {
        user.addLog("[" + LocalDateTime.now().format(LOG_FMT) + "] " + event);
    }
}
