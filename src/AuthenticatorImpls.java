import java.util.Random;

/**
 * Stub authenticator implementations — full logic implemented by Person 2 & 3.
 * Token generation/expiration/failed-attempt tracking hooks are marked TODO.
 */

// ─── Email ───────────────────────────────────────────────────────────────────
class EmailAuthenticator implements IAuthenticator {
    private String lastToken = "";

    @Override
    public String getMethodName() { return "Email"; }

    @Override
    public String generateToken() {
        lastToken = String.format("%04d", new Random().nextInt(10000));
        System.out.println("  [SIM] Token " + lastToken + " sent via Email.");
        // TODO (Person 2): start expiration timer
        return lastToken;
    }

    @Override
    public boolean verifyToken(String input) {
        // TODO (Person 2): check expiration, track failed attempts
        return lastToken.equals(input.trim());
    }
}

// ─── SMS ─────────────────────────────────────────────────────────────────────
class SMSAuthenticator implements IAuthenticator {
    private String lastToken = "";

    @Override
    public String getMethodName() { return "SMS"; }

    @Override
    public String generateToken() {
        lastToken = String.format("%04d", new Random().nextInt(10000));
        System.out.println("  [SIM] Token " + lastToken + " sent via SMS.");
        // TODO (Person 2): start expiration timer
        return lastToken;
    }

    @Override
    public boolean verifyToken(String input) {
        // TODO (Person 2): check expiration, track failed attempts
        return lastToken.equals(input.trim());
    }
}

// ─── Authenticator App ───────────────────────────────────────────────────────
class AppAuthenticator implements IAuthenticator {
    private String lastToken = "";

    @Override
    public String getMethodName() { return "Authenticator App"; }

    @Override
    public String generateToken() {
        lastToken = String.format("%06d", new Random().nextInt(1_000_000));
        System.out.println("  [SIM] TOTP token " + lastToken + " generated in Authenticator App.");
        // TODO (Person 2): start 30-second TOTP window
        return lastToken;
    }

    @Override
    public boolean verifyToken(String input) {
        // TODO (Person 2): check 30-second window, track failed attempts
        return lastToken.equals(input.trim());
    }
}
