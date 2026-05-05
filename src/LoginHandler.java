import java.util.Scanner;

/**
 * Handles primary login (username + password).
 * Returns a MFAGateway session on success, or null on failure.
 */
public class LoginHandler {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final UserRepository userRepository;
    private final Scanner scanner;

    public LoginHandler(UserRepository userRepository, Scanner scanner) {
        this.userRepository = userRepository;
        this.scanner = scanner;
    }

    /**
     * Prompts for credentials up to MAX_LOGIN_ATTEMPTS times.
     * @return an MFAGateway bound to the authenticated user, or null if all attempts fail
     */
    public MFAGateway run() {
        System.out.println();
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║           USER  LOGIN            ║");
        System.out.println("╚══════════════════════════════════╝");

        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("  Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("  Password: ");
            String password = scanner.nextLine().trim();

            User user = userRepository.findByUsername(username);
            if (user != null && user.checkPassword(password)) {
                System.out.println("\n  Login successful. Welcome, " + username + "!");
                return new MFAGateway(user);
            }

            int remaining = MAX_LOGIN_ATTEMPTS - attempt;
            if (remaining > 0) {
                System.out.println("\n  Invalid credentials. " + remaining + " attempt(s) remaining.\n");
            } else {
                System.out.println("\n  Too many failed attempts. Access denied.");
            }
        }
        return null;
    }
}
