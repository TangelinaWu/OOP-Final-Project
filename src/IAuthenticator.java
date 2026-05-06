public interface IAuthenticator {
    String getMethodName();
    String generateToken();
    boolean verifyToken(String input);
}
