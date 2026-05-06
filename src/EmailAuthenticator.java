public class EmailAuthenticator extends BaseAuthenticator {
    @Override public String getMethodName() { return "Email"; }
    @Override protected void simulateDispatch(String token) {
        System.out.printf("  [Email] Simulated email sent — code: %s (valid %ds)%n", token, TOKEN_TTL_SECONDS);
    }
}
