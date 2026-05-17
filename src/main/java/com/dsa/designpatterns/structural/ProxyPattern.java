package com.dsa.designpatterns.structural;

// ============================================
// Proxy Pattern
// ============================================
//
// Intent: Provide a surrogate or placeholder for another object to control access to it.
//
// When to use:
// - Lazy initialization (virtual proxy): expensive object created on demand
// - Access control (protection proxy): restrict access based on permissions
// - Logging (logging proxy): log method calls
// - Caching (cache proxy): store results of expensive operations
// - Remote proxy: local representative for remote objects
//
// Benefits:
// - Controls access to the real subject
// - Can add functionality without changing the real subject
// - Supports lazy initialization
// - Can manage lifecycle of the real subject
//
// Real-world examples:
// - java.lang.reflect.Proxy (dynamic proxies)
// - Hibernate lazy loading proxies
// - Spring AOP proxies
// - RMI/IIOP stubs (remote proxies)

// Step 1: Subject interface
interface Image {
    void display();
    String getFileName();
}

// Step 2: Real Subject - the actual object
class RealImage implements Image {
    private final String fileName;

    public RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk();
    }

    private void loadFromDisk() {
        System.out.println("    Loading image from disk: " + fileName);
        // Simulate expensive operation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void display() {
        System.out.println("    Displaying image: " + fileName);
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}

// Step 3: Proxy - controls access to RealSubject

// 3a. Virtual Proxy - lazy initialization
class ImageProxy implements Image {
    private final String fileName;
    private RealImage realImage;

    public ImageProxy(String fileName) {
        this.fileName = fileName;
        // No loading - deferred until display() is called
    }

    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(fileName);
        }
        realImage.display();
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}

// 3b. Protection Proxy - access control
class ProtectedImageProxy implements Image {
    private final String fileName;
    private RealImage realImage;
    private final String userRole;

    public ProtectedImageProxy(String fileName, String userRole) {
        this.fileName = fileName;
        this.userRole = userRole;
    }

    @Override
    public void display() {
        if (!userRole.equals("ADMIN")) {
            System.out.println("    Access denied: " + userRole + " cannot view " + fileName);
            return;
        }
        if (realImage == null) {
            realImage = new RealImage(fileName);
        }
        realImage.display();
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}

// 3c. Logging Proxy - adds logging
class LoggingImageProxy implements Image {
    private final Image target;

    public LoggingImageProxy(Image target) {
        this.target = target;
    }

    @Override
    public void display() {
        System.out.println("    [LOG] Displaying image: " + target.getFileName()
                + " at " + System.currentTimeMillis());
        target.display();
        System.out.println("    [LOG] Finished displaying: " + target.getFileName());
    }

    @Override
    public String getFileName() {
        return target.getFileName();
    }
}

// Step 4: Demo
public class ProxyPattern {

    public static void main(String[] args) {
        System.out.println("=== Proxy Pattern ===");

        // Virtual Proxy - lazy initialization
        System.out.println("\n1. Virtual Proxy (Lazy Initialization):");
        System.out.println("   Creating proxy objects (no loading yet)...");
        Image image1 = new ImageProxy("photo1.jpg");
        Image image2 = new ImageProxy("photo2.jpg");
        Image image3 = new ImageProxy("photo3.jpg");

        System.out.println("\n   First display() call triggers loading:");
        image1.display();

        System.out.println("\n   Second display() call uses cached object:");
        image1.display();

        System.out.println("\n   Displaying another image:");
        image2.display();

        // Protection Proxy - access control
        System.out.println("\n2. Protection Proxy (Access Control):");
        Image adminImage = new ProtectedImageProxy("confidential.png", "ADMIN");
        Image userImage = new ProtectedImageProxy("confidential.png", "USER");

        System.out.println("   Admin tries to view:");
        adminImage.display();

        System.out.println("   Regular user tries to view:");
        userImage.display();

        // Logging Proxy
        System.out.println("\n3. Logging Proxy:");
        Image loggedImage = new LoggingImageProxy(new RealImage("report.pdf"));
        loggedImage.display();

        // Combined proxies
        System.out.println("\n4. Combined Proxies (Virtual + Logging):");
        Image combined = new LoggingImageProxy(new ImageProxy("combined.jpg"));
        combined.display();

        System.out.println("\nKey points:");
        System.out.println("- Proxy controls access to the real subject");
        System.out.println("- Virtual Proxy: lazy initialization (defer expensive creation)");
        System.out.println("- Protection Proxy: access control based on permissions");
        System.out.println("- Logging Proxy: add cross-cutting concerns");
        System.out.println("- Proxies can be combined for multiple concerns");
        System.out.println("- Same interface as RealSubject (transparent to client)");
    }
}
