import java.util.List;
import java.util.Scanner;

/**
 * Profile & Preference Management Dashboard.
 * Lets an authenticated user add/remove MFA methods and change their default.
 */
public class ProfileDashboard {

    private final MFAGateway gateway;
    private final Scanner scanner;

    public ProfileDashboard(MFAGateway gateway, Scanner scanner) {
        this.gateway = gateway;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printDashboard();
            System.out.print("  Enter choice: ");
            int choice = readIntInRange(0, 4);

            switch (choice) {
                case 1 -> viewMethods();
                case 2 -> addMethod();
                case 3 -> removeMethod();
                case 4 -> changeDefault();
                case 0 -> running = false;
            }
        }
    }

    // ── Menu display ─────────────────────────────────────────────────────────
    private void printDashboard() {
        User user = gateway.getUser();
        System.out.println();
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║      PROFILE  SETTINGS           ║");
        System.out.printf( "║  Logged in as: %-18s║%n", user.getUsername());
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  [1] View MFA methods            ║");
        System.out.println("║  [2] Add MFA method              ║");
        System.out.println("║  [3] Remove MFA method           ║");
        System.out.println("║  [4] Change default MFA method   ║");
        System.out.println("║  [0] Back to main menu           ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    // ── Option handlers ───────────────────────────────────────────────────────
    private void viewMethods() {
        User user = gateway.getUser();
        List<IAuthenticator> methods = user.getAuthMethods();
        System.out.println("\n  ── Your MFA Methods ──");
        if (methods.isEmpty()) {
            System.out.println("  No methods configured.");
            return;
        }
        for (int i = 0; i < methods.size(); i++) {
            String tag = (i == user.getDefaultMethodIndex()) ? " ★ default" : "";
            System.out.printf("  [%d] %s%s%n", i + 1, methods.get(i).getMethodName(), tag);
        }
        System.out.printf("%n  Backup codes remaining: %d%n", user.getBackupCodes().size());
    }

    private void addMethod() {
        System.out.println("\n  ── Add MFA Method ──");
        System.out.println("  [1] Email");
        System.out.println("  [2] SMS");
        System.out.println("  [3] Authenticator App");
        System.out.println("  [0] Cancel");
        System.out.print("\n  Enter choice: ");
        int choice = readIntInRange(0, 3);

        IAuthenticator method = switch (choice) {
            case 1 -> new EmailAuthenticator();
            case 2 -> new SMSAuthenticator();
            case 3 -> new AppAuthenticator();
            default -> null;
        };

        if (method == null) { System.out.println("  Cancelled."); return; }

        // Prevent duplicates
        boolean exists = gateway.getUser().getAuthMethods().stream()
                .anyMatch(m -> m.getMethodName().equalsIgnoreCase(method.getMethodName()));
        if (exists) {
            System.out.println("  That method is already configured.");
            return;
        }

        gateway.getUser().addAuthMethod(method);
        System.out.println("  " + method.getMethodName() + " added successfully.");
    }

    private void removeMethod() {
        User user = gateway.getUser();
        List<IAuthenticator> methods = user.getAuthMethods();

        if (methods.isEmpty()) { System.out.println("\n  No methods to remove."); return; }

        System.out.println("\n  ── Remove MFA Method ──");
        for (int i = 0; i < methods.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, methods.get(i).getMethodName());
        }
        System.out.println("  [0] Cancel");
        System.out.print("\n  Enter choice: ");
        int choice = readIntInRange(0, methods.size());

        if (choice == 0) { System.out.println("  Cancelled."); return; }

        String name = methods.get(choice - 1).getMethodName();
        user.removeAuthMethod(name);
        System.out.println("  " + name + " removed.");
    }

    private void changeDefault() {
        User user = gateway.getUser();
        List<IAuthenticator> methods = user.getAuthMethods();

        if (methods.isEmpty()) { System.out.println("\n  No methods configured."); return; }

        System.out.println("\n  ── Change Default MFA Method ──");
        for (int i = 0; i < methods.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, methods.get(i).getMethodName());
        }
        System.out.println("  [0] Cancel");
        System.out.print("\n  Enter choice: ");
        int choice = readIntInRange(0, methods.size());

        if (choice == 0) { System.out.println("  Cancelled."); return; }

        user.setDefaultMethodIndex(choice - 1);
        System.out.println("  Default method set to: " + methods.get(choice - 1).getMethodName());
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
