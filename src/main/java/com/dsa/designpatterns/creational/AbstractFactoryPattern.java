package com.dsa.designpatterns.creational;

// ============================================
// Abstract Factory Pattern
// ============================================
//
// Intent: Provide an interface for creating families of related or dependent
// objects without specifying their concrete classes.
//
// When to use:
// - A system should be independent of how its products are created
// - A system should be configured with one of multiple families of products
// - You want to enforce consistency among products
//
// Benefits:
// - Isolates concrete classes from client
// - Makes exchanging product families easy
// - Promotes consistency among products
//
// Difference from Factory Pattern:
// - Factory: Creates one product type via inheritance
// - Abstract Factory: Creates families of related products via composition
//
// Real-world examples:
// - javax.xml.parsers.DocumentBuilderFactory
// - java.sql.Connection (family: Statement, PreparedStatement, etc.)

// Step 1: Abstract product interfaces
interface Button {
    void paint();
}

interface Checkbox {
    void paint();
}

// Step 2: Concrete products for Windows
class WindowsButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendered a Windows-style Button");
    }
}

class WindowsCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendered a Windows-style Checkbox");
    }
}

// Step 3: Concrete products for Mac
class MacButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendered a Mac-style Button");
    }
}

class MacCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendered a Mac-style Checkbox");
    }
}

// Step 4: Abstract factory interface
interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Step 5: Concrete factories
class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

class MacFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}

// Step 6: Client code
class Application {
    private Button button;
    private Checkbox checkbox;

    public Application(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    public void paint() {
        button.paint();
        checkbox.paint();
    }
}

// Step 7: Factory producer
class FactoryProducer {
    public static GUIFactory getFactory(String osType) {
        switch (osType.toLowerCase()) {
            case "windows":
                return new WindowsFactory();
            case "mac":
                return new MacFactory();
            default:
                throw new IllegalArgumentException("Unknown OS: " + osType);
        }
    }
}

// Step 8: Demo
public class AbstractFactoryPattern {

    public static void main(String[] args) {
        System.out.println("=== Abstract Factory Pattern ===");

        // Create Windows UI
        System.out.println("\nWindows UI:");
        Application windowsApp = new Application(FactoryProducer.getFactory("windows"));
        windowsApp.paint();

        // Create Mac UI
        System.out.println("\nMac UI:");
        Application macApp = new Application(FactoryProducer.getFactory("mac"));
        macApp.paint();

        System.out.println("\nKey points:");
        System.out.println("- Client works with abstract interfaces, not concrete classes");
        System.out.println("- Entire families of products can be swapped easily");
        System.out.println("- Products within a family are guaranteed to be compatible");
    }
}
