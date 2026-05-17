package com.dsa.interview;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Java 8 Interview Programming Questions
 * 
 * Covers: Stream API, Lambda Expressions, Functional Interfaces,
 * Optional, Method References, Collectors, Parallel Streams,
 * Date/Time API, CompletableFuture
 */
public class Java8InterviewQuestions {

    // =============================================
    // 1. STREAM API QUESTIONS
    // =============================================

    /**
     * Q1: Filter even numbers from a list using Stream
     */
    public static List<Integer> filterEvenNumbers(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
    }

    /**
     * Q2: Find the sum of all elements in a list
     */
    public static int sumOfElements(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Q3: Find the maximum element in a list
     */
    public static Optional<Integer> findMax(List<Integer> numbers) {
        return numbers.stream()
                .max(Integer::compareTo);
    }

    /**
     * Q4: Convert list of strings to uppercase
     */
    public static List<String> toUpperCase(List<String> strings) {
        return strings.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    /**
     * Q5: Count strings with length > 3
     */
    public static long countStringsWithLengthGreaterThan(List<String> strings, int length) {
        return strings.stream()
                .filter(s -> s.length() > length)
                .count();
    }

    /**
     * Q6: Remove duplicates from a list
     */
    public static List<Integer> removeDuplicates(List<Integer> numbers) {
        return numbers.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Q7: Sort a list of strings by length
     */
    public static List<String> sortByLength(List<String> strings) {
        return strings.stream()
                .sorted(Comparator.comparingInt(String::length))
                .collect(Collectors.toList());
    }

    /**
     * Q8: Find the first element starting with 'A'
     */
    public static Optional<String> findFirstStartingWith(List<String> strings, String prefix) {
        return strings.stream()
                .filter(s -> s.startsWith(prefix))
                .findFirst();
    }

    /**
     * Q9: Check if any element matches a condition
     */
    public static boolean anyMatch(List<Integer> numbers, int threshold) {
        return numbers.stream()
                .anyMatch(n -> n > threshold);
    }

    /**
     * Q10: Group strings by their length
     */
    public static Map<Integer, List<String>> groupByLength(List<String> strings) {
        return strings.stream()
                .collect(Collectors.groupingBy(String::length));
    }

    /**
     * Q11: Partition numbers into even and odd
     */
    public static Map<Boolean, List<Integer>> partitionEvenOdd(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
    }

    /**
     * Q12: Join list of strings with delimiter
     */
    public static String joinStrings(List<String> strings, String delimiter) {
        return strings.stream()
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Q13: Find the average of numbers
     */
    public static OptionalDouble findAverage(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .average();
    }

    /**
     * Q14: Flatten a list of lists
     */
    public static List<Integer> flattenList(List<List<Integer>> listOfLists) {
        return listOfLists.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Q15: Find the second highest number
     */
    public static Optional<Integer> findSecondHighest(List<Integer> numbers) {
        return numbers.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst();
    }

    /**
     * Q16: Find the most frequent element
     */
    public static Optional<Integer> findMostFrequent(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    /**
     * Q17: Find duplicate elements in a list
     */
    public static Set<Integer> findDuplicates(List<Integer> numbers) {
        Set<Integer> seen = new HashSet<>();
        return numbers.stream()
                .filter(n -> !seen.add(n))
                .collect(Collectors.toSet());
    }

    /**
     * Q18: Find common elements between two lists
     */
    public static List<Integer> findCommonElements(List<Integer> list1, List<Integer> list2) {
        Set<Integer> set2 = new HashSet<>(list2);
        return list1.stream()
                .filter(set2::contains)
                .collect(Collectors.toList());
    }

    /**
     * Q19: Reverse a list using Stream
     */
    public static <T> List<T> reverseList(List<T> list) {
        return IntStream.range(0, list.size())
                .mapToObj(i -> list.get(list.size() - 1 - i))
                .collect(Collectors.toList());
    }

    /**
     * Q20: Find the sum of squares of odd numbers
     */
    public static int sumOfSquaresOfOdds(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 != 0)
                .mapToInt(n -> n * n)
                .sum();
    }

    /**
     * Q21: Convert list of integers to their binary strings
     */
    public static List<String> toBinaryStrings(List<Integer> numbers) {
        return numbers.stream()
                .map(Integer::toBinaryString)
                .collect(Collectors.toList());
    }

    /**
     * Q22: Find the longest string in a list
     */
    public static Optional<String> findLongestString(List<String> strings) {
        return strings.stream()
                .max(Comparator.comparingInt(String::length));
    }

    /**
     * Q23: Check if list is sorted
     */
    public static boolean isSorted(List<Integer> numbers) {
        return IntStream.range(0, numbers.size() - 1)
                .allMatch(i -> numbers.get(i) <= numbers.get(i + 1));
    }

    /**
     * Q24: Find frequency of each character in a string
     */
    public static Map<Character, Long> characterFrequency(String str) {
        return str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Q25: Find the first non-repeating character in a string
     */
    public static Optional<Character> firstNonRepeatingChar(String str) {
        Map<Character, Long> freq = str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
        
        return freq.entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    // =============================================
    // 2. OPTIONAL QUESTIONS
    // =============================================

    /**
     * Q26: Use Optional to avoid NullPointerException
     */
    public static String getOrDefault(String value, String defaultValue) {
        return Optional.ofNullable(value)
                .orElse(defaultValue);
    }

    /**
     * Q27: Execute action only if value is present
     */
    public static void ifPresent(String value, Consumer<String> action) {
        Optional.ofNullable(value).ifPresent(action);
    }

    /**
     * Q28: Chain Optional with map and filter
     */
    public static Optional<Integer> getStringLength(String value) {
        return Optional.ofNullable(value)
                .map(String::length)
                .filter(len -> len > 0);
    }

    /**
     * Q29: Throw exception if value is null
     */
    public static String getOrThrow(String value, Supplier<RuntimeException> exceptionSupplier) {
        return Optional.ofNullable(value)
                .orElseThrow(exceptionSupplier);
    }

    /**
     * Q30: Optional with flatMap for nested objects
     */
    static class Address {
        private String city;
        public Address(String city) { this.city = city; }
        public Optional<String> getCity() { return Optional.ofNullable(city); }
    }

    static class Employee {
        private Address address;
        public Employee(Address address) { this.address = address; }
        public Optional<Address> getAddress() { return Optional.ofNullable(address); }
    }

    public static Optional<String> getEmployeeCity(Employee emp) {
        return Optional.ofNullable(emp)
                .flatMap(Employee::getAddress)
                .flatMap(Address::getCity);
    }

    // =============================================
    // 3. FUNCTIONAL INTERFACES QUESTIONS
    // =============================================

    /**
     * Q31: Implement custom functional interface
     */
    @FunctionalInterface
    interface MathOperation {
        int operate(int a, int b);
    }

    public static int calculate(int a, int b, MathOperation operation) {
        return operation.operate(a, b);
    }

    /**
     * Q32: Use Predicate to filter
     */
    public static <T> List<T> filter(List<T> items, Predicate<T> predicate) {
        return items.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Q33: Use Function to transform
     */
    public static <T, R> List<R> transform(List<T> items, Function<T, R> function) {
        return items.stream()
                .map(function)
                .collect(Collectors.toList());
    }

    /**
     * Q34: Use Consumer to process
     */
    public static <T> void process(List<T> items, Consumer<T> consumer) {
        items.forEach(consumer);
    }

    /**
     * Q35: Use Supplier to generate
     */
    public static List<Integer> generate(int count, Supplier<Integer> supplier) {
        return Stream.generate(supplier)
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Q36: Use BiFunction
     */
    public static <T, U, R> List<R> combine(List<T> list1, List<U> list2, BiFunction<T, U, R> combiner) {
        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException("Lists must have same size");
        }
        return IntStream.range(0, list1.size())
                .mapToObj(i -> combiner.apply(list1.get(i), list2.get(i)))
                .collect(Collectors.toList());
    }

    /**
     * Q37: Chain multiple Predicates
     */
    public static <T> Predicate<T> allOf(List<Predicate<T>> predicates) {
        return predicates.stream()
                .reduce(Predicate::and)
                .orElse(t -> true);
    }

    /**
     * Q38: Create thread using lambda
     */
    public static Thread createThread(Runnable task) {
        return new Thread(task);
    }

    /**
     * Q39: Comparator using lambda
     */
    public static <T extends Comparable<T>> Comparator<T> reverseComparator() {
        return (a, b) -> b.compareTo(a);
    }

    /**
     * Q40: Custom collector implementation
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, List<T>> toImmutableList() {
        return Collector.of(
                (Supplier<List<T>>) ArrayList::new,
                List::add,
                (left, right) -> { left.addAll(right); return left; },
                Collections::unmodifiableList
        );
    }

    // =============================================
    // 4. PARALLEL STREAM QUESTIONS
    // =============================================

    /**
     * Q41: Parallel stream for performance
     */
    public static long sumUsingParallelStream(List<Integer> numbers) {
        return numbers.parallelStream()
                .mapToLong(Integer::longValue)
                .sum();
    }

    /**
     * Q42: Parallel stream with thread-safe collection
     */
    public static List<Integer> parallelFilter(List<Integer> numbers, Predicate<Integer> predicate) {
        return numbers.parallelStream()
                .filter(predicate)
                .collect(Collectors.toCollection(ConcurrentLinkedQueue::new))
                .stream()
                .collect(Collectors.toList());
    }

    // =============================================
    // 5. DATE/TIME API QUESTIONS
    // =============================================

    /**
     * Q43: Find age from birth date
     */
    public static int calculateAge(java.time.LocalDate birthDate) {
        return java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
    }

    /**
     * Q44: Find number of days between two dates
     */
    public static long daysBetween(java.time.LocalDate start, java.time.LocalDate end) {
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Q45: Check if a year is leap year
     */
    public static boolean isLeapYear(int year) {
        return java.time.Year.isLeap(year);
    }

    /**
     * Q46: Get current date/time in different formats
     */
    public static String getCurrentDateTime() {
        return java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
    }

    /**
     * Q47: Convert string to date
     */
    public static java.time.LocalDate parseDate(String dateStr, String pattern) {
        return java.time.LocalDate.parse(dateStr,
                java.time.format.DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Q48: Find the day of week for a given date
     */
    public static String getDayOfWeek(String dateStr) {
        return java.time.LocalDate.parse(dateStr)
                .getDayOfWeek()
                .toString();
    }

    // =============================================
    // 6. COMPLETABLEFUTURE QUESTIONS
    // =============================================

    /**
     * Q49: Run async task
     */
    public static CompletableFuture<String> runAsyncTask(String input) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Processed: " + input;
        });
    }

    /**
     * Q50: Combine two async tasks
     */
    public static CompletableFuture<String> combineAsyncTasks(String input1, String input2) {
        CompletableFuture<String> future1 = runAsyncTask(input1);
        CompletableFuture<String> future2 = runAsyncTask(input2);
        return future1.thenCombine(future2, (r1, r2) -> r1 + " | " + r2);
    }

    /**
     * Q51: Handle errors in CompletableFuture
     */
    public static CompletableFuture<String> safeAsyncTask(String input) {
        return CompletableFuture.supplyAsync(() -> {
            if (input == null) throw new IllegalArgumentException("Input cannot be null");
            return "Result: " + input;
        }).exceptionally(ex -> "Error: " + ex.getMessage());
    }

    // =============================================
    // 7. ADVANCED STREAM QUESTIONS
    // =============================================

    /**
     * Q52: Find the employee with highest salary
     */
    static class Emp {
        String name;
        double salary;
        String department;

        Emp(String name, double salary, String department) {
            this.name = name;
            this.salary = salary;
            this.department = department;
        }

        public String getName() { return name; }
        public double getSalary() { return salary; }
        public String getDepartment() { return department; }

        @Override
        public String toString() {
            return name + "($" + salary + ", " + department + ")";
        }
    }

    public static Optional<Emp> highestSalaryEmployee(List<Emp> employees) {
        return employees.stream()
                .max(Comparator.comparingDouble(Emp::getSalary));
    }

    /**
     * Q53: Group employees by department
     */
    public static Map<String, List<Emp>> groupByDepartment(List<Emp> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Emp::getDepartment));
    }

    /**
     * Q54: Find average salary by department
     */
    public static Map<String, Double> averageSalaryByDepartment(List<Emp> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Emp::getDepartment,
                        Collectors.averagingDouble(Emp::getSalary)
                ));
    }

    /**
     * Q55: Find top 3 highest paid employees
     */
    public static List<Emp> top3HighestPaid(List<Emp> employees) {
        return employees.stream()
                .sorted(Comparator.comparingDouble(Emp::getSalary).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * Q56: Find employees with salary > threshold in each department
     */
    public static Map<String, List<Emp>> highEarnersByDepartment(
            List<Emp> employees, double threshold) {
        return employees.stream()
                .filter(e -> e.getSalary() > threshold)
                .collect(Collectors.groupingBy(Emp::getDepartment));
    }

    /**
     * Q57: Find department with highest average salary
     */
    public static Optional<Map.Entry<String, Double>> departmentWithHighestAvgSalary(
            List<Emp> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Emp::getDepartment,
                        Collectors.averagingDouble(Emp::getSalary)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue());
    }

    /**
     * Q58: Count employees in each department
     */
    public static Map<String, Long> countByDepartment(List<Emp> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Emp::getDepartment,
                        Collectors.counting()
                ));
    }

    /**
     * Q59: Find the nth highest salary (distinct)
     */
    public static Optional<Double> nthHighestSalary(List<Emp> employees, int n) {
        return employees.stream()
                .map(Emp::getSalary)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(n - 1)
                .findFirst();
    }

    /**
     * Q60: Partition employees by salary threshold
     */
    public static Map<Boolean, List<Emp>> partitionBySalary(
            List<Emp> employees, double threshold) {
        return employees.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() > threshold));
    }

    // =============================================
    // 8. STRING MANIPULATION WITH STREAMS
    // =============================================

    /**
     * Q61: Count vowels in a string
     */
    public static long countVowels(String str) {
        return str.chars()
                .mapToObj(c -> (char) c)
                .filter(c -> "aeiouAEIOU".indexOf(c) >= 0)
                .count();
    }

    /**
     * Q62: Find all words starting with a vowel
     */
    public static List<String> wordsStartingWithVowel(String sentence) {
        return Arrays.stream(sentence.split("\\s+"))
                .filter(w -> !w.isEmpty() && "aeiouAEIOU".indexOf(w.charAt(0)) >= 0)
                .collect(Collectors.toList());
    }

    /**
     * Q63: Find the most common word in a sentence
     */
    public static Optional<String> mostCommonWord(String sentence) {
        return Arrays.stream(sentence.toLowerCase().split("\\s+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    /**
     * Q64: Check if string is palindrome using streams
     */
    public static boolean isPalindrome(String str) {
        String cleaned = str.replaceAll("\\s+", "").toLowerCase();
        return IntStream.range(0, cleaned.length() / 2)
                .allMatch(i -> cleaned.charAt(i) == cleaned.charAt(cleaned.length() - 1 - i));
    }

    /**
     * Q65: Find the longest word in a sentence
     */
    public static Optional<String> longestWord(String sentence) {
        return Arrays.stream(sentence.split("\\s+"))
                .max(Comparator.comparingInt(String::length));
    }

    /**
     * Q66: Remove duplicate characters from string
     */
    public static String removeDuplicateChars(String str) {
        return str.chars()
                .distinct()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    /**
     * Q67: Find the frequency of each word
     */
    public static Map<String, Long> wordFrequency(String sentence) {
        return Arrays.stream(sentence.toLowerCase().split("\\s+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Q68: Sort characters in a string
     */
    public static String sortCharacters(String str) {
        return str.chars()
                .sorted()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    /**
     * Q69: Find anagrams in a list of strings
     */
    public static List<List<String>> groupAnagrams(List<String> words) {
        return new ArrayList<>(words.stream()
                .collect(Collectors.groupingBy(w -> {
                    char[] chars = w.toCharArray();
                    Arrays.sort(chars);
                    return new String(chars);
                }))
                .values());
    }

    /**
     * Q70: Check if two strings are anagrams
     */
    public static boolean areAnagrams(String s1, String s2) {
        if (s1.length() != s2.length()) return false;
        char[] a1 = s1.toCharArray();
        char[] a2 = s2.toCharArray();
        Arrays.sort(a1);
        Arrays.sort(a2);
        return Arrays.equals(a1, a2);
    }

    // =============================================
    // 9. NUMERIC OPERATIONS WITH STREAMS
    // =============================================

    /**
     * Q71: Generate Fibonacci series using Stream
     */
    public static List<Integer> fibonacci(int count) {
        return Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
                .limit(count)
                .map(f -> f[0])
                .collect(Collectors.toList());
    }

    /**
     * Q72: Check if number is prime using Stream
     */
    public static boolean isPrime(int number) {
        if (number <= 1) return false;
        return IntStream.rangeClosed(2, (int) Math.sqrt(number))
                .noneMatch(i -> number % i == 0);
    }

    /**
     * Q73: Find prime numbers in a range
     */
    public static List<Integer> findPrimes(int start, int end) {
        return IntStream.rangeClosed(start, end)
                .filter(Java8InterviewQuestions::isPrime)
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Q74: Find factorial using Stream
     */
    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative input");
        return IntStream.rangeClosed(1, n)
                .mapToLong(Long::valueOf)
                .reduce(1, (a, b) -> a * b);
    }

    /**
     * Q75: Find GCD of two numbers using Stream
     */
    public static int gcd(int a, int b) {
        return Stream.iterate(new int[]{a, b}, pair -> new int[]{pair[1], pair[0] % pair[1]})
                .filter(pair -> pair[1] == 0)
                .findFirst()
                .map(pair -> pair[0])
                .orElse(a);
    }

    /**
     * Q76: Find LCM of two numbers
     */
    public static long lcm(int a, int b) {
        return (long) a * b / gcd(a, b);
    }

    /**
     * Q77: Generate random numbers using Stream
     */
    public static List<Integer> generateRandomNumbers(int count, int min, int max) {
        return new Random().ints(count, min, max + 1)
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Q78: Find the median of a list
     */
    public static double findMedian(List<Integer> numbers) {
        List<Integer> sorted = numbers.stream()
                .sorted()
                .collect(Collectors.toList());
        int n = sorted.size();
        if (n % 2 == 0) {
            return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
        }
        return sorted.get(n / 2);
    }

    /**
     * Q79: Find the mode of a list
     */
    public static List<Integer> findMode(List<Integer> numbers) {
        long maxFreq = numbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        return numbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() == maxFreq)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Q80: Find the kth smallest element
     */
    public static Optional<Integer> kthSmallest(List<Integer> numbers, int k) {
        return numbers.stream()
                .distinct()
                .sorted()
                .skip(k - 1)
                .findFirst();
    }

    // =============================================
    // 10. COLLECTION OPERATIONS
    // =============================================

    /**
     * Q81: Convert List to Map
     */
    public static Map<String, Integer> listToMap(List<Emp> employees) {
        return employees.stream()
                .collect(Collectors.toMap(
                        Emp::getName,
                        e -> (int) e.getSalary(),
                        (existing, replacement) -> existing // Handle duplicates
                ));
    }

    /**
     * Q82: Merge two maps
     */
    public static Map<String, Integer> mergeMaps(
            Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> result = new HashMap<>(map1);
        map2.forEach((key, value) ->
                result.merge(key, value, Integer::sum));
        return result;
    }

    /**
     * Q83: Sort a map by values
     */
    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(Map<K, V> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Q84: Find intersection of two sets
     */
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        return set1.stream()
                .filter(set2::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Q85: Find union of two sets
     */
    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
        return Stream.concat(set1.stream(), set2.stream())
                .collect(Collectors.toSet());
    }

    // =============================================
    // 11. REDUCTION OPERATIONS
    // =============================================

    /**
     * Q86: Find product of all elements using reduce
     */
    public static Optional<Integer> product(List<Integer> numbers) {
        return numbers.stream()
                .reduce((a, b) -> a * b);
    }

    /**
     * Q87: Find the longest string using reduce
     */
    public static Optional<String> longestString(List<String> strings) {
        return strings.stream()
                .reduce((s1, s2) -> s1.length() >= s2.length() ? s1 : s2);
    }

    /**
     * Q88: Combine all strings with comma using reduce
     */
    public static String combineWithComma(List<String> strings) {
        return strings.stream()
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");
    }

    /**
     * Q89: Find the total length of all strings
     */
    public static int totalLength(List<String> strings) {
        return strings.stream()
                .mapToInt(String::length)
                .sum();
    }

    /**
     * Q90: Find the minimum and maximum using summary statistics
     */
    public static IntSummaryStatistics getStatistics(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
    }

    // =============================================
    // 12. METHOD REFERENCE QUESTIONS
    // =============================================

    /**
     * Q91: Static method reference
     */
    public static List<Integer> parseStringsToInts(List<String> strings) {
        return strings.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * Q92: Instance method reference of a particular object
     */
    public static List<String> filterEmptyStrings(List<String> strings) {
        Predicate<String> nonEmpty = String::isEmpty;
        return strings.stream()
                .filter(nonEmpty.negate())
                .collect(Collectors.toList());
    }

    /**
     * Q93: Constructor reference
     */
    public static List<String> createStringList(List<Integer> numbers) {
        return numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * Q94: Array constructor reference
     */
    public static String[] toArray(List<String> list) {
        return list.stream()
                .toArray(String[]::new);
    }

    /**
     * Q95: Arbitrary object method reference
     */
    public static List<String> toLowercase(List<String> strings) {
        return strings.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    // =============================================
    // 13. INFINITE STREAMS
    // =============================================

    /**
     * Q96: Generate infinite stream of even numbers
     */
    public static List<Integer> generateEvenNumbers(int count) {
        return Stream.iterate(0, n -> n + 2)
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Q97: Generate infinite stream of random UUIDs
     */
    public static List<String> generateUUIDs(int count) {
        return Stream.generate(UUID::randomUUID)
                .limit(count)
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    /**
     * Q98: Generate powers of 2
     */
    public static List<Integer> powersOfTwo(int count) {
        return Stream.iterate(1, n -> n * 2)
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Q99: Generate prime numbers using infinite stream
     */
    public static List<Integer> generatePrimes(int count) {
        return Stream.iterate(2, n -> n + 1)
                .filter(Java8InterviewQuestions::isPrime)
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Q100: Generate Collatz sequence
     */
    public static List<Integer> collatzSequence(int start) {
        return Stream.iterate(start, n -> n != 1,
                        n -> n % 2 == 0 ? n / 2 : 3 * n + 1)
                .collect(Collectors.toList());
    }

    // =============================================
    // MAIN METHOD - DEMONSTRATION
    // =============================================

    public static void main(String[] args) {
        System.out.println("JAVA 8 INTERVIEW QUESTIONS DEMONSTRATION\n");
        System.out.println("================================================");
        System.out.println("1. STREAM API QUESTIONS");
        System.out.println("================================================\n");

        // Q1-Q5: Basic Stream operations
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("Q1 - Even numbers: " + filterEvenNumbers(numbers));
        System.out.println("Q2 - Sum: " + sumOfElements(numbers));
        System.out.println("Q3 - Max: " + findMax(numbers).orElse(-1));
        
        List<String> words = Arrays.asList("apple", "banana", "avocado", "cherry", "apricot", "date");
        System.out.println("Q4 - Uppercase: " + toUpperCase(words));
        System.out.println("Q5 - Count > 5: " + countStringsWithLengthGreaterThan(words, 5));

        // Q6-Q10
        List<Integer> withDups = Arrays.asList(1, 2, 3, 2, 4, 1, 5);
        System.out.println("Q6 - No duplicates: " + removeDuplicates(withDups));
        System.out.println("Q7 - Sorted by length: " + sortByLength(words));
        System.out.println("Q8 - First starting with 'a': " + findFirstStartingWith(words, "a").orElse("none"));
        System.out.println("Q9 - Any > 8: " + anyMatch(numbers, 8));
        System.out.println("Q10 - Group by length: " + groupByLength(words));

        // Q11-Q15
        System.out.println("Q11 - Even/Odd partition: " + partitionEvenOdd(numbers));
        System.out.println("Q12 - Joined: '" + joinStrings(words, ", ") + "'");
        System.out.println("Q13 - Average: " + findAverage(numbers).orElse(0));
        
        List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6)
        );
        System.out.println("Q14 - Flattened: " + flattenList(nested));
        System.out.println("Q15 - Second highest: " + findSecondHighest(numbers).orElse(-1));

        // Q16-Q20
        List<Integer> freqTest = Arrays.asList(1, 3, 2, 3, 4, 3, 2, 1, 5);
        System.out.println("Q16 - Most frequent: " + findMostFrequent(freqTest).orElse(-1));
        System.out.println("Q17 - Duplicates: " + findDuplicates(freqTest));
        
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(4, 5, 6, 7, 8);
        System.out.println("Q18 - Common: " + findCommonElements(list1, list2));
        System.out.println("Q19 - Reversed: " + reverseList(numbers));
        System.out.println("Q20 - Sum of squares of odds: " + sumOfSquaresOfOdds(numbers));

        // Q21-Q25
        System.out.println("Q21 - Binary: " + toBinaryStrings(Arrays.asList(1, 2, 3, 4, 5)));
        System.out.println("Q22 - Longest: " + findLongestString(words).orElse(""));
        System.out.println("Q23 - Sorted: " + isSorted(numbers));
        System.out.println("Q24 - Char freq: " + characterFrequency("hello world"));
        System.out.println("Q25 - First non-repeating: " + firstNonRepeatingChar("swiss").orElse(' '));

        System.out.println("\n================================================");
        System.out.println("2. OPTIONAL QUESTIONS");
        System.out.println("================================================\n");

        // Q26-Q30
        System.out.println("Q26 - orElse: '" + getOrDefault(null, "default") + "'");
        System.out.println("Q27 - ifPresent: ");
        ifPresent("Hello", s -> System.out.println("    Action executed: " + s));
        System.out.println("Q28 - map/filter: " + getStringLength("Java").orElse(0));
        try {
            System.out.println("Q29 - orElseThrow: " + getOrThrow("value", () -> new RuntimeException("Custom ex")));
        } catch (Exception e) {
            System.out.println("Q29 - orElseThrow caught: " + e.getMessage());
        }
        Employee emp = new Employee(new Address("New York"));
        System.out.println("Q30 - flatMap city: " + getEmployeeCity(emp).orElse("unknown"));

        System.out.println("\n================================================");
        System.out.println("3. FUNCTIONAL INTERFACES QUESTIONS");
        System.out.println("================================================\n");

        // Q31-Q40
        MathOperation add = (a, b) -> a + b;
        System.out.println("Q31 - Custom FI (add): " + calculate(10, 5, add));
        System.out.println("Q31 - Custom FI (multiply): " + calculate(10, 5, (a, b) -> a * b));
        System.out.println("Q32 - Predicate filter: " + filter(numbers, n -> n > 5));
        System.out.println("Q33 - Function transform: " + transform(words, String::toUpperCase));
        System.out.print("Q34 - Consumer process: ");
        process(Arrays.asList("a", "b", "c"), s -> System.out.print(s + " "));
        System.out.println();
        System.out.println("Q35 - Supplier generate: " + generate(5, () -> new Random().nextInt(100)));
        System.out.println("Q36 - BiFunction combine: " + combine(
                Arrays.asList(1, 2, 3), Arrays.asList(10, 20, 30), (a, b) -> a + b));
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        System.out.println("Q37 - Chained predicates: " + filter(Arrays.asList(-1, 0, 1, 2, 3),
                allOf(Arrays.asList(isEven, isPositive))));
        Thread t = createThread(() -> System.out.println("    Q38 - Lambda thread running"));
        t.start();
        try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        List<String> sortWords = new ArrayList<>(Arrays.asList("banana", "apple", "date", "cherry"));
        sortWords.sort(reverseComparator());
        System.out.println("Q39 - Reverse comparator: " + sortWords);
        List<Integer> immutable = Arrays.asList(1, 2, 3).stream()
                .collect(toImmutableList());
        System.out.println("Q40 - Immutable list: " + immutable);

        System.out.println("\n================================================");
        System.out.println("4. PARALLEL STREAM QUESTIONS");
        System.out.println("================================================\n");

        // Q41-Q42
        List<Integer> largeList = IntStream.rangeClosed(1, 1_000_000).boxed().collect(Collectors.toList());
        long start = System.currentTimeMillis();
        long sum = sumUsingParallelStream(largeList);
        long end = System.currentTimeMillis();
        System.out.println("Q41 - Parallel sum: " + sum + " (took " + (end - start) + "ms)");
        System.out.println("Q42 - Parallel filter: " + parallelFilter(largeList, n -> n % 100_000 == 0));

        System.out.println("\n================================================");
        System.out.println("5. DATE/TIME API QUESTIONS");
        System.out.println("================================================\n");

        // Q43-Q48
        java.time.LocalDate birthDate = java.time.LocalDate.of(1990, 1, 15);
        System.out.println("Q43 - Age: " + calculateAge(birthDate));
        java.time.LocalDate startDate = java.time.LocalDate.of(2024, 1, 1);
        java.time.LocalDate endDate = java.time.LocalDate.of(2024, 12, 31);
        System.out.println("Q44 - Days between: " + daysBetween(startDate, endDate));
        System.out.println("Q45 - Leap year 2024: " + isLeapYear(2024) + ", 2023: " + isLeapYear(2023));
        System.out.println("Q46 - Current datetime: " + getCurrentDateTime());
        System.out.println("Q47 - Parse date: " + parseDate("25-12-2024", "dd-MM-yyyy"));
        System.out.println("Q48 - Day of week for 2024-12-25: " + getDayOfWeek("2024-12-25"));

        System.out.println("\n================================================");
        System.out.println("6. COMPLETABLEFUTURE QUESTIONS");
        System.out.println("================================================\n");

        // Q49-Q51
        try {
            System.out.println("Q49 - Async: " + runAsyncTask("Hello").get());
            System.out.println("Q50 - Combined: " + combineAsyncTasks("Hello", "World").get());
            System.out.println("Q51 - Safe (valid): " + safeAsyncTask("Test").get());
            System.out.println("Q51 - Safe (error): " + safeAsyncTask(null).get());
        } catch (Exception e) {
            System.out.println("CompletableFuture error: " + e.getMessage());
        }

        System.out.println("\n================================================");
        System.out.println("7. ADVANCED STREAM WITH EMPLOYEE");
        System.out.println("================================================\n");

        // Q52-Q60
        List<Emp> employees = Arrays.asList(
            new Emp("Alice", 75000, "IT"),
            new Emp("Bob", 85000, "HR"),
            new Emp("Charlie", 95000, "IT"),
            new Emp("David", 65000, "Finance"),
            new Emp("Eve", 80000, "HR"),
            new Emp("Frank", 90000, "IT"),
            new Emp("Grace", 70000, "Finance")
        );
        System.out.println("Q52 - Highest salary: " + highestSalaryEmployee(employees).orElse(null));
        System.out.println("Q53 - By department: " + groupByDepartment(employees));
        System.out.println("Q54 - Avg salary by dept: " + averageSalaryByDepartment(employees));
        System.out.println("Q55 - Top 3 paid: " + top3HighestPaid(employees));
        System.out.println("Q56 - High earners (>80000): " + highEarnersByDepartment(employees, 80000));
        System.out.println("Q57 - Dept highest avg: " + departmentWithHighestAvgSalary(employees).orElse(null));
        System.out.println("Q58 - Count by dept: " + countByDepartment(employees));
        System.out.println("Q59 - 2nd highest salary: " + nthHighestSalary(employees, 2).orElse(0.0));
        System.out.println("Q60 - Partition (>75000): " + partitionBySalary(employees, 75000));

        System.out.println("\n================================================");
        System.out.println("8. STRING MANIPULATION WITH STREAMS");
        System.out.println("================================================\n");

        // Q61-Q70
        System.out.println("Q61 - Vowels in 'Hello World': " + countVowels("Hello World"));
        System.out.println("Q62 - Words starting with vowel: " + wordsStartingWithVowel("apple banana orange umbrella"));
        System.out.println("Q63 - Most common word: " + mostCommonWord("the cat and the dog and the bird").orElse(""));
        System.out.println("Q64 - Palindrome 'racecar': " + isPalindrome("racecar") + ", 'hello': " + isPalindrome("hello"));
        System.out.println("Q65 - Longest word: " + longestWord("Java streams are powerful and expressive").orElse(""));
        System.out.println("Q66 - Remove duplicates 'banana': " + removeDuplicateChars("banana"));
        System.out.println("Q67 - Word frequency: " + wordFrequency("hello world hello java world"));
        System.out.println("Q68 - Sort chars 'dcba': " + sortCharacters("dcba"));
        System.out.println("Q69 - Group anagrams: " + groupAnagrams(Arrays.asList("eat", "tea", "tan", "ate", "nat", "bat")));
        System.out.println("Q70 - Are anagrams 'listen'/'silent': " + areAnagrams("listen", "silent"));

        System.out.println("\n================================================");
        System.out.println("9. NUMERIC OPERATIONS WITH STREAMS");
        System.out.println("================================================\n");

        // Q71-Q80
        System.out.println("Q71 - Fibonacci (10): " + fibonacci(10));
        System.out.println("Q72 - Is 17 prime: " + isPrime(17) + ", 15: " + isPrime(15));
        System.out.println("Q73 - Primes 10-50: " + findPrimes(10, 50));
        System.out.println("Q74 - Factorial 5: " + factorial(5));
        System.out.println("Q75 - GCD(48, 18): " + gcd(48, 18));
        System.out.println("Q76 - LCM(12, 18): " + lcm(12, 18));
        System.out.println("Q77 - Random numbers (5, 1-100): " + generateRandomNumbers(5, 1, 100));
        System.out.println("Q78 - Median: " + findMedian(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6)));
        System.out.println("Q79 - Mode: " + findMode(Arrays.asList(1, 2, 2, 3, 3, 3, 4)));
        System.out.println("Q80 - 3rd smallest: " + kthSmallest(Arrays.asList(5, 2, 8, 1, 9, 3), 3).orElse(-1));

        System.out.println("\n================================================");
        System.out.println("10. COLLECTION OPERATIONS");
        System.out.println("================================================\n");

        // Q81-Q85
        System.out.println("Q81 - List to Map: " + listToMap(employees));
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("a", 1); map1.put("b", 2);
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("b", 3); map2.put("c", 4);
        System.out.println("Q82 - Merge maps: " + mergeMaps(map1, map2));
        Map<String, Integer> unsorted = new HashMap<>();
        unsorted.put("x", 30); unsorted.put("y", 10); unsorted.put("z", 20);
        System.out.println("Q83 - Sort by values: " + sortByValues(unsorted));
        Set<Integer> setA = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        Set<Integer> setB = new HashSet<>(Arrays.asList(3, 4, 5, 6));
        System.out.println("Q84 - Intersection: " + intersection(setA, setB));
        System.out.println("Q85 - Union: " + union(setA, setB));

        System.out.println("\n================================================");
        System.out.println("11. REDUCTION OPERATIONS");
        System.out.println("================================================\n");

        // Q86-Q90
        System.out.println("Q86 - Product: " + product(Arrays.asList(1, 2, 3, 4, 5)).orElse(0));
        System.out.println("Q87 - Longest string: " + longestString(words).orElse(""));
        System.out.println("Q88 - Combine with comma: '" + combineWithComma(words) + "'");
        System.out.println("Q89 - Total length: " + totalLength(words));
        IntSummaryStatistics stats = getStatistics(numbers);
        System.out.println("Q90 - Stats: min=" + stats.getMin() + ", max=" + stats.getMax()
                + ", avg=" + stats.getAverage() + ", sum=" + stats.getSum());

        System.out.println("\n================================================");
        System.out.println("12. METHOD REFERENCE QUESTIONS");
        System.out.println("================================================\n");

        // Q91-Q95
        System.out.println("Q91 - Parse strings to ints: " + parseStringsToInts(Arrays.asList("1", "2", "3")));
        System.out.println("Q92 - Filter empty: " + filterEmptyStrings(Arrays.asList("a", "", "b", "", "c")));
        System.out.println("Q93 - Create string list: " + createStringList(Arrays.asList(1, 2, 3)));
        System.out.println("Q94 - To array: " + Arrays.toString(toArray(words)));
        System.out.println("Q95 - To lowercase: " + toLowercase(Arrays.asList("JAVA", "STREAMS", "API")));

        System.out.println("\n================================================");
        System.out.println("13. INFINITE STREAMS");
        System.out.println("================================================\n");

        // Q96-Q100
        System.out.println("Q96 - Even numbers (10): " + generateEvenNumbers(10));
        System.out.println("Q97 - UUIDs (3): " + generateUUIDs(3));
        System.out.println("Q98 - Powers of 2 (10): " + powersOfTwo(10));
        System.out.println("Q99 - Primes (10): " + generatePrimes(10));
        System.out.println("Q100 - Collatz (6): " + collatzSequence(6));

        System.out.println("\n================================================");
        System.out.println("DEMONSTRATION COMPLETE");
        System.out.println("================================================");
    }
}