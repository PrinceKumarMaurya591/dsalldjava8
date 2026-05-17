package com.dsa.designpatterns.behavioral;

// ============================================
// Chain of Responsibility Pattern
// ============================================
//
// Intent: Avoid coupling the sender of a request to its receiver by giving more
// than one object a chance to handle the request. Chain the receiving objects
// and pass the request along the chain until an object handles it.
//
// When to use:
// - More than one object may handle a request, and the handler isn't known a priori
// - You want to issue a request to one of several objects without specifying the receiver
// - The set of handlers should be defined dynamically
//
// Benefits:
// - Reduced coupling (sender doesn't know which handler processes the request)
// - Added flexibility in assigning responsibilities to objects
// - Allows dynamic chain configuration
//
// Real-world examples:
// - java.util.logging.Logger (log levels propagate up)
// - javax.servlet.Filter (filter chains)
// - Spring Security filter chain
// - Exception handling (catch blocks)

// Step 1: Handler interface
abstract class Logger {
    public static final int INFO = 1;
    public static final int DEBUG = 2;
    public static final int ERROR = 3;

    protected int level;
    protected Logger nextLogger;

    public void setNextLogger(Logger nextLogger) {
        this.nextLogger = nextLogger;
    }

    public void logMessage(int level, String message) {
        if (this.level <= level) {
            write(message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }

    protected abstract void write(String message);
}

// Step 2: Concrete Handlers
class ConsoleLogger extends Logger {
    public ConsoleLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("    [Console] " + message);
    }
}

class FileLogger extends Logger {
    public FileLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("    [File] " + message);
    }
}

class EmailLogger extends Logger {
    public EmailLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("    [Email] " + message);
    }
}

// ============================================
// Another example: ATM Dispenser
// ============================================

abstract class Dispenser {
    protected Dispenser nextDispenser;
    protected int denomination;

    public Dispenser(int denomination) {
        this.denomination = denomination;
    }

    public void setNext(Dispenser next) {
        this.nextDispenser = next;
    }

    public void dispense(int amount) {
        if (amount >= denomination) {
            int count = amount / denomination;
            int remainder = amount % denomination;
            System.out.println("    Dispensing " + count + " x $" + denomination + " notes");
            if (remainder > 0 && nextDispenser != null) {
                nextDispenser.dispense(remainder);
            } else if (remainder > 0) {
                System.out.println("    Cannot dispense remaining: $" + remainder);
            }
        } else if (nextDispenser != null) {
            nextDispenser.dispense(amount);
        } else {
            System.out.println("    Cannot dispense: $" + amount);
        }
    }
}

class HundredDispenser extends Dispenser {
    public HundredDispenser() { super(100); }
}

class FiftyDispenser extends Dispenser {
    public FiftyDispenser() { super(50); }
}

class TwentyDispenser extends Dispenser {
    public TwentyDispenser() { super(20); }
}

class TenDispenser extends Dispenser {
    public TenDispenser() { super(10); }
}

// Step 3: Demo
public class ChainOfResponsibilityPattern {

    private static Logger getLoggerChain() {
        Logger consoleLogger = new ConsoleLogger(Logger.INFO);
        Logger fileLogger = new FileLogger(Logger.DEBUG);
        Logger emailLogger = new EmailLogger(Logger.ERROR);

        consoleLogger.setNextLogger(fileLogger);
        fileLogger.setNextLogger(emailLogger);

        return consoleLogger;
    }

    private static Dispenser getDispenserChain() {
        Dispenser hundred = new HundredDispenser();
        Dispenser fifty = new FiftyDispenser();
        Dispenser twenty = new TwentyDispenser();
        Dispenser ten = new TenDispenser();

        hundred.setNext(fifty);
        fifty.setNext(twenty);
        twenty.setNext(ten);

        return hundred;
    }

    public static void main(String[] args) {
        System.out.println("=== Chain of Responsibility Pattern ===");

        // Logger chain
        System.out.println("\n1. Logger Chain:");
        Logger loggerChain = getLoggerChain();

        System.out.println("  Logging INFO message:");
        loggerChain.logMessage(Logger.INFO, "This is an info message");

        System.out.println("\n  Logging DEBUG message:");
        loggerChain.logMessage(Logger.DEBUG, "This is a debug message");

        System.out.println("\n  Logging ERROR message:");
        loggerChain.logMessage(Logger.ERROR, "This is an error message");

        // ATM Dispenser chain
        System.out.println("\n2. ATM Dispenser Chain:");
        Dispenser atm = getDispenserChain();

        System.out.println("  Withdrawing $380:");
        atm.dispense(380);

        System.out.println("\n  Withdrawing $70:");
        atm.dispense(70);

        System.out.println("\n  Withdrawing $25:");
        atm.dispense(25);

        System.out.println("\nKey points:");
        System.out.println("- Request passes through chain until handled");
        System.out.println("- Each handler decides to process and/or forward");
        System.out.println("- Chain can be configured dynamically");
        System.out.println("- Decouples sender from receiver");
    }
}
