package com.dsa.designpatterns.behavioral;

// ============================================
// Strategy Pattern
// ============================================
//
// Intent: Define a family of algorithms, encapsulate each one, and make them
// interchangeable. Strategy lets the algorithm vary independently from clients that use it.
//
// When to use:
// - Many related classes differ only in their behavior
// - You need different variants of an algorithm
// - An algorithm uses data that clients shouldn't know about
// - A class defines many behaviors as conditional statements
//
// Benefits:
// - Open/Closed: new strategies can be added without changing context
// - Eliminates conditional statements
// - Algorithms can be reused across different contexts
// - Strategy can be changed at runtime
//
// Real-world examples:
// - java.util.Comparator (sorting strategy)
// - javax.servlet.http.HttpServlet (service method)
// - Spring Dependency Injection (strategy via configuration)
// - Payment processing (different payment methods)

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Step 1: Strategy interface
interface PaymentStrategy {
    void pay(double amount);
    String getPaymentMethod();
}

// Step 2: Concrete Strategies
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String name;

    public CreditCardPayment(String cardNumber, String name) {
        this.cardNumber = maskCardNumber(cardNumber);
        this.name = name;
    }

    private String maskCardNumber(String number) {
        if (number.length() >= 4) {
            return "****-****-****-" + number.substring(number.length() - 4);
        }
        return number;
    }

    @Override
    public void pay(double amount) {
        System.out.println("    Paid $" + String.format("%.2f", amount)
                + " via Credit Card (" + cardNumber + ")");
    }

    @Override
    public String getPaymentMethod() { return "Credit Card"; }
}

class PayPalPayment implements PaymentStrategy {
    private String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(double amount) {
        System.out.println("    Paid $" + String.format("%.2f", amount)
                + " via PayPal (" + email + ")");
    }

    @Override
    public String getPaymentMethod() { return "PayPal"; }
}

class CryptoPayment implements PaymentStrategy {
    private String walletAddress;

    public CryptoPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void pay(double amount) {
        System.out.println("    Paid $" + String.format("%.2f", amount)
                + " via Cryptocurrency (" + walletAddress.substring(0, 8) + "...)");
    }

    @Override
    public String getPaymentMethod() { return "Cryptocurrency"; }
}

class CashPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("    Paid $" + String.format("%.2f", amount) + " in Cash");
    }

    @Override
    public String getPaymentMethod() { return "Cash"; }
}

// Step 3: Context - uses a Strategy
class ShoppingCart {
    private List<Item> items;
    private PaymentStrategy paymentStrategy;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
        System.out.println("    Added: " + item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
        System.out.println("    Payment method changed to: " + strategy.getPaymentMethod());
    }

    public void checkout() {
        double total = getTotal();
        if (total == 0) {
            System.out.println("    Cart is empty!");
            return;
        }
        if (paymentStrategy == null) {
            System.out.println("    No payment method selected!");
            return;
        }
        System.out.println("\n  --- Checkout ---");
        System.out.println("  Total: $" + String.format("%.2f", total));
        paymentStrategy.pay(total);
        items.clear();
        System.out.println("  --- Order Complete ---\n");
    }
}

class Item {
    private String name;
    private double price;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " ($" + String.format("%.2f", price) + ")";
    }
}

// ============================================
// Another example: Sorting strategies
// ============================================

interface SortStrategy {
    void sort(int[] array);
    String getName();
}

class BubbleSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    @Override
    public String getName() { return "Bubble Sort"; }
}

class QuickSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    @Override
    public String getName() { return "Quick Sort"; }
}

class Sorter {
    private SortStrategy strategy;

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
        System.out.println("    Strategy: " + strategy.getName());
    }

    public void sort(int[] array) {
        if (strategy == null) {
            System.out.println("    No sorting strategy set!");
            return;
        }
        System.out.print("    Before: ");
        printArray(array);
        strategy.sort(array);
        System.out.print("    After:  ");
        printArray(array);
    }

    private void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) System.out.print(", ");
        }
        System.out.println();
    }
}

// Step 4: Demo
public class StrategyPattern {

    public static void main(String[] args) {
        System.out.println("=== Strategy Pattern ===");

        // Payment strategies
        System.out.println("\n1. Payment Strategies:");
        ShoppingCart cart = new ShoppingCart();

        cart.addItem(new Item("Laptop", 999.99));
        cart.addItem(new Item("Mouse", 29.99));
        cart.addItem(new Item("Keyboard", 79.99));

        // Pay with different strategies
        cart.setPaymentStrategy(new CreditCardPayment("1234567890123456", "John Doe"));
        cart.checkout();

        // New cart, different payment
        ShoppingCart cart2 = new ShoppingCart();
        cart2.addItem(new Item("Book", 19.99));
        cart2.setPaymentStrategy(new PayPalPayment("john@example.com"));
        cart2.checkout();

        // Change strategy at runtime
        ShoppingCart cart3 = new ShoppingCart();
        cart3.addItem(new Item("NFT Art", 500.00));
        cart3.setPaymentStrategy(new CryptoPayment("0xabc123def456..."));
        cart3.checkout();

        // Sorting strategies
        System.out.println("\n2. Sorting Strategies:");
        Sorter sorter = new Sorter();
        int[] data = {64, 34, 25, 12, 22, 11, 90};

        sorter.setStrategy(new BubbleSortStrategy());
        sorter.sort(data.clone());

        int[] data2 = {64, 34, 25, 12, 22, 11, 90};
        sorter.setStrategy(new QuickSortStrategy());
        sorter.sort(data2);

        System.out.println("\nKey points:");
        System.out.println("- Family of interchangeable algorithms");
        System.out.println("- Strategy can be changed at runtime");
        System.out.println("- Eliminates conditional statements");
        System.out.println("- Open/Closed: easy to add new strategies");
    }
}
