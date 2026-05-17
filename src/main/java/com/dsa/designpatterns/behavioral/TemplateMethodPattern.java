package com.dsa.designpatterns.behavioral;

// ============================================
// Template Method Pattern
// ============================================
//
// Intent: Define the skeleton of an algorithm in an operation, deferring some
// steps to subclasses. Template Method lets subclasses redefine certain steps
// of an algorithm without changing the algorithm's structure.
//
// When to use:
// - To implement the invariant parts of an algorithm once
// - Common behavior among subclasses should be factored to avoid duplication
// - To control extensions at specific points (hook operations)
//
// Benefits:
// - Code reuse (common algorithm structure in base class)
// - Inversion of control (Hollywood Principle: "Don't call us, we'll call you")
// - Hook operations for optional customization
// - Ensures algorithm structure is consistent
//
// Real-world examples:
// - java.io.InputStream.read() (subclasses implement read())
// - java.util.AbstractList
// - javax.servlet.http.HttpServlet (doGet, doPost)
// - Spring JdbcTemplate (execute, query, update)

// Step 1: Abstract class with template method
abstract class BeverageMaker {

    // Template method - defines the algorithm skeleton
    public final void makeBeverage() {
        boilWater();
        brew();
        pourInCup();
        addCondiments();
        if (customerWantsExtras()) {
            addExtras();
        }
        serve();
    }

    // Common steps (implemented in base class)
    private void boilWater() {
        System.out.println("    Boiling water");
    }

    private void pourInCup() {
        System.out.println("    Pouring into cup");
    }

    private void serve() {
        System.out.println("    Beverage is ready! ☕\n");
    }

    // Steps to be implemented by subclasses
    protected abstract void brew();
    protected abstract void addCondiments();

    // Hook method - optional override, default returns false
    protected boolean customerWantsExtras() {
        return false;
    }

    // Hook method - optional override
    protected void addExtras() {
        // Default: no extras
    }
}

// Step 2: Concrete classes implementing the template

class Tea extends BeverageMaker {
    @Override
    protected void brew() {
        System.out.println("    Steeping the tea bag");
    }

    @Override
    protected void addCondiments() {
        System.out.println("    Adding lemon");
    }

    @Override
    protected boolean customerWantsExtras() {
        return true;
    }

    @Override
    protected void addExtras() {
        System.out.println("    Adding honey");
    }
}

class Coffee extends BeverageMaker {
    @Override
    protected void brew() {
        System.out.println("    Dripping coffee through filter");
    }

    @Override
    protected void addCondiments() {
        System.out.println("    Adding sugar and milk");
    }
}

class HotChocolate extends BeverageMaker {
    @Override
    protected void brew() {
        System.out.println("    Mixing cocoa powder with hot water");
    }

    @Override
    protected void addCondiments() {
        System.out.println("    Adding marshmallows");
    }

    @Override
    protected boolean customerWantsExtras() {
        return true;
    }

    @Override
    protected void addExtras() {
        System.out.println("    Adding whipped cream");
    }
}

// ============================================
// Another example: Data Migration
// ============================================

abstract class DataMigrator {

    // Template method
    public final void migrate() {
        System.out.println("  Starting data migration...");
        extract();
        transform();
        if (shouldValidate()) {
            validate();
        }
        load();
        if (shouldCleanup()) {
            cleanup();
        }
        logCompletion();
    }

    // Required steps
    protected abstract void extract();
    protected abstract void transform();
    protected abstract void load();

    // Optional steps with default implementations
    protected void validate() {
        System.out.println("    Validating data integrity");
    }

    protected void cleanup() {
        System.out.println("    Cleaning up temporary resources");
    }

    // Hook methods
    protected boolean shouldValidate() { return true; }
    protected boolean shouldCleanup() { return false; }

    private void logCompletion() {
        System.out.println("  Migration completed successfully!\n");
    }
}

class CsvToDatabaseMigrator extends DataMigrator {
    @Override
    protected void extract() {
        System.out.println("    Extracting data from CSV file");
    }

    @Override
    protected void transform() {
        System.out.println("    Transforming CSV rows to database records");
    }

    @Override
    protected void load() {
        System.out.println("    Loading records into database");
    }

    @Override
    protected boolean shouldCleanup() {
        return true;
    }

    @Override
    protected void cleanup() {
        System.out.println("    Archiving CSV file");
    }
}

class ApiToDataWarehouseMigrator extends DataMigrator {
    @Override
    protected void extract() {
        System.out.println("    Fetching data from REST API");
    }

    @Override
    protected void transform() {
        System.out.println("    Normalizing API response to warehouse schema");
    }

    @Override
    protected void validate() {
        System.out.println("    Running data quality checks");
    }

    @Override
    protected void load() {
        System.out.println("    Loading into data warehouse");
    }

    @Override
    protected boolean shouldValidate() {
        return true;
    }
}

// Step 3: Demo
public class TemplateMethodPattern {

    public static void main(String[] args) {
        System.out.println("=== Template Method Pattern ===");

        // Beverage Maker
        System.out.println("\n1. Beverage Maker:");
        System.out.println("  Making Tea:");
        BeverageMaker tea = new Tea();
        tea.makeBeverage();

        System.out.println("  Making Coffee:");
        BeverageMaker coffee = new Coffee();
        coffee.makeBeverage();

        System.out.println("  Making Hot Chocolate:");
        BeverageMaker hotChocolate = new HotChocolate();
        hotChocolate.makeBeverage();

        // Data Migration
        System.out.println("2. Data Migration:");
        System.out.println("  CSV to Database:");
        DataMigrator csvMigrator = new CsvToDatabaseMigrator();
        csvMigrator.migrate();

        System.out.println("  API to Data Warehouse:");
        DataMigrator apiMigrator = new ApiToDataWarehouseMigrator();
        apiMigrator.migrate();

        System.out.println("Key points:");
        System.out.println("- Template method defines algorithm skeleton");
        System.out.println("- Subclasses implement specific steps");
        System.out.println("- Hook methods allow optional customization");
        System.out.println("- Common code is reused in base class");
        System.out.println("- Hollywood Principle: base class controls the flow");
    }
}
