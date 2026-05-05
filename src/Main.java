import java.util.Scanner;

/**
 * Entry point — CLI menu system for the MFA Gateway simulator.
 * Person 1 owns this file and all menu/UI classes.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserRepository userRepository = new UserRepository();

    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMainMenu();
            System.out.print("  Enter choice: ");
            int choice = readIntInRange(0, 2);

            switch (choice) {
                case 1 -> runLoginFlow();
                case 2 -> printAbout();
                case 0 -> running = false;
            }
        }

        System.out.println("\n  Goodbye!\n");
        scanner.close();
    }

    // ── Main menu ─────────────────────────────────────────────────────────────
    private static void printMainMenu() {
        System.out.println();
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║      MFA  GATEWAY  SIMULATOR     ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  [1] Log In                      ║");
        System.out.println("║  [2] About                       ║");
        System.out.println("║  [0] Exit                        ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    // ── Full login + MFA + post-login session ─────────────────────────────────
    private static void runLoginFlow() {
        // Step 1: primary credential check
        LoginHandler loginHandler = new LoginHandler(userRepository, scanner);
        MFAGateway gateway = loginHandler.run();

        if (gateway == null) return;    // failed login — back to main menu

        // Step 2: MFA verification
        MFASelectionMenu mfaMenu = new MFASelectionMenu(gateway, scanner);
        boolean verified = mfaMenu.run();

        if (!verified) {
            System.out.println("\n  MFA verification failed. Returning to main menu.");
            return;
        }

        // Step 3: post-login session menu
        runSessionMenu(gateway);
    }

    private static void runSessionMenu(MFAGateway gateway) {
        boolean active = true;
        while (active) {
            printSessionMenu(gateway.getUser().getUsername());
            System.out.print("  Enter choice: ");
            int choice = readIntInRange(0, 2);

            switch (choice) {
                case 1 -> new ProfileDashboard(gateway, scanner).run();
                case 2 -> System.out.println("\n  [Feature placeholder] View activity log.");
                case 0 -> active = false;
            }
        }
        gateway.resetSession();
        System.out.println("\n  Logged out successfully.");
    }

    private static void printSessionMenu(String username) {
        System.out.println();
        System.out.println("╔══════════════════════════════════╗");
        System.out.printf( "║  Welcome, %-22s║%n", username + "!");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  [1] Profile & MFA Settings      ║");
        System.out.println("║  [2] View Activity Log           ║");
        System.out.println("║  [0] Log Out                     ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    // ── About ─────────────────────────────────────────────────────────────────
    private static void printAbout() {
        System.out.println();
        System.out.println("  MFA Gateway Simulator — OOP Final Project");
        System.out.println("  Simulates multi-factor authentication via Email, SMS,");
        System.out.println("  and Authenticator App using the Strategy Design Pattern.");
        System.out.println("  Demo accounts: alice / password123  |  bob / securepass");
    }

    // ── Banner ────────────────────────────────────────────────────────────────
    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║   Multi-Factor Authentication Gateway    ║");
        System.out.println("  ║          Security Simulator v1.0         ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
    }

    // ── Input helper ─────────────────────────────────────────────────────────
    private static int readIntInRange(int min, int max) {
        while (true) {
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val >= min && val <= max) return val;
            } catch (NumberFormatException ignored) {}
            System.out.print("  Please enter a number between " + min + " and " + max + ": ");
        }
    }
}
