# Multi-Factor Authentication (MFA) Gateway Simulator

**Date of Submission:** May 7 2026  
**Github URL:** [(https://github.com/TangelinaWu/OOP-Final-Project)]   

### 👥 Team Members 
|Student 1 (Yifan Zuo) | Student 2 ([Angelina Wu]) | Student 3 (Zheqi Zhang) |

---

## 1. System Analysis

### General Description, Goals, and Benefits
This project is an Object-Oriented simulation of a Multi-Factor Authentication (MFA) gateway. The primary goal is to demonstrate robust backend security architecture balanced with a seamless user experience. The system models how users interact with different verification methods (Email, SMS, Authenticator App), allowing them to dynamically select their preferred method during login, securely update their MFA preferences, and view their recent activity within a profile dashboard. 

The main benefit of this system is its modularity; by utilizing the Strategy Design Pattern, new authentication methods can be added to the gateway without altering the core login logic or session management.

### Special Requirements & Constraints
* **Language:** Java (JDK 17+)
* **Interface:** Command-Line Interface (CLI)
* **Constraints:** Must operate without an external database (utilizes an encapsulated in-memory `UserRepository` for simulation).
* **Reliability:** Enforces strict state management. Users are locked out after 3 failed token attempts and must utilize secure backup recovery codes.

---

## 2. Set up & Run 

1. Clone this repository to your local machine.
2. Navigate to the `src` directory:
   ```bash
   cd src
3. Compile the Java files: javac *.java
4. Run the application: java Main

5. Pre-Seeded Demo Accounts & Testing Flow
You can test the system's dynamic routing, 30-second token expiration, and lockout features using these accounts:

Account 1: Alice
Username: alice | Password: password123
Configured Methods: Email, SMS, Authenticator App
Backup Recovery Codes: BACK-1111, BACK-2222, BACK-3333

Account 2: Bob
Username: bob | Password: securepass
Configured Methods: SMS only
Backup Recovery Codes: BACK-9999

Test Scenarios to Try:
    1. Log in, select a method, input the exact generated token, and access the profile dashboard to view the Activity Log.

    2. Log in, request an Authenticator App token, wait 31 seconds, and attempt to use it. Type resend to get a new code.

    3. Deliberately fail the verification 3 times to trigger an account lock. Input a Backup Recovery Code to regain access.

## 3. System Design (UML)

### A. Class Diagram
This diagram details the static structure of the system, highlighting the polymorphic `IAuthenticator` interface, the `BaseAuthenticator` abstract class managing token expiration, and the strict encapsulation of the `User` state.

```mermaid
classDiagram
    direction TB
    
    class IAuthenticator {
        <<interface>>
        +generateToken() String
        +verifyToken(input: String) boolean
        +getMethodName() String
    }
    
    class BaseAuthenticator {
        <<abstract>>
        #TOKEN_TTL_SECONDS: int
        -lastToken: String
        -tokenGeneratedAt: Instant
        +generateToken() String
        #simulateDispatch(token: String)*
        +verifyToken(input: String) boolean
        +isExpired() boolean
        +secondsRemaining() long
        #invalidateToken()
        +resetToken()
    }
    
    class SMSAuthenticator {
        +getMethodName() String
        #simulateDispatch(token: String)
    }
    
    class EmailAuthenticator {
        +getMethodName() String
        #simulateDispatch(token: String)
    }
    
    class AppAuthenticator {
        +getMethodName() String
        #simulateDispatch(token: String)
    }
    
    IAuthenticator <|.. BaseAuthenticator : implements
    BaseAuthenticator <|-- SMSAuthenticator : extends
    BaseAuthenticator <|-- EmailAuthenticator : extends
    BaseAuthenticator <|-- AppAuthenticator : extends

    class User {
        -username: String
        -password: String
        -authMethods: List~IAuthenticator~
        -defaultMethodIndex: int
        -backupCodes: List~String~
        -activityLog: List~String~
        +checkPassword(input: String) boolean
        +addAuthMethod(method: IAuthenticator)
        +removeAuthMethod(methodName: String)
        +consumeBackupCode(code: String) boolean
        +addLog(event: String)
        +getActivityLog() List~String~
    }

    class MFAGateway {
        -user: User
        -activeAuthenticator: IAuthenticator
        -failedAttempts: int
        -locked: boolean
        +selectAuthenticator(index: int) boolean
        +dispatchToken() String
        +verify(input: String) boolean
        +verifyBackupCode(code: String) boolean
        +resetSession()
    }

    User o-- IAuthenticator : owns
    MFAGateway *-- User : manages