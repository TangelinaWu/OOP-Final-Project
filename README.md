# Multi-Factor Authentication (MFA) Gateway

## Project Title
Multi-Factor Authentication (MFA) Gateway: A User-Centric Security Simulator

## Project Objective
This project simulates a Multi-Factor Authentication gateway that combines secure backend design with a clear, user-friendly CLI experience. Users can log in, choose a verification method (Email, SMS, or Authenticator App), and manage their MFA preferences in a profile dashboard.

## System Overview
The system is a Java command-line application centered around an MFA gateway flow:
1. User logs in with username and password.
2. User selects an available MFA method.
3. System generates and validates verification tokens.
4. If verification fails repeatedly, fallback recovery flow is offered.
5. User can manage MFA settings from a profile dashboard.

## Team Members
[Angelina Wu](github.com/TangelinaWu)

## Run Instructions (Java CLI)
From project root:

```bash
cd src
javac *.java
java Main
```

## Demo Credentials
- `alice / password123`
- `bob / securepass`

## Core Features
- Dynamic authentication routing after successful primary login.
- Profile and preference management (add/remove methods, set default).
- Simulated token dispatch and verification.
- Failed-attempt handling with fallback backup code support.
- Strategy Pattern-based architecture for interchangeable MFA methods.

## Architecture and OOP Design
The project uses the Strategy Design Pattern to support multiple MFA methods cleanly.

- `IAuthenticator` defines shared behavior:
  - `generateToken()`
  - `verifyToken(String input)`
- Concrete strategies:
  - `EmailAuthenticator`
  - `SMSAuthenticator`
  - `AppAuthenticator`
- `User` encapsulates credentials, active authenticators, default method, and backup codes.
- `MFAGateway` manages session state and coordinates user-authenticator interaction.

## Current Project Structure
- `ProjectProposal` - Initial proposal and requirements.
- `src/Main.java` - Application entry point and top-level CLI menu.
- `src/LoginHandler.java` - Primary login prompt and credential checks.
- `src/MFASelectionMenu.java` - MFA choice, token prompt, and fallback path.
- `src/ProfileDashboard.java` - Profile settings and MFA preference management.
- `src/MFAGateway.java` - Gateway/session coordination logic.
- `src/User.java` - User model and encapsulated state.
- `src/IAuthenticator.java` - Strategy interface for MFA methods.
- `src/AuthenticatorImpls.java` - MFA strategy class implementations/stubs.
- `src/UserRepository.java` - In-memory demo user store.

## UML Documentation Scope
The final report includes:
- Use Case Diagram(s):
  - Login flow
  - MFA method selection
  - Profile settings updates
- Sequence Diagram(s):
  - Login flow
  - MFA token generation
  - Token verification
  - Recovery/fallback process
- Class Diagram(s):
  - `IAuthenticator`
  - `EmailAuthenticator`
  - `SMSAuthenticator`
  - `AppAuthenticator`
  - `User`
  - `MFAGateway`

## Team Task Split
### Person 1
- Documentation:
  - Project objective
  - System overview
  - Core features
  - User interaction explanation
  - Use case diagram + use case descriptions
- Coding:
  - CLI menu system
  - Login prompts
  - MFA selection menus
  - Profile settings dashboard
  - Console I/O formatting

### Person 2
- Documentation:
  - Sequence diagrams for login, token generation, token verification, and recovery flow
- Coding:
  - Token generation/verification workflow details
  - Expiration handling
  - Failed-attempt tracking
  - MFA flow integration with `MFAGateway`

### Person 3
- Documentation:
  - Class diagrams
  - Conclusion (accomplishments, design pattern usage, future improvements)
- Coding:
  - Backend class completion/refinement:
    - `IAuthenticator`
    - Authenticator subclasses
    - `User`
    - `MFAGateway`
  - Object relationships and encapsulation
  - Integration/testing support

### Shared Responsibilities
- Push code to GitHub.
- Review and test implementation.
- Fix bugs collaboratively.
- Integrate all components for final submission.


