package com.dsa.designpatterns.behavioral;

// ============================================
// Observer Pattern
// ============================================
//
// Intent: Define a one-to-many dependency between objects so that when one object
// changes state, all its dependents are notified and updated automatically.
//
// When to use:
// - An abstraction has two aspects, one dependent on the other
// - A change to one object requires changing others, and you don't know how many
// - An object should notify other objects without making assumptions about who they are
//
// Benefits:
// - Abstract coupling between subject and observer
// - Support for broadcast communication
// - Open/Closed: new observers can be added without modifying subject
//
// Real-world examples:
// - java.util.Observer / java.util.Observable (deprecated)
// - java.util.EventListener (GUI event handling)
// - javax.servlet.http.HttpSessionBindingListener
// - Reactive programming (RxJava, Reactor)

import java.util.ArrayList;
import java.util.List;

// Step 1: Subject interface
interface StockMarket {
    void registerObserver(Investor observer);
    void removeObserver(Investor observer);
    void notifyObservers();
}

// Step 2: Observer interface
interface Investor {
    void update(String stockSymbol, double price);
    String getName();
}

// Step 3: Concrete Subject
class StockExchange implements StockMarket {
    private List<Investor> investors;
    private String stockSymbol;
    private double price;

    public StockExchange(String stockSymbol, double initialPrice) {
        this.investors = new ArrayList<>();
        this.stockSymbol = stockSymbol;
        this.price = initialPrice;
    }

    @Override
    public void registerObserver(Investor investor) {
        investors.add(investor);
        System.out.println("    " + investor.getName() + " is now watching " + stockSymbol);
    }

    @Override
    public void removeObserver(Investor investor) {
        investors.remove(investor);
        System.out.println("    " + investor.getName() + " stopped watching " + stockSymbol);
    }

    @Override
    public void notifyObservers() {
        System.out.println("  Notifying " + investors.size() + " investors about " + stockSymbol + "...");
        for (Investor investor : investors) {
            investor.update(stockSymbol, price);
        }
    }

    // Business method that triggers notifications
    public void setPrice(double newPrice) {
        double oldPrice = this.price;
        this.price = newPrice;
        double change = ((newPrice - oldPrice) / oldPrice) * 100;
        System.out.println("\n  " + stockSymbol + " price changed: $" + oldPrice
                + " → $" + newPrice + " (" + String.format("%.2f", change) + "%)");
        notifyObservers();
    }

    public String getStockSymbol() { return stockSymbol; }
    public double getPrice() { return price; }
}

// Step 4: Concrete Observers
class IndividualInvestor implements Investor {
    private String name;
    private double investmentThreshold;

    public IndividualInvestor(String name, double investmentThreshold) {
        this.name = name;
        this.investmentThreshold = investmentThreshold;
    }

    @Override
    public void update(String stockSymbol, double price) {
        System.out.println("    " + name + " [Individual]: " + stockSymbol
                + " is now $" + price);
        if (price < investmentThreshold) {
            System.out.println("      → " + name + " is BUYING! (below $" + investmentThreshold + ")");
        }
    }

    @Override
    public String getName() { return name; }
}

class InstitutionalInvestor implements Investor {
    private String name;
    private double portfolioValue;

    public InstitutionalInvestor(String name, double portfolioValue) {
        this.name = name;
        this.portfolioValue = portfolioValue;
    }

    @Override
    public void update(String stockSymbol, double price) {
        System.out.println("    " + name + " [Institutional]: " + stockSymbol
                + " is now $" + price + " (Portfolio: $" + portfolioValue + ")");
        if (price > 150) {
            System.out.println("      → " + name + " is considering SELLING for profit taking");
        }
    }

    @Override
    public String getName() { return name; }
}

class TradingBot implements Investor {
    private String name;
    private double lastPrice = 0;

    public TradingBot(String name) {
        this.name = name;
    }

    @Override
    public void update(String stockSymbol, double price) {
        if (lastPrice > 0) {
            double change = Math.abs((price - lastPrice) / lastPrice) * 100;
            System.out.println("    " + name + " [Bot]: " + stockSymbol
                    + " changed by " + String.format("%.2f", change) + "%");
            if (change > 5) {
                System.out.println("      → " + name + " ALERT: High volatility detected!");
            }
        } else {
            System.out.println("    " + name + " [Bot]: Monitoring " + stockSymbol + " at $" + price);
        }
        lastPrice = price;
    }

    @Override
    public String getName() { return name; }
}

// ============================================
// Another example: Weather Station
// ============================================

interface WeatherObserver {
    void update(float temperature, float humidity, float pressure);
}

class WeatherStation {
    private List<WeatherObserver> observers = new ArrayList<>();
    private float temperature;
    private float humidity;
    private float pressure;

    public void addObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WeatherObserver observer) {
        observers.remove(observer);
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        System.out.println("\n  Weather Station: New readings (T:" + temperature
                + "°C, H:" + humidity + "%, P:" + pressure + "hPa)");
        notifyObservers();
    }

    private void notifyObservers() {
        for (WeatherObserver observer : observers) {
            observer.update(temperature, humidity, pressure);
        }
    }
}

class PhoneDisplay implements WeatherObserver {
    private String name;

    public PhoneDisplay(String name) { this.name = name; }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        System.out.println("    " + name + " Display: " + temperature + "°C, "
                + humidity + "% humidity");
    }
}

class WindowDisplay implements WeatherObserver {
    @Override
    public void update(float temperature, float humidity, float pressure) {
        String advice;
        if (temperature > 25) {
            advice = "☀️ Nice weather!";
        } else if (temperature > 15) {
            advice = "⛅ Mild weather";
        } else {
            advice = "❄️ Cold weather!";
        }
        System.out.println("    Window Display: " + advice + " (" + temperature + "°C)");
    }
}

// Step 5: Demo
public class ObserverPattern {

    public static void main(String[] args) {
        System.out.println("=== Observer Pattern ===");

        // Stock Market
        System.out.println("\n1. Stock Market:");
        StockExchange apple = new StockExchange("AAPL", 150.0);

        Investor john = new IndividualInvestor("John", 145.0);
        Investor goldman = new InstitutionalInvestor("Goldman Sachs", 1_000_000);
        Investor bot = new TradingBot("AlphaBot");

        apple.registerObserver(john);
        apple.registerObserver(goldman);
        apple.registerObserver(bot);

        apple.setPrice(148.0);
        apple.setPrice(142.0); // Below John's threshold
        apple.setPrice(155.0); // Above Goldman's threshold

        apple.removeObserver(john);
        apple.setPrice(160.0);

        // Weather Station
        System.out.println("\n2. Weather Station:");
        WeatherStation station = new WeatherStation();
        station.addObserver(new PhoneDisplay("iPhone"));
        station.addObserver(new PhoneDisplay("Android"));
        station.addObserver(new WindowDisplay());

        station.setMeasurements(28.5f, 65.0f, 1013.0f);
        station.setMeasurements(18.0f, 80.0f, 1008.0f);
        station.setMeasurements(10.0f, 90.0f, 1002.0f);

        System.out.println("\nKey points:");
        System.out.println("- One-to-many dependency between subject and observers");
        System.out.println("- Observers are notified automatically on state change");
        System.out.println("- Loose coupling: subject doesn't know observer details");
        System.out.println("- Observers can be added/removed dynamically");
    }
}
