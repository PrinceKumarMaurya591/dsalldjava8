package com.dsa.interview;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.lang.ref.*;
import java.lang.reflect.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * Java Core Interview Questions - Code Examples
 * 
 * Covers: OOP Concepts, Collections, Multithreading, Exception Handling,
 * Memory Management, Serialization, Immutability, Equals/HashCode,
 * Generics, Annotations, Reflection, NIO, Garbage Collection, Enums
 */
public class JavaCoreInterviewQuestions {

    // =============================================
    // 1. OOP CONCEPTS
    // =============================================

    /** Q1: Encapsulation */
    static class BankAccount {
        private String accountNumber;
        private double balance;

        public BankAccount(String accountNumber, double initialBalance) {
            this.accountNumber = accountNumber;
            this.balance = initialBalance;
        }

        public String getAccountNumber() { return accountNumber; }
        public double getBalance() { return balance; }

        public void deposit(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
            balance += amount;
        }

        public void withdraw(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
            if (amount > balance) throw new IllegalStateException("Insufficient funds");
            balance -= amount;
        }
    }

    /** Q2: Inheritance and Polymorphism */
    static class Animal {
        public String sound() { return "Some sound"; }
    }

    static class Dog extends Animal {
        @Override
        public String sound() { return "Bark"; }
    }

    static class Cat extends Animal {
        @Override
        public String sound() { return "Meow"; }
    }

    /** Q3: Abstraction */
    abstract static class Database {
        public abstract void connect();
        public abstract void disconnect();
        public abstract void executeQuery(String query);

        public void performOperation(String query) {
            connect();
            executeQuery(query);
            disconnect();
        }
    }

    static class MySQLDatabase extends Database {
        @Override
        public void connect() { System.out.println("Connecting to MySQL..."); }
        @Override
        public void disconnect() { System.out.println("Disconnecting from MySQL..."); }
        @Override
        public void executeQuery(String query) { System.out.println("MySQL executing: " + query); }
    }

    /** Q4: Composition vs Inheritance */
    static class Engine {
        private String type;
        public Engine(String type) { this.type = type; }
        public void start() { System.out.println(type + " engine started"); }
    }

    static class Car {
        private Engine engine;
        private String model;

        public Car(String model, String engineType) {
            this.model = model;
            this.engine = new Engine(engineType);
        }

        public void start() {
            engine.start();
            System.out.println(model + " is running");
        }
    }

    // =============================================
    // 2. EQUALS & HASHCODE
    // =============================================

    /** Q5: Proper equals() and hashCode() */
    static class Person {
        private String name;
        private int age;
        private String email;

        public Person(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age &&
                    Objects.equals(name, person.name) &&
                    Objects.equals(email, person.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, email);
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    // =============================================
    // 3. IMMUTABLE CLASS
    // =============================================

    /** Q6: Create an immutable class */
    static final class ImmutableEmployee {
        private final int id;
        private final String name;
        private final List<String> skills;

        public ImmutableEmployee(int id, String name, List<String> skills) {
            this.id = id;
            this.name = name;
            this.skills = skills == null ? Collections.emptyList() : new ArrayList<>(skills);
        }

        public int getId() { return id; }
        public String getName() { return name; }

        public List<String> getSkills() {
            return Collections.unmodifiableList(skills);
        }
    }

    // =============================================
    // 4. COLLECTIONS
    // =============================================

    /** Q7: HashMap internal working */
    public static void hashMapDemo() {
        Map<String, Integer> map = new HashMap<>();
        map.put("apple", 1);
        map.put("banana", 2);
        map.put("cherry", 3);
        System.out.println("Get 'apple': " + map.get("apple"));
        System.out.println("HashMap entries: " + map);

        // ConcurrentModificationException demo
        try {
            for (String key : map.keySet()) {
                if (key.equals("apple")) {
                    map.remove(key);
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException caught - use Iterator.remove()");
        }

        // Safe removal with iterator
        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = it.next();
            if (entry.getKey().equals("banana")) {
                it.remove();
            }
        }
        System.out.println("After safe removal: " + map);
    }

    /** Q8: ConcurrentHashMap and CopyOnWriteArrayList */
    public static void concurrentCollectionDemo() throws InterruptedException {
        Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        concurrentMap.put("x", 1);
        concurrentMap.put("y", 2);
        System.out.println("ConcurrentHashMap: " + concurrentMap);

        List<String> copyOnWriteList = new CopyOnWriteArrayList<>(Arrays.asList("a", "b", "c"));
        for (String s : copyOnWriteList) {
            if (s.equals("b")) {
                copyOnWriteList.add("d");
            }
        }
        System.out.println("CopyOnWriteArrayList: " + copyOnWriteList);

        // BlockingQueue producer-consumer
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    queue.put(i);
                    System.out.println("Produced: " + i);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    Integer value = queue.take();
                    System.out.println("Consumed: " + value);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

    // =============================================
    // 5. MULTITHREADING
    // =============================================

    /** Q9: Thread creation */
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Thread extending Thread: " + Thread.currentThread().getName());
        }
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Thread implementing Runnable: " + Thread.currentThread().getName());
        }
    }

    /** Q10: Synchronization */
    static class Counter {
        private int count = 0;
        private final Object lock = new Object();

        public synchronized void incrementSyncMethod() { count++; }

        public void incrementSyncBlock() {
            synchronized (lock) { count++; }
        }

        public int getCount() { return count; }
    }

    /** Q11: Deadlock example */
    static class DeadlockExample {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();

        public void method1() {
            synchronized (lock1) {
                System.out.println(Thread.currentThread().getName() + " acquired lock1");
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                synchronized (lock2) {
                    System.out.println(Thread.currentThread().getName() + " acquired lock2");
                }
            }
        }

        public void method2() {
            synchronized (lock2) {
                System.out.println(Thread.currentThread().getName() + " acquired lock2");
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                synchronized (lock1) {
                    System.out.println(Thread.currentThread().getName() + " acquired lock1");
                }
            }
        }

        // Deadlock prevention: consistent lock ordering
        public void method1Safe() {
            synchronized (lock1) { synchronized (lock2) { /* safe */ } }
        }
        public void method2Safe() {
            synchronized (lock1) { synchronized (lock2) { /* safe */ } }
        }
    }

    /** Q12: ReentrantLock */
    static class ReentrantLockDemo {
        private final ReentrantLock lock = new ReentrantLock();
        private int sharedResource = 0;

        public void safeIncrement() {
            lock.lock();
            try { sharedResource++; }
            finally { lock.unlock(); }
        }

        public boolean tryIncrement(long timeout, TimeUnit unit) throws InterruptedException {
            if (lock.tryLock(timeout, unit)) {
                try { sharedResource++; return true; }
                finally { lock.unlock(); }
            }
            return false;
        }

        public int getValue() { return sharedResource; }
    }

    /** Q13: CountDownLatch */
    public static void countDownLatchDemo() throws InterruptedException {
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            final int taskId = i;
            new Thread(() -> {
                try {
                    System.out.println("Task " + taskId + " executing");
                    Thread.sleep(new Random().nextInt(500));
                    System.out.println("Task " + taskId + " completed");
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                finally { latch.countDown(); }
            }).start();
        }
        latch.await();
        System.out.println("All tasks completed, main thread proceeding");
    }

    /** Q14: CyclicBarrier */
    public static void cyclicBarrierDemo() {
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties, () ->
                System.out.println("All parties arrived at barrier"));
        for (int i = 0; i < parties; i++) {
            final int taskId = i;
            new Thread(() -> {
                try {
                    System.out.println("Task " + taskId + " waiting at barrier");
                    barrier.await();
                    System.out.println("Task " + taskId + " passed barrier");
                } catch (Exception e) { Thread.currentThread().interrupt(); }
            }).start();
        }
    }

    /** Q15: Semaphore */
    static class ConnectionPool {
        private final Semaphore semaphore;
        private final List<String> connections = new ArrayList<>();

        public ConnectionPool(int poolSize) {
            semaphore = new Semaphore(poolSize);
            for (int i = 0; i < poolSize; i++) {
                connections.add("Connection-" + i);
            }
        }

        public String acquireConnection() throws InterruptedException {
            semaphore.acquire();
            synchronized (connections) { return connections.remove(0); }
        }

        public void releaseConnection(String connection) {
            synchronized (connections) { connections.add(connection); }
            semaphore.release();
        }
    }

    /** Q16: ThreadLocal */
    static class ThreadLocalDemo {
        private static final ThreadLocal<Integer> userId = ThreadLocal.withInitial(() -> -1);

        public static void setUserId(int id) { userId.set(id); }
        public static int getUserId() { return userId.get(); }
        public static void clear() { userId.remove(); }
    }

    /** Q17: Volatile */
    static class VolatileFlag {
        private volatile boolean running = true;

        public void stop() { running = false; }

        public void doWork() {
            while (running) { /* busy wait */ }
            System.out.println("Stopped");
        }
    }

    /** Q18: Atomic classes */
    static class AtomicCounter {
        private final AtomicInteger count = new AtomicInteger(0);

        public int incrementAndGet() { return count.incrementAndGet(); }
        public int getCount() { return count.get(); }
    }

    // =============================================
    // 6. EXCEPTION HANDLING
    // =============================================

    /** Q19: Custom exceptions */
    static class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message) { super(message); }
    }

    static class InvalidTransactionException extends RuntimeException {
        public InvalidTransactionException(String message) { super(message); }
    }

    /** Q20: Try-with-resources */
    static class Resource implements AutoCloseable {
        private final String name;
        public Resource(String name) { this.name = name; }
        public void doSomething() { System.out.println(name + " doing something"); }
        @Override
        public void close() { System.out.println(name + " closed"); }
    }

    public static void tryWithResourcesDemo() {
        try (Resource r1 = new Resource("Resource1");
             Resource r2 = new Resource("Resource2")) {
            r1.doSomething();
            r2.doSomething();
        }
    }

    /** Q21: Exception chaining */
    public static void exceptionChainingDemo() {
        try {
            try {
                throw new IllegalArgumentException("Original cause");
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Wrapped exception", e);
            }
        } catch (RuntimeException e) {
            System.out.println("Exception: " + e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
        }
    }

    // =============================================
    // 7. SERIALIZATION
    // =============================================

    /** Q22: Custom serialization */
    static class Employee implements Serializable {
        private static final long serialVersionUID = 1L;
        private int id;
        private String name;
        private transient String password;

        public Employee(int id, String name, String password) {
            this.id = id;
            this.name = name;
            this.password = password;
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject();
            oos.writeObject(password != null ? "encrypted:" + password : null);
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject();
            String encrypted = (String) ois.readObject();
            if (encrypted != null && encrypted.startsWith("encrypted:")) {
                password = encrypted.substring("encrypted:".length());
            }
        }

        @Override
        public String toString() {
            return "Employee{id=" + id + ", name='" + name + "', password='" + password + "'}";
        }
    }

    // =============================================
    // 8. GENERICS
    // =============================================

    /** Q23: Generic class with bounded type */
    static class Box<T extends Comparable<T>> {
        private T value;
        public Box(T value) { this.value = value; }
        public T getValue() { return value; }
        public boolean isGreaterThan(Box<T> other) {
            return this.value.compareTo(other.value) > 0;
        }
    }

    /** Q24: Wildcards - PECS */
    static class WildcardDemo {
        public static double sumOfNumbers(List<? extends Number> numbers) {
            double sum = 0;
            for (Number n : numbers) sum += n.doubleValue();
            return sum;
        }

        public static void addIntegers(List<? super Integer> list) {
            for (int i = 1; i <= 5; i++) list.add(i);
        }

        public static void printList(List<?> list) {
            for (Object obj : list) System.out.print(obj + " ");
            System.out.println();
        }

        public static <T> void copy(List<? extends T> source, List<? super T> dest) {
            for (T item : source) dest.add(item);
        }
    }

    // =============================================
    // 9. ANNOTATIONS & REFLECTION
    // =============================================

    /** Q25: Custom annotations */
    @interface JsonField {
        String name() default "";
        boolean required() default true;
    }

    @interface JsonSerializable {
        String version() default "1.0";
    }

    @JsonSerializable(version = "2.0")
    static class User {
        @JsonField(name = "user_name", required = true)
        private String username;

        @JsonField(name = "email_address", required = true)
        private String email;

        @JsonField(name = "age", required = false)
        private int age;

        public User(String username, String email, int age) {
            this.username = username;
            this.email = email;
            this.age = age;
        }

        public String getUsername() { return username; }
    }

    /** Q26: Reflection API */
    public static void reflectionDemo() throws Exception {
        Class<?> clazz = Class.forName("com.dsa.interview.JavaCoreInterviewQuestions$User");
        System.out.println("Annotations on class:");
        for (var ann : clazz.getAnnotations()) {
            System.out.println("  " + ann);
        }
        System.out.println("Declared fields:");
        for (var field : clazz.getDeclaredFields()) {
            System.out.println("  " + field.getName() + " (" + field.getType().getSimpleName() + ")");
            for (var ann : field.getAnnotations()) {
                System.out.println("    @" + ann.annotationType().getSimpleName());
            }
        }
        Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, String.class, int.class);
        Object user = constructor.newInstance("john_doe", "john@example.com", 25);
        Field usernameField = clazz.getDeclaredField("username");
        usernameField.setAccessible(true);
        System.out.println("Username via reflection: " + usernameField.get(user));
    }

    // =============================================
    // 10. STRING & MEMORY
    // =============================================

    /** Q27: String pool */
    public static void stringPoolDemo() {
        String s1 = "hello";
        String s2 = "hello";
        String s3 = new String("hello");
        String s4 = s3.intern();

        System.out.println("s1 == s2: " + (s1 == s2));
        System.out.println("s1 == s3: " + (s1 == s3));
        System.out.println("s1 == s4: " + (s1 == s4));
        System.out.println("s1.equals(s3): " + s1.equals(s3));

        StringBuilder sb = new StringBuilder();
        sb.append("Hello").append(" ").append("World");
        System.out.println("StringBuilder: " + sb);
    }

    /** Q28: GC eligibility */
    static class GCDemo {
        private String name;
        private GCDemo reference;

        public GCDemo(String name) { this.name = name; }

        @Override
        protected void finalize() throws Throwable {
            System.out.println(name + " garbage collected");
        }

        public static void gcEligibilityDemo() {
            GCDemo obj1 = new GCDemo("Obj1");
            obj1 = null;

            GCDemo obj2 = new GCDemo("Obj2");
            GCDemo obj3 = new GCDemo("Obj3");
            obj2 = obj3;

            GCDemo objA = new GCDemo("ObjA");
            GCDemo objB = new GCDemo("ObjB");
            objA.reference = objB;
            objB.reference = objA;
            objA = null;
            objB = null;

            System.gc();
        }
    }

    /** Q29: Reference types */
    public static void referenceTypesDemo() {
        String strongRef = new String("Strong Reference");
        WeakReference<String> weakRef = new WeakReference<>(new String("Weak Reference"));
        SoftReference<String> softRef = new SoftReference<>(new String("Soft Reference"));

        System.out.println("Strong: " + strongRef);
        System.out.println("Weak before GC: " + weakRef.get());
        System.out.println("Soft before GC: " + softRef.get());

        System.gc();

        System.out.println("Weak after GC: " + weakRef.get());
        System.out.println("Soft after GC: " + softRef.get());
    }

    // =============================================
    // 11. NIO
    // =============================================

    /** Q30: NIO FileChannel */
    public static void nioFileReadDemo(String filePath) throws Exception {
        try (var fileChannel = FileChannel.open(
                java.nio.file.Path.of(filePath), java.nio.file.StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = fileChannel.read(buffer);
            while (bytesRead != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                buffer.clear();
                bytesRead = fileChannel.read(buffer);
            }
        }
    }

    /** Q31: Files.walk() */
    public static void fileTraversalDemo(String directory) throws Exception {
        try (var stream = java.nio.file.Files.walk(java.nio.file.Path.of(directory))) {
            stream.filter(p -> p.toString().endsWith(".java"))
                    .limit(5)
                    .forEach(System.out::println);
        }
    }

    // =============================================
    // 12. ENUMS
    // =============================================

    /** Q32: Enum with abstract methods */
    enum Operation {
        ADD("+") { public double apply(double x, double y) { return x + y; } },
        SUBTRACT("-") { public double apply(double x, double y) { return x - y; } },
        MULTIPLY("*") { public double apply(double x, double y) { return x * y; } },
        DIVIDE("/") { public double apply(double x, double y) {
            if (y == 0) throw new ArithmeticException("Division by zero");
            return x / y;
        } };

        private final String symbol;
        Operation(String symbol) { this.symbol = symbol; }
        public String getSymbol() { return symbol; }
        public abstract double apply(double x, double y);
    }

    /** Q33: EnumMap and EnumSet */
    enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    public static void enumCollectionsDemo() {
        Map<Day, String> schedule = new EnumMap<>(Day.class);
        schedule.put(Day.MONDAY, "Work");
        schedule.put(Day.SATURDAY, "Party");
        schedule.put(Day.SUNDAY, "Rest");
        System.out.println("EnumMap: " + schedule);

        Set<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
        Set<Day> weekdays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
        System.out.println("Weekend: " + weekend);
        System.out.println("Weekdays: " + weekdays);
    }

    // =============================================
    // MAIN METHOD
    // =============================================

    public static void main(String[] args) throws Exception {
        System.out.println("JAVA CORE INTERVIEW QUESTIONS DEMONSTRATION\n");

        System.out.println("================================================");
        System.out.println("1. OOP CONCEPTS");
        System.out.println("================================================\n");

        BankAccount account = new BankAccount("ACC123", 1000);
        account.deposit(500);
        account.withdraw(200);
        System.out.println("Q1 - Encapsulation: Balance = " + account.getBalance());

        Animal dog = new Dog();
        Animal cat = new Cat();
        System.out.println("Q2 - Polymorphism: Dog says " + dog.sound() + ", Cat says " + cat.sound());

        Database db = new MySQLDatabase();
        System.out.print("Q3 - Abstraction: ");
        db.performOperation("SELECT * FROM users");

        Car car = new Car("Sedan", "V6");
        System.out.print("Q4 - Composition: ");
        car.start();

        System.out.println("\n================================================");
        System.out.println("2. EQUALS & HASHCODE");
        System.out.println("================================================\n");

        Person p1 = new Person("Alice", 30, "alice@example.com");
        Person p2 = new Person("Alice", 30, "alice@example.com");
        Person p3 = new Person("Bob", 25, "bob@example.com");
        System.out.println("Q5 - equals: p1.equals(p2) = " + p1.equals(p2));
        System.out.println("Q5 - hashCode: p1=" + p1.hashCode() + ", p2=" + p2.hashCode());
        Set<Person> personSet = new HashSet<>();
        personSet.add(p1);
        personSet.add(p2);
        personSet.add(p3);
        System.out.println("Q5 - HashSet size (should be 2): " + personSet.size());

        System.out.println("\n================================================");
        System.out.println("3. IMMUTABLE CLASS");
        System.out.println("================================================\n");

        List<String> skills = new ArrayList<>(Arrays.asList("Java", "Spring", "AWS"));
        ImmutableEmployee emp = new ImmutableEmployee(1, "John", skills);
        skills.add("Python");
        System.out.println("Q6 - Immutable: " + emp.getName() + ", skills=" + emp.getSkills());
        try {
            emp.getSkills().add("Hack");
        } catch (UnsupportedOperationException e) {
            System.out.println("Q6 - Cannot modify: " + e.getClass().getSimpleName());
        }

        System.out.println("\n================================================");
        System.out.println("4. COLLECTIONS");
        System.out.println("================================================\n");

        System.out.println("Q7 - HashMap demo:");
        hashMapDemo();

        System.out.println("\nQ8 - Concurrent collections:");
        concurrentCollectionDemo();

        System.out.println("\n================================================");
        System.out.println("5. MULTITHREADING");
        System.out.println("================================================\n");

        System.out.println("Q9 - Thread creation:");
        new MyThread().start();
        new Thread(new MyRunnable()).start();
        new Thread(() -> System.out.println("Thread with lambda: " + Thread.currentThread().getName())).start();
        Thread.sleep(100);

        System.out.println("\nQ10 - Synchronization:");
        Counter counter = new Counter();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) counter.incrementSyncBlock();
            });
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        System.out.println("Counter (should be 10000): " + counter.getCount());

        System.out.println("\nQ11 - Deadlock:");
        DeadlockExample deadlock = new DeadlockExample();
        Thread t1 = new Thread(deadlock::method1, "T1");
        Thread t2 = new Thread(deadlock::method2, "T2");
        t1.start();
        t2.start();
        t1.join(300);
        t2.join(300);
        System.out.println("(Deadlock may occur - use consistent lock ordering to prevent)");

        System.out.println("\nQ12 - ReentrantLock:");
        ReentrantLockDemo lockDemo = new ReentrantLockDemo();
        Thread[] lockThreads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            lockThreads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) lockDemo.safeIncrement();
            });
            lockThreads[i].start();
        }
        for (Thread t : lockThreads) t.join();
        System.out.println("ReentrantLock (should be 10000): " + lockDemo.getValue());

        System.out.println("\nQ13 - CountDownLatch:");
        countDownLatchDemo();

        System.out.println("\nQ14 - CyclicBarrier:");
        cyclicBarrierDemo();
        Thread.sleep(500);

        System.out.println("\nQ15 - Semaphore:");
        ConnectionPool pool = new ConnectionPool(2);
        Runnable poolTask = () -> {
            try {
                String conn = pool.acquireConnection();
                System.out.println("Acquired: " + conn);
                Thread.sleep(100);
                pool.releaseConnection(conn);
                System.out.println("Released: " + conn);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        };
        for (int i = 0; i < 4; i++) new Thread(poolTask).start();
        Thread.sleep(800);

        System.out.println("\nQ16 - ThreadLocal:");
        ThreadLocalDemo.setUserId(100);
        System.out.println("Main thread userId: " + ThreadLocalDemo.getUserId());
        new Thread(() -> {
            ThreadLocalDemo.setUserId(200);
            System.out.println("Child thread userId: " + ThreadLocalDemo.getUserId());
            ThreadLocalDemo.clear();
        }).start();
        Thread.sleep(100);

        System.out.println("\nQ17 - Volatile:");
        VolatileFlag flag = new VolatileFlag();
        new Thread(flag::doWork).start();
        Thread.sleep(100);
        flag.stop();
        Thread.sleep(100);

        System.out.println("\nQ18 - Atomic classes:");
        AtomicCounter atomicCounter = new AtomicCounter();
        Thread[] atomicThreads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            atomicThreads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) atomicCounter.incrementAndGet();
            });
            atomicThreads[i].start();
        }
        for (Thread t : atomicThreads) t.join();
        System.out.println("Atomic counter (should be 10000): " + atomicCounter.getCount());

        System.out.println("\n================================================");
        System.out.println("6. EXCEPTION HANDLING");
        System.out.println("================================================\n");

        System.out.println("Q20 - Try-with-resources:");
        tryWithResourcesDemo();

        System.out.println("\nQ21 - Exception chaining:");
        exceptionChainingDemo();

        System.out.println("\n================================================");
        System.out.println("7. SERIALIZATION");
        System.out.println("================================================\n");

        Employee empSer = new Employee(1, "John", "secret123");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(empSer);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Employee deserialized = (Employee) ois.readObject();
        ois.close();
        System.out.println("Q22 - Serialization: " + deserialized);

        System.out.println("\n================================================");
        System.out.println("8. GENERICS");
        System.out.println("================================================\n");

        Box<Integer> box1 = new Box<>(10);
        Box<Integer> box2 = new Box<>(5);
        System.out.println("Q23 - Box: box1 > box2 = " + box1.isGreaterThan(box2));

        System.out.println("Q24 - Wildcards:");
        System.out.println("  Sum: " + WildcardDemo.sumOfNumbers(Arrays.asList(1, 2, 3, 4.5)));
        List<Object> dest = new ArrayList<>();
        WildcardDemo.addIntegers(dest);
        System.out.println("  After addIntegers: " + dest);

        System.out.println("\n================================================");
        System.out.println("9. ANNOTATIONS & REFLECTION");
        System.out.println("================================================\n");

        System.out.println("Q26 - Reflection:");
        reflectionDemo();

        System.out.println("\n================================================");
        System.out.println("10. STRING & MEMORY");
        System.out.println("================================================\n");

        System.out.println("Q27 - String pool:");
        stringPoolDemo();

        System.out.println("\nQ28 - GC eligibility:");
        GCDemo.gcEligibilityDemo();
        Thread.sleep(500);

        System.out.println("\nQ29 - Reference types:");
        referenceTypesDemo();

        System.out.println("\n================================================");
        System.out.println("11. NIO");
        System.out.println("================================================\n");

        System.out.println("Q31 - File traversal:");
        fileTraversalDemo("src/main/java/com/dsa/interview");

        System.out.println("\n================================================");
        System.out.println("12. ENUMS");
        System.out.println("================================================\n");

        System.out.println("Q32 - Enum operations:");
        System.out.println("  10 + 5 = " + Operation.ADD.apply(10, 5));
        System.out.println("  10 / 3 = " + Operation.DIVIDE.apply(10, 3));

        System.out.println("\nQ33 - EnumMap/EnumSet:");
        enumCollectionsDemo();

        System.out.println("\n================================================");
        System.out.println("DEMONSTRATION COMPLETE");
        System.out.println("================================================");
    }
}
