package com.dsa.designpatterns.structural;

// ============================================
// Decorator Pattern
// ============================================
//
// Intent: Attach additional responsibilities to an object dynamically.
// Decorators provide a flexible alternative to subclassing for extending functionality.
//
// When to use:
// - You need to add responsibilities to individual objects, not to entire classes
// - You want to avoid class explosion from subclassing every combination
// - Responsibilities can be added and removed dynamically at runtime
// - Extension by subclassing is impractical (too many combinations)
//
// Benefits:
// - More flexible than static inheritance
// - Avoids feature-laden classes high up in the hierarchy
// - Single Responsibility: a class can focus on one feature, then compose with decorators
// - Open/Closed: new decorators can be added without changing existing code
//
// Real-world examples:
// - java.io.BufferedInputStream(InputStream)
// - java.io.BufferedReader(Reader)
// - java.util.Collections.synchronizedList(List)
// - javax.servlet.http.HttpServletRequestWrapper

// Step 1: Component interface
interface Coffee {
    String getDescription();
    double getCost();
}

// Step 2: Concrete Component
class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Simple coffee";
    }

    @Override
    public double getCost() {
        return 2.0;
    }
}

// Step 3: Base Decorator - maintains reference to Component
abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
}

// Step 4: Concrete Decorators
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.5;
    }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.25;
    }
}

class WhippedCreamDecorator extends CoffeeDecorator {
    public WhippedCreamDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Whipped Cream";
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.75;
    }
}

class CaramelDecorator extends CoffeeDecorator {
    public CaramelDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Caramel";
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.6;
    }
}

// Step 5: Demo
public class DecoratorPattern {

    public static void main(String[] args) {
        System.out.println("=== Decorator Pattern ===");

        // Simple coffee
        System.out.println("\n1. Simple Coffee:");
        Coffee simpleCoffee = new SimpleCoffee();
        System.out.println("   " + simpleCoffee.getDescription());
        System.out.println("   Cost: $" + String.format("%.2f", simpleCoffee.getCost()));

        // Coffee with milk
        System.out.println("\n2. Coffee with Milk:");
        Coffee milkCoffee = new MilkDecorator(new SimpleCoffee());
        System.out.println("   " + milkCoffee.getDescription());
        System.out.println("   Cost: $" + String.format("%.2f", milkCoffee.getCost()));

        // Coffee with milk and sugar
        System.out.println("\n3. Coffee with Milk and Sugar:");
        Coffee milkSugarCoffee = new SugarDecorator(new MilkDecorator(new SimpleCoffee()));
        System.out.println("   " + milkSugarCoffee.getDescription());
        System.out.println("   Cost: $" + String.format("%.2f", milkSugarCoffee.getCost()));

        // Full fancy coffee
        System.out.println("\n4. Full Fancy Coffee (Milk + Sugar + Whipped Cream + Caramel):");
        Coffee fancyCoffee = new CaramelDecorator(
                new WhippedCreamDecorator(
                        new SugarDecorator(
                                new MilkDecorator(
                                        new SimpleCoffee()))));
        System.out.println("   " + fancyCoffee.getDescription());
        System.out.println("   Cost: $" + String.format("%.2f", fancyCoffee.getCost()));

        // Dynamic runtime composition
        System.out.println("\n5. Dynamic runtime composition:");
        Coffee base = new SimpleCoffee();
        System.out.println("   Base: $" + String.format("%.2f", base.getCost()));

        base = new MilkDecorator(base);
        System.out.println("   After adding Milk: $" + String.format("%.2f", base.getCost()));

        base = new CaramelDecorator(base);
        System.out.println("   After adding Caramel: $" + String.format("%.2f", base.getCost()));

        System.out.println("\nKey points:");
        System.out.println("- Decorators wrap objects to add behavior dynamically");
        System.out.println("- More flexible than static inheritance");
        System.out.println("- Can combine decorators in any order");
        System.out.println("- Same interface throughout the chain");
        System.out.println("- Open for extension, closed for modification");
    }
}
