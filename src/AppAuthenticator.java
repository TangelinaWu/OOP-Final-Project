public class AppAuthenticator extends BaseAuthenticator {
    @Override public String getMethodName() { return "Authenticator App"; }
    @Override protected void simulateDispatch(String token) {
        System.out.printf("  [App] TOTP code generated — code: %s (valid %ds)%n", token, TOKEN_TTL_SECONDS);
    }
}
