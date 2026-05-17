package com.dsa.interview;

import java.sql.*;
import java.util.*;
import java.util.stream.*;

/**
 * SQL Database Interview Questions - Code Examples & Concepts
 * 
 * Covers: SQL Basics, Joins, Aggregation, Subqueries, Window Functions,
 * CTEs, Database Design, Indexing, Query Optimization, Transactions,
 * Isolation Levels, Stored Procedures, Triggers, Views, Normalization,
 * NoSQL vs SQL, Common Interview Problems
 */
public class SQLInterviewQuestions {

    // =============================================
    // 1. SQL BASICS - SELECT, WHERE, ORDER BY
    // =============================================

    /**
     * Q1: Basic SELECT query structure
     * 
     * SELECT column1, column2, ...
     * FROM table_name
     * WHERE condition
     * GROUP BY column
     * HAVING condition
     * ORDER BY column ASC|DESC
     * LIMIT n;
     * 
     * Execution Order:
     * FROM -> WHERE -> GROUP BY -> HAVING -> SELECT -> ORDER BY -> LIMIT
     */
    public static void selectBasics() {
        String sql = """
                SELECT e.name, e.salary, d.department_name
                FROM employees e
                JOIN departments d ON e.dept_id = d.id
                WHERE e.salary > 50000
                  AND e.status = 'ACTIVE'
                ORDER BY e.salary DESC
                LIMIT 10;
                """;
        System.out.println("Q1 - Basic SELECT:\n" + sql);
    }

    /**
     * Q2: WHERE clause operators
     * 
     * =, !=, <>, >, <, >=, <=
     * BETWEEN, IN, LIKE, IS NULL, IS NOT NULL
     * AND, OR, NOT
     * 
     * LIKE patterns:
     * '%' - matches any sequence of characters
     * '_' - matches any single character
     */
    public static void whereClause() {
        String sql = """
                -- Find employees whose name starts with 'J'
                SELECT * FROM employees WHERE name LIKE 'J%';
                
                -- Find employees with salary between 50k and 100k
                SELECT * FROM employees WHERE salary BETWEEN 50000 AND 100000;
                
                -- Find employees in specific departments
                SELECT * FROM employees WHERE dept_id IN (1, 3, 5);
                
                -- Find employees with missing email
                SELECT * FROM employees WHERE email IS NULL;
                
                -- Find employees whose name has exactly 5 characters
                SELECT * FROM employees WHERE name LIKE '_____';
                """;
        System.out.println("Q2 - WHERE Clause:\n" + sql);
    }

    /**
     * Q3: DISTINCT vs GROUP BY
     * 
     * DISTINCT - removes duplicate rows (simpler, less flexible)
     * GROUP BY - groups rows for aggregation (more powerful)
     */
    public static void distinctVsGroupBy() {
        String sql = """
                -- DISTINCT: Get unique department IDs
                SELECT DISTINCT dept_id FROM employees;
                
                -- GROUP BY: Count employees per department
                SELECT dept_id, COUNT(*) as emp_count
                FROM employees
                GROUP BY dept_id;
                
                -- GROUP BY with HAVING (filter after aggregation)
                SELECT dept_id, COUNT(*) as emp_count
                FROM employees
                GROUP BY dept_id
                HAVING COUNT(*) > 5;
                """;
        System.out.println("Q3 - DISTINCT vs GROUP BY:\n" + sql);
    }

    // =============================================
    // 2. JOINS
    // =============================================

    /**
     * Q4: Types of JOINs
     * 
     * INNER JOIN - Returns rows that match in BOTH tables
     * LEFT JOIN - Returns ALL rows from left table + matching from right
     * RIGHT JOIN - Returns ALL rows from right table + matching from left
     * FULL OUTER JOIN - Returns ALL rows when there's a match in either table
     * CROSS JOIN - Cartesian product of both tables
     * SELF JOIN - Table joined with itself
     */
    public static void joinTypes() {
        String sql = """
                -- Sample tables:
                -- employees: id, name, dept_id
                -- departments: id, name, location
                
                -- INNER JOIN - Only employees with departments
                SELECT e.name, d.name as department
                FROM employees e
                INNER JOIN departments d ON e.dept_id = d.id;
                
                -- LEFT JOIN - All employees, even without departments
                SELECT e.name, d.name as department
                FROM employees e
                LEFT JOIN departments d ON e.dept_id = d.id;
                
                -- RIGHT JOIN - All departments, even without employees
                SELECT e.name, d.name as department
                FROM employees e
                RIGHT JOIN departments d ON e.dept_id = d.id;
                
                -- FULL OUTER JOIN - All employees and all departments
                SELECT e.name, d.name as department
                FROM employees e
                FULL OUTER JOIN departments d ON e.dept_id = d.id;
                
                -- SELF JOIN - Find employee-manager relationships
                SELECT e1.name as employee, e2.name as manager
                FROM employees e1
                LEFT JOIN employees e2 ON e1.manager_id = e2.id;
                
                -- CROSS JOIN - All possible combinations
                SELECT e.name, p.project_name
                FROM employees e
                CROSS JOIN projects p;
                """;
        System.out.println("Q4 - JOIN Types:\n" + sql);
    }

    /**
     * Q5: JOIN vs Subquery - When to use which?
     * 
     * JOIN is generally faster for correlated data
     * Subqueries are better for aggregation comparisons
     * EXISTS vs IN: EXISTS is faster for large result sets
     */
    public static void joinVsSubquery() {
        String sql = """
                -- Using JOIN (more efficient for related data)
                SELECT e.name, d.name as department
                FROM employees e
                JOIN departments d ON e.dept_id = d.id;
                
                -- Using Subquery (equivalent)
                SELECT e.name,
                       (SELECT d.name FROM departments d WHERE d.id = e.dept_id) as department
                FROM employees e;
                
                -- EXISTS vs IN
                -- Find departments with at least one employee
                SELECT d.name FROM departments d
                WHERE EXISTS (SELECT 1 FROM employees e WHERE e.dept_id = d.id);
                -- vs
                SELECT d.name FROM departments d
                WHERE d.id IN (SELECT DISTINCT dept_id FROM employees);
                """;
        System.out.println("Q5 - JOIN vs Subquery:\n" + sql);
    }

    // =============================================
    // 3. AGGREGATION & GROUP BY
    // =============================================

    /**
     * Q6: Aggregate Functions
     * 
     * COUNT() - Counts rows
     * SUM() - Sum of values
     * AVG() - Average of values
     * MIN() - Minimum value
     * MAX() - Maximum value
     * 
     * COUNT(*) vs COUNT(column):
     * COUNT(*) counts all rows including NULLs
     * COUNT(column) counts non-NULL values only
     */
    public static void aggregateFunctions() {
        String sql = """
                SELECT
                    COUNT(*) as total_employees,
                    COUNT(email) as employees_with_email,
                    COUNT(DISTINCT dept_id) as unique_departments,
                    AVG(salary) as avg_salary,
                    SUM(salary) as total_salary,
                    MIN(salary) as min_salary,
                    MAX(salary) as max_salary,
                    MAX(salary) - MIN(salary) as salary_range
                FROM employees;
                """;
        System.out.println("Q6 - Aggregate Functions:\n" + sql);
    }

    /**
     * Q7: GROUP BY with multiple columns
     * 
     * Groups by the combination of all specified columns
     * ORDER of columns in GROUP BY affects how data is grouped
     */
    public static void groupByMultiple() {
        String sql = """
                -- Group by department AND location
                SELECT 
                    d.name as department,
                    e.city,
                    COUNT(*) as emp_count,
                    AVG(e.salary) as avg_salary
                FROM employees e
                JOIN departments d ON e.dept_id = d.id
                GROUP BY d.name, e.city
                ORDER BY d.name, e.city;
                
                -- ROLLUP - Adds subtotal rows
                SELECT 
                    d.name as department,
                    e.city,
                    COUNT(*) as emp_count
                FROM employees e
                JOIN departments d ON e.dept_id = d.id
                GROUP BY ROLLUP(d.name, e.city);
                
                -- CUBE - All possible combinations of subtotals
                SELECT 
                    d.name as department,
                    e.city,
                    COUNT(*) as emp_count
                FROM employees e
                JOIN departments d ON e.dept_id = d.id
                GROUP BY CUBE(d.name, e.city);
                """;
        System.out.println("Q7 - GROUP BY Multiple Columns:\n" + sql);
    }

    /**
     * Q8: HAVING vs WHERE
     * 
     * WHERE filters rows BEFORE aggregation
     * HAVING filters groups AFTER aggregation
     * WHERE cannot use aggregate functions
     * HAVING can use aggregate functions
     */
    public static void havingVsWhere() {
        String sql = """
                -- WHERE filters individual rows before grouping
                SELECT dept_id, COUNT(*) as emp_count
                FROM employees
                WHERE salary > 30000
                GROUP BY dept_id;
                
                -- HAVING filters groups after aggregation
                SELECT dept_id, COUNT(*) as emp_count, AVG(salary) as avg_salary
                FROM employees
                GROUP BY dept_id
                HAVING COUNT(*) > 5
                   AND AVG(salary) > 60000;
                
                -- Both WHERE and HAVING together
                SELECT dept_id, COUNT(*) as emp_count
                FROM employees
                WHERE status = 'ACTIVE'
                GROUP BY dept_id
                HAVING COUNT(*) >= 3;
                """;
        System.out.println("Q8 - HAVING vs WHERE:\n" + sql);
    }

    // =============================================
    // 4. SUBQUERIES
    // =============================================

    /**
     * Q9: Types of Subqueries
     * 
     * 1. Scalar Subquery - Returns single value (used in SELECT, WHERE)
     * 2. Row Subquery - Returns single row (used in WHERE)
     * 3. Table Subquery - Returns result set (used in FROM, JOIN)
     * 4. Correlated Subquery - References outer query (executed per row)
     * 
     * Performance: Correlated subqueries can be slow for large datasets
     */
    public static void subqueryTypes() {
        String sql = """
                -- Scalar Subquery (in SELECT)
                SELECT 
                    name,
                    salary,
                    (SELECT AVG(salary) FROM employees) as company_avg,
                    salary - (SELECT AVG(salary) FROM employees) as diff_from_avg
                FROM employees;
                
                -- Scalar Subquery (in WHERE)
                SELECT name, salary
                FROM employees
                WHERE salary > (SELECT AVG(salary) FROM employees);
                
                -- Table Subquery (in FROM)
                SELECT dept_name, avg_salary
                FROM (
                    SELECT d.name as dept_name, AVG(e.salary) as avg_salary
                    FROM employees e
                    JOIN departments d ON e.dept_id = d.id
                    GROUP BY d.name
                ) dept_stats
                WHERE avg_salary > 60000;
                
                -- Correlated Subquery
                SELECT e1.name, e1.salary, e1.dept_id
                FROM employees e1
                WHERE e1.salary > (
                    SELECT AVG(e2.salary)
                    FROM employees e2
                    WHERE e2.dept_id = e1.dept_id
                );
                """;
        System.out.println("Q9 - Subquery Types:\n" + sql);
    }

    /**
     * Q10: EXISTS vs IN vs ANY vs ALL
     * 
     * EXISTS - Returns true if subquery returns any rows
     * IN - Checks if value matches any in list
     * ANY - Compares value to any value in subquery
     * ALL - Compares value to all values in subquery
     */
    public static void existsInAnyAll() {
        String sql = """
                -- EXISTS - Find departments with employees
                SELECT d.name FROM departments d
                WHERE EXISTS (SELECT 1 FROM employees e WHERE e.dept_id = d.id);
                
                -- NOT EXISTS - Find departments with NO employees
                SELECT d.name FROM departments d
                WHERE NOT EXISTS (SELECT 1 FROM employees e WHERE e.dept_id = d.id);
                
                -- IN - Find employees in specific departments
                SELECT name FROM employees
                WHERE dept_id IN (SELECT id FROM departments WHERE location = 'New York');
                
                -- ANY - Find employees earning more than ANY employee in dept 1
                SELECT name, salary FROM employees
                WHERE salary > ANY (SELECT salary FROM employees WHERE dept_id = 1);
                
                -- ALL - Find employees earning more than ALL employees in dept 1
                SELECT name, salary FROM employees
                WHERE salary > ALL (SELECT salary FROM employees WHERE dept_id = 1);
                """;
        System.out.println("Q10 - EXISTS vs IN vs ANY vs ALL:\n" + sql);
    }

    // =============================================
    // 5. WINDOW FUNCTIONS
    // =============================================

    /**
     * Q11: Window Functions Overview
     * 
     * Window functions perform calculations across a set of rows related to
     * the current row, WITHOUT collapsing rows like GROUP BY.
     * 
     * Syntax: function() OVER (PARTITION BY col ORDER BY col frame_clause)
     * 
     * Types:
     * 1. Ranking: ROW_NUMBER(), RANK(), DENSE_RANK(), NTILE()
     * 2. Aggregate: SUM(), AVG(), COUNT(), MIN(), MAX() as window functions
     * 3. Value: LAG(), LEAD(), FIRST_VALUE(), LAST_VALUE(), NTH_VALUE()
     * 
     * Frame clause: ROWS/RANGE BETWEEN ... AND ...
     * - UNBOUNDED PRECEDING, n PRECEDING, CURRENT ROW, n FOLLOWING, UNBOUNDED FOLLOWING
     */
    public static void windowFunctions() {
        String sql = """
                -- ROW_NUMBER - Unique sequential number per partition
                SELECT 
                    name, department, salary,
                    ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) as rank
                FROM employees;
                
                -- RANK vs DENSE_RANK vs ROW_NUMBER
                SELECT 
                    name, salary,
                    ROW_NUMBER() OVER (ORDER BY salary DESC) as row_num,
                    RANK() OVER (ORDER BY salary DESC) as rank,
                    DENSE_RANK() OVER (ORDER BY salary DESC) as dense_rank
                FROM employees;
                
                -- NTILE - Divide rows into buckets
                SELECT 
                    name, salary,
                    NTILE(4) OVER (ORDER BY salary DESC) as quartile
                FROM employees;
                
                -- LAG and LEAD - Access previous/next rows
                SELECT 
                    name, salary,
                    LAG(salary, 1, 0) OVER (ORDER BY salary) as prev_salary,
                    LEAD(salary, 1, 0) OVER (ORDER BY salary) as next_salary
                FROM employees;
                
                -- Running total with window frame
                SELECT 
                    date, amount,
                    SUM(amount) OVER (ORDER BY date 
                        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as running_total
                FROM sales;
                
                -- Moving average (last 3 months)
                SELECT 
                    date, amount,
                    AVG(amount) OVER (ORDER BY date 
                        ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) as moving_avg_3
                FROM sales;
                """;
        System.out.println("Q11 - Window Functions:\n" + sql);
    }

    /**
     * Q12: Common Window Function Interview Problems
     */
    public static void windowFunctionProblems() {
        String sql = """
                -- Problem 1: Find nth highest salary per department
                WITH ranked AS (
                    SELECT 
                        name, department, salary,
                        DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC) as rnk
                    FROM employees
                )
                SELECT name, department, salary
                FROM ranked
                WHERE rnk = 2;
                
                -- Problem 2: Find employees earning more than their department average
                SELECT name, department, salary
                FROM (
                    SELECT 
                        name, department, salary,
                        AVG(salary) OVER (PARTITION BY department) as dept_avg
                    FROM employees
                ) t
                WHERE salary > dept_avg;
                
                -- Problem 3: Find salary difference from previous employee
                SELECT 
                    name, salary,
                    salary - LAG(salary, 1, 0) OVER (ORDER BY salary) as diff_from_prev
                FROM employees;
                
                -- Problem 4: First and last value in each group
                SELECT DISTINCT
                    department,
                    FIRST_VALUE(name) OVER (PARTITION BY department ORDER BY salary DESC) as highest_paid,
                    LAST_VALUE(name) OVER (PARTITION BY department ORDER BY salary DESC
                        RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) as lowest_paid
                FROM employees;
                """;
        System.out.println("Q12 - Window Function Problems:\n" + sql);
    }

    // =============================================
    // 6. CTEs (Common Table Expressions)
    // =============================================

    /**
     * Q13: CTE Basics
     * 
     * CTE (WITH clause) creates a temporary named result set that exists
     * only within the scope of a single SQL statement.
     * 
     * Benefits:
     * - Improves readability
     * - Enables recursion
     * - Can be referenced multiple times in the same query
     * - Better than subqueries for complex queries
     */
    public static void cteBasics() {
        String sql = """
                -- Simple CTE
                WITH dept_stats AS (
                    SELECT 
                        d.name as dept_name,
                        COUNT(*) as emp_count,
                        AVG(e.salary) as avg_salary
                    FROM employees e
                    JOIN departments d ON e.dept_id = d.id
                    GROUP BY d.name
                )
                SELECT * FROM dept_stats WHERE emp_count > 5;
                
                -- Multiple CTEs
                WITH 
                dept_stats AS (
                    SELECT dept_id, COUNT(*) as emp_count, AVG(salary) as avg_salary
                    FROM employees GROUP BY dept_id
                ),
                high_performers AS (
                    SELECT name, salary, dept_id
                    FROM employees
                    WHERE salary > (SELECT AVG(salary) * 1.5 FROM employees)
                )
                SELECT d.name, ds.emp_count, ds.avg_salary, hp.name as top_earner
                FROM departments d
                JOIN dept_stats ds ON d.id = ds.dept_id
                LEFT JOIN high_performers hp ON d.id = hp.dept_id;
                """;
        System.out.println("Q13 - CTE Basics:\n" + sql);
    }

    /**
     * Q14: Recursive CTE
     * 
     * Used for hierarchical/tree data (org charts, category trees, etc.)
     * Must have:
     * 1. Anchor member (initial query)
     * 2. UNION ALL
     * 3. Recursive member (references CTE itself)
     */
    public static void recursiveCTE() {
        String sql = """
                -- Employee hierarchy (org chart)
                WITH RECURSIVE org_chart AS (
                    -- Anchor: CEO (top-level manager)
                    SELECT id, name, manager_id, 0 as level, CAST(name AS VARCHAR(1000)) as path
                    FROM employees
                    WHERE manager_id IS NULL
                    
                    UNION ALL
                    
                    -- Recursive: direct reports
                    SELECT e.id, e.name, e.manager_id, oc.level + 1,
                           CAST(oc.path || ' -> ' || e.name AS VARCHAR(1000))
                    FROM employees e
                    JOIN org_chart oc ON e.manager_id = oc.id
                )
                SELECT * FROM org_chart ORDER BY path;
                
                -- Category tree with product count
                WITH RECURSIVE category_tree AS (
                    SELECT id, name, parent_id, 0 as level
                    FROM categories WHERE parent_id IS NULL
                    
                    UNION ALL
                    
                    SELECT c.id, c.name, c.parent_id, ct.level + 1
                    FROM categories c
                    JOIN category_tree ct ON c.parent_id = ct.id
                )
                SELECT 
                    ct.name,
                    ct.level,
                    COUNT(p.id) as product_count
                FROM category_tree ct
                LEFT JOIN products p ON p.category_id = ct.id
                GROUP BY ct.name, ct.level
                ORDER BY ct.level, ct.name;
                
                -- Generate number series (1 to 10)
                WITH RECURSIVE numbers(n) AS (
                    SELECT 1
                    UNION ALL
                    SELECT n + 1 FROM numbers WHERE n < 10
                )
                SELECT * FROM numbers;
                
                -- Fibonacci sequence
                WITH RECURSIVE fibonacci(a, b, n) AS (
                    SELECT 0, 1, 1
                    UNION ALL
                    SELECT b, a + b, n + 1
                    FROM fibonacci
                    WHERE n < 10
                )
                SELECT a as fibonacci_number FROM fibonacci;
                """;
        System.out.println("Q14 - Recursive CTE:\n" + sql);
    }

    // =============================================
    // 7. DATABASE DESIGN & NORMALIZATION
    // =============================================

    /**
     * Q15: Normalization Forms
     * 
     * 1NF (First Normal Form):
     * - Each column contains atomic (indivisible) values
     * - Each column contains values of a single type
     * - Each row is unique (has a primary key)
     * 
     * 2NF (Second Normal Form):
     * - Must be in 1NF
     * - All non-key columns must depend on the ENTIRE primary key
     *   (No partial dependencies)
     * 
     * 3NF (Third Normal Form):
     * - Must be in 2NF
     * - No transitive dependencies (non-key column depends on another non-key)
     * 
     * BCNF (Boyce-Codd Normal Form):
     * - Must be in 3NF
     * - Every determinant must be a candidate key
     * 
     * Denormalization: Intentionally adding redundancy for performance
     */
    public static void normalization() {
        String sql = """
                -- UNNORMALIZED (Bad Design)
                CREATE TABLE orders_unnormalized (
                    order_id INT PRIMARY KEY,
                    customer_name VARCHAR(100),
                    customer_email VARCHAR(100),
                    customer_phone VARCHAR(20),
                    products VARCHAR(500),
                    quantities VARCHAR(100)
                );
                
                -- 1NF: Atomic values
                CREATE TABLE orders_1nf (
                    order_id INT,
                    customer_name VARCHAR(100),
                    customer_email VARCHAR(100),
                    customer_phone VARCHAR(20),
                    product_name VARCHAR(100),
                    quantity INT,
                    PRIMARY KEY (order_id, product_name)
                );
                
                -- 2NF: Remove partial dependencies
                CREATE TABLE orders (
                    order_id INT PRIMARY KEY,
                    customer_name VARCHAR(100),
                    customer_email VARCHAR(100),
                    customer_phone VARCHAR(20)
                );
                CREATE TABLE order_items (
                    order_id INT,
                    product_name VARCHAR(100),
                    quantity INT,
                    PRIMARY KEY (order_id, product_name),
                    FOREIGN KEY (order_id) REFERENCES orders(order_id)
                );
                
                -- 3NF: Remove transitive dependencies
                CREATE TABLE customers (
                    customer_id INT PRIMARY KEY,
                    name VARCHAR(100),
                    email VARCHAR(100),
                    phone VARCHAR(20)
                );
                CREATE TABLE orders_3nf (
                    order_id INT PRIMARY KEY,
                    customer_id INT,
                    order_date DATE,
                    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                );
                CREATE TABLE order_items_3nf (
                    order_id INT,
                    product_id INT,
                    quantity INT,
                    price DECIMAL(10,2),
                    PRIMARY KEY (order_id, product_id),
                    FOREIGN KEY (order_id) REFERENCES orders_3nf(order_id),
                    FOREIGN KEY (product_id) REFERENCES products(product_id)
                );
                """;
        System.out.println("Q15 - Normalization:\n" + sql);
    }

    /**
     * Q16: Entity-Relationship (ER) Diagram Concepts
     * 
     * Cardinality:
     * 1:1 - One-to-One (Person <-> Passport)
     * 1:N - One-to-Many (Department -> Employees)
     * M:N - Many-to-Many (Students <-> Courses) - needs junction table
     * 
     * Relationships:
     * - Identifying: Child's PK includes parent's PK (weak entity)
     * - Non-identifying: Child has its own PK
     */
    public static void erDiagram() {
        String sql = """
                -- One-to-One: Person <-> Passport
                CREATE TABLE persons (
                    person_id INT PRIMARY KEY,
                    name VARCHAR(100)
                );
                CREATE TABLE passports (
                    passport_id INT PRIMARY KEY,
                    person_id INT UNIQUE,
                    passport_number VARCHAR(20),
                    FOREIGN KEY (person_id) REFERENCES persons(person_id)
                );
                
                -- One-to-Many: Department -> Employees
                CREATE TABLE departments (
                    dept_id INT PRIMARY KEY,
                    name VARCHAR(100)
                );
                CREATE TABLE employees (
                    emp_id INT PRIMARY KEY,
                    name VARCHAR(100),
                    dept_id INT,
                    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
                );
                
                -- Many-to-Many: Students <-> Courses (with junction table)
                CREATE TABLE students (
                    student_id INT PRIMARY KEY,
                    name VARCHAR(100)
                );
                CREATE TABLE courses (
                    course_id INT PRIMARY KEY,
                    title VARCHAR(100)
                );
                CREATE TABLE enrollments (
                    student_id INT,
                    course_id INT,
                    enrollment_date DATE,
                    grade CHAR(1),
                    PRIMARY KEY (student_id, course_id),
                    FOREIGN KEY (student_id) REFERENCES students(student_id),
                    FOREIGN KEY (course_id) REFERENCES courses(course_id)
                );
                """;
        System.out.println("Q16 - ER Diagram Concepts:\n" + sql);
    }

    // =============================================
    // 8. INDEXING
    // =============================================

    /**
     * Q17: Index Types and Strategies
     * 
     * B-Tree Index (default):
     * - Balanced tree structure
     * - Good for equality and range queries
     * - Supports ORDER BY, GROUP BY
     * 
     * Hash Index:
     * - Only equality lookups (=)
     * - No range queries, no ORDER BY
     * - Very fast for exact matches
     * 
     * Bitmap Index:
     * - Good for low-cardinality columns (gender, status)
     * - Used in data warehouses
     * 
     * Full-Text Index:
     * - For text search (MATCH ... AGAINST)
     * - Supports stemming, stop words
     * 
     * Composite Index:
     * - Multiple columns
     * - Column order matters (leftmost prefix rule)
     * 
     * Clustered vs Non-Clustered:
     * - Clustered: Data physically ordered by index (1 per table)
     * - Non-Clustered: Separate structure with pointers to data
     */
    public static void indexing() {
        String sql = """
                -- Single column index
                CREATE INDEX idx_employees_dept_id ON employees(dept_id);
                
                -- Unique index (also enforces uniqueness)
                CREATE UNIQUE INDEX idx_employees_email ON employees(email);
                
                -- Composite index (column order matters!)
                CREATE INDEX idx_employees_dept_salary 
                    ON employees(dept_id, salary);
                -- This index supports:
                -- WHERE dept_id = 5
                -- WHERE dept_id = 5 AND salary > 50000
                -- But NOT: WHERE salary > 50000 (leftmost prefix rule)
                
                -- Partial index (index on subset of rows)
                CREATE INDEX idx_active_employees 
                    ON employees(dept_id) WHERE status = 'ACTIVE';
                
                -- Covering index (includes all needed columns)
                CREATE INDEX idx_employee_names 
                    ON employees(dept_id) INCLUDE (name, salary);
                
                -- Index on expression
                CREATE INDEX idx_employees_lower_name 
                    ON employees(LOWER(name));
                
                -- Drop index
                DROP INDEX idx_employees_dept_id;
                
                -- When to index:
                -- 1. Columns used in WHERE, JOIN, ORDER BY, GROUP BY
                -- 2. High-cardinality columns (more unique values)
                -- 3. Foreign key columns
                -- 
                -- When NOT to index:
                -- 1. Small tables (full scan is faster)
                -- 2. Columns rarely used in queries
                -- 3. Columns with frequent updates (index maintenance cost)
                -- 4. Low-cardinality columns (gender, boolean)
                """;
        System.out.println("Q17 - Indexing:\n" + sql);
    }

    /**
     * Q18: EXPLAIN PLAN - Query Analysis
     * 
     * EXPLAIN shows how the database executes a query:
     * - Table access method (full scan, index scan, index seek)
     * - Join type (nested loop, hash join, merge join)
     * - Estimated rows and cost
     * - Sort operations
     * 
     * Scan types (best to worst):
     * 1. Index Unique Scan (single row by unique index)
     * 2. Index Range Scan (range of index entries)
     * 3. Index Full Scan (full index, but smaller than table)
     * 4. Table Access by Index RowID (lookup after index scan)
     * 5. Full Table Scan (worst - reads entire table)
     */
    public static void explainPlan() {
        String sql = """
                -- Basic EXPLAIN
                EXPLAIN SELECT * FROM employees WHERE dept_id = 5;
                
                -- EXPLAIN with ANALYZE (actually executes)
                EXPLAIN ANALYZE 
                SELECT e.name, d.name as department
                FROM employees e
                JOIN departments d ON e.dept_id = d.id
                WHERE e.salary > 50000
                ORDER BY e.salary DESC;
                
                -- Output interpretation:
                -- Seq Scan on employees (cost=0.00..35.50 rows=10 width=68)
                --   Filter: (salary > 50000)
                --   -> Hash Join (cost=1.05..2.08 rows=10 width=136)
                --        Hash Cond: (e.dept_id = d.id)
                --        -> Seq Scan on departments (cost=0.00..1.01 rows=1 width=68)
                --
                -- cost: first row cost..total cost
                -- rows: estimated number of rows
                -- width: estimated average row width in bytes
                """;
        System.out.println("Q18 - EXPLAIN PLAN:\n" + sql);
    }

    // =============================================
    // 9. TRANSACTIONS & ISOLATION LEVELS
    // =============================================

    /**
     * Q19: ACID Properties
     * 
     * Atomicity - All or nothing (transaction completes fully or not at all)
     * Consistency - Data remains valid (constraints, triggers, rules)
     * Isolation - Concurrent transactions don't interfere
     * Durability - Committed data persists (survives crashes)
     */
    public static void acidProperties() {
        String sql = """
                -- Transaction example
                BEGIN TRANSACTION;
                
                -- Atomicity: Both updates succeed or both fail
                UPDATE accounts SET balance = balance - 1000 WHERE account_id = 1;
                UPDATE accounts SET balance = balance + 1000 WHERE account_id = 2;
                
                -- Consistency: Check constraint (balance >= 0) prevents invalid state
                
                -- Isolation: Other transactions see either old or new state, not intermediate
                
                -- Durability: Once committed, data is written to disk (WAL - Write Ahead Log)
                
                COMMIT;
                
                -- Savepoints (partial rollback)
                BEGIN TRANSACTION;
                INSERT INTO orders VALUES (1, 'Pending');
                SAVE TRANSACTION savepoint1;
                INSERT INTO order_items VALUES (1, 'Product A', 5);
                ROLLBACK TO SAVEPOINT savepoint1;
                INSERT INTO order_items VALUES (1, 'Product A', 5);
                COMMIT;
                """;
        System.out.println("Q19 - ACID Properties:\n" + sql);
    }

    /**
     * Q20: Isolation Levels and Phenomena
     * 
     * Phenomena:
     * 1. Dirty Read - Reading uncommitted data from another transaction
     * 2. Non-Repeatable Read - Same row read twice gives different values
     * 3. Phantom Read - Same query returns different rows (new rows inserted)
     * 
     * Isolation Levels (lowest to highest):
     * 
     * READ UNCOMMITTED:
     *   Dirty Read: Yes | Non-Repeatable: Yes | Phantom: Yes
     * 
     * READ COMMITTED (default in PostgreSQL, SQL Server, Oracle):
     *   Dirty Read: No  | Non-Repeatable: Yes | Phantom: Yes
     * 
     * REPEATABLE READ:
     *   Dirty Read: No  | Non-Repeatable: No  | Phantom: Yes (MySQL InnoDB: No)
     * 
     * SERIALIZABLE:
     *   Dirty Read: No  | Non-Repeatable: No  | Phantom: No
     * 
     * Lost Update: When two transactions read and write same row concurrently
     * - Prevented by: Pessimistic locking (SELECT ... FOR UPDATE)
     *   or Optimistic locking (version column)
     */
    public static void isolationLevels() {
        String sql = """
                -- Set isolation level
                SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
                SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;
                
                -- Dirty Read example (READ UNCOMMITTED)
                -- Transaction 1:
                BEGIN TRANSACTION;
                UPDATE accounts SET balance = 1000 WHERE id = 1;
                -- Not committed yet!
                
                -- Transaction 2 (READ UNCOMMITTED):
                SELECT balance FROM accounts WHERE id = 1;
                
                -- Transaction 1:
                ROLLBACK;
                -- Transaction 2 read invalid data (Dirty Read)!
                
                -- Non-Repeatable Read (READ COMMITTED)
                -- Transaction 1:
                BEGIN TRANSACTION;
                SELECT balance FROM accounts WHERE id = 1;
                
                -- Transaction 2:
                UPDATE accounts SET balance = 700 WHERE id = 1;
                COMMIT;
                
                -- Transaction 1:
                SELECT balance FROM accounts WHERE id = 1;
                -- Different value! (Non-Repeatable Read)
                
                -- Phantom Read (REPEATABLE READ)
                -- Transaction 1:
                BEGIN TRANSACTION;
                SELECT * FROM employees WHERE salary > 50000;
                
                -- Transaction 2:
                INSERT INTO employees VALUES (100, 'New', 60000);
                COMMIT;
                
                -- Transaction 1:
                SELECT * FROM employees WHERE salary > 50000;
                -- New row appears! (Phantom Read)
                
                -- Preventing Lost Update with SELECT ... FOR UPDATE
                BEGIN TRANSACTION;
                SELECT balance FROM accounts WHERE id = 1 FOR UPDATE;
                -- Lock acquired, other transactions wait
                UPDATE accounts SET balance = balance - 100 WHERE id = 1;
                COMMIT;
                -- Lock released
                """;
        System.out.println("Q20 - Isolation Levels:\n" + sql);
    }

    // =============================================
    // 10. STORED PROCEDURES, FUNCTIONS, TRIGGERS, VIEWS
    // =============================================

    /**
     * Q21: Stored Procedures vs Functions
     * 
     * Stored Procedure:
     * - Can have input/output parameters
     * - Can perform DML (INSERT, UPDATE, DELETE)
     * - Can call other procedures
     * - Cannot be used in SELECT statement
     * - Can have transaction control (COMMIT, ROLLBACK)
     * 
     * Function:
     * - Must return a value
     * - Can be used in SELECT statements
     * - Cannot have transaction control
     * - Cannot modify data (in most databases)
     * - Can have only input parameters
     */

    public static void storedProceduresAndFunctions() {
        String sql = """
                -- Stored Procedure: Get employees by department
                CREATE PROCEDURE GetEmployeesByDept
                    @dept_id INT
                AS
                BEGIN
                    SELECT name, salary, hire_date
                    FROM employees
                    WHERE dept_id = @dept_id
                    ORDER BY salary DESC;
                END;
                
                -- Execute stored procedure
                EXEC GetEmployeesByDept @dept_id = 5;
                
                -- Stored Procedure with output parameter
                CREATE PROCEDURE GetDeptSalaryStats
                    @dept_id INT,
                    @avg_salary DECIMAL(10,2) OUTPUT,
                    @emp_count INT OUTPUT
                AS
                BEGIN
                    SELECT 
                        @avg_salary = AVG(salary),
                        @emp_count = COUNT(*)
                    FROM employees
                    WHERE dept_id = @dept_id;
                END;
                
                -- Function: Calculate annual bonus
                CREATE FUNCTION CalculateBonus
                    (@salary DECIMAL(10,2), @years_of_service INT)
                RETURNS DECIMAL(10,2)
                AS
                BEGIN
                    RETURN @salary * 0.1 * 
                           CASE WHEN @years_of_service > 5 THEN 1.5 ELSE 1.0 END;
                END;
                
                -- Using function in SELECT
                SELECT 
                    name, salary,
                    dbo.CalculateBonus(salary, 7) as bonus
                FROM employees;
                
                -- Table-valued function
                CREATE FUNCTION GetHighEarners(@min_salary DECIMAL(10,2))
                RETURNS TABLE
                AS
                RETURN (
                    SELECT e.name, e.salary, d.name as department
                    FROM employees e
                    JOIN departments d ON e.dept_id = d.id
                    WHERE e.salary >= @min_salary
                );
                
                -- Using table-valued function
                SELECT * FROM GetHighEarners(80000);
                """;
        System.out.println("Q21 - Stored Procedures vs Functions:\n" + sql);
    }

    /**
     * Q22: Triggers
     * 
     * Triggers are automatically executed in response to DML events
     * 
     * Types:
     * - BEFORE / AFTER INSERT, UPDATE, DELETE
     * - INSTEAD OF (for views)
     * 
     * Use cases:
     * - Auditing changes
     * - Enforcing complex business rules
     * - Maintaining derived data
     * - Cascading operations
     * 
     * NEW vs OLD (pseudo-tables):
     * - INSERT: NEW contains new values
     * - DELETE: OLD contains deleted values
     * - UPDATE: OLD has old values, NEW has new values
     */
    public static void triggers() {
        String sql = """
                -- Audit trigger: Log salary changes
                CREATE TABLE salary_audit_log (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    employee_id INT,
                    old_salary DECIMAL(10,2),
                    new_salary DECIMAL(10,2),
                    changed_by VARCHAR(100),
                    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                
                CREATE TRIGGER trg_salary_audit
                AFTER UPDATE ON employees
                FOR EACH ROW
                BEGIN
                    IF OLD.salary != NEW.salary THEN
                        INSERT INTO salary_audit_log 
                            (employee_id, old_salary, new_salary, changed_by)
                        VALUES 
                            (NEW.id, OLD.salary, NEW.salary, CURRENT_USER);
                    END IF;
                END;
                
                -- Prevent deleting managers with direct reports
                CREATE TRIGGER trg_prevent_manager_delete
                BEFORE DELETE ON employees
                FOR EACH ROW
                BEGIN
                    IF EXISTS (SELECT 1 FROM employees WHERE manager_id = OLD.id) THEN
                        SIGNAL SQLSTATE '45000'
                            SET MESSAGE_TEXT = 'Cannot delete employee who has direct reports';
                    END IF;
                END;
                
                -- INSTEAD OF trigger on view
                CREATE VIEW employee_details AS
                SELECT e.id, e.name, e.salary, d.name as department
                FROM employees e JOIN departments d ON e.dept_id = d.id;
                
                CREATE TRIGGER trg_employee_details_insert
                INSTEAD OF INSERT ON employee_details
                FOR EACH ROW
                BEGIN
                    INSERT INTO employees (name, salary, dept_id)
                    VALUES (NEW.name, NEW.salary, 
                            (SELECT id FROM departments WHERE name = NEW.department));
                END;
                """;
        System.out.println("Q22 - Triggers:\n" + sql);
    }

    /**
     * Q23: Views
     * 
     * Views are virtual tables based on SELECT queries
     * 
     * Benefits:
     * - Security (hide sensitive columns)
     * - Simplicity (encapsulate complex joins)
     * - Consistency (standardized data access)
     * - Logical data independence
     * 
     * Types:
     * - Simple views (single table, no aggregation) - updatable
     * - Complex views (joins, aggregation) - read-only
     * - Materialized views (physically stored, refreshed periodically)
     */
    public static void views() {
        String sql = """
                -- Simple view: Hide salary information
                CREATE VIEW employee_public AS
                SELECT id, name, email, department, hire_date
                FROM employees;
                
                -- Complex view: Department statistics
                CREATE VIEW dept_statistics AS
                SELECT 
                    d.name as department,
                    COUNT(*) as emp_count,
                    AVG(e.salary) as avg_salary,
                    MAX(e.salary) as max_salary,
                    MIN(e.salary) as min_salary
                FROM employees e
                JOIN departments d ON e.dept_id = d.id
                GROUP BY d.name;
                
                -- Querying views
                SELECT * FROM employee_public;
                SELECT * FROM dept_statistics WHERE avg_salary > 60000;
                
                -- Materialized view (PostgreSQL)
                CREATE MATERIALIZED VIEW dept_stats_materialized AS
                SELECT d.name, COUNT(*) as emp_count, AVG(e.salary) as avg_salary
                FROM employees e
                JOIN departments d ON e.dept_id = d.id
                GROUP BY d.name
                WITH DATA;
                
                -- Refresh materialized view
                REFRESH MATERIALIZED VIEW dept_stats_materialized;
                
                -- Check if view is updatable
                INSERT INTO employee_public (id, name, email, department, hire_date)
                VALUES (100, 'John Doe', 'john@example.com', 'Engineering', '2024-01-15');
                """;
        System.out.println("Q23 - Views:\n" + sql);
    }

    // =============================================
    // 11. QUERY OPTIMIZATION
    // =============================================

    /**
     * Q24: Query Optimization Techniques
     * 
     * 1. Use EXPLAIN to analyze query plans
     * 2. Create appropriate indexes
     * 3. Avoid SELECT * (only fetch needed columns)
     * 4. Use JOIN instead of subqueries when possible
     * 5. Avoid functions in WHERE clauses (prevents index usage)
     * 6. Use UNION ALL instead of UNION when duplicates don't matter
     * 7. Use EXISTS instead of IN for large subquery results
     * 8. Avoid leading wildcard in LIKE (LIKE '%text' can't use index)
     * 9. Use appropriate data types
     * 10. Partition large tables
     */
    public static void queryOptimization() {
        String sql = """
                -- BAD: Function on column prevents index usage
                SELECT * FROM employees WHERE YEAR(hire_date) = 2023;
                
                -- GOOD: Range query can use index
                SELECT * FROM employees 
                WHERE hire_date >= '2023-01-01' AND hire_date < '2024-01-01';
                
                -- BAD: Leading wildcard prevents index usage
                SELECT * FROM employees WHERE name LIKE '%smith';
                
                -- GOOD: Trailing wildcard can use index
                SELECT * FROM employees WHERE name LIKE 'smith%';
                
                -- BAD: Implicit type conversion prevents index usage
                SELECT * FROM employees WHERE phone_number = 1234567890;
                
                -- GOOD: Use correct data type
                SELECT * FROM employees WHERE phone_number = '1234567890';
                
                -- BAD: SELECT * fetches unnecessary data
                SELECT * FROM employees WHERE dept_id = 5;
                
                -- GOOD: Only fetch needed columns
                SELECT id, name, salary FROM employees WHERE dept_id = 5;
                
                -- BAD: UNION removes duplicates (extra sort)
                SELECT name FROM current_employees
                UNION
                SELECT name FROM former_employees;
                
                -- GOOD: UNION ALL when duplicates don't matter
                SELECT name FROM current_employees
                UNION ALL
                SELECT name FROM former_employees;
                
                -- BAD: IN with subquery (materializes entire result)
                SELECT * FROM employees
                WHERE dept_id IN (SELECT id FROM departments WHERE location = 'NY');
                
                -- GOOD: EXISTS (can short-circuit)
                SELECT * FROM employees e
                WHERE EXISTS (SELECT 1 FROM departments d 
                              WHERE d.id = e.dept_id AND d.location = 'NY');
                
                -- BAD: Nested subqueries
                SELECT name FROM employees
                WHERE salary > (SELECT AVG(salary) FROM employees
                                WHERE dept_id = (SELECT id FROM departments WHERE name = 'Engineering'));
                
                -- GOOD: Use CTE for readability
                WITH eng_dept AS (
                    SELECT id FROM departments WHERE name = 'Engineering'
                ),
                eng_avg AS (
                    SELECT AVG(salary) as avg_sal FROM employees WHERE dept_id IN (SELECT id FROM eng_dept)
                )
                SELECT name FROM employees e, eng_avg
                WHERE e.dept_id IN (SELECT id FROM eng_dept) AND e.salary > eng_avg.avg_sal;
                """;
        System.out.println("Q24 - Query Optimization:\n" + sql);
    }

    /**
     * Q25: Common SQL Performance Anti-patterns
     */
    public static void performanceAntiPatterns() {
        String sql = """
                -- Anti-pattern 1: Correlated subquery instead of JOIN
                -- BAD (executes subquery for each row):
                SELECT e.name,
                       (SELECT d.name FROM departments d WHERE d.id = e.dept_id) as dept
                FROM employees e;
                
                -- GOOD (single scan):
                SELECT e.name, d.name as dept
                FROM employees e
                JOIN departments d ON e.dept_id = d.id;
                
                -- Anti-pattern 2: Using DISTINCT to hide bad JOINs
                -- BAD:
                SELECT DISTINCT e.name, e.salary
                FROM employees e
                JOIN orders o ON e.id = o.employee_id;
                
                -- GOOD: Understand why duplicates occur
                SELECT e.name, e.salary
                FROM employees e
                WHERE EXISTS (SELECT 1 FROM orders o WHERE o.employee_id = e.id);
                
                -- Anti-pattern 3: Not using pagination
                -- BAD (returns all rows):
                SELECT * FROM orders ORDER BY created_at;
                
                -- GOOD (paginate):
                SELECT * FROM orders ORDER BY created_at LIMIT 50 OFFSET 0;
                
                -- Anti-pattern 4: Using OR instead of UNION
                -- BAD (may not use indexes):
                SELECT * FROM employees WHERE name = 'John' OR email = 'john@example.com';
                
                -- GOOD (can use separate indexes):
                SELECT * FROM employees WHERE name = 'John'
                UNION
                SELECT * FROM employees WHERE email = 'john@example.com';
                
                -- Anti-pattern 5: Not using covering indexes
                -- Create covering index for this query:
                -- CREATE INDEX idx_covering ON employees(dept_id) INCLUDE (name, salary);
                SELECT name, salary FROM employees WHERE dept_id = 5;
                """;
        System.out.println("Q25 - Performance Anti-patterns:\n" + sql);
    }

    // =============================================
    // 12. LOCKING & CONCURRENCY
    // =============================================

    /**
     * Q26: Locking Mechanisms
     * 
     * Pessimistic Locking:
     * - Locks data when read, prevents others from modifying
     * - SELECT ... FOR UPDATE (row-level lock)
     * - LOCK TABLE (table-level lock)
     * - Good for high contention scenarios
     * 
     * Optimistic Locking:
     * - Assumes no conflict, checks at update time
     * - Uses version column or timestamp
     * - If version changed, retry the transaction
     * - Good for low contention scenarios
     * 
     * Lock Granularity:
     * - Row-level: Most concurrent, highest overhead
     * - Page-level: Balance between concurrency and overhead
     * - Table-level: Least concurrent, lowest overhead
     * 
     * Deadlock: Two transactions waiting for each other's locks
     * - Database detects and kills one transaction (deadlock victim)
     * - Prevention: Access resources in consistent order
     */
    public static void locking() {
        String sql = """
                -- Pessimistic Locking: SELECT ... FOR UPDATE
                BEGIN TRANSACTION;
                
                -- Lock the account row (other transactions wait)
                SELECT balance FROM accounts WHERE id = 1 FOR UPDATE;
                
                -- Perform the transfer
                UPDATE accounts SET balance = balance - 100 WHERE id = 1;
                UPDATE accounts SET balance = balance + 100 WHERE id = 2;
                
                COMMIT;
                -- Lock released
                
                -- Optimistic Locking with version column
                -- Table: accounts (id, balance, version)
                
                -- Step 1: Read with version
                SELECT id, balance, version FROM accounts WHERE id = 1;
                -- Result: id=1, balance=1000, version=5
                
                -- Step 2: Update with version check
                UPDATE accounts 
                SET balance = 900, version = version + 1
                WHERE id = 1 AND version = 5;
                -- If rows_affected = 0, someone else modified it, retry!
                
                -- Deadlock example
                -- Transaction 1:
                BEGIN TRANSACTION;
                UPDATE accounts SET balance = balance - 100 WHERE id = 1;
                -- Transaction 2:
                BEGIN TRANSACTION;
                UPDATE accounts SET balance = balance - 100 WHERE id = 2;
                -- Transaction 1:
                UPDATE accounts SET balance = balance + 100 WHERE id = 2;
                -- Transaction 2:
                UPDATE accounts SET balance = balance + 100 WHERE id = 1;
                -- DEADLOCK! Database kills one transaction
                
                -- Prevention: Always access resources in same order
                
                -- NOWAIT and SKIP LOCKED (PostgreSQL)
                SELECT * FROM accounts WHERE id = 1 FOR UPDATE NOWAIT;
                SELECT * FROM jobs ORDER BY priority FOR UPDATE SKIP LOCKED LIMIT 1;
                """;
        System.out.println("Q26 - Locking:\n" + sql);
    }

    // =============================================
    // 13. NoSQL vs SQL
    // =============================================

    /**
     * Q27: SQL vs NoSQL Comparison
     * 
     * SQL (Relational):
     * - Structured data with predefined schema
     * - ACID compliant
     * - Strong consistency
     * - Complex queries (JOINs, aggregations)
     * - Vertical scaling primarily
     * - Examples: PostgreSQL, MySQL, Oracle, SQL Server
     * 
     * NoSQL:
     * - Flexible/ schema-less data
     * - BASE (Basically Available, Soft state, Eventually consistent)
     * - Horizontal scaling
     * - Types: Document, Key-Value, Column-Family, Graph
     * - Examples: MongoDB, Redis, Cassandra, Neo4j
     * 
     * When to use SQL:
     * - Complex relationships and joins
     * - ACID compliance required
     * - Structured, predictable data
     * - Reporting and analytics
     * 
     * When to use NoSQL:
     * - Rapid prototyping with evolving schema
     * - Massive scale (billions of records)
     * - Simple key-value lookups
     * - Time-series data
     * - Graph/tree data structures
     */
    public static void sqlVsNoSQL() {
        String sql = """
                -- SQL: Structured, relational data
                CREATE TABLE orders (
                    id INT PRIMARY KEY,
                    customer_id INT,
                    order_date TIMESTAMP,
                    total DECIMAL(10,2),
                    FOREIGN KEY (customer_id) REFERENCES customers(id)
                );
                
                -- SQL: Complex join query
                SELECT c.name, COUNT(o.id) as order_count, SUM(o.total) as total_spent
                FROM customers c
                LEFT JOIN orders o ON c.id = o.customer_id
                WHERE o.order_date >= '2024-01-01'
                GROUP BY c.name
                HAVING COUNT(o.id) > 5
                ORDER BY total_spent DESC;
                
                -- NoSQL (MongoDB): Document model (equivalent)
                -- db.orders.insertOne({
                --     _id: ObjectId(),
                --     customer: { name: "John Doe", email: "john@example.com" },
                --     items: [
                --         { product: "Laptop", price: 1200, qty: 1 },
                --         { product: "Mouse", price: 25, qty: 2 }
                --     ],
                --     total: 1250,
                --     order_date: ISODate("2024-06-15")
                -- });
                
                -- Polyglot Persistence: Using both SQL and NoSQL
                -- SQL for orders/transactions (ACID)
                -- Redis for session cache
                -- Elasticsearch for full-text search
                -- Cassandra for time-series metrics
                """;
        System.out.println("Q27 - SQL vs NoSQL:\n" + sql);
    }

    // =============================================
    // 14. COMMON SQL INTERVIEW PROBLEMS
    // =============================================

    /**
     * Q28: Second Highest Salary
     * 
     * Problem: Find the employee with the second highest salary
     * Variations: Find nth highest, find per department
     */
    public static void secondHighestSalary() {
        String sql = """
                -- Method 1: Subquery with MAX
                SELECT MAX(salary) as second_highest
                FROM employees
                WHERE salary < (SELECT MAX(salary) FROM employees);
                
                -- Method 2: LIMIT with OFFSET
                SELECT DISTINCT salary
                FROM employees
                ORDER BY salary DESC
                LIMIT 1 OFFSET 1;
                
                -- Method 3: Window function (nth highest)
                SELECT DISTINCT salary
                FROM (
                    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) as rnk
                    FROM employees
                ) t
                WHERE rnk = 2;
                
                -- Method 4: Correlated subquery
                SELECT DISTINCT e1.salary
                FROM employees e1
                WHERE 2 = (SELECT COUNT(DISTINCT e2.salary) 
                           FROM employees e2 
                           WHERE e2.salary >= e1.salary);
                
                -- Nth highest per department
                SELECT name, department, salary
                FROM (
                    SELECT name, department, salary,
                           DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC) as rnk
                    FROM employees
                ) t
                WHERE rnk = 2;
                """;
        System.out.println("Q28 - Second Highest Salary:\n" + sql);
    }

    /**
     * Q29: Department-wise Employee Count and Average Salary
     */
    public static void departmentStats() {
        String sql = """
                -- Department statistics
                SELECT 
                    d.name as department,
                    COUNT(*) as employee_count,
                    AVG(e.salary) as avg_salary,
                    MAX(e.salary) as max_salary,
                    MIN(e.salary) as min_salary,
                    SUM(e.salary) as total_salary
                FROM departments d
                LEFT JOIN employees e ON d.id = e.dept_id
                GROUP BY d.name
                ORDER BY avg_salary DESC;
                
                -- Department with highest average salary
                SELECT d.name, AVG(e.salary) as avg_salary
                FROM departments d
                JOIN employees e ON d.id = e.dept_id
                GROUP BY d.name
                ORDER BY avg_salary DESC
                LIMIT 1;
                
                -- Employees earning more than their department average
                SELECT e.name, e.salary, d.name as department
                FROM employees e
                JOIN departments d ON e.dept_id = d.id
                WHERE e.salary > (
                    SELECT AVG(e2.salary)
                    FROM employees e2
                    WHERE e2.dept_id = e.dept_id
                );
                """;
        System.out.println("Q29 - Department Stats:\n" + sql);
    }

    /**
     * Q30: Find Duplicate Records
     */
    public static void findDuplicates() {
        String sql = """
                -- Find duplicate emails
                SELECT email, COUNT(*) as occurrence_count
                FROM employees
                GROUP BY email
                HAVING COUNT(*) > 1;
                
                -- Find duplicate records with all columns
                SELECT *
                FROM employees
                WHERE email IN (
                    SELECT email
                    FROM employees
                    GROUP BY email
                    HAVING COUNT(*) > 1
                )
                ORDER BY email;
                
                -- Delete duplicates keeping the one with lowest ID
                DELETE FROM employees
                WHERE id NOT IN (
                    SELECT MIN(id)
                    FROM employees
                    GROUP BY email
                );
                
                -- Alternative: Using ROW_NUMBER
                DELETE FROM employees
                WHERE id IN (
                    SELECT id
                    FROM (
                        SELECT id,
                               ROW_NUMBER() OVER (PARTITION BY email ORDER BY id) as rn
                        FROM employees
                    ) t
                    WHERE rn > 1
                );
                """;
        System.out.println("Q30 - Find Duplicates:\n" + sql);
    }

    /**
     * Q31: Running Total and Moving Average
     */
    public static void runningTotal() {
        String sql = """
                -- Running total of sales by date
                SELECT 
                    sale_date,
                    amount,
                    SUM(amount) OVER (ORDER BY sale_date) as running_total
                FROM sales
                ORDER BY sale_date;
                
                -- Running total with reset each month
                SELECT 
                    sale_date,
                    amount,
                    SUM(amount) OVER (
                        PARTITION BY EXTRACT(YEAR FROM sale_date), EXTRACT(MONTH FROM sale_date)
                        ORDER BY sale_date
                    ) as monthly_running_total
                FROM sales
                ORDER BY sale_date;
                
                -- 3-day moving average
                SELECT 
                    sale_date,
                    amount,
                    AVG(amount) OVER (
                        ORDER BY sale_date
                        ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
                    ) as moving_avg_3days
                FROM sales;
                
                -- Year-over-year comparison
                SELECT 
                    EXTRACT(YEAR FROM sale_date) as year,
                    EXTRACT(MONTH FROM sale_date) as month,
                    SUM(amount) as monthly_total,
                    LAG(SUM(amount), 12) OVER (ORDER BY EXTRACT(YEAR FROM sale_date), EXTRACT(MONTH FROM sale_date)) as same_month_last_year,
                    SUM(amount) - LAG(SUM(amount), 12) OVER (ORDER BY EXTRACT(YEAR FROM sale_date), EXTRACT(MONTH FROM sale_date)) as yoy_change
                FROM sales
                GROUP BY EXTRACT(YEAR FROM sale_date), EXTRACT(MONTH FROM sale_date)
                ORDER BY year, month;
                """;
        System.out.println("Q31 - Running Total:\n" + sql);
    }

    /**
     * Q32: Employee-Manager Hierarchy
     */
    public static void employeeHierarchy() {
        String sql = """
                -- Self-join to find manager name
                SELECT 
                    e1.name as employee,
                    e2.name as manager
                FROM employees e1
                LEFT JOIN employees e2 ON e1.manager_id = e2.id
                ORDER BY e1.name;
                
                -- Employees who are managers (have direct reports)
                SELECT DISTINCT e2.id, e2.name, e2.salary
                FROM employees e1
                JOIN employees e2 ON e1.manager_id = e2.id;
                
                -- Count direct reports per manager
                SELECT 
                    e2.name as manager,
                    COUNT(e1.id) as direct_reports
                FROM employees e1
                JOIN employees e2 ON e1.manager_id = e2.id
                GROUP BY e2.name
                HAVING COUNT(e1.id) > 0
                ORDER BY direct_reports DESC;
                
                -- Full org chart with levels (recursive CTE)
                WITH RECURSIVE org_chart AS (
                    SELECT id, name, manager_id, 0 as level
                    FROM employees WHERE manager_id IS NULL
                    
                    UNION ALL
                    
                    SELECT e.id, e.name, e.manager_id, oc.level + 1
                    FROM employees e
                    JOIN org_chart oc ON e.manager_id = oc.id
                )
                SELECT 
                    LPAD('', level * 2, ' ') || name as org_chart,
                    level
                FROM org_chart
                ORDER BY level, name;
                """;
        System.out.println("Q32 - Employee Hierarchy:\n" + sql);
    }

    /**
     * Q33: Date and Time Queries
     */
    public static void dateTimeQueries() {
        String sql = """
                -- Employees hired in the last 30 days
                SELECT * FROM employees
                WHERE hire_date >= CURRENT_DATE - INTERVAL '30 days';
                
                -- Employees hired in 2023
                SELECT * FROM employees
                WHERE EXTRACT(YEAR FROM hire_date) = 2023;
                
                -- Age calculation
                SELECT 
                    name,
                    birth_date,
                    EXTRACT(YEAR FROM AGE(CURRENT_DATE, birth_date)) as age
                FROM employees;
                
                -- Orders by month
                SELECT 
                    DATE_TRUNC('month', order_date) as month,
                    COUNT(*) as order_count,
                    SUM(total) as revenue
                FROM orders
                GROUP BY DATE_TRUNC('month', order_date)
                ORDER BY month DESC;
                
                -- Day of week analysis
                SELECT 
                    EXTRACT(DOW FROM order_date) as day_of_week,
                    COUNT(*) as order_count
                FROM orders
                GROUP BY EXTRACT(DOW FROM order_date)
                ORDER BY day_of_week;
                
                -- Find gaps in dates (missing dates)
                WITH date_series AS (
                    SELECT generate_series(
                        '2024-01-01'::date,
                        '2024-01-31'::date,
                        '1 day'::interval
                    )::date as date
                )
                SELECT ds.date as missing_date
                FROM date_series ds
                LEFT JOIN orders o ON ds.date = o.order_date::date
                WHERE o.id IS NULL;
                """;
        System.out.println("Q33 - Date/Time Queries:\n" + sql);
    }

    /**
     * Q34: Pivot and Unpivot
     */
    public static void pivotQueries() {
        String sql = """
                -- Pivot: Convert rows to columns
                -- Sample data: sales (product, year, amount)
                
                -- Manual pivot with CASE
                SELECT 
                    product,
                    SUM(CASE WHEN year = 2022 THEN amount ELSE 0 END) as sales_2022,
                    SUM(CASE WHEN year = 2023 THEN amount ELSE 0 END) as sales_2023,
                    SUM(CASE WHEN year = 2024 THEN amount ELSE 0 END) as sales_2024
                FROM sales
                GROUP BY product;
                
                -- Unpivot: Convert columns to rows
                -- Sample table: product_sales (product, q1, q2, q3, q4)
                SELECT product, 'Q1' as quarter, q1 as sales FROM product_sales
                UNION ALL
                SELECT product, 'Q2' as quarter, q2 as sales FROM product_sales
                UNION ALL
                SELECT product, 'Q3' as quarter, q3 as sales FROM product_sales
                UNION ALL
                SELECT product, 'Q4' as quarter, q4 as sales FROM product_sales
                ORDER BY product, quarter;
                """;
        System.out.println("Q34 - Pivot Queries:\n" + sql);
    }

    /**
     * Q35: Complex Query - Consecutive Days / Streaks
     */
    public static void consecutiveDays() {
        String sql = """
                -- Find users who logged in for 3+ consecutive days
                WITH login_dates AS (
                    SELECT DISTINCT user_id, login_date::date as login_date
                    FROM user_logins
                ),
                with_row_num AS (
                    SELECT 
                        user_id, 
                        login_date,
                        ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY login_date) as rn
                    FROM login_dates
                ),
                date_diff AS (
                    SELECT 
                        user_id,
                        login_date,
                        rn,
                        login_date - rn * INTERVAL '1 day' as group_date
                    FROM with_row_num
                ),
                consecutive_groups AS (
                    SELECT 
                        user_id,
                        group_date,
                        COUNT(*) as consecutive_days,
                        MIN(login_date) as start_date,
                        MAX(login_date) as end_date
                    FROM date_diff
                    GROUP BY user_id, group_date
                    HAVING COUNT(*) >= 3
                )
                SELECT * FROM consecutive_groups
                ORDER BY user_id, start_date;
                
                -- Alternative: Using LAG
                WITH login_dates AS (
                    SELECT DISTINCT user_id, login_date::date as login_date
                    FROM user_logins
                ),
                with_lag AS (
                    SELECT 
                        user_id,
                        login_date,
                        LAG(login_date, 1) OVER (PARTITION BY user_id ORDER BY login_date) as prev_date,
                        LAG(login_date, 2) OVER (PARTITION BY user_id ORDER BY login_date) as prev_prev_date
                    FROM login_dates
                )
                SELECT DISTINCT user_id
                FROM with_lag
                WHERE login_date - prev_date = 1 
                  AND prev_date - prev_prev_date = 1;
                """;
        System.out.println("Q35 - Consecutive Days:\n" + sql);
    }

    /**
     * Q36: Sessionization - Time Between Events
     */
    public static void sessionization() {
        String sql = """
                -- Define sessions: gap of 30+ minutes = new session
                WITH user_events AS (
                    SELECT 
                        user_id,
                        event_time,
                        LAG(event_time) OVER (PARTITION BY user_id ORDER BY event_time) as prev_event_time
                    FROM user_activity
                ),
                session_starts AS (
                    SELECT 
                        user_id,
                        event_time,
                        CASE 
                            WHEN prev_event_time IS NULL THEN 1
                            WHEN EXTRACT(EPOCH FROM (event_time - prev_event_time)) > 1800 THEN 1
                            ELSE 0
                        END as is_new_session
                    FROM user_events
                ),
                session_ids AS (
                    SELECT 
                        user_id,
                        event_time,
                        SUM(is_new_session) OVER (PARTITION BY user_id ORDER BY event_time 
                            ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as session_id
                    FROM session_starts
                )
                SELECT 
                    user_id,
                    session_id,
                    MIN(event_time) as session_start,
                    MAX(event_time) as session_end,
                    COUNT(*) as events_in_session,
                    MAX(event_time) - MIN(event_time) as session_duration
                FROM session_ids
                GROUP BY user_id, session_id
                ORDER BY user_id, session_start;
                """;
        System.out.println("Q36 - Sessionization:\n" + sql);
    }

    /**
     * Q37: Median Calculation
     */
    public static void medianCalculation() {
        String sql = """
                -- Method 1: Using PERCENTILE_CONT (PostgreSQL, SQL Server)
                SELECT 
                    department,
                    PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary) as median_salary
                FROM employees
                GROUP BY department;
                
                -- Method 2: Using ROW_NUMBER (works in all databases)
                WITH ranked AS (
                    SELECT 
                        salary,
                        ROW_NUMBER() OVER (ORDER BY salary) as rn,
                        COUNT(*) OVER () as total_count
                    FROM employees
                )
                SELECT AVG(salary) as median
                FROM ranked
                WHERE rn IN ((total_count + 1) / 2, (total_count + 2) / 2);
                
                -- Method 3: Median per department
                WITH ranked AS (
                    SELECT 
                        department,
                        salary,
                        ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary) as rn,
                        COUNT(*) OVER (PARTITION BY department) as dept_count
                    FROM employees
                )
                SELECT 
                    department,
                    AVG(salary) as median_salary
                FROM ranked
                WHERE rn IN ((dept_count + 1) / 2, (dept_count + 2) / 2)
                GROUP BY department;
                """;
        System.out.println("Q37 - Median Calculation:\n" + sql);
    }

    /**
     * Q38: Top N per Category
     */
    public static void topNPerCategory() {
        String sql = """
                -- Top 3 highest paid employees per department
                WITH ranked AS (
                    SELECT 
                        name,
                        department,
                        salary,
                        ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) as rn
                    FROM employees
                )
                SELECT name, department, salary
                FROM ranked
                WHERE rn <= 3
                ORDER BY department, rn;
                
                -- Top 3 products by sales in each category
                WITH product_sales AS (
                    SELECT 
                        p.category,
                        p.name as product,
                        SUM(s.quantity * s.unit_price) as total_sales
                    FROM products p
                    JOIN sales s ON p.id = s.product_id
                    GROUP BY p.category, p.name
                ),
                ranked AS (
                    SELECT 
                        category,
                        product,
                        total_sales,
                        ROW_NUMBER() OVER (PARTITION BY category ORDER BY total_sales DESC) as rn
                    FROM product_sales
                )
                SELECT category, product, total_sales
                FROM ranked
                WHERE rn <= 3
                ORDER BY category, rn;
                """;
        System.out.println("Q38 - Top N per Category:\n" + sql);
    }

    /**
     * Q39: Gaps and Islands Problem
     */
    public static void gapsAndIslands() {
        String sql = """
                -- Islands: Find consecutive date ranges
                -- Sample: employee_leave (emp_id, leave_date)
                -- Find all consecutive leave periods
                
                WITH ordered_leaves AS (
                    SELECT DISTINCT emp_id, leave_date,
                           ROW_NUMBER() OVER (PARTITION BY emp_id ORDER BY leave_date) as rn
                    FROM employee_leave
                ),
                groups AS (
                    SELECT emp_id, leave_date,
                           leave_date - rn * INTERVAL '1 day' as island_id
                    FROM ordered_leaves
                )
                SELECT 
                    emp_id,
                    MIN(leave_date) as period_start,
                    MAX(leave_date) as period_end,
                    COUNT(*) as consecutive_days
                FROM groups
                GROUP BY emp_id, island_id
                HAVING COUNT(*) > 1
                ORDER BY emp_id, period_start;
                
                -- Gaps: Find missing dates in a sequence
                WITH date_range AS (
                    SELECT generate_series(
                        '2024-01-01'::date,
                        '2024-01-31'::date,
                        '1 day'::interval
                    )::date as date
                ),
                actual_dates AS (
                    SELECT DISTINCT order_date::date as date
                    FROM orders
                    WHERE order_date >= '2024-01-01' AND order_date < '2024-02-01'
                )
                SELECT dr.date as missing_date
                FROM date_range dr
                LEFT JOIN actual_dates ad ON dr.date = ad.date
                WHERE ad.date IS NULL
                ORDER BY dr.date;
                """;
        System.out.println("Q39 - Gaps and Islands:\n" + sql);
    }

    /**
     * Q40: String Manipulation and Pattern Matching
     */
    public static void stringPatternMatching() {
        String sql = """
                -- Find names matching a pattern
                SELECT name FROM employees
                WHERE name ~ '^[A-Z].*son$';
                
                -- Extract domain from email
                SELECT 
                    email,
                    SUBSTRING(email FROM '@(.*)$') as email_domain
                FROM employees;
                
                -- Split full name into first and last
                SELECT 
                    name,
                    SPLIT_PART(name, ' ', 1) as first_name,
                    SPLIT_PART(name, ' ', 2) as last_name
                FROM employees;
                
                -- Concatenate strings
                SELECT 
                    CONCAT(first_name, ' ', last_name) as full_name,
                    first_name || ' ' || last_name as full_name_alt
                FROM employees;
                
                -- String length and position
                SELECT 
                    name,
                    LENGTH(name) as name_length,
                    POSITION('a' IN name) as first_a_position
                FROM employees;
                """;
        System.out.println("Q40 - String Pattern Matching:\n" + sql);
    }

    /**
     * Q41: Database Sharding and Partitioning
     * 
     * Partitioning: Dividing a table into smaller pieces (logical)
     * - Range Partitioning: By date range
     * - List Partitioning: By discrete values
     * - Hash Partitioning: By hash of a column
     * 
     * Sharding: Distributing data across multiple databases (physical)
     * - Horizontal sharding: Rows distributed
     * - Vertical sharding: Columns distributed
     * - Directory-based: Lookup table for shard location
     */
    public static void partitioningAndSharding() {
        String sql = """
                -- Range Partitioning (PostgreSQL)
                CREATE TABLE sales (
                    id INT,
                    sale_date DATE,
                    amount DECIMAL(10,2)
                ) PARTITION BY RANGE (sale_date);
                
                CREATE TABLE sales_2023_q1 PARTITION OF sales
                    FOR VALUES FROM ('2023-01-01') TO ('2023-04-01');
                CREATE TABLE sales_2023_q2 PARTITION OF sales
                    FOR VALUES FROM ('2023-04-01') TO ('2023-07-01');
                CREATE TABLE sales_2023_q3 PARTITION OF sales
                    FOR VALUES FROM ('2023-07-01') TO ('2023-10-01');
                CREATE TABLE sales_2023_q4 PARTITION OF sales
                    FOR VALUES FROM ('2023-10-01') TO ('2024-01-01');
                
                -- List Partitioning
                CREATE TABLE employees_by_region (
                    id INT,
                    name VARCHAR(100),
                    region VARCHAR(20)
                ) PARTITION BY LIST (region);
                
                CREATE TABLE employees_us PARTITION OF employees_by_region
                    FOR VALUES IN ('US', 'USA', 'America');
                CREATE TABLE employees_eu PARTITION OF employees_by_region
                    FOR VALUES IN ('UK', 'DE', 'FR', 'IT');
                CREATE TABLE employees_asia PARTITION OF employees_by_region
                    FOR VALUES IN ('IN', 'CN', 'JP', 'KR');
                
                -- Hash Partitioning
                CREATE TABLE user_sessions (
                    user_id INT,
                    session_data TEXT
                ) PARTITION BY HASH (user_id);
                
                CREATE TABLE user_sessions_0 PARTITION OF user_sessions
                    FOR VALUES WITH (MODULUS 4, REMAINDER 0);
                CREATE TABLE user_sessions_1 PARTITION OF user_sessions
                    FOR VALUES WITH (MODULUS 4, REMAINDER 1);
                CREATE TABLE user_sessions_2 PARTITION OF user_sessions
                    FOR VALUES WITH (MODULUS 4, REMAINDER 2);
                CREATE TABLE user_sessions_3 PARTITION OF user_sessions
                    FOR VALUES WITH (MODULUS 4, REMAINDER 3);
                
                -- Querying partitioned tables (same as regular tables)
                SELECT * FROM sales WHERE sale_date BETWEEN '2023-06-01' AND '2023-06-30';
                -- Database automatically prunes partitions!
                """;
        System.out.println("Q41 - Partitioning and Sharding:\n" + sql);
    }

    /**
     * Q42: Common Table Expressions (Advanced)
     */
    public static void advancedCTEs() {
        String sql = """
                -- CTE with UPDATE
                WITH high_salary_emps AS (
                    SELECT id FROM employees WHERE salary > 100000
                )
                UPDATE employees 
                SET bonus = salary * 0.2
                WHERE id IN (SELECT id FROM high_salary_emps);
                
                -- CTE with DELETE
                WITH old_orders AS (
                    SELECT id FROM orders WHERE order_date < CURRENT_DATE - INTERVAL '5 years'
                )
                DELETE FROM order_items WHERE order_id IN (SELECT id FROM old_orders);
                
                -- CTE with INSERT (generate data)
                WITH RECURSIVE date_series AS (
                    SELECT '2024-01-01'::date as date
                    UNION ALL
                    SELECT date + 1 FROM date_series WHERE date < '2024-12-31'
                )
                INSERT INTO calendar_dates (date, year, month, day, quarter)
                SELECT 
                    date,
                    EXTRACT(YEAR FROM date) as year,
                    EXTRACT(MONTH FROM date) as month,
                    EXTRACT(DAY FROM date) as day,
                    EXTRACT(QUARTER FROM date) as quarter
                FROM date_series;
                
                -- Multiple CTEs with dependencies
                WITH 
                dept_avg AS (
                    SELECT dept_id, AVG(salary) as avg_sal
                    FROM employees GROUP BY dept_id
                ),
                dept_above_avg AS (
                    SELECT dept_id, avg_sal
                    FROM dept_avg
                    WHERE avg_sal > (SELECT AVG(salary) FROM employees)
                ),
                high_earners AS (
                    SELECT e.name, e.salary, d.name as dept
                    FROM employees e
                    JOIN dept_above_avg d ON e.dept_id = d.dept_id
                    WHERE e.salary > d.avg_sal
                )
                SELECT * FROM high_earners ORDER BY salary DESC;
                """;
        System.out.println("Q42 - Advanced CTEs:\n" + sql);
    }

    /**
     * Q43: Database Backup and Recovery Strategies
     * 
     * Backup Types:
     * - Full backup: Complete database
     * - Incremental backup: Changes since last backup
     * - Differential backup: Changes since last full backup
     * 
     * Point-in-Time Recovery (PITR):
     * - Uses WAL (Write Ahead Log) archives
     * - Can restore to any point in time
     * 
     * High Availability:
     * - Master-Slave replication
     * - Multi-master replication
     * - Synchronous vs Asynchronous replication
     * - Failover strategies
     */
    public static void backupAndRecovery() {
        String sql = """
                -- PostgreSQL: Create backup
                -- pg_dump -h localhost -U postgres -d mydb > mydb_backup.sql
                
                -- PostgreSQL: Create compressed backup
                -- pg_dump -h localhost -U postgres -d mydb -F c > mydb_backup.dump
                
                -- PostgreSQL: Restore from backup
                -- psql -h localhost -U postgres -d mydb < mydb_backup.sql
                -- pg_restore -h localhost -U postgres -d mydb mydb_backup.dump
                
                -- PostgreSQL: Continuous archiving (WAL)
                -- archive_mode = on
                -- archive_command = 'cp %p /archive/%f'
                
                -- PostgreSQL: Point-in-time recovery
                -- recovery_target_time = '2024-06-15 14:30:00'
                
                -- MySQL: Create backup
                -- mysqldump -u root -p mydb > mydb_backup.sql
                
                -- MySQL: Restore from backup
                -- mysql -u root -p mydb < mydb_backup.sql
                
                -- MySQL: Binary log for PITR
                -- mysqlbinlog mysql-bin.000001 | mysql -u root -p
                
                -- Replication setup (PostgreSQL)
                -- Primary: wal_level = replica, max_wal_senders = 5
                -- Standby: primary_conninfo = 'host=primary_host port=5432'
                
                -- Failover commands
                -- Promote standby to primary:
                -- pg_ctl promote -D /var/lib/postgresql/data
                """;
        System.out.println("Q43 - Backup and Recovery:\n" + sql);
    }

    /**
     * Q44: Database Security Best Practices
     * 
     * 1. Principle of least privilege
     * 2. Use parameterized queries (prevent SQL injection)
     * 3. Encrypt sensitive data at rest and in transit
     * 4. Regular security audits
     * 5. Row-level security policies
     * 6. Column-level security (views or permissions)
     * 7. Audit logging
     * 8. Network security (firewalls, VPC)
     */
    public static void databaseSecurity() {
        String sql = """
                -- Create roles with specific permissions
                CREATE ROLE read_only;
                CREATE ROLE read_write;
                CREATE ROLE admin;
                
                -- Grant schema-level permissions
                GRANT USAGE ON SCHEMA public TO read_only;
                GRANT SELECT ON ALL TABLES IN SCHEMA public TO read_only;
                
                GRANT USAGE ON SCHEMA public TO read_write;
                GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO read_write;
                
                GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO admin;
                
                -- Column-level security (hide sensitive columns)
                CREATE VIEW employee_public AS
                SELECT id, name, email, department
                FROM employees;
                -- Grant access to view instead of table
                GRANT SELECT ON employee_public TO read_only;
                
                -- Row-level security (PostgreSQL)
                CREATE POLICY user_isolation ON employees
                    FOR ALL
                    USING (department = CURRENT_SETTING('app.current_department'));
                
                ALTER TABLE employees ENABLE ROW LEVEL SECURITY;
                
                -- Encrypt sensitive data
                -- Using pgcrypto extension (PostgreSQL)
                -- CREATE EXTENSION pgcrypto;
                -- INSERT INTO users (username, password_hash)
                -- VALUES ('john', crypt('mypassword', gen_salt('bf')));
                
                -- Audit logging (trigger-based)
                CREATE TABLE audit_log (
                    table_name VARCHAR(100),
                    operation VARCHAR(10),
                    old_data JSONB,
                    new_data JSONB,
                    changed_by VARCHAR(100),
                    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                
                -- Parameterized queries (application side)
                -- Java PreparedStatement:
                -- PreparedStatement ps = conn.prepareStatement(
                --     "SELECT * FROM users WHERE username = ? AND password = ?");
                -- ps.setString(1, username);
                -- ps.setString(2, password);
                """;
        System.out.println("Q44 - Database Security:\n" + sql);
    }

    /**
     * Q45: SQL Injection Prevention
     * 
     * SQL Injection occurs when user input is directly concatenated into SQL.
     * 
     * Prevention:
     * 1. Use parameterized queries (PreparedStatement)
     * 2. Use ORM frameworks (Hibernate, JPA)
     * 3. Validate and sanitize input
     * 4. Use stored procedures
     * 5. Least privilege database accounts
     */
    public static void sqlInjectionPrevention() {
        String sql = """
                -- VULNERABLE (DO NOT USE):
                -- String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
                -- Input: username = "admin' --"
                -- Result: SELECT * FROM users WHERE username = 'admin' --' AND password = 'anything'
                -- Bypasses authentication!
                
                -- Input: username = "'; DROP TABLE users; --"
                -- Result: SELECT * FROM users WHERE username = ''; DROP TABLE users; --'
                -- Deletes the users table!
                
                -- SAFE: Use parameterized queries
                -- PreparedStatement ps = conn.prepareStatement(
                --     "SELECT * FROM users WHERE username = ? AND password = ?");
                -- ps.setString(1, username);
                -- ps.setString(2, password);
                
                -- SAFE: Use stored procedures
                -- CREATE PROCEDURE AuthenticateUser
                --     @username VARCHAR(100),
                --     @password VARCHAR(100)
                -- AS
                -- BEGIN
                --     SELECT * FROM users 
                --     WHERE username = @username AND password_hash = HASHBYTES('SHA2_256', @password);
                -- END;
                
                -- SAFE: Use ORM (Hibernate)
                -- Query q = session.createQuery("FROM User WHERE username = :username");
                -- q.setParameter("username", userInput);
                
                -- Additional protections:
                -- 1. Input validation (whitelist patterns)
                -- 2. Escape special characters
                -- 3. WAF (Web Application Firewall)
                -- 4. Database account with minimum privileges
                """;
        System.out.println("Q45 - SQL Injection Prevention:\n" + sql);
    }

    /**
     * Q46: Database Migration Strategies
     * 
     * Tools: Flyway, Liquibase, Alembic
     * 
     * Best Practices:
     * - Version-controlled migrations
     * - Idempotent migrations (can run multiple times safely)
     * - Backward compatible changes
     * - Rollback scripts
     * - Test on staging first
     */
    public static void databaseMigrations() {
        String sql = """
                -- Flyway migration example: V1__create_users.sql
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                
                -- V2__add_roles.sql
                CREATE TABLE roles (
                    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                    name VARCHAR(50) NOT NULL UNIQUE
                );
                
                CREATE TABLE user_roles (
                    user_id BIGINT NOT NULL REFERENCES users(id),
                    role_id BIGINT NOT NULL REFERENCES roles(id),
                    PRIMARY KEY (user_id, role_id)
                );
                
                -- V3__add_email_verified.sql
                ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;
                ALTER TABLE users ADD COLUMN verification_token VARCHAR(255);
                
                -- V4__create_indexes.sql
                CREATE INDEX idx_users_email ON users(email);
                CREATE INDEX idx_users_username ON users(username);
                CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
                
                -- Safe migration practices:
                -- 1. Add new columns as nullable or with defaults
                -- ALTER TABLE users ADD COLUMN phone VARCHAR(20);
                
                -- 2. Add NOT NULL constraint after backfilling data
                -- UPDATE users SET phone = '000-000-0000' WHERE phone IS NULL;
                -- ALTER TABLE users ALTER COLUMN phone SET NOT NULL;
                
                -- 3. Rename columns (add new, migrate data, drop old)
                -- ALTER TABLE users ADD COLUMN email_address VARCHAR(100);
                -- UPDATE users SET email_address = email;
                -- ALTER TABLE users DROP COLUMN email;
                -- ALTER TABLE users RENAME COLUMN email_address TO email;
                
                -- 4. Zero-down-time migration pattern
                -- Step 1: Add new column
                -- Step 2: Deploy app that writes to both old and new
                -- Step 3: Backfill old data to new
                -- Step 4: Deploy app that reads from new only
                -- Step 5: Drop old column
                """;
        System.out.println("Q46 - Database Migrations:\n" + sql);
    }

    /**
     * Q47: Connection Pooling
     * 
     * Connection pooling reuses database connections to avoid the overhead
     * of establishing new connections for each request.
     * 
     * Popular pools: HikariCP, Tomcat CP, DBCP2
     * 
     * Key parameters:
     * - maximumPoolSize: Max connections in pool
     * - minimumIdle: Min idle connections
     * - connectionTimeout: Max wait time for connection
     * - idleTimeout: Max time connection can stay idle
     * - maxLifetime: Max lifetime of a connection
     */
    public static void connectionPooling() {
        String sql = """
                -- HikariCP Configuration (Java)
                -- HikariConfig config = new HikariConfig();
                -- config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
                -- config.setUsername("user");
                -- config.setPassword("password");
                -- config.setMaximumPoolSize(20);
                -- config.setMinimumIdle(5);
                -- config.setConnectionTimeout(30000);
                -- config.setIdleTimeout(600000);
                -- config.setMaxLifetime(1800000);
                -- config.setConnectionTestQuery("SELECT 1");
                -- 
                -- HikariDataSource dataSource = new HikariDataSource(config);
                
                -- Spring Boot configuration (application.properties):
                -- spring.datasource.hikari.maximum-pool-size=20
                -- spring.datasource.hikari.minimum-idle=5
                -- spring.datasource.hikari.connection-timeout=30000
                -- spring.datasource.hikari.idle-timeout=600000
                -- spring.datasource.hikari.max-lifetime=1800000
                
                -- Best practices:
                -- 1. Pool size = (core_count * 2) + effective_spindle_count
                -- 2. Monitor pool usage (HikariMetrics)
                -- 3. Test connection on borrow (validationQuery)
                -- 4. Set appropriate timeouts
                -- 5. Close connections properly (try-with-resources)
                
                -- JDBC try-with-resources pattern:
                -- try (Connection conn = dataSource.getConnection();
                --      PreparedStatement ps = conn.prepareStatement(sql);
                --      ResultSet rs = ps.executeQuery()) {
                --     while (rs.next()) {
                --         // process results
                --     }
                -- } // Auto-closed!
                """;
        System.out.println("Q47 - Connection Pooling:\n" + sql);
    }

    /**
     * Q48: N+1 Query Problem
     * 
     * The N+1 problem occurs when an application executes 1 query to fetch
     * parent records, then N queries to fetch related child records.
     * 
     * Solutions:
     * 1. JOIN FETCH (JPA/Hibernate)
     * 2. Batch fetching
     * 3. Entity Graphs
     * 4. Second-level cache
     */
    public static void nPlusOneProblem() {
        String sql = """
                -- N+1 Problem Example:
                -- 1 query to get all departments:
                SELECT * FROM departments;
                -- Returns 10 departments
                
                -- N queries to get employees for each department:
                SELECT * FROM employees WHERE dept_id = 1;
                SELECT * FROM employees WHERE dept_id = 2;
                -- ... 8 more queries (total 11 queries!)
                
                -- Solution 1: JOIN (single query)
                SELECT d.name as department, e.name as employee
                FROM departments d
                LEFT JOIN employees e ON d.id = e.dept_id
                ORDER BY d.name, e.name;
                
                -- Solution 2: Batch query (2 queries)
                SELECT * FROM departments;
                SELECT * FROM employees WHERE dept_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
                
                -- Solution 3: JPA/Hibernate JOIN FETCH
                -- SELECT d FROM Department d JOIN FETCH d.employees
                
                -- Solution 4: Entity Graph (JPA)
                -- @EntityGraph(attributePaths = {"employees"})
                -- List<Department> findAll();
                
                -- Solution 5: Batch fetching size
                -- @BatchSize(size = 10)
                -- or hibernate.default_batch_fetch_size=10
                """;
        System.out.println("Q48 - N+1 Problem:\n" + sql);
    }

    /**
     * Q49: Database Design - Anti-patterns to Avoid
     * 
     * 1. Using comma-separated values in a column (violates 1NF)
     * 2. Using generic key-value table instead of proper columns
     * 3. Not using foreign keys (referential integrity)
     * 4. Over-indexing (too many indexes slow down writes)
     * 5. Using SELECT * in production code
     * 6. Not having proper indexes on foreign keys
     * 7. Using too many joins (consider denormalization)
     * 8. Storing calculated values that can be derived
     */
    public static void designAntiPatterns() {
        String sql = """
                -- Anti-pattern 1: Comma-separated values
                -- BAD:
                CREATE TABLE orders (
                    id INT PRIMARY KEY,
                    products VARCHAR(500) -- "1,2,3,4,5"
                );
                -- GOOD:
                CREATE TABLE orders (
                    id INT PRIMARY KEY
                );
                CREATE TABLE order_items (
                    order_id INT,
                    product_id INT,
                    PRIMARY KEY (order_id, product_id),
                    FOREIGN KEY (order_id) REFERENCES orders(id)
                );
                
                -- Anti-pattern 2: Generic key-value table
                -- BAD:
                CREATE TABLE user_attributes (
                    user_id INT,
                    attr_key VARCHAR(50),
                    attr_value VARCHAR(500),
                    PRIMARY KEY (user_id, attr_key)
                );
                -- GOOD:
                CREATE TABLE users (
                    id INT PRIMARY KEY,
                    name VARCHAR(100),
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    address TEXT
                );
                
                -- Anti-pattern 3: No foreign keys
                -- BAD:
                CREATE TABLE orders (
                    customer_id INT -- No FK constraint!
                );
                -- GOOD:
                CREATE TABLE orders (
                    customer_id INT REFERENCES customers(id)
                );
                
                -- Anti-pattern 4: Over-indexing
                -- BAD: Index on every column
                -- CREATE INDEX idx_col1 ON table(col1);
                -- CREATE INDEX idx_col2 ON table(col2);
                -- CREATE INDEX idx_col3 ON table(col3);
                -- ...
                -- GOOD: Index based on query patterns
                -- CREATE INDEX idx_covering ON table(col1, col2) INCLUDE (col3);
                
                -- Anti-pattern 5: Storing calculated values
                -- BAD:
                -- CREATE TABLE orders (
                --     unit_price DECIMAL,
                --     quantity INT,
                --     total_price DECIMAL -- Can be calculated!
                -- );
                -- GOOD: Calculate when needed
                -- SELECT unit_price * quantity as total_price FROM orders;
                """;
        System.out.println("Q49 - Design Anti-patterns:\n" + sql);
    }

    /**
     * Q50: SQL Database Interview Summary - Key Topics
     * 
     * Must-Know Topics:
     * 1. SQL Basics: SELECT, WHERE, JOINs, GROUP BY, HAVING, ORDER BY
     * 2. Subqueries: Scalar, Correlated, EXISTS, IN, ANY, ALL
     * 3. Window Functions: ROW_NUMBER, RANK, DENSE_RANK, LAG, LEAD
     * 4. CTEs: Simple, Recursive, Multiple CTEs
     * 5. Database Design: Normalization (1NF, 2NF, 3NF, BCNF), ER Diagrams
     * 6. Indexing: B-Tree, Composite, Covering, Clustered vs Non-Clustered
     * 7. Query Optimization: EXPLAIN, Index usage, Anti-patterns
     * 8. Transactions: ACID, Isolation Levels, Locking, Deadlocks
     * 9. Stored Procedures, Functions, Triggers, Views
     * 10. NoSQL vs SQL: When to use which
     * 
     * Common Interview Problems:
     * - Second highest salary
     * - Department statistics
     * - Find duplicates
     * - Running total
     * - Employee hierarchy
     * - Consecutive days
     * - Median calculation
     * - Top N per category
     * - Gaps and islands
     * - Pivot queries
     */
    public static void summary() {
        System.out.println("""
                Q50 - SQL Interview Summary:
                
                Must-Know Topics:
                1. SQL Basics: SELECT, WHERE, JOINs, GROUP BY, HAVING, ORDER BY
                2. Subqueries: Scalar, Correlated, EXISTS, IN, ANY, ALL
                3. Window Functions: ROW_NUMBER, RANK, DENSE_RANK, LAG, LEAD
                4. CTEs: Simple, Recursive, Multiple CTEs
                5. Database Design: Normalization (1NF, 2NF, 3NF, BCNF), ER Diagrams
                6. Indexing: B-Tree, Composite, Covering, Clustered vs Non-Clustered
                7. Query Optimization: EXPLAIN, Index usage, Anti-patterns
                8. Transactions: ACID, Isolation Levels, Locking, Deadlocks
                9. Stored Procedures, Functions, Triggers, Views
                10. NoSQL vs SQL: When to use which
                
                Common Interview Problems:
                - Second highest salary
                - Department statistics
                - Find duplicates
                - Running total
                - Employee hierarchy
                - Consecutive days
                - Median calculation
                - Top N per category
                - Gaps and islands
                - Pivot queries
                """);
    }

    // =============================================
    // MAIN METHOD - Run all demonstrations
    // =============================================

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("SQL DATABASE INTERVIEW QUESTIONS");
        System.out.println("==========================================\n");

        // Section 1: SQL Basics
        selectBasics();
        whereClause();
        distinctVsGroupBy();

        // Section 2: Joins
        joinTypes();
        joinVsSubquery();

        // Section 3: Aggregation
        aggregateFunctions();
        groupByMultiple();
        havingVsWhere();

        // Section 4: Subqueries
        subqueryTypes();
        existsInAnyAll();

        // Section 5: Window Functions
        windowFunctions();
        windowFunctionProblems();

        // Section 6: CTEs
        cteBasics();
        recursiveCTE();

        // Section 7: Database Design
        normalization();
        erDiagram();

        // Section 8: Indexing
        indexing();
        explainPlan();

        // Section 9: Transactions
        acidProperties();
        isolationLevels();

        // Section 10: Stored Procedures, Triggers, Views
        storedProceduresAndFunctions();
        triggers();
        views();

        // Section 11: Query Optimization
        queryOptimization();
        performanceAntiPatterns();

        // Section 12: Locking
        locking();

        // Section 13: NoSQL vs SQL
        sqlVsNoSQL();

        // Section 14: Common Problems
        secondHighestSalary();
        departmentStats();
        findDuplicates();
        runningTotal();
        employeeHierarchy();
        dateTimeQueries();
        pivotQueries();
        consecutiveDays();
        sessionization();
        medianCalculation();
        topNPerCategory();
        gapsAndIslands();
        stringPatternMatching();
        partitioningAndSharding();
        advancedCTEs();
        backupAndRecovery();
        databaseSecurity();
        sqlInjectionPrevention();
        databaseMigrations();
        connectionPooling();
        nPlusOneProblem();
        designAntiPatterns();
        summary();

        System.out.println("==========================================");
        System.out.println("END OF SQL INTERVIEW QUESTIONS");
        System.out.println("==========================================");
    }
}
