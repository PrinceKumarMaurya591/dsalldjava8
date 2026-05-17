package com.dsa.designpatterns.creational;

// ============================================
// Singleton Pattern
// ============================================
//
// Intent: Ensure a class has only one instance and provide a global point of
// access to it.
//
// When to use:
// - Exactly one instance of a class is needed
// - The instance must be accessible from a well-known access point
// - The sole instance should be extensible by subclassing
//
// Benefits:
// - Controlled access to sole instance
// - Reduced memory footprint
// - Lazy initialization possible
// - Global access point
//
// Real-world examples:
// - java.lang.Runtime.getRuntime()
// - java.awt.Desktop.getDesktop()
// - Logger classes, Configuration classes
// - Database connection pools

// ============================================
// Implementation 1: Eager Initialization
// ============================================
// - Instance created at class loading time
// - Thread-safe by default
// - Wastes resources if never used
class EagerSingleton {
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {}

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }

    public void showMessage() {
        System.out.println("EagerSingleton: Instance hash = " + System.identityHashCode(this));
    }
}

// ============================================
// Implementation 2: Lazy Initialization (not thread-safe)
// ============================================
class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("LazySingleton: Instance hash = " + System.identityHashCode(this));
    }
}

// ============================================
// Implementation 3: Thread-Safe (synchronized method)
// ============================================
class ThreadSafeSingleton {
    private static ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {}

    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("ThreadSafeSingleton: Instance hash = " + System.identityHashCode(this));
    }
}

// ============================================
// Implementation 4: Double-Checked Locking (DCL)
// ============================================
class DoubleCheckedLockingSingleton {
    private static volatile DoubleCheckedLockingSingleton instance;

    private DoubleCheckedLockingSingleton() {}

    public static DoubleCheckedLockingSingleton getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedLockingSingleton.class) {
                if (instance == null) {
                    instance = new DoubleCheckedLockingSingleton();
                }
            }
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("DCLSingleton: Instance hash = " + System.identityHashCode(this));
    }
}

// ============================================
// Implementation 5: Bill Pugh (Static Inner Class)
// ============================================
// - Most efficient thread-safe approach
// - Lazy initialization without synchronization overhead
class BillPughSingleton {
    private BillPughSingleton() {}

    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void showMessage() {
        System.out.println("BillPughSingleton: Instance hash = " + System.identityHashCode(this));
    }
}

// ============================================
// Implementation 6: Enum Singleton
// ============================================
// - Best approach according to Effective Java (Joshua Bloch)
// - Inherently thread-safe, serialization-safe, reflection-safe
enum EnumSingleton {
    INSTANCE;

    public void showMessage() {
        System.out.println("EnumSingleton: Instance hash = " + System.identityHashCode(this));
    }
}

// Demo
public class SingletonPattern {

    public static void main(String[] args) {
        System.out.println("=== Singleton Pattern ===");

        // Test Eager Singleton
        System.out.println("\n1. Eager Singleton:");
        EagerSingleton eager1 = EagerSingleton.getInstance();
        EagerSingleton eager2 = EagerSingleton.getInstance();
        System.out.println("Same instance? " + (eager1 == eager2));
        eager1.showMessage();
        eager2.showMessage();

        // Test Lazy Singleton
        System.out.println("\n2. Lazy Singleton:");
        LazySingleton lazy1 = LazySingleton.getInstance();
        LazySingleton lazy2 = LazySingleton.getInstance();
        System.out.println("Same instance? " + (lazy1 == lazy2));

        // Test Thread-Safe Singleton
        System.out.println("\n3. Thread-Safe Singleton:");
        ThreadSafeSingleton ts1 = ThreadSafeSingleton.getInstance();
        ThreadSafeSingleton ts2 = ThreadSafeSingleton.getInstance();
        System.out.println("Same instance? " + (ts1 == ts2));

        // Test Double-Checked Locking Singleton
        System.out.println("\n4. Double-Checked Locking Singleton:");
        DoubleCheckedLockingSingleton dcl1 = DoubleCheckedLockingSingleton.getInstance();
        DoubleCheckedLockingSingleton dcl2 = DoubleCheckedLockingSingleton.getInstance();
        System.out.println("Same instance? " + (dcl1 == dcl2));

        // Test Bill Pugh Singleton
        System.out.println("\n5. Bill Pugh Singleton:");
        BillPughSingleton bp1 = BillPughSingleton.getInstance();
        BillPughSingleton bp2 = BillPughSingleton.getInstance();
        System.out.println("Same instance? " + (bp1 == bp2));

        // Test Enum Singleton
        System.out.println("\n6. Enum Singleton:");
        EnumSingleton enum1 = EnumSingleton.INSTANCE;
        EnumSingleton enum2 = EnumSingleton.INSTANCE;
        System.out.println("Same instance? " + (enum1 == enum2));
        enum1.showMessage();
        enum2.showMessage();

        System.out.println("\nKey points:");
        System.out.println("- All implementations ensure only one instance exists");
        System.out.println("- Enum Singleton is the most robust (serialization & reflection safe)");
        System.out.println("- Bill Pugh is best for performance (lazy + no synchronization overhead)");
    }
}
