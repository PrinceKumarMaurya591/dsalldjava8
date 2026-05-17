package com.dsa.designpatterns.creational;

// ============================================
// Factory Pattern
// ============================================
//
// Intent: Define an interface for creating an object, but let subclasses decide
// which class to instantiate. Factory Method lets a class defer instantiation
// to subclasses.
//
// When to use:
// - A class can't anticipate the class of objects it must create
// - A class wants its subclasses to specify the objects it creates
// - You want to localize the logic of object creation
//
// Benefits:
// - Eliminates tight coupling between creator and concrete products
// - Follows Single Responsibility Principle (SRP)
// - Follows Open/Closed Principle (OCP) - new products don't break existing code
//
// Real-world examples:
// - java.util.Calendar.getInstance()
// - java.util.ResourceBundle.getBundle()
// - JDBC DriverManager.getConnection()

// Step 1: Product interface
interface Shape {
    void draw();
}

// Step 2: Concrete products
class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing a Circle");
    }
}

class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing a Rectangle");
    }
}

class Square implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing a Square");
    }
}

// Step 3: Factory class
class ShapeFactory {
    public Shape getShape(String shapeType) {
        if (shapeType == null) return null;

        switch (shapeType.toLowerCase()) {
            case "circle":
                return new Circle();
            case "rectangle":
                return new Rectangle();
            case "square":
                return new Square();
            default:
                throw new IllegalArgumentException("Unknown shape type: " + shapeType);
        }
    }
}

// Step 4: Demo
public class FactoryPattern {

    public static void main(String[] args) {
        System.out.println("=== Factory Pattern ===");
        ShapeFactory shapeFactory = new ShapeFactory();

        // Get Circle and call its draw method
        Shape shape1 = shapeFactory.getShape("circle");
        shape1.draw();

        // Get Rectangle and call its draw method
        Shape shape2 = shapeFactory.getShape("rectangle");
        shape2.draw();

        // Get Square and call its draw method
        Shape shape3 = shapeFactory.getShape("square");
        shape3.draw();

        System.out.println("\nKey points:");
        System.out.println("- Client code depends on Shape interface, not concrete classes");
        System.out.println("- New shapes can be added without changing client code");
        System.out.println("- Object creation logic is centralized in the factory");
    }
}
