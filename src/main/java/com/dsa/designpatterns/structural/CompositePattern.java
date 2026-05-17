package com.dsa.designpatterns.structural;

// ============================================
// Composite Pattern
// ============================================
//
// Intent: Compose objects into tree structures to represent part-whole hierarchies.
// Composite lets clients treat individual objects and compositions uniformly.
//
// When to use:
// - You want to represent part-whole hierarchies of objects
// - You want clients to be able to ignore the difference between compositions and individuals
// - Tree structures are natural for the problem domain
//
// Benefits:
// - Defines class hierarchies consisting of primitive and complex objects
// - Makes clients simpler (they can treat composite and leaf nodes uniformly)
// - Easier to add new kinds of components
// - Open/Closed: new element types can be added without changing client code
//
// Real-world examples:
// - java.awt.Container (add(Component))
// - javax.swing.JComponent
// - XML/HTML DOM parsing
// - File system directories and files

import java.util.ArrayList;
import java.util.List;

// Step 1: Component - declares the common interface
abstract class FileSystemComponent {
    protected String name;

    public FileSystemComponent(String name) {
        this.name = name;
    }

    public abstract void display(String indent);
    public abstract int getSize();

    // Default implementations for composite operations
    public void add(FileSystemComponent component) {
        throw new UnsupportedOperationException("Cannot add to a leaf node");
    }

    public void remove(FileSystemComponent component) {
        throw new UnsupportedOperationException("Cannot remove from a leaf node");
    }

    public FileSystemComponent getChild(int index) {
        throw new UnsupportedOperationException("Cannot get child from a leaf node");
    }
}

// Step 2: Leaf - represents leaf objects (no children)
class FileLeaf extends FileSystemComponent {
    private int size;

    public FileLeaf(String name, int size) {
        super(name);
        this.size = size;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📄 File: " + name + " (" + size + " KB)");
    }

    @Override
    public int getSize() {
        return size;
    }
}

// Step 3: Composite - stores child components and implements child-related operations
class DirectoryComposite extends FileSystemComponent {
    private List<FileSystemComponent> children = new ArrayList<>();

    public DirectoryComposite(String name) {
        super(name);
    }

    @Override
    public void add(FileSystemComponent component) {
        children.add(component);
    }

    @Override
    public void remove(FileSystemComponent component) {
        children.remove(component);
    }

    @Override
    public FileSystemComponent getChild(int index) {
        if (index >= 0 && index < children.size()) {
            return children.get(index);
        }
        return null;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📁 Directory: " + name + "/");
        for (FileSystemComponent child : children) {
            child.display(indent + "    ");
        }
    }

    @Override
    public int getSize() {
        int totalSize = 0;
        for (FileSystemComponent child : children) {
            totalSize += child.getSize();
        }
        return totalSize;
    }
}

// Step 4: Demo
public class CompositePattern {

    public static void main(String[] args) {
        System.out.println("=== Composite Pattern ===");

        // Build a file system tree
        System.out.println("\nBuilding file system structure...\n");

        // Root directory
        DirectoryComposite root = new DirectoryComposite("root");

        // Documents directory
        DirectoryComposite documents = new DirectoryComposite("Documents");
        documents.add(new FileLeaf("resume.pdf", 500));
        documents.add(new FileLeaf("cover_letter.docx", 120));

        // Pictures directory
        DirectoryComposite pictures = new DirectoryComposite("Pictures");
        pictures.add(new FileLeaf("vacation.jpg", 2048));
        pictures.add(new FileLeaf("family.png", 1536));

        // Sub-directory inside Pictures
        DirectoryComposite screenshots = new DirectoryComposite("Screenshots");
        screenshots.add(new FileLeaf("screenshot1.png", 256));
        screenshots.add(new FileLeaf("screenshot2.png", 384));
        pictures.add(screenshots);

        // Music directory
        DirectoryComposite music = new DirectoryComposite("Music");
        music.add(new FileLeaf("song1.mp3", 5120));
        music.add(new FileLeaf("song2.mp3", 4096));

        // Assemble tree
        root.add(documents);
        root.add(pictures);
        root.add(music);
        root.add(new FileLeaf("README.txt", 10));

        // Display the tree
        System.out.println("File System Structure:");
        root.display("");

        // Calculate total size
        System.out.println("\nTotal size: " + root.getSize() + " KB");

        // Demonstrate uniform treatment
        System.out.println("\n2. Uniform treatment of leaf and composite:");
        FileSystemComponent leaf = new FileLeaf("notes.txt", 50);
        FileSystemComponent dir = new DirectoryComposite("Project");
        dir.add(new FileLeaf("main.java", 100));
        dir.add(new FileLeaf("utils.java", 75));

        System.out.println("Leaf size: " + leaf.getSize() + " KB");
        System.out.println("Directory size: " + dir.getSize() + " KB");

        System.out.println("\nKey points:");
        System.out.println("- Leaf and Composite share the same interface");
        System.out.println("- Clients treat individual objects and compositions uniformly");
        System.out.println("- Easy to add new component types");
        System.out.println("- Natural for tree-structured data");
    }
}
