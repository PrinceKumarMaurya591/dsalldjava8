package com.dsa.designpatterns.behavioral;

// ============================================
// Command Pattern
// ============================================
//
// Intent: Encapsulate a request as an object, thereby letting you parameterize
// clients with different requests, queue or log requests, and support undoable operations.
//
// When to use:
// - Parameterize objects by an action to perform
// - Specify, queue, and execute requests at different times
// - Support undo/redo functionality
// - Support logging changes for recovery
//
// Benefits:
// - Decouples invoker from receiver
// - Commands are first-class objects (can be manipulated, extended)
// - Supports composite commands (macro commands)
// - Easy to add new commands without changing existing code
//
// Real-world examples:
// - java.lang.Runnable
// - javax.swing.Action
// - GUI menu items and buttons
// - Transactional behavior in databases

import java.util.Stack;

// Step 1: Command interface
interface Command {
    void execute();
    void undo();
}

// Step 2: Receiver - knows how to perform the operations
class Light {
    private String location;
    private boolean isOn = false;

    public Light(String location) {
        this.location = location;
    }

    public void on() {
        isOn = true;
        System.out.println("    " + location + " light is ON");
    }

    public void off() {
        isOn = false;
        System.out.println("    " + location + " light is OFF");
    }

    public boolean isOn() { return isOn; }
}

class CeilingFan {
    public static final int OFF = 0;
    public static final int LOW = 1;
    public static final int MEDIUM = 2;
    public static final int HIGH = 3;

    private String location;
    private int speed = OFF;

    public CeilingFan(String location) {
        this.location = location;
    }

    public void high() {
        speed = HIGH;
        System.out.println("    " + location + " fan is on HIGH");
    }

    public void medium() {
        speed = MEDIUM;
        System.out.println("    " + location + " fan is on MEDIUM");
    }

    public void low() {
        speed = LOW;
        System.out.println("    " + location + " fan is on LOW");
    }

    public void off() {
        speed = OFF;
        System.out.println("    " + location + " fan is OFF");
    }

    public int getSpeed() { return speed; }
}

// Step 3: Concrete Commands
class LightOnCommand implements Command {
    private Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }

    @Override
    public void undo() {
        light.off();
    }
}

class LightOffCommand implements Command {
    private Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.off();
    }

    @Override
    public void undo() {
        light.on();
    }
}

class CeilingFanOffCommand implements Command {
    private CeilingFan fan;
    private int previousSpeed;

    public CeilingFanOffCommand(CeilingFan fan) {
        this.fan = fan;
    }

    @Override
    public void execute() {
        previousSpeed = fan.getSpeed();
        fan.off();
    }

    @Override
    public void undo() {
        switch (previousSpeed) {
            case CeilingFan.HIGH:    fan.high(); break;
            case CeilingFan.MEDIUM:  fan.medium(); break;
            case CeilingFan.LOW:     fan.low(); break;
            default:                 fan.off(); break;
        }
    }
}

class CeilingFanHighCommand implements Command {
    private CeilingFan fan;
    private int previousSpeed;

    public CeilingFanHighCommand(CeilingFan fan) {
        this.fan = fan;
    }

    @Override
    public void execute() {
        previousSpeed = fan.getSpeed();
        fan.high();
    }

    @Override
    public void undo() {
        switch (previousSpeed) {
            case CeilingFan.HIGH:    fan.high(); break;
            case CeilingFan.MEDIUM:  fan.medium(); break;
            case CeilingFan.LOW:     fan.low(); break;
            default:                 fan.off(); break;
        }
    }
}

// Macro command - composite of multiple commands
class MacroCommand implements Command {
    private Command[] commands;

    public MacroCommand(Command[] commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        for (Command cmd : commands) {
            cmd.execute();
        }
    }

    @Override
    public void undo() {
        for (int i = commands.length - 1; i >= 0; i--) {
            commands[i].undo();
        }
    }
}

// Step 4: Invoker - asks the command to carry out the request
class RemoteControlInvoker {
    private Command[] onCommands;
    private Command[] offCommands;
    private Stack<Command> undoStack;

    public RemoteControlInvoker(int slots) {
        onCommands = new Command[slots];
        offCommands = new Command[slots];
        undoStack = new Stack<>();

        // Initialize with no-op commands
        Command noCommand = new Command() {
            @Override public void execute() { System.out.println("    No command assigned"); }
            @Override public void undo() { System.out.println("    No command assigned"); }
        };
        for (int i = 0; i < slots; i++) {
            onCommands[i] = noCommand;
            offCommands[i] = noCommand;
        }
    }

    public void setCommand(int slot, Command onCommand, Command offCommand) {
        onCommands[slot] = onCommand;
        offCommands[slot] = offCommand;
    }

    public void pressOn(int slot) {
        onCommands[slot].execute();
        undoStack.push(onCommands[slot]);
    }

    public void pressOff(int slot) {
        offCommands[slot].execute();
        undoStack.push(offCommands[slot]);
    }

    public void pressUndo() {
        if (!undoStack.isEmpty()) {
            System.out.println("    [UNDO]");
            undoStack.pop().undo();
        }
    }
}

// Step 5: Demo
public class CommandPattern {

    public static void main(String[] args) {
        System.out.println("=== Command Pattern ===");

        // Receivers
        Light livingRoomLight = new Light("Living Room");
        Light kitchenLight = new Light("Kitchen");
        CeilingFan livingRoomFan = new CeilingFan("Living Room");

        // Commands
        LightOnCommand livingRoomLightOn = new LightOnCommand(livingRoomLight);
        LightOffCommand livingRoomLightOff = new LightOffCommand(livingRoomLight);
        LightOnCommand kitchenLightOn = new LightOnCommand(kitchenLight);
        LightOffCommand kitchenLightOff = new LightOffCommand(kitchenLight);
        CeilingFanHighCommand fanHigh = new CeilingFanHighCommand(livingRoomFan);

        // Invoker
        CeilingFanOffCommand fanOff = new CeilingFanOffCommand(livingRoomFan);
        RemoteControlInvoker remote = new RemoteControlInvoker(3);
        remote.setCommand(0, livingRoomLightOn, livingRoomLightOff);
        remote.setCommand(1, kitchenLightOn, kitchenLightOff);
        remote.setCommand(2, fanHigh, fanOff);

        System.out.println("\n1. Using remote control:");
        remote.pressOn(0);
        remote.pressOff(0);
        remote.pressOn(1);
        remote.pressOn(2);

        System.out.println("\n2. Undo operations:");
        remote.pressUndo(); // undo fan high
        remote.pressUndo(); // undo kitchen light on
        remote.pressUndo(); // undo living room light off

        // Macro command
        System.out.println("\n3. Macro Command (Party Mode):");
        Command[] partyOn = { livingRoomLightOn, kitchenLightOn, fanHigh };
        Command[] partyOff = { livingRoomLightOff, kitchenLightOff, fanOff };
        MacroCommand partyOnMacro = new MacroCommand(partyOn);
        MacroCommand partyOffMacro = new MacroCommand(partyOff);

        remote.setCommand(0, partyOnMacro, partyOffMacro);
        remote.pressOn(0);
        System.out.println("  ...party over...");
        remote.pressOff(0);

        System.out.println("\nKey points:");
        System.out.println("- Encapsulates request as an object");
        System.out.println("- Supports undo/redo via command history");
        System.out.println("- Macro commands compose multiple commands");
        System.out.println("- Decouples invoker from receiver");
    }
}
