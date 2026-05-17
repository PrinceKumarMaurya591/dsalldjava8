package com.dsa.designpatterns.structural;

// ============================================
// Adapter Pattern
// ============================================
//
// Intent: Convert the interface of a class into another interface clients expect.
// Adapter lets classes work together that couldn't otherwise because of incompatible interfaces.
//
// When to use:
// - You want to use an existing class but its interface doesn't match the one you need
// - You want to create a reusable class that cooperates with unrelated or unforeseen classes
// - You need to use several existing subclasses but it's impractical to adapt their interface
//
// Benefits:
// - Single Responsibility: separates interface conversion from business logic
// - Open/Closed: can introduce new adapters without breaking existing code
// - Reusability: enables integration of legacy/third-party code
//
// Real-world examples:
// - java.util.Arrays.asList()
// - java.io.InputStreamReader (adapts InputStream to Reader)
// - JDBC drivers (adapts database-specific protocols to JDBC API)

// Step 1: Target interface - what the client expects
interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Step 2: Adaptee - existing class with incompatible interface
class AdvancedMediaPlayer {
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file: " + fileName);
    }

    public void playMp4(String fileName) {
        System.out.println("Playing mp4 file: " + fileName);
    }
}

// Step 3: Adapter - converts Adaptee interface to Target interface
class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMediaPlayer;

    public MediaAdapter() {
        this.advancedMediaPlayer = new AdvancedMediaPlayer();
    }

    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMediaPlayer.playVlc(fileName);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedMediaPlayer.playMp4(fileName);
        } else {
            System.out.println("Unsupported format: " + audioType);
        }
    }
}

// Step 4: Client - uses the Target interface
class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;

    @Override
    public void play(String audioType, String fileName) {
        // Built-in support for mp3
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing mp3 file: " + fileName);
        }
        // Use adapter for other formats
        else if (audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            mediaAdapter = new MediaAdapter();
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("Invalid media type: " + audioType + ". Format not supported.");
        }
    }
}

// ============================================
// Object Adapter variant (using composition)
// ============================================

// Another example: Legacy Rectangle to modern Shape interface
interface Shape {
    void draw(int x, int y, int width, int height);
}

// Legacy class with different interface
class LegacyRectangle {
    public void display(int x1, int y1, int x2, int y2) {
        System.out.println("LegacyRectangle.display(): (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
    }
}

// Adapter using composition
class RectangleAdapter implements Shape {
    private LegacyRectangle legacyRectangle;

    public RectangleAdapter(LegacyRectangle legacyRectangle) {
        this.legacyRectangle = legacyRectangle;
    }

    @Override
    public void draw(int x, int y, int width, int height) {
        // Convert (x, y, width, height) to (x1, y1, x2, y2)
        int x2 = x + width;
        int y2 = y + height;
        legacyRectangle.display(x, y, x2, y2);
    }
}

// ============================================
// Class Adapter variant (using inheritance)
// ============================================

// Target interface
interface TemperatureSensor {
    double getTemperatureCelsius();
}

// Adaptee
class FahrenheitSensor {
    public double getTemperatureFahrenheit() {
        return 98.6; // example
    }
}

// Class adapter using inheritance
class TemperatureAdapter extends FahrenheitSensor implements TemperatureSensor {
    @Override
    public double getTemperatureCelsius() {
        double fahrenheit = getTemperatureFahrenheit();
        return (fahrenheit - 32) * 5.0 / 9.0;
    }
}

// Step 5: Demo
public class AdapterPattern {

    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern ===");

        // Media Player example
        System.out.println("\n1. Media Player (Object Adapter):");
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.play("mp3", "song.mp3");
        audioPlayer.play("mp4", "video.mp4");
        audioPlayer.play("vlc", "movie.vlc");
        audioPlayer.play("avi", "unsupported.avi");

        // Legacy Rectangle adapter
        System.out.println("\n2. Legacy Rectangle Adapter (Object Adapter):");
        Shape shape = new RectangleAdapter(new LegacyRectangle());
        shape.draw(10, 20, 100, 50);

        // Temperature adapter
        System.out.println("\n3. Temperature Adapter (Class Adapter):");
        TemperatureSensor sensor = new TemperatureAdapter();
        System.out.println("Temperature in Celsius: " + String.format("%.1f", sensor.getTemperatureCelsius()) + "°C");

        System.out.println("\nKey points:");
        System.out.println("- Object Adapter uses composition (more flexible)");
        System.out.println("- Class Adapter uses inheritance (requires multiple inheritance in Java via interfaces)");
        System.out.println("- Adapter converts interface, not functionality");
        System.out.println("- Client remains decoupled from Adaptee");
    }
}
