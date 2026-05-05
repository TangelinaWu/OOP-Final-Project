/**
 * Strategy interface for all MFA verification methods.
 * Implemented by: EmailAuthenticator, SMSAuthenticator, AppAuthenticator (Person 3)
 */
public interface IAuthenticator {

    /** Returns the display label for this method, e.g. "Email", "SMS", "App" */
    String getMethodName();

    /**
     * Generates a security token and simulates dispatching it
     * (e.g., prints "Token 4920 sent via SMS" to the console).
     * @return the generated token string
     */
    String generateToken();

    /**
     * Verifies the user-supplied code against the last generated token.
     * @param input the code entered by the user
     * @return true if the code matches and has not expired
     */
    boolean verifyToken(String input);
}
