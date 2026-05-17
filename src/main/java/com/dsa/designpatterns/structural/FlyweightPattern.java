package com.dsa.designpatterns.structural;

// ============================================
// Flyweight Pattern
// ============================================
//
// Intent: Use sharing to support large numbers of fine-grained objects efficiently.
//
// When to use:
// - Large numbers of objects are needed
// - Memory cost of storing all objects is high
// - Most object state can be made extrinsic (context-dependent)
// - The application doesn't depend on object identity
//
// Benefits:
// - Reduces memory footprint significantly
// - Reduces number of objects
// - Centralizes state management
//
// Trade-offs:
// - Increased complexity (separating intrinsic/extrinsic state)
// - May increase CPU time (computing extrinsic state)
// - Requires careful thread-safety consideration
//
// Real-world examples:
// - java.lang.String.intern()
// - Integer.valueOf() (caches -128 to 127)
// - Text editors (character glyphs shared across document)
// - Game development (tree/particle rendering)

import java.util.HashMap;
import java.util.Map;

// Step 1: Flyweight interface
interface TreeModel {
    void display(int x, int y, int height);
}

// Step 2: Concrete Flyweight - intrinsic state (shared)
class TreeType implements TreeModel {
    private final String name;      // intrinsic
    private final String color;     // intrinsic
    private final String texture;   // intrinsic

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
        System.out.println("    [Creating TreeType: " + name + " (" + color + ", " + texture + ")]");
    }

    @Override
    public void display(int x, int y, int height) {
        System.out.println("    Tree '" + name + "' at (" + x + "," + y + ") height=" + height
                + " [color=" + color + ", texture=" + texture + "]");
    }
}

// Step 3: Flyweight Factory - creates and manages flyweight objects
class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String texture) {
        String key = name + "|" + color + "|" + texture;
        TreeType type = treeTypes.get(key);
        if (type == null) {
            type = new TreeType(name, color, texture);
            treeTypes.put(key, type);
        }
        return type;
    }

    public static int getTotalTreeTypes() {
        return treeTypes.size();
    }
}

// Step 4: Context class - extrinsic state (not shared)
class Tree {
    private final int x;            // extrinsic
    private final int y;            // extrinsic
    private final int height;       // extrinsic
    private final TreeType type;    // shared flyweight

    public Tree(int x, int y, int height, TreeType type) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.type = type;
    }

    public void display() {
        type.display(x, y, height);
    }
}

// Step 5: Forest - client that manages trees
class Forest {
    private final java.util.List<Tree> trees = new java.util.ArrayList<>();

    public void plantTree(int x, int y, int height, String name, String color, String texture) {
        TreeType type = TreeFactory.getTreeType(name, color, texture);
        Tree tree = new Tree(x, y, height, type);
        trees.add(tree);
    }

    public void display() {
        System.out.println("\n  Displaying " + trees.size() + " trees:");
        for (Tree tree : trees) {
            tree.display();
        }
    }

    public int getTotalTrees() {
        return trees.size();
    }
}

// Step 6: Demo
public class FlyweightPattern {

    public static void main(String[] args) {
        System.out.println("=== Flyweight Pattern ===");

        // Create a forest
        Forest forest = new Forest();

        System.out.println("\nPlanting trees in the forest...");

        // Plant many trees - only 3 unique tree types needed
        forest.plantTree(10, 20, 5, "Oak", "Green", "Rough");
        forest.plantTree(30, 40, 7, "Oak", "Green", "Rough");
        forest.plantTree(50, 60, 6, "Oak", "Green", "Rough");

        forest.plantTree(15, 25, 3, "Birch", "Yellow", "Smooth");
        forest.plantTree(35, 45, 4, "Birch", "Yellow", "Smooth");

        forest.plantTree(70, 80, 10, "Pine", "Dark Green", "Needle");
        forest.plantTree(90, 100, 12, "Pine", "Dark Green", "Needle");
        forest.plantTree(110, 120, 9, "Pine", "Dark Green", "Needle");
        forest.plantTree(130, 140, 11, "Pine", "Dark Green", "Needle");

        // Display all trees
        forest.display();

        // Show memory savings
        System.out.println("\n  --- Memory Efficiency ---");
        System.out.println("  Total trees planted: " + forest.getTotalTrees());
        System.out.println("  Unique tree types (shared): " + TreeFactory.getTotalTreeTypes());
        System.out.println("  Memory saved: " + (forest.getTotalTrees() - TreeFactory.getTotalTreeTypes())
                + " object creations avoided!");

        // Demonstrate that same type is reused
        System.out.println("\n2. Verifying object sharing:");
        TreeType type1 = TreeFactory.getTreeType("Oak", "Green", "Rough");
        TreeType type2 = TreeFactory.getTreeType("Oak", "Green", "Rough");
        System.out.println("    Same object reused: " + (type1 == type2));

        System.out.println("\nKey points:");
        System.out.println("- Intrinsic state (shared): name, color, texture");
        System.out.println("- Extrinsic state (context): position, height");
        System.out.println("- Flyweight factory ensures sharing");
        System.out.println("- Significant memory savings with many objects");
        System.out.println("- Useful when object identity is not important");
    }
}
