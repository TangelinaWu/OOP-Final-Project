import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates all user state: credentials, active MFA methods, backup codes, and activity logs.
 * Full encapsulation implemented by Person 3; used throughout by Person 1 (CLI) and Person 2 (auth flow).
 */
public class User {

    private final String username;
    private final String password;          // plaintext for simulation; hash in prod
    private final List<IAuthenticator> authMethods = new ArrayList<>();
    private int defaultMethodIndex = 0;
    private final List<String> backupCodes = new ArrayList<>();
    private final List<String> activityLog = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ── Credentials ──────────────────────────────────────────────────────────

    public String getUsername() { return username; }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    // ── MFA Methods ──────────────────────────────────────────────────────────

    public List<IAuthenticator> getAuthMethods() { return authMethods; }

    public void addAuthMethod(IAuthenticator method) {
        authMethods.add(method);
    }

    /** Removes method by display name; no-op if not found. */
    public void removeAuthMethod(String methodName) {
        authMethods.removeIf(m -> m.getMethodName().equalsIgnoreCase(methodName));
        // Guard default index
        if (defaultMethodIndex >= authMethods.size() && !authMethods.isEmpty()) {
            defaultMethodIndex = 0;
        }
    }

    public int getDefaultMethodIndex() { return defaultMethodIndex; }

    public void setDefaultMethodIndex(int index) {
        if (index >= 0 && index < authMethods.size()) {
            defaultMethodIndex = index;
        }
    }

    // ── Backup Codes ─────────────────────────────────────────────────────────

    public List<String> getBackupCodes() { return backupCodes; }

    public void addBackupCode(String code) { backupCodes.add(code); }

    /** Consumes a backup code (one-time use). Returns true if valid. */
    public boolean consumeBackupCode(String code) {
        return backupCodes.remove(code.trim());
    }

    public boolean hasAuthMethods() { return !authMethods.isEmpty(); }

    // ── Activity Logging ─────────────────────────────────────────────────────

    public void addLog(String event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        activityLog.add(timestamp + " - " + event);
    }

    public List<String> getActivityLog() {
        return activityLog;
    }
}