import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory user store for simulation.
 * Pre-seeds demo accounts so the CLI can run without a database.
 */
public class UserRepository {

    private final Map<String, User> store = new HashMap<>();

    public UserRepository() {
        seedDemoUsers();
    }

    public User findByUsername(String username) {
        return store.get(username.toLowerCase());
    }

    public void save(User user) {
        store.put(user.getUsername().toLowerCase(), user);
    }

    // ── Demo data ─────────────────────────────────────────────────────────────
    private void seedDemoUsers() {
        // User 1 — has all three MFA methods
        User alice = new User("alice", "password123");
        alice.addAuthMethod(new EmailAuthenticator());
        alice.addAuthMethod(new SMSAuthenticator());
        alice.addAuthMethod(new AppAuthenticator());
        alice.addBackupCode("BACK-1111");
        alice.addBackupCode("BACK-2222");
        alice.addBackupCode("BACK-3333");
        alice.setDefaultMethodIndex(0);
        store.put("alice", alice);

        // User 2 — SMS only
        User bob = new User("bob", "securepass");
        bob.addAuthMethod(new SMSAuthenticator());
        bob.addBackupCode("BACK-9999");
        store.put("bob", bob);
    }
}
