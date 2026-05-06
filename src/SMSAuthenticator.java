public class SMSAuthenticator extends BaseAuthenticator {
    @Override public String getMethodName() { return "SMS"; }
    @Override protected void simulateDispatch(String token) {
        System.out.printf("  [SMS] Simulated text sent — code: %s (valid %ds)%n", token, TOKEN_TTL_SECONDS);
    }
}
