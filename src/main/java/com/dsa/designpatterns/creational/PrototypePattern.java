package com.dsa.designpatterns.creational;

// ============================================
// Prototype Pattern
// ============================================
//
// Intent: Specify the kinds of objects to create using a prototypical instance,
// and create new objects by copying this prototype.
//
// When to use:
// - Object creation is expensive (e.g., database calls, network requests)
// - Objects have many common fields with few variations
// - You want to avoid subclassing for object creation
//
// Benefits:
// - Reduces need for subclassing
// - Hides complexity of object creation
// - Better performance than creating new instances
// - Can add/remove objects at runtime
//
// Real-world examples:
// - java.lang.Object.clone()
// - Cell division in biology (mitosis)
// - Document templates (copy with modifications)

// Step 1: Prototype interface (implements Cloneable)
abstract class ShapePrototype implements Cloneable {
    private String id;
    protected String type;

    abstract void draw();

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // The clone method is the key part of the Prototype pattern
    @Override
    public ShapePrototype clone() {
        try {
            return (ShapePrototype) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Step 2: Concrete prototypes
class CirclePrototype extends ShapePrototype {
    private int radius;
    private String color;

    public CirclePrototype() {
        type = "Circle";
        radius = 10;
        color = "Red";
    }

    public CirclePrototype(int radius, String color) {
        this.type = "Circle";
        this.radius = radius;
        this.color = color;
    }

    @Override
    void draw() {
        System.out.println("Drawing a " + color + " Circle with radius " + radius);
    }

    // Getters and setters for customization
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public CirclePrototype clone() {
        CirclePrototype cloned = (CirclePrototype) super.clone();
        // For primitive fields, super.clone() handles shallow copy correctly
        return cloned;
    }
}

class RectanglePrototype extends ShapePrototype {
    private int width;
    private int height;
    private String color;

    public RectanglePrototype() {
        type = "Rectangle";
        width = 20;
        height = 10;
        color = "Blue";
    }

    public RectanglePrototype(int width, int height, String color) {
        this.type = "Rectangle";
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    void draw() {
        System.out.println("Drawing a " + color + " Rectangle (" + width + "x" + height + ")");
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public RectanglePrototype clone() {
        return (RectanglePrototype) super.clone();
    }
}

// Step 3: Prototype registry (cache)
class ShapeCache {
    private static java.util.Map<String, ShapePrototype> shapeMap = new java.util.HashMap<>();

    public static ShapePrototype getShape(String shapeId) {
        ShapePrototype cachedShape = shapeMap.get(shapeId);
        // Return a cloned copy (not the original)
        return cachedShape.clone();
    }

    // Load cache with prototype objects
    public static void loadCache() {
        CirclePrototype circle = new CirclePrototype();
        circle.setId("1");
        shapeMap.put(circle.getId(), circle);

        RectanglePrototype rectangle = new RectanglePrototype();
        rectangle.setId("2");
        shapeMap.put(rectangle.getId(), rectangle);
    }
}

// Step 4: Demo
public class PrototypePattern {

    public static void main(String[] args) {
        System.out.println("=== Prototype Pattern ===");

        // Load the prototype cache
        ShapeCache.loadCache();

        // Get cloned shapes from cache
        System.out.println("\nGetting shapes from cache (cloned copies):");
        ShapePrototype clonedCircle = ShapeCache.getShape("1");
        System.out.print("Shape 1: ");
        clonedCircle.draw();

        ShapePrototype clonedRectangle = ShapeCache.getShape("2");
        System.out.print("Shape 2: ");
        clonedRectangle.draw();

        // Demonstrate that cloned objects can be modified independently
        System.out.println("\nModifying cloned copies independently:");
        if (clonedCircle instanceof CirclePrototype) {
            CirclePrototype circle = (CirclePrototype) clonedCircle;
            circle.setColor("Green");
            circle.setRadius(25);
            System.out.print("Modified circle: ");
            circle.draw();
        }

        // Get another clone from cache (still has original values)
        ShapePrototype anotherCircle = ShapeCache.getShape("1");
        System.out.print("Fresh clone from cache: ");
        anotherCircle.draw();

        System.out.println("\nKey points:");
        System.out.println("- Objects are created by cloning prototypes, not using 'new'");
        System.out.println("- Cloned objects can be modified independently");
        System.out.println("- Prototype registry provides easy access to prototypes");
        System.out.println("- Avoids expensive object creation (e.g., database calls)");
    }
}
