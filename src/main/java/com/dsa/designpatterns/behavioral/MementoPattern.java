package com.dsa.designpatterns.behavioral;

// ============================================
// Memento Pattern
// ============================================
//
// Intent: Without violating encapsulation, capture and externalize an object's
// internal state so that the object can be restored to this state later.
//
// When to use:
// - A snapshot of an object's state must be saved for later restoration
// - A direct interface to obtaining the state would expose implementation details
//
// Benefits:
// - Preserves encapsulation boundaries
// - Simplifies the originator (doesn't need to manage version history)
// - Provides undo/rollback capabilities
//
// Real-world examples:
// - java.util.Date (cloneable)
// - Text editor undo/redo
// - Game save points
// - Database transactions (rollback)

import java.util.Stack;

// Step 1: Memento - stores internal state of the Originator
class TextMemento {
    private final String content;
    private final int cursorPosition;
    private final long timestamp;

    public TextMemento(String content, int cursorPosition) {
        this.content = content;
        this.cursorPosition = cursorPosition;
        this.timestamp = System.currentTimeMillis();
    }

    public String getContent() { return content; }
    public int getCursorPosition() { return cursorPosition; }
    public long getTimestamp() { return timestamp; }
}

// Step 2: Originator - creates and restores mementos
class TextEditor {
    private StringBuilder content;
    private int cursorPosition;

    public TextEditor() {
        this.content = new StringBuilder();
        this.cursorPosition = 0;
    }

    public void write(String text) {
        content.insert(cursorPosition, text);
        cursorPosition += text.length();
        System.out.println("    Wrote: \"" + text + "\"");
        System.out.println("    Content: \"" + content + "\"");
    }

    public void delete(int chars) {
        if (chars > 0 && cursorPosition >= chars) {
            String deleted = content.substring(cursorPosition - chars, cursorPosition);
            content.delete(cursorPosition - chars, cursorPosition);
            cursorPosition -= chars;
            System.out.println("    Deleted: \"" + deleted + "\"");
            System.out.println("    Content: \"" + content + "\"");
        }
    }

    public void moveCursor(int position) {
        if (position >= 0 && position <= content.length()) {
            cursorPosition = position;
            System.out.println("    Cursor moved to position " + position);
        }
    }

    // Create memento
    public TextMemento save() {
        System.out.println("    [Saved state: \"" + content + "\"]");
        return new TextMemento(content.toString(), cursorPosition);
    }

    // Restore from memento
    public void restore(TextMemento memento) {
        this.content = new StringBuilder(memento.getContent());
        this.cursorPosition = memento.getCursorPosition();
        System.out.println("    [Restored: \"" + content + "\"]");
    }

    public String getContent() { return content.toString(); }
    public int getCursorPosition() { return cursorPosition; }
}

// Step 3: Caretaker - manages mementos (does not modify them)
class History {
    private Stack<TextMemento> undoStack = new Stack<>();
    private Stack<TextMemento> redoStack = new Stack<>();

    public void saveState(TextMemento memento) {
        undoStack.push(memento);
        redoStack.clear(); // Clear redo on new action
    }

    public TextMemento undo() {
        if (!undoStack.isEmpty()) {
            TextMemento memento = undoStack.pop();
            redoStack.push(memento);
            return memento;
        }
        return null;
    }

    public TextMemento redo() {
        if (!redoStack.isEmpty()) {
            TextMemento memento = redoStack.pop();
            undoStack.push(memento);
            return memento;
        }
        return null;
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
}

// ============================================
// Another example: Game Save System
// ============================================

class GameMemento {
    private final int level;
    private final int health;
    private final String[] inventory;
    private final long timestamp;

    public GameMemento(int level, int health, String[] inventory) {
        this.level = level;
        this.health = health;
        this.inventory = inventory.clone(); // defensive copy
        this.timestamp = System.currentTimeMillis();
    }

    public int getLevel() { return level; }
    public int getHealth() { return health; }
    public String[] getInventory() { return inventory.clone(); }
    public long getTimestamp() { return timestamp; }
}

class GameCharacter {
    private int level = 1;
    private int health = 100;
    private java.util.List<String> inventory = new java.util.ArrayList<>();

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
        System.out.println("    Took " + damage + " damage. Health: " + health);
    }

    public void heal(int amount) {
        health = Math.min(100, health + amount);
        System.out.println("    Healed " + amount + ". Health: " + health);
    }

    public void levelUp() {
        level++;
        System.out.println("    Leveled up! Now level " + level);
    }

    public void addItem(String item) {
        inventory.add(item);
        System.out.println("    Acquired: " + item);
    }

    public GameMemento save() {
        System.out.println("    [Game Saved - Level: " + level + ", Health: " + health + "]");
        return new GameMemento(level, health, inventory.toArray(new String[0]));
    }

    public void restore(GameMemento memento) {
        this.level = memento.getLevel();
        this.health = memento.getHealth();
        this.inventory = new java.util.ArrayList<>(java.util.Arrays.asList(memento.getInventory()));
        System.out.println("    [Game Loaded - Level: " + level + ", Health: " + health
                + ", Items: " + inventory + "]");
    }

    @Override
    public String toString() {
        return "    Character: Level " + level + ", HP: " + health + ", Items: " + inventory;
    }
}

// Step 4: Demo
public class MementoPattern {

    public static void main(String[] args) {
        System.out.println("=== Memento Pattern ===");

        // Text Editor with Undo/Redo
        System.out.println("\n1. Text Editor with Undo/Redo:");
        TextEditor editor = new TextEditor();
        History history = new History();

        history.saveState(editor.save());
        editor.write("Hello");

        history.saveState(editor.save());
        editor.write(" World");

        history.saveState(editor.save());
        editor.write("!");

        System.out.println("\n  Undo operations:");
        editor.restore(history.undo());
        editor.restore(history.undo());

        System.out.println("\n  Redo operation:");
        editor.restore(history.redo());

        // Game Save System
        System.out.println("\n2. Game Save System:");
        GameCharacter hero = new GameCharacter();
        System.out.println(hero);

        // Play and save
        hero.levelUp();
        hero.addItem("Sword");
        GameMemento save1 = hero.save();

        hero.takeDamage(30);
        hero.addItem("Shield");
        hero.levelUp();
        GameMemento save2 = hero.save();

        hero.takeDamage(50);
        hero.addItem("Potion");
        System.out.println("\n  After more damage:");
        System.out.println(hero);

        // Restore to earlier state
        System.out.println("\n  Loading save point 1:");
        hero.restore(save1);
        System.out.println(hero);

        System.out.println("\n  Loading save point 2:");
        hero.restore(save2);
        System.out.println(hero);

        System.out.println("\nKey points:");
        System.out.println("- Memento captures internal state without breaking encapsulation");
        System.out.println("- Originator creates and restores mementos");
        System.out.println("- Caretaker manages memento lifecycle");
        System.out.println("- Supports undo/redo and save/load functionality");
    }
}
