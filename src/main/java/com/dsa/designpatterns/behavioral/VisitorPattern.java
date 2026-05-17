package com.dsa.designpatterns.behavioral;

// ============================================
// Visitor Pattern
// ============================================
//
// Intent: Represent an operation to be performed on the elements of an object
// structure. Visitor lets you define a new operation without changing the classes
// of the elements on which it operates.
//
// When to use:
// - An object structure contains many classes with differing interfaces
// - Many distinct and unrelated operations need to be performed on objects
// - The classes are stable but operations change frequently
//
// Benefits:
// - Open/Closed: new operations can be added without changing element classes
// - Single Responsibility: related behavior is grouped in a single visitor
// - Accumulates state across elements (visitor can maintain state)
//
// Trade-offs:
// - Adding new element types requires changing all visitors
// - Visitors may need access to private fields (breaking encapsulation)
//
// Real-world examples:
// - java.nio.file.FileVisitor (walking file trees)
// - javax.lang.model.element.ElementVisitor
// - ASM library (ClassVisitor, MethodVisitor)
// - Spring BeanPostProcessor

import java.util.List;
import java.util.ArrayList;

// Step 1: Element interface
interface ShapeElement {
    void accept(ShapeVisitor visitor);
    String getName();
}

// Step 2: Concrete Elements
class CircleElement implements ShapeElement {
    private double radius;

    public CircleElement(double radius) {
        this.radius = radius;
    }

    public double getRadius() { return radius; }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getName() { return "Circle"; }
}

class RectangleElement implements ShapeElement {
    private double width;
    private double height;

    public RectangleElement(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getName() { return "Rectangle"; }
}

class TriangleElement implements ShapeElement {
    private double base;
    private double height;

    public TriangleElement(double base, double height) {
        this.base = base;
        this.height = height;
    }

    public double getBase() { return base; }
    public double getHeight() { return height; }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getName() { return "Triangle"; }
}

// Step 3: Visitor interface
interface ShapeVisitor {
    void visit(CircleElement circle);
    void visit(RectangleElement rectangle);
    void visit(TriangleElement triangle);
    String getResult();
}

// Step 4: Concrete Visitors

class AreaCalculator implements ShapeVisitor {
    private double totalArea = 0;
    private StringBuilder details = new StringBuilder();

    @Override
    public void visit(CircleElement circle) {
        double area = Math.PI * circle.getRadius() * circle.getRadius();
        totalArea += area;
        details.append(String.format("    Circle (r=%.1f): Area = %.2f%n",
                circle.getRadius(), area));
    }

    @Override
    public void visit(RectangleElement rectangle) {
        double area = rectangle.getWidth() * rectangle.getHeight();
        totalArea += area;
        details.append(String.format("    Rectangle (%.1f x %.1f): Area = %.2f%n",
                rectangle.getWidth(), rectangle.getHeight(), area));
    }

    @Override
    public void visit(TriangleElement triangle) {
        double area = 0.5 * triangle.getBase() * triangle.getHeight();
        totalArea += area;
        details.append(String.format("    Triangle (b=%.1f, h=%.1f): Area = %.2f%n",
                triangle.getBase(), triangle.getHeight(), area));
    }

    @Override
    public String getResult() {
        return details.toString() + "    Total Area: " + String.format("%.2f", totalArea);
    }
}

class PerimeterCalculator implements ShapeVisitor {
    private double totalPerimeter = 0;
    private StringBuilder details = new StringBuilder();

    @Override
    public void visit(CircleElement circle) {
        double perimeter = 2 * Math.PI * circle.getRadius();
        totalPerimeter += perimeter;
        details.append(String.format("    Circle (r=%.1f): Perimeter = %.2f%n",
                circle.getRadius(), perimeter));
    }

    @Override
    public void visit(RectangleElement rectangle) {
        double perimeter = 2 * (rectangle.getWidth() + rectangle.getHeight());
        totalPerimeter += perimeter;
        details.append(String.format("    Rectangle (%.1f x %.1f): Perimeter = %.2f%n",
                rectangle.getWidth(), rectangle.getHeight(), perimeter));
    }

    @Override
    public void visit(TriangleElement triangle) {
        // Assume equilateral for simplicity
        double perimeter = 3 * triangle.getBase();
        totalPerimeter += perimeter;
        details.append(String.format("    Triangle (b=%.1f): Perimeter = %.2f%n",
                triangle.getBase(), perimeter));
    }

    @Override
    public String getResult() {
        return details.toString() + "    Total Perimeter: " + String.format("%.2f", totalPerimeter);
    }
}

class ShapeDescriptionVisitor implements ShapeVisitor {
    private StringBuilder description = new StringBuilder();

    @Override
    public void visit(CircleElement circle) {
        description.append("    ⭕ Circle with radius " + circle.getRadius() + "\n");
    }

    @Override
    public void visit(RectangleElement rectangle) {
        description.append("    ▬ Rectangle " + rectangle.getWidth()
                + " x " + rectangle.getHeight() + "\n");
    }

    @Override
    public void visit(TriangleElement triangle) {
        description.append("    ▲ Triangle with base " + triangle.getBase()
                + " and height " + triangle.getHeight() + "\n");
    }

    @Override
    public String getResult() {
        return description.toString();
    }
}

// ============================================
// Another example: File System Visitor
// ============================================

interface FileElement {
    void accept(FileVisitor visitor);
    String getName();
    int getSize();
}

class TextFile implements FileElement {
    private String name;
    private int size;
    private int wordCount;

    public TextFile(String name, int size, int wordCount) {
        this.name = name;
        this.size = size;
        this.wordCount = wordCount;
    }

    public int getWordCount() { return wordCount; }

    @Override
    public void accept(FileVisitor visitor) { visitor.visit(this); }
    @Override
    public String getName() { return name; }
    @Override
    public int getSize() { return size; }
}

class ImageFile implements FileElement {
    private String name;
    private int size;
    private int width;
    private int height;

    public ImageFile(String name, int size, int width, int height) {
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public void accept(FileVisitor visitor) { visitor.visit(this); }
    @Override
    public String getName() { return name; }
    @Override
    public int getSize() { return size; }
}

class CompressedFile implements FileElement {
    private String name;
    private int size;
    private int compressionRatio;

    public CompressedFile(String name, int size, int compressionRatio) {
        this.name = name;
        this.size = size;
        this.compressionRatio = compressionRatio;
    }

    public int getCompressionRatio() { return compressionRatio; }

    @Override
    public void accept(FileVisitor visitor) { visitor.visit(this); }
    @Override
    public String getName() { return name; }
    @Override
    public int getSize() { return size; }
}

interface FileVisitor {
    void visit(TextFile file);
    void visit(ImageFile file);
    void visit(CompressedFile file);
    String getResult();
}

class FileIndexer implements FileVisitor {
    private StringBuilder index = new StringBuilder();

    @Override
    public void visit(TextFile file) {
        index.append("    [TEXT] " + file.getName() + " (" + file.getWordCount() + " words)\n");
    }

    @Override
    public void visit(ImageFile file) {
        index.append("    [IMAGE] " + file.getName() + " (" + file.getWidth()
                + "x" + file.getHeight() + ")\n");
    }

    @Override
    public void visit(CompressedFile file) {
        index.append("    [ARCHIVE] " + file.getName() + " (ratio: " + file.getCompressionRatio() + "%)\n");
    }

    @Override
    public String getResult() { return index.toString(); }
}

// Step 5: Demo
public class VisitorPattern {

    public static void main(String[] args) {
        System.out.println("=== Visitor Pattern ===");

        // Shape calculations
        System.out.println("\n1. Shape Calculations:");
        List<ShapeElement> shapes = new ArrayList<>();
        shapes.add(new CircleElement(5));
        shapes.add(new RectangleElement(4, 6));
        shapes.add(new TriangleElement(3, 4));
        shapes.add(new CircleElement(2.5));

        // Calculate area
        System.out.println("  Area Calculation:");
        AreaCalculator areaCalc = new AreaCalculator();
        for (ShapeElement shape : shapes) {
            shape.accept(areaCalc);
        }
        System.out.println(areaCalc.getResult());

        // Calculate perimeter
        System.out.println("\n  Perimeter Calculation:");
        PerimeterCalculator perimeterCalc = new PerimeterCalculator();
        for (ShapeElement shape : shapes) {
            shape.accept(perimeterCalc);
        }
        System.out.println(perimeterCalc.getResult());

        // Describe shapes
        System.out.println("\n  Shape Descriptions:");
        ShapeDescriptionVisitor descVisitor = new ShapeDescriptionVisitor();
        for (ShapeElement shape : shapes) {
            shape.accept(descVisitor);
        }
        System.out.print(descVisitor.getResult());

        // File System Visitor
        System.out.println("\n2. File System Indexer:");
        List<FileElement> files = new ArrayList<>();
        files.add(new TextFile("readme.txt", 1024, 500));
        files.add(new ImageFile("photo.jpg", 2048000, 1920, 1080));
        files.add(new CompressedFile("archive.zip", 512000, 60));
        files.add(new TextFile("notes.txt", 2048, 1000));

        FileIndexer indexer = new FileIndexer();
        for (FileElement file : files) {
            file.accept(indexer);
        }
        System.out.print(indexer.getResult());

        System.out.println("\nKey points:");
        System.out.println("- Separate operations from object structure");
        System.out.println("- New operations added without changing element classes");
        System.out.println("- Visitor can accumulate state across elements");
        System.out.println("- Double dispatch: element type + visitor type");
        System.out.println("- Open/Closed: new visitors are easy to add");
    }
}
