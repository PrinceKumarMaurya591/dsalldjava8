package com.dsa.designpatterns.behavioral;

// ============================================
// State Pattern
// ============================================
//
// Intent: Allow an object to alter its behavior when its internal state changes.
// The object will appear to change its class.
//
// When to use:
// - An object's behavior depends on its state, and it must change at runtime
// - Operations have large, multipart conditional statements that depend on state
// - State-specific behavior should be localized and easy to add
//
// Benefits:
// - Localizes state-specific behavior (each state in its own class)
// - Makes state transitions explicit
// - Eliminates complex if/else or switch statements
// - Open/Closed: new states can be added easily
//
// Real-world examples:
// - java.util.Iterator (hasNext/next state-dependent)
// - TCP connection states (ESTABLISHED, LISTEN, CLOSE_WAIT)
// - Vending machine states
// - Workflow engines

// Step 1: State interface
interface VendingMachineState {
    void insertCoin(VendingMachine machine);
    void selectProduct(VendingMachine machine);
    void dispense(VendingMachine machine);
    void refund(VendingMachine machine);
}

// Step 2: Concrete States

class NoCoinState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine) {
        System.out.println("    Coin inserted");
        machine.setState(new HasCoinState());
    }

    @Override
    public void selectProduct(VendingMachine machine) {
        System.out.println("    Insert coin first");
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("    Insert coin first");
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("    No coin to refund");
    }
}

class HasCoinState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine) {
        System.out.println("    Coin already inserted. Select a product or get refund.");
    }

    @Override
    public void selectProduct(VendingMachine machine) {
        if (machine.hasProducts()) {
            System.out.println("    Product selected");
            machine.setState(new DispensingState());
        } else {
            System.out.println("    Sold out! Get refund.");
            machine.refund();
        }
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("    Select a product first");
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("    Coin refunded");
        machine.setState(new NoCoinState());
    }
}

class DispensingState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine) {
        System.out.println("    Please wait, dispensing product");
    }

    @Override
    public void selectProduct(VendingMachine machine) {
        System.out.println("    Already dispensing, please wait");
    }

    @Override
    public void dispense(VendingMachine machine) {
        machine.dispenseProduct();
        System.out.println("    Product dispensed! Thank you!");
        if (machine.hasProducts()) {
            machine.setState(new NoCoinState());
        } else {
            machine.setState(new SoldOutState());
        }
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("    Cannot refund, product already being dispensed");
    }
}

class SoldOutState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine) {
        System.out.println("    Sold out! Coin returned");
    }

    @Override
    public void selectProduct(VendingMachine machine) {
        System.out.println("    Sold out!");
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("    Sold out!");
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("    No coin to refund");
    }
}

// Step 3: Context - maintains state and delegates to state objects
class VendingMachine {
    private VendingMachineState state;
    private int productCount;

    public VendingMachine(int productCount) {
        this.productCount = productCount;
        this.state = new NoCoinState();
        System.out.println("  Vending Machine initialized with " + productCount + " products");
    }

    public void setState(VendingMachineState state) {
        this.state = state;
    }

    public boolean hasProducts() {
        return productCount > 0;
    }

    public void dispenseProduct() {
        if (productCount > 0) {
            productCount--;
        }
    }

    // Delegated actions
    public void insertCoin() {
        System.out.print("  Action: Insert coin → ");
        state.insertCoin(this);
    }

    public void selectProduct() {
        System.out.print("  Action: Select product → ");
        state.selectProduct(this);
    }

    public void dispense() {
        System.out.print("  Action: Dispense → ");
        state.dispense(this);
    }

    public void refund() {
        System.out.print("  Action: Refund → ");
        state.refund(this);
    }

    public int getProductCount() { return productCount; }
}

// ============================================
// Another example: Document Workflow
// ============================================

interface DocumentState {
    void review(Document document);
    void approve(Document document);
    void reject(Document document);
    void submit(Document document);
    String getStatusName();
}

class DraftState implements DocumentState {
    @Override
    public void review(Document document) {
        System.out.println("    Cannot review a draft. Submit first.");
    }

    @Override
    public void approve(Document document) {
        System.out.println("    Cannot approve a draft. Submit first.");
    }

    @Override
    public void reject(Document document) {
        System.out.println("    Cannot reject a draft.");
    }

    @Override
    public void submit(Document document) {
        System.out.println("    Document submitted for review");
        document.setState(new ReviewState());
    }

    @Override
    public String getStatusName() { return "Draft"; }
}

class ReviewState implements DocumentState {
    @Override
    public void review(Document document) {
        System.out.println("    Document is being reviewed");
    }

    @Override
    public void approve(Document document) {
        System.out.println("    Document approved!");
        document.setState(new ApprovedState());
    }

    @Override
    public void reject(Document document) {
        System.out.println("    Document rejected, returning to draft");
        document.setState(new DraftState());
    }

    @Override
    public void submit(Document document) {
        System.out.println("    Already submitted for review");
    }

    @Override
    public String getStatusName() { return "In Review"; }
}

class ApprovedState implements DocumentState {
    @Override
    public void review(Document document) {
        System.out.println("    Document already approved");
    }

    @Override
    public void approve(Document document) {
        System.out.println("    Document already approved");
    }

    @Override
    public void reject(Document document) {
        System.out.println("    Cannot reject an approved document");
    }

    @Override
    public void submit(Document document) {
        System.out.println("    Document already approved");
    }

    @Override
    public String getStatusName() { return "Approved"; }
}

class Document {
    private DocumentState state;
    private String title;

    public Document(String title) {
        this.title = title;
        this.state = new DraftState();
        System.out.println("  Document \"" + title + "\" created (Status: " + state.getStatusName() + ")");
    }

    public void setState(DocumentState state) {
        this.state = state;
        System.out.println("  Status changed to: " + state.getStatusName());
    }

    public void submit() { System.out.print("  Action: Submit → "); state.submit(this); }
    public void review() { System.out.print("  Action: Review → "); state.review(this); }
    public void approve() { System.out.print("  Action: Approve → "); state.approve(this); }
    public void reject() { System.out.print("  Action: Reject → "); state.reject(this); }
}

// Step 4: Demo
public class StatePattern {

    public static void main(String[] args) {
        System.out.println("=== State Pattern ===");

        // Vending Machine
        System.out.println("\n1. Vending Machine:");
        VendingMachine machine = new VendingMachine(2);

        machine.selectProduct();  // No coin
        machine.insertCoin();     // Coin inserted
        machine.insertCoin();     // Already has coin
        machine.selectProduct();  // Product selected
        machine.insertCoin();     // Dispensing
        machine.dispense();       // Product dispensed

        System.out.println("\n  Products remaining: " + machine.getProductCount());

        machine.insertCoin();
        machine.selectProduct();
        machine.dispense();       // Last product

        System.out.println("\n  Products remaining: " + machine.getProductCount());
        machine.insertCoin();     // Sold out

        // Document Workflow
        System.out.println("\n2. Document Workflow:");
        Document doc = new Document("Design Spec");

        doc.approve();  // Can't approve draft
        doc.submit();   // Submit for review
        doc.submit();   // Already submitted
        doc.review();   // Being reviewed
        doc.approve();  // Approved!
        doc.reject();   // Can't reject approved

        System.out.println("\nKey points:");
        System.out.println("- Object behavior changes with internal state");
        System.out.println("- Each state is encapsulated in its own class");
        System.out.println("- Eliminates complex conditional logic");
        System.out.println("- State transitions are explicit and controlled");
    }
}
