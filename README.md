# Array Problems Package

A comprehensive Java package containing optimal solutions to 25 essential array problems commonly asked in coding interviews and competitive programming.

## Overview

This package provides clean, well-documented implementations of 25 fundamental array problems. Each solution includes:
- Problem statement
- Optimal solution with time and space complexity analysis
- Algorithm explanation
- Dry run example
- Complete Java implementation

## Problems Included

1. **Two Sum** (00:11:20) - Find two numbers that add up to target
2. **Best Time to Buy and Sell Stock** (00:21:46) - Maximize profit from stock prices
3. **Contains Duplicate** (00:31:13) - Check if array contains duplicates
4. **Contains Duplicate II** (00:37:06) - Check for duplicates within k distance
5. **Product of Array Except Self** (00:49:57) - Product without division
6. **Maximum Subarray** (01:06:41) - Kadane's algorithm
7. **Maximum Product Subarray** (01:15:54) - Maximum product subarray
8. **Find Minimum in Rotated Sorted Array** (01:28:36) - Binary search in rotated array
9. **Search in Rotated Sorted Array** (01:41:18) - Search target in rotated array
10. **Two Sum II** (01:54:52) - Two sum with sorted input
11. **3 Sum** (02:07:25) - Find triplets that sum to zero
12. **Merge Sorted Array** (02:27:32) - Merge two sorted arrays
13. **Container With Most Water** (02:37:10) - Maximum water container
14. **Verifying an Alien Dictionary** (02:51:42) - Check alien language ordering
15. **Next Permutation** (03:10:26) - Lexicographically next permutation
16. **Remove Duplicates from Sorted Array** (03:28:09) - In-place duplicate removal
17. **Find First and Last Position of Element in Sorted Array** (03:35:46) - Binary search range
18. **Trapping Rain Water** (03:56:40) - Calculate trapped rainwater
19. **Median of Two Sorted Arrays** (04:23:34) - Find median of two arrays
20. **Valid Anagram** (04:50:36) - Check if strings are anagrams
21. **Top K Frequent Elements** (05:01:42) - Most frequent elements
22. **Group Anagrams** (05:12:18) - Group anagram strings
23. **Valid Sudoku** (05:25:02) - Validate Sudoku board
24. **Encode and Decode Strings** (05:43:04) - String encoding/decoding
25. **Longest Consecutive Sequence** (05:51:27) - Longest consecutive sequence

## Project Structure

```
src/main/java/com/dsa/array/
├── ArrayProblems.java      # Main implementation of all 25 problems
src/test/java/com/dsa/array/
└── ArrayProblemsTest.java  # Comprehensive test suite
```

## Installation

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher

### Building the Project
```bash
mvn clean compile
```

### Running Tests
```bash
mvn test
```

## Usage Examples

### Basic Usage
```java
import com.dsa.array.ArrayProblems;

public class Main {
    public static void main(String[] args) {
        // Example 1: Two Sum
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = ArrayProblems.twoSum(nums, target);
        System.out.println("Two Sum Result: [" + result[0] + ", " + result[1] + "]");
        
        // Example 2: Maximum Subarray
        int[] nums2 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int maxSum = ArrayProblems.maxSubArray(nums2);
        System.out.println("Maximum Subarray Sum: " + maxSum);
        
        // Example 3: Valid Anagram
        boolean isAnagram = ArrayProblems.isAnagram("anagram", "nagaram");
        System.out.println("Is Anagram: " + isAnagram);
    }
}
```

### Running the Built-in Main Method
The `ArrayProblems` class includes a main method that demonstrates several key solutions:
```bash
mvn compile exec:java -Dexec.mainClass="com.dsa.array.ArrayProblems"
```

## Algorithm Highlights

### Optimal Solutions Implemented
- **Two Sum**: O(n) time using HashMap
- **Maximum Subarray**: O(n) time using Kadane's algorithm
- **Product Except Self**: O(n) time without division
- **3 Sum**: O(n²) time using sorting + two pointers
- **Median of Two Sorted Arrays**: O(log(min(m,n))) time using binary search
- **Longest Consecutive Sequence**: O(n) time using HashSet
- **Top K Frequent Elements**: O(n) time using bucket sort
- **Trapping Rain Water**: O(n) time using two pointers

### Key Design Patterns
1. **Two Pointers Technique**: Used in problems like Two Sum II, Container With Most Water, Trapping Rain Water
2. **Sliding Window**: Used in Contains Duplicate II
3. **Binary Search**: Used in rotated array problems and search range
4. **Dynamic Programming**: Used in Maximum Subarray and Maximum Product Subarray
5. **Hash-based Solutions**: Used for frequency counting and lookups

## Testing

The package includes comprehensive unit tests for all 25 problems. Run the tests with:
```bash
mvn test
```

Test coverage includes:
- Edge cases
- Boundary conditions
- Performance validation
- Correctness verification

## Time and Space Complexity

Each method is documented with its time and space complexity:
- **Time Complexity**: Ranges from O(n) to O(n²) depending on the problem
- **Space Complexity**: Ranges from O(1) to O(n) depending on the algorithm

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is intended for educational purposes and interview preparation.

## Acknowledgments

- Problems based on LeetCode's top interview questions
- Solutions optimized for clarity and performance
- Dry run examples included for better understanding

## Contact

For questions or feedback, please refer to the project documentation.# dsalldjava8
