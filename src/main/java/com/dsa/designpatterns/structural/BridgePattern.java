package com.dsa.designpatterns.structural;

// ============================================
// Bridge Pattern
// ============================================
//
// Intent: Decouple an abstraction from its implementation so that the two can
// vary independently.
//
// When to use:
// - You want to avoid a permanent binding between abstraction and implementation
// - Both abstractions and implementations should be extensible via subclassing
// - Changes in implementation should not affect clients
// - You have a proliferation of classes (e.g., different shapes × different colors)
//
// Benefits:
// - Decouples interface and implementation
// - Improved extensibility (can extend abstraction and implementation hierarchies independently)
// - Hides implementation details from clients
// - Eliminates compile-time dependencies
//
// Real-world examples:
// - JDBC DriverManager (abstraction) with different database drivers (implementations)
// - Java AWT (Abstract Window Toolkit) - peer architecture
// - SLF4J logging facade with different logging backends

// Step 1: Implementor interface - defines the implementation hierarchy
interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int percent);
    int getChannel();
    void setChannel(int channel);
    void printStatus();
}

// Step 2: Concrete Implementors
class TV implements Device {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;

    @Override
    public boolean isEnabled() { return on; }

    @Override
    public void enable() { on = true; }

    @Override
    public void disable() { on = false; }

    @Override
    public int getVolume() { return volume; }

    @Override
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
    }

    @Override
    public int getChannel() { return channel; }

    @Override
    public void setChannel(int channel) {
        this.channel = Math.max(1, channel);
    }

    @Override
    public void printStatus() {
        System.out.println("------------------------------------");
        System.out.println("| I'm TV.");
        System.out.println("| I'm " + (on ? "enabled" : "disabled"));
        System.out.println("| Current volume is " + volume + "%");
        System.out.println("| Current channel is " + channel);
        System.out.println("------------------------------------\n");
    }
}

class Radio implements Device {
    private boolean on = false;
    private int volume = 20;
    private int channel = 88; // FM frequency

    @Override
    public boolean isEnabled() { return on; }

    @Override
    public void enable() { on = true; }

    @Override
    public void disable() { on = false; }

    @Override
    public int getVolume() { return volume; }

    @Override
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
    }

    @Override
    public int getChannel() { return channel; }

    @Override
    public void setChannel(int channel) {
        this.channel = Math.max(88, Math.min(108, channel));
    }

    @Override
    public void printStatus() {
        System.out.println("------------------------------------");
        System.out.println("| I'm Radio.");
        System.out.println("| I'm " + (on ? "enabled" : "disabled"));
        System.out.println("| Current volume is " + volume + "%");
        System.out.println("| Current frequency is " + channel + ".0 FM");
        System.out.println("------------------------------------\n");
    }
}

// Step 3: Abstraction - defines the abstraction hierarchy
class RemoteControl {
    protected Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        System.out.println("Remote: toggle power");
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }

    public void volumeDown() {
        System.out.println("Remote: volume down");
        device.setVolume(device.getVolume() - 10);
    }

    public void volumeUp() {
        System.out.println("Remote: volume up");
        device.setVolume(device.getVolume() + 10);
    }

    public void channelDown() {
        System.out.println("Remote: channel down");
        device.setChannel(device.getChannel() - 1);
    }

    public void channelUp() {
        System.out.println("Remote: channel up");
        device.setChannel(device.getChannel() + 1);
    }
}

// Step 4: Refined Abstraction
class AdvancedRemoteControl extends RemoteControl {

    public AdvancedRemoteControl(Device device) {
        super(device);
    }

    public void mute() {
        System.out.println("Remote: mute");
        device.setVolume(0);
    }

    public void setChannelDirectly(int channel) {
        System.out.println("Remote: set channel to " + channel);
        device.setChannel(channel);
    }
}

// Step 5: Demo
public class BridgePattern {

    public static void main(String[] args) {
        System.out.println("=== Bridge Pattern ===");

        System.out.println("\n1. Testing TV with basic remote:");
        Device tv = new TV();
        RemoteControl basicRemote = new RemoteControl(tv);
        basicRemote.togglePower();
        basicRemote.volumeUp();
        basicRemote.channelUp();
        tv.printStatus();

        System.out.println("\n2. Testing Radio with basic remote:");
        Device radio = new Radio();
        RemoteControl radioRemote = new RemoteControl(radio);
        radioRemote.togglePower();
        radioRemote.volumeUp();
        radioRemote.volumeUp();
        radioRemote.channelUp();
        radio.printStatus();

        System.out.println("\n3. Testing TV with advanced remote:");
        Device tv2 = new TV();
        AdvancedRemoteControl advancedRemote = new AdvancedRemoteControl(tv2);
        advancedRemote.togglePower();
        advancedRemote.volumeUp();
        advancedRemote.setChannelDirectly(42);
        advancedRemote.mute();
        tv2.printStatus();

        System.out.println("\nKey points:");
        System.out.println("- Abstraction (RemoteControl) and Implementation (Device) vary independently");
        System.out.println("- Can add new remotes without affecting devices");
        System.out.println("- Can add new devices without affecting remotes");
        System.out.println("- Eliminates class explosion (e.g., BasicTVRemote, AdvancedTVRemote, BasicRadioRemote...)");
    }
}
