import java.util.List;
import java.util.Scanner;

/**
 * Presents the MFA method selection menu, dispatches the token,
 * and collects/verifies the user's code.
 * Falls back to backup codes after MAX_ATTEMPTS failures.
 */
public class MFASelectionMenu {

    private final MFAGateway gateway;
    private final Scanner scanner;

    public MFASelectionMenu(MFAGateway gateway, Scanner scanner) {
        this.gateway = gateway;
        this.scanner = scanner;
    }

    /**
     * Runs the full MFA verification flow.
     * @return true if the user successfully verified
     */
    public boolean run() {
        User user = gateway.getUser();
        List<IAuthenticator> methods = user.getAuthMethods();

        if (methods.isEmpty()) {
            System.out.println("\n  [ERROR] No MFA methods configured for this account.");
            return false;
        }

        // ── Method selection ─────────────────────────────────────────────────
        System.out.println();
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║     SELECT  VERIFICATION  METHOD ║");
        System.out.println("╚══════════════════════════════════╝");

        for (int i = 0; i < methods.size(); i++) {
            String defaultTag = (i == user.getDefaultMethodIndex()) ? " [default]" : "";
            System.out.printf("  [%d] %s%s%n", i + 1, methods.get(i).getMethodName(), defaultTag);
        }
        System.out.println("  [0] Use backup recovery code");
        System.out.print("\n  Enter choice: ");

        int choice = readIntInRange(0, methods.size());

        if (choice == 0) {
            return runBackupCodeFlow();
        }

        gateway.selectAuthenticator(choice - 1);

        // ── Token dispatch & verification ────────────────────────────────────
        System.out.println();
        gateway.dispatchToken();

        while (!gateway.isLocked()) {
            System.out.print("\n  Enter verification code: ");
            String input = scanner.nextLine().trim();

            if (gateway.verify(input)) {
                System.out.println("\n  Verification successful!");
                return true;
            }

            int remaining = 3 - gateway.getFailedAttempts();
            if (remaining > 0) {
                System.out.println("  Incorrect code. " + remaining + " attempt(s) remaining.");
            }
        }

        // ── Locked — offer fallback ──────────────────────────────────────────
        System.out.println("\n  Too many failed attempts.");
        System.out.print("  Would you like to use a backup recovery code instead? (y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("y")) {
            return runBackupCodeFlow();
        }
        return false;
    }

    // ── Backup code sub-flow ─────────────────────────────────────────────────
    private boolean runBackupCodeFlow() {
        System.out.println();
        System.out.println("  ── Backup Recovery Code ──");
        System.out.print("  Enter recovery code: ");
        String code = scanner.nextLine().trim();

        if (gateway.verifyBackupCode(code)) {
            System.out.println("  Recovery code accepted. Access granted.");
            return true;
        }
        System.out.println("  Invalid recovery code. Access denied.");
        return false;
    }

    // ── Input helper ─────────────────────────────────────────────────────────
    private int readIntInRange(int min, int max) {
        while (true) {
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val >= min && val <= max) return val;
            } catch (NumberFormatException ignored) {}
            System.out.print("  Please enter a number between " + min + " and " + max + ": ");
        }
    }
}
