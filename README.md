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
Pre-Seeded Demo Accounts:
    Username: alice | Password: password123 (Configured with Email, SMS, and App)
    Username: bob | Password: securepass (Configured with SMS only)

## 3. System Design(UML)
    A. Class Diagram   
        This diagram details the static structure of the system, highlighting the polymorphic IAuthenticator interface and the strict encapsulation of the User state, including activity logging.

        classDiagram
        direction LR
        
        class IAuthenticator {
            <<interface>>
            +generateToken() String
            +verifyToken(input: String) boolean
            +getMethodName() String
        }
        
        class SMSAuthenticator {
            -lastToken: String
            +generateToken() String
            +verifyToken(input: String) boolean
            +getMethodName() String
        }
        
        class EmailAuthenticator {
            -lastToken: String
            +generateToken() String
            +verifyToken(input: String) boolean
            +getMethodName() String
        }
        
        class AppAuthenticator {
            -lastToken: String
            +generateToken() String
            +verifyToken(input: String) boolean
            +getMethodName() String
        }
        
        IAuthenticator <|.. SMSAuthenticator : implements
        IAuthenticator <|.. EmailAuthenticator : implements
        IAuthenticator <|.. AppAuthenticator : implements

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
            +dispatchToken() String
            +verify(input: String) boolean
            +verifyBackupCode(code: String) boolean
            +resetSession()
        }

        User o-- IAuthenticator : owns
        MFAGateway *-- User : manages


    B. Sequence Diagram
        This diagram illustrates the dynamic object interaction during a successful login and token verification event.

        sequenceDiagram
        actor U as User
        participant CLI as Main (UI)
        participant DB as UserRepository
        participant GW as MFAGateway
        participant Auth as IAuthenticator

        U->>CLI: Enters Username & Password
        CLI->>DB: findByUsername(username)
        DB-->>CLI: Returns User Object
        
        CLI->>GW: Initiates Session(User)
        CLI->>GW: selectAuthenticator(index)
        
        CLI->>GW: dispatchToken()
        GW->>Auth: generateToken()
        Auth-->>GW: Returns Token
        GW-->>CLI: Prints Simulated Dispatch
        
        CLI->>U: Prompts for Verification Code
        U->>CLI: Inputs Code
        
        CLI->>GW: verify(input)
        GW->>Auth: verifyToken(input)
        Auth-->>GW: Returns True
        GW-->>CLI: Returns True
        
        CLI-->>U: Access Granted


    C. Uses Cases Diagram
        This outlines the boundaries of the simulator and the actions available to the user from the CLI.

        graph LR
        User((User))
        
        subgraph MFA Gateway Simulator
            UC1([UC-01: Login with Credentials])
            UC2([UC-02: Select MFA Method])
            UC3([UC-03: Generate Secure Token])
            UC4([UC-04: Verify MFA Token])
            UC5([UC-05: Update Default MFA Settings])
            UC6([UC-06: View Activity Log])
        end
        
        User --> UC1
        User --> UC4
        User --> UC5
        User --> UC6
        
        UC1 -. "<<includes>>" .-> UC2
        UC2 -. "<<includes>>" .-> UC3
        UC4 -. "<<includes>>" .-> UC3
---
