package com.dsa.designpatterns.structural;

// ============================================
// Facade Pattern
// ============================================
//
// Intent: Provide a unified interface to a set of interfaces in a subsystem.
// Facade defines a higher-level interface that makes the subsystem easier to use.
//
// When to use:
// - You want to provide a simple interface to a complex subsystem
// - There are many dependencies between clients and implementation classes
// - You want to layer your subsystems (facade as entry point to each layer)
//
// Benefits:
// - Simplifies client usage of complex systems
// - Decouples client from subsystem components
// - Promotes weak coupling between subsystems
// - Reduces compilation dependencies (client only depends on facade)
// - Provides a single point of entry for a subsystem
//
// Real-world examples:
// - java.net.URL (facade for complex network operations)
// - javax.faces.context.FacesContext
// - Spring Framework's JdbcTemplate (facade for JDBC operations)
// - Home theater systems (one remote controls multiple devices)

// Step 1: Complex subsystem classes

// Subsystem: Amplifier
class Amplifier {
    public void on() { System.out.println("  Amplifier: ON"); }
    public void off() { System.out.println("  Amplifier: OFF"); }
    public void setVolume(int level) { System.out.println("  Amplifier: Setting volume to " + level); }
    public void setSource(String source) { System.out.println("  Amplifier: Setting source to " + source); }
}

// Subsystem: DVD Player
class DVDPlayer {
    public void on() { System.out.println("  DVD Player: ON"); }
    public void off() { System.out.println("  DVD Player: OFF"); }
    public void play(String movie) { System.out.println("  DVD Player: Playing \"" + movie + "\""); }
    public void stop() { System.out.println("  DVD Player: Stopped"); }
    public void eject() { System.out.println("  DVD Player: Ejecting disc"); }
}

// Subsystem: Projector
class Projector {
    public void on() { System.out.println("  Projector: ON"); }
    public void off() { System.out.println("  Projector: OFF"); }
    public void setInput(String input) { System.out.println("  Projector: Setting input to " + input); }
    public void setWideScreenMode() { System.out.println("  Projector: Widescreen mode ON"); }
}

// Subsystem: Screen
class Screen {
    public void down() { System.out.println("  Screen: Lowered"); }
    public void up() { System.out.println("  Screen: Raised"); }
}

// Subsystem: Lights
class TheaterLights {
    public void dim(int level) { System.out.println("  Theater Lights: Dimming to " + level + "%"); }
    public void on() { System.out.println("  Theater Lights: ON"); }
}

// Subsystem: Popcorn Popper
class PopcornPopper {
    public void on() { System.out.println("  Popcorn Popper: ON"); }
    public void off() { System.out.println("  Popcorn Popper: OFF"); }
    public void pop() { System.out.println("  Popcorn Popper: Popping popcorn!"); }
}

// Step 2: Facade - provides a unified interface
class HomeTheaterFacade {
    private Amplifier amplifier;
    private DVDPlayer dvdPlayer;
    private Projector projector;
    private Screen screen;
    private TheaterLights lights;
    private PopcornPopper popper;

    public HomeTheaterFacade(
            Amplifier amplifier,
            DVDPlayer dvdPlayer,
            Projector projector,
            Screen screen,
            TheaterLights lights,
            PopcornPopper popper) {
        this.amplifier = amplifier;
        this.dvdPlayer = dvdPlayer;
        this.projector = projector;
        this.screen = screen;
        this.lights = lights;
        this.popper = popper;
    }

    public void watchMovie(String movie) {
        System.out.println("\n--- Starting movie night! ---");
        popper.on();
        popper.pop();
        lights.dim(10);
        screen.down();
        projector.on();
        projector.setWideScreenMode();
        projector.setInput("DVD");
        amplifier.on();
        amplifier.setSource("DVD");
        amplifier.setVolume(5);
        dvdPlayer.on();
        dvdPlayer.play(movie);
    }

    public void endMovie() {
        System.out.println("\n--- Ending movie night... ---");
        popper.off();
        lights.on();
        screen.up();
        projector.off();
        amplifier.off();
        dvdPlayer.stop();
        dvdPlayer.eject();
        dvdPlayer.off();
    }

    public void pause() {
        System.out.println("\n--- Pausing movie ---");
        dvdPlayer.stop();
        lights.dim(30);
    }

    public void resume() {
        System.out.println("\n--- Resuming movie ---");
        lights.dim(10);
        // In a real system, we'd remember the position
        System.out.println("  DVD Player: Resuming playback");
    }
}

// Step 3: Demo
public class FacadePattern {

    public static void main(String[] args) {
        System.out.println("=== Facade Pattern ===");

        // Create subsystem components
        Amplifier amp = new Amplifier();
        DVDPlayer dvd = new DVDPlayer();
        Projector projector = new Projector();
        Screen screen = new Screen();
        TheaterLights lights = new TheaterLights();
        PopcornPopper popper = new PopcornPopper();

        // Create facade
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(amp, dvd, projector, screen, lights, popper);

        // Client uses simple facade interface
        System.out.println("\nClient wants to watch a movie:");
        homeTheater.watchMovie("Inception");

        System.out.println("\nClient wants to pause:");
        homeTheater.pause();

        System.out.println("\nClient wants to resume:");
        homeTheater.resume();

        System.out.println("\nClient is done watching:");
        homeTheater.endMovie();

        System.out.println("\nKey points:");
        System.out.println("- Facade simplifies complex subsystem into a single interface");
        System.out.println("- Client is decoupled from subsystem components");
        System.out.println("- Subsystem can still be used directly if needed");
        System.out.println("- Facade doesn't encapsulate the subsystem, it just simplifies it");
    }
}
