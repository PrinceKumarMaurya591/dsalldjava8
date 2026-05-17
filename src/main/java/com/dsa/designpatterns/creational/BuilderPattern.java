package com.dsa.designpatterns.creational;

// ============================================
// Builder Pattern
// ============================================
//
// Intent: Separate the construction of a complex object from its representation
// so that the same construction process can create different representations.
//
// When to use:
// - Object has many optional parameters (telescoping constructor problem)
// - Object construction involves multiple steps
// - You want to create different representations of the same object
//
// Benefits:
// - Clear separation of construction and representation
// - Better control over construction process
// - Supports immutability (no setters needed)
// - Eliminates telescoping constructors
// - Readable code (method chaining / fluent interface)
//
// Real-world examples:
// - java.lang.StringBuilder.append()
// - java.nio.ByteBuffer
// - javax.swing.GroupLayout
// - Lombok @Builder annotation

// Step 1: Product class
class Computer {
    // Required parameters
    private final String cpu;
    private final String ram;

    // Optional parameters
    private final String storage;
    private final String gpu;
    private final String motherboard;
    private final boolean bluetoothEnabled;
    private final boolean wifiEnabled;
    private final String operatingSystem;

    // Private constructor - only Builder can create
    private Computer(Builder builder) {
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.gpu = builder.gpu;
        this.motherboard = builder.motherboard;
        this.bluetoothEnabled = builder.bluetoothEnabled;
        this.wifiEnabled = builder.wifiEnabled;
        this.operatingSystem = builder.operatingSystem;
    }

    // Getters only (no setters - immutable)
    public String getCpu() { return cpu; }
    public String getRam() { return ram; }
    public String getStorage() { return storage; }
    public String getGpu() { return gpu; }
    public String getMotherboard() { return motherboard; }
    public boolean isBluetoothEnabled() { return bluetoothEnabled; }
    public boolean isWifiEnabled() { return wifiEnabled; }
    public String getOperatingSystem() { return operatingSystem; }

    @Override
    public String toString() {
        return "Computer{" +
                "cpu='" + cpu + '\'' +
                ", ram='" + ram + '\'' +
                ", storage='" + storage + '\'' +
                ", gpu='" + gpu + '\'' +
                ", motherboard='" + motherboard + '\'' +
                ", bluetoothEnabled=" + bluetoothEnabled +
                ", wifiEnabled=" + wifiEnabled +
                ", operatingSystem='" + operatingSystem + '\'' +
                '}';
    }

    // Step 2: Static Builder class
    public static class Builder implements ComputerDirector.BuilderComputerBuilder {
        // Required parameters
        private String cpu;
        private String ram;

        // Optional parameters with defaults
        private String storage = "256GB SSD";
        private String gpu = "Integrated";
        private String motherboard = "Standard";
        private boolean bluetoothEnabled = false;
        private boolean wifiEnabled = false;
        private String operatingSystem = "None";

        // Builder constructor with required parameters
        public Builder(String cpu, String ram) {
            this.cpu = cpu;
            this.ram = ram;
        }

        // No-arg constructor for Director use (Director sets all values via fluent methods)
        public Builder() {
        }

        // Builder methods for all parameters (fluent interface)
        @Override
        public ComputerDirector.BuilderComputerBuilder cpu(String cpu) {
            this.cpu = cpu;
            return this;
        }

        @Override
        public ComputerDirector.BuilderComputerBuilder ram(String ram) {
            this.ram = ram;
            return this;
        }

        @Override
        public Builder storage(String storage) {
            this.storage = storage;
            return this;
        }

        @Override
        public Builder gpu(String gpu) {
            this.gpu = gpu;
            return this;
        }

        @Override
        public Builder motherboard(String motherboard) {
            this.motherboard = motherboard;
            return this;
        }

        @Override
        public Builder bluetoothEnabled(boolean bluetoothEnabled) {
            this.bluetoothEnabled = bluetoothEnabled;
            return this;
        }

        @Override
        public Builder wifiEnabled(boolean wifiEnabled) {
            this.wifiEnabled = wifiEnabled;
            return this;
        }

        @Override
        public Builder operatingSystem(String operatingSystem) {
            this.operatingSystem = operatingSystem;
            return this;
        }

        // Build method to create the final product
        @Override
        public Computer build() {
            return new Computer(this);
        }
    }
}

// Step 3: Director (optional) - orchestrates the building process
class ComputerDirector {
    // Construct a gaming computer using the builder
    public static Computer buildGamingComputer(BuilderComputerBuilder builder) {
        return builder
                .cpu("Intel Core i9-13900K")
                .ram("32GB DDR5")
                .storage("1TB NVMe SSD")
                .gpu("NVIDIA RTX 4090")
                .motherboard("Z790 Gaming")
                .bluetoothEnabled(true)
                .wifiEnabled(true)
                .operatingSystem("Windows 11 Pro")
                .build();
    }

    // Construct an office computer using the builder
    public static Computer buildOfficeComputer(BuilderComputerBuilder builder) {
        return builder
                .cpu("Intel Core i5-13600")
                .ram("16GB DDR4")
                .storage("512GB SSD")
                .gpu("Integrated")
                .motherboard("B760")
                .bluetoothEnabled(true)
                .wifiEnabled(true)
                .operatingSystem("Windows 11")
                .build();
    }

    // Helper interface for the director pattern
    interface BuilderComputerBuilder {
        BuilderComputerBuilder cpu(String cpu);
        BuilderComputerBuilder ram(String ram);
        BuilderComputerBuilder storage(String storage);
        BuilderComputerBuilder gpu(String gpu);
        BuilderComputerBuilder motherboard(String motherboard);
        BuilderComputerBuilder bluetoothEnabled(boolean enabled);
        BuilderComputerBuilder wifiEnabled(boolean enabled);
        BuilderComputerBuilder operatingSystem(String os);
        Computer build();
    }
}

// Step 4: Demo
public class BuilderPattern {

    public static void main(String[] args) {
        System.out.println("=== Builder Pattern ===");

        // Using builder directly (fluent interface)
        System.out.println("\n1. Custom Computer (using builder directly):");
        Computer gamingPC = new Computer.Builder("AMD Ryzen 9 7950X", "64GB DDR5")
                .storage("2TB NVMe SSD")
                .gpu("NVIDIA RTX 4090")
                .motherboard("X670E")
                .bluetoothEnabled(true)
                .wifiEnabled(true)
                .operatingSystem("Windows 11 Pro")
                .build();
        System.out.println(gamingPC);

        // Minimal configuration (only required params)
        System.out.println("\n2. Minimal Computer (only required params):");
        Computer basicPC = new Computer.Builder("Intel Core i3-12100", "8GB DDR4")
                .build();
        System.out.println(basicPC);

        // Using Director for standard configurations
        System.out.println("\n3. Gaming Computer (via Director):");
        Computer directorGamingPC = ComputerDirector.buildGamingComputer(
                new Computer.Builder() // Director sets all values via fluent interface
        );
        System.out.println(directorGamingPC);

        System.out.println("\n4. Office Computer (via Director):");
        Computer officePC = ComputerDirector.buildOfficeComputer(
                new Computer.Builder() // Director sets all values via fluent interface
        );
        System.out.println(officePC);

        System.out.println("\nKey points:");
        System.out.println("- Eliminates telescoping constructors");
        System.out.println("- Immutable product (no setters)");
        System.out.println("- Fluent interface for readable code");
        System.out.println("- Director can encapsulate standard configurations");
        System.out.println("- Optional parameters have sensible defaults");
    }
}
