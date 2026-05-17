package com.dsa.designpatterns.behavioral;

// ============================================
// Mediator Pattern
// ============================================
//
// Intent: Define an object that encapsulates how a set of objects interact.
// Mediator promotes loose coupling by keeping objects from referring to each
// other explicitly, and it lets you vary their interaction independently.
//
// When to use:
// - A set of objects communicate in well-defined but complex ways
// - Reusing an object is difficult because it refers to many other objects
// - A behavior that's distributed between several classes should be customizable
//
// Benefits:
// - Reduces coupling between colleague classes
// - Centralizes control (interaction logic in one place)
// - Simplifies object protocols (many-to-many → one-to-many)
// - Makes individual components simpler and more reusable
//
// Real-world examples:
// - java.util.Timer (schedules tasks)
// - java.util.concurrent.ExecutorService
// - Message queues / event buses
// - Air traffic control (airplanes communicate via control tower)

import java.util.ArrayList;
import java.util.List;

// Step 1: Mediator interface
interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
    void removeUser(User user);
}

// Step 2: Colleague interface
abstract class User {
    protected String name;
    protected ChatMediator mediator;

    public User(String name, ChatMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public abstract void send(String message);
    public abstract void receive(String message);

    public String getName() { return name; }
}

// Step 3: Concrete Mediator
class ChatRoom implements ChatMediator {
    private List<User> users;

    public ChatRoom() {
        this.users = new ArrayList<>();
    }

    @Override
    public void addUser(User user) {
        users.add(user);
        System.out.println("    " + user.getName() + " joined the chat");
    }

    @Override
    public void removeUser(User user) {
        users.remove(user);
        System.out.println("    " + user.getName() + " left the chat");
    }

    @Override
    public void sendMessage(String message, User sender) {
        System.out.println("    [" + sender.getName() + "]: " + message);
        for (User user : users) {
            // Don't send to the sender
            if (user != sender) {
                user.receive(message);
            }
        }
    }
}

// Step 4: Concrete Colleagues
class ChatUser extends User {
    public ChatUser(String name, ChatMediator mediator) {
        super(name, mediator);
    }

    @Override
    public void send(String message) {
        System.out.print("  " + name + " sends: ");
        mediator.sendMessage(message, this);
    }

    @Override
    public void receive(String message) {
        System.out.println("    " + name + " received: " + message);
    }
}

// ============================================
// Another example: Smart Home Mediator
// ============================================

interface SmartHomeMediator {
    void notify(String event, SmartDevice sender);
    void addDevice(SmartDevice device);
}

abstract class SmartDevice {
    protected String name;
    protected SmartHomeMediator mediator;

    public SmartDevice(String name, SmartHomeMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public abstract void onEvent(String event);
    public String getName() { return name; }
}

class SmartHomeHub implements SmartHomeMediator {
    private List<SmartDevice> devices = new ArrayList<>();

    @Override
    public void addDevice(SmartDevice device) {
        devices.add(device);
        System.out.println("    " + device.getName() + " connected to Smart Home Hub");
    }

    @Override
    public void notify(String event, SmartDevice sender) {
        System.out.println("  Hub received: " + event + " from " + sender.getName());
        for (SmartDevice device : devices) {
            if (device != sender) {
                device.onEvent(event);
            }
        }
    }
}

class TemperatureSensor extends SmartDevice {
    private int temperature = 22;

    public TemperatureSensor(String name, SmartHomeMediator mediator) {
        super(name, mediator);
    }

    public void setTemperature(int temp) {
        this.temperature = temp;
        System.out.println("  " + name + ": Temperature changed to " + temp + "°C");
        if (temp > 28) {
            mediator.notify("HIGH_TEMP", this);
        } else if (temp < 15) {
            mediator.notify("LOW_TEMP", this);
        }
    }

    @Override
    public void onEvent(String event) {
        // Sensors don't react to events
    }
}

class AirConditioner extends SmartDevice {
    private boolean running = false;

    public AirConditioner(String name, SmartHomeMediator mediator) {
        super(name, mediator);
    }

    @Override
    public void onEvent(String event) {
        if (event.equals("HIGH_TEMP") && !running) {
            running = true;
            System.out.println("    " + name + ": Turning ON (high temperature detected)");
        } else if (event.equals("LOW_TEMP") && running) {
            running = false;
            System.out.println("    " + name + ": Turning OFF (low temperature detected)");
        }
    }
}

class Window extends SmartDevice {
    private boolean open = false;

    public Window(String name, SmartHomeMediator mediator) {
        super(name, mediator);
    }

    @Override
    public void onEvent(String event) {
        if (event.equals("HIGH_TEMP") && !open) {
            open = true;
            System.out.println("    " + name + ": Opening (let some air in)");
        } else if (event.equals("LOW_TEMP") && open) {
            open = false;
            System.out.println("    " + name + ": Closing (keep warmth inside)");
        }
    }
}

// Step 5: Demo
public class MediatorPattern {

    public static void main(String[] args) {
        System.out.println("=== Mediator Pattern ===");

        // Chat room example
        System.out.println("\n1. Chat Room:");
        ChatMediator chatRoom = new ChatRoom();

        User alice = new ChatUser("Alice", chatRoom);
        User bob = new ChatUser("Bob", chatRoom);
        User charlie = new ChatUser("Charlie", chatRoom);

        chatRoom.addUser(alice);
        chatRoom.addUser(bob);
        chatRoom.addUser(charlie);

        System.out.println();
        alice.send("Hello everyone!");
        System.out.println();
        bob.send("Hi Alice!");
        System.out.println();
        charlie.send("Hey folks!");

        // Smart Home example
        System.out.println("\n2. Smart Home Hub:");
        SmartHomeHub hub = new SmartHomeHub();

        TemperatureSensor sensor = new TemperatureSensor("Living Room Sensor", hub);
        AirConditioner ac = new AirConditioner("Living Room AC", hub);
        Window window = new Window("Living Room Window", hub);

        hub.addDevice(sensor);
        hub.addDevice(ac);
        hub.addDevice(window);

        System.out.println();
        sensor.setTemperature(30); // Triggers HIGH_TEMP
        System.out.println();
        sensor.setTemperature(12); // Triggers LOW_TEMP

        System.out.println("\nKey points:");
        System.out.println("- Mediator centralizes communication between objects");
        System.out.println("- Colleagues don't reference each other directly");
        System.out.println("- Reduces coupling (many-to-many → one-to-many)");
        System.out.println("- Easier to add new colleague types");
    }
}
