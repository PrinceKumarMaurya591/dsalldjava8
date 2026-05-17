package com.dsa.designpatterns.behavioral;

// ============================================
// Iterator Pattern
// ============================================
//
// Intent: Provide a way to access the elements of an aggregate object sequentially
// without exposing its underlying representation.
//
// When to use:
// - To access contents of a collection without exposing its internal structure
// - To support multiple traversals of aggregate objects
// - To provide a uniform interface for traversing different aggregate structures
//
// Benefits:
// - Single Responsibility: separates traversal from collection
// - Open/Closed: new iterators can be added for existing collections
// - Supports multiple concurrent traversals
// - Uniform iteration interface across different collections
//
// Real-world examples:
// - java.util.Iterator
// - java.util.Enumeration
// - Java's for-each loop (Iterable interface)
// - Database cursors

import java.util.List;
import java.util.ArrayList;

// Step 1: Iterator interface
interface Iterator<T> {
    boolean hasNext();
    T next();
    T current();
    void reset();
}

// Step 2: Aggregate interface
interface IterableCollection<T> {
    Iterator<T> createIterator();
    Iterator<T> createReverseIterator();
}

// Step 3: Concrete Aggregate
class SongCollection implements IterableCollection<String> {
    private List<String> songs = new ArrayList<>();

    public void addSong(String song) {
        songs.add(song);
    }

    public int size() {
        return songs.size();
    }

    public String get(int index) {
        if (index >= 0 && index < songs.size()) {
            return songs.get(index);
        }
        return null;
    }

    @Override
    public Iterator<String> createIterator() {
        return new SongIterator(this);
    }

    @Override
    public Iterator<String> createReverseIterator() {
        return new ReverseSongIterator(this);
    }
}

// Step 4: Concrete Iterators
class SongIterator implements Iterator<String> {
    private SongCollection collection;
    private int position = 0;

    public SongIterator(SongCollection collection) {
        this.collection = collection;
    }

    @Override
    public boolean hasNext() {
        return position < collection.size();
    }

    @Override
    public String next() {
        if (hasNext()) {
            return collection.get(position++);
        }
        return null;
    }

    @Override
    public String current() {
        return collection.get(position);
    }

    @Override
    public void reset() {
        position = 0;
    }
}

class ReverseSongIterator implements Iterator<String> {
    private SongCollection collection;
    private int position;

    public ReverseSongIterator(SongCollection collection) {
        this.collection = collection;
        this.position = collection.size() - 1;
    }

    @Override
    public boolean hasNext() {
        return position >= 0;
    }

    @Override
    public String next() {
        if (hasNext()) {
            return collection.get(position--);
        }
        return null;
    }

    @Override
    public String current() {
        return collection.get(position);
    }

    @Override
    public void reset() {
        position = collection.size() - 1;
    }
}

// ============================================
// Another example: Tree traversal iterators
// ============================================

class TreeNode {
    int value;
    TreeNode left;
    TreeNode right;

    public TreeNode(int value) {
        this.value = value;
    }
}

class BinaryTreeCollection implements IterableCollection<Integer> {
    private TreeNode root;

    public BinaryTreeCollection(TreeNode root) {
        this.root = root;
    }

    @Override
    public Iterator<Integer> createIterator() {
        return new InOrderIterator(root);
    }

    @Override
    public Iterator<Integer> createReverseIterator() {
        return new PreOrderIterator(root);
    }
}

class InOrderIterator implements Iterator<Integer> {
    private java.util.Stack<TreeNode> stack = new java.util.Stack<>();
    private TreeNode current;

    public InOrderIterator(TreeNode root) {
        this.current = root;
        pushLeft(current);
    }

    private void pushLeft(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public Integer next() {
        if (!hasNext()) return null;
        TreeNode node = stack.pop();
        Integer result = node.value;
        if (node.right != null) {
            pushLeft(node.right);
        }
        return result;
    }

    @Override
    public Integer current() {
        return stack.isEmpty() ? null : stack.peek().value;
    }

    @Override
    public void reset() {
        stack.clear();
        pushLeft(current);
    }
}

class PreOrderIterator implements Iterator<Integer> {
    private java.util.Stack<TreeNode> stack = new java.util.Stack<>();

    public PreOrderIterator(TreeNode root) {
        if (root != null) {
            stack.push(root);
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public Integer next() {
        if (!hasNext()) return null;
        TreeNode node = stack.pop();
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
        return node.value;
    }

    @Override
    public Integer current() {
        return stack.isEmpty() ? null : stack.peek().value;
    }

    @Override
    public void reset() {
        // Would need reference to root to reset properly
    }
}

// Step 5: Demo
public class IteratorPattern {

    public static void main(String[] args) {
        System.out.println("=== Iterator Pattern ===");

        // Song playlist
        System.out.println("\n1. Song Playlist (Forward Iterator):");
        SongCollection playlist = new SongCollection();
        playlist.addSong("Bohemian Rhapsody");
        playlist.addSong("Stairway to Heaven");
        playlist.addSong("Hotel California");
        playlist.addSong("Imagine");
        playlist.addSong("Smells Like Teen Spirit");

        Iterator<String> forward = playlist.createIterator();
        while (forward.hasNext()) {
            System.out.println("  Playing: " + forward.next());
        }

        System.out.println("\n2. Song Playlist (Reverse Iterator):");
        Iterator<String> reverse = playlist.createReverseIterator();
        while (reverse.hasNext()) {
            System.out.println("  Playing: " + reverse.next());
        }

        // Binary tree traversal
        System.out.println("\n3. Binary Tree (In-Order Iterator):");
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(7);

        BinaryTreeCollection tree = new BinaryTreeCollection(root);
        Iterator<Integer> inOrder = tree.createIterator();
        System.out.print("  In-order: ");
        while (inOrder.hasNext()) {
            System.out.print(inOrder.next() + " ");
        }
        System.out.println();

        System.out.println("\n4. Binary Tree (Pre-Order Iterator):");
        Iterator<Integer> preOrder = tree.createReverseIterator();
        System.out.print("  Pre-order: ");
        while (preOrder.hasNext()) {
            System.out.print(preOrder.next() + " ");
        }
        System.out.println();

        System.out.println("\nKey points:");
        System.out.println("- Provides uniform traversal interface");
        System.out.println("- Multiple traversal strategies (forward, reverse, in-order, pre-order)");
        System.out.println("- Hides internal collection structure");
        System.out.println("- Supports multiple concurrent traversals");
    }
}
