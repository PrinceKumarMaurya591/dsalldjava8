package com.dsa.designpatterns.behavioral;

// ============================================
// Interpreter Pattern
// ============================================
//
// Intent: Given a language, define a representation for its grammar along with
// an interpreter that uses the representation to interpret sentences in the language.
//
// When to use:
// - The grammar is simple and straightforward
// - Efficiency is not a critical concern
// - You need to interpret expressions in a domain-specific language
//
// Benefits:
// - Easy to change and extend the grammar
// - Grammar implementation is simple (class per grammar rule)
// - Adding new expressions is straightforward
//
// Real-world examples:
// - java.util.regex.Pattern
// - java.text.Normalizer
// - SQL parsers
// - Mathematical expression evaluators

import java.util.Map;
import java.util.HashMap;

// Step 1: Abstract Expression
interface Expression {
    int interpret(Map<String, Integer> context);
}

// Step 2: Terminal Expressions
class NumberExpression implements Expression {
    private int number;

    public NumberExpression(int number) {
        this.number = number;
    }

    @Override
    public int interpret(Map<String, Integer> context) {
        return number;
    }
}

class VariableExpression implements Expression {
    private String name;

    public VariableExpression(String name) {
        this.name = name;
    }

    @Override
    public int interpret(Map<String, Integer> context) {
        if (context.containsKey(name)) {
            return context.get(name);
        }
        return 0;
    }
}

// Step 3: Non-terminal Expressions
class AddExpression implements Expression {
    private Expression left;
    private Expression right;

    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret(Map<String, Integer> context) {
        return left.interpret(context) + right.interpret(context);
    }
}

class SubtractExpression implements Expression {
    private Expression left;
    private Expression right;

    public SubtractExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret(Map<String, Integer> context) {
        return left.interpret(context) - right.interpret(context);
    }
}

class MultiplyExpression implements Expression {
    private Expression left;
    private Expression right;

    public MultiplyExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret(Map<String, Integer> context) {
        return left.interpret(context) * right.interpret(context);
    }
}

class DivideExpression implements Expression {
    private Expression left;
    private Expression right;

    public DivideExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret(Map<String, Integer> context) {
        int divisor = right.interpret(context);
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left.interpret(context) / divisor;
    }
}

// Step 4: Parser - builds the AST from a simple expression string
class ExpressionParser {
    // Parses simple expressions like: "a + b * c - d / e"
    // Supports: +, -, *, /, numbers, and variable names
    public static Expression parse(String expression) {
        // Simple recursive descent parser for expressions
        return parseAddSubtract(expression.trim(), new int[]{0});
    }

    private static Expression parseAddSubtract(String expr, int[] pos) {
        Expression left = parseMultiplyDivide(expr, pos);

        while (pos[0] < expr.length()) {
            skipWhitespace(expr, pos);
            if (pos[0] >= expr.length()) break;
            char op = expr.charAt(pos[0]);
            if (op == '+' || op == '-') {
                pos[0]++; // consume operator
                Expression right = parseMultiplyDivide(expr, pos);
                if (op == '+') {
                    left = new AddExpression(left, right);
                } else {
                    left = new SubtractExpression(left, right);
                }
            } else {
                break;
            }
        }
        return left;
    }

    private static Expression parseMultiplyDivide(String expr, int[] pos) {
        Expression left = parsePrimary(expr, pos);

        while (pos[0] < expr.length()) {
            skipWhitespace(expr, pos);
            if (pos[0] >= expr.length()) break;
            char op = expr.charAt(pos[0]);
            if (op == '*' || op == '/') {
                pos[0]++; // consume operator
                Expression right = parsePrimary(expr, pos);
                if (op == '*') {
                    left = new MultiplyExpression(left, right);
                } else {
                    left = new DivideExpression(left, right);
                }
            } else {
                break;
            }
        }
        return left;
    }

    private static Expression parsePrimary(String expr, int[] pos) {
        skipWhitespace(expr, pos);

        if (pos[0] >= expr.length()) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }

        char c = expr.charAt(pos[0]);

        // Parenthesized expression
        if (c == '(') {
            pos[0]++; // consume '('
            Expression expr_inner = parseAddSubtract(expr, pos);
            skipWhitespace(expr, pos);
            if (pos[0] < expr.length() && expr.charAt(pos[0]) == ')') {
                pos[0]++; // consume ')'
            }
            return expr_inner;
        }

        // Number or variable
        if (Character.isDigit(c)) {
            return parseNumber(expr, pos);
        } else if (Character.isLetter(c)) {
            return parseVariable(expr, pos);
        }

        throw new IllegalArgumentException("Unexpected character: " + c);
    }

    private static NumberExpression parseNumber(String expr, int[] pos) {
        int start = pos[0];
        while (pos[0] < expr.length() && Character.isDigit(expr.charAt(pos[0]))) {
            pos[0]++;
        }
        int value = Integer.parseInt(expr.substring(start, pos[0]));
        return new NumberExpression(value);
    }

    private static VariableExpression parseVariable(String expr, int[] pos) {
        int start = pos[0];
        while (pos[0] < expr.length() && Character.isLetter(expr.charAt(pos[0]))) {
            pos[0]++;
        }
        String name = expr.substring(start, pos[0]);
        return new VariableExpression(name);
    }

    private static void skipWhitespace(String expr, int[] pos) {
        while (pos[0] < expr.length() && Character.isWhitespace(expr.charAt(pos[0]))) {
            pos[0]++;
        }
    }
}

// Step 5: Demo
public class InterpreterPattern {

    public static void main(String[] args) {
        System.out.println("=== Interpreter Pattern ===");

        // Simple arithmetic: 10 + 20 - 5
        System.out.println("\n1. Simple arithmetic:");
        Expression expr1 = ExpressionParser.parse("10 + 20 - 5");
        System.out.println("  10 + 20 - 5 = " + expr1.interpret(new HashMap<>()));

        // With multiplication and division
        System.out.println("\n2. With multiplication and division:");
        Expression expr2 = ExpressionParser.parse("10 + 5 * 3");
        System.out.println("  10 + 5 * 3 = " + expr2.interpret(new HashMap<>()));

        // With parentheses
        System.out.println("\n3. With parentheses:");
        Expression expr3 = ExpressionParser.parse("(10 + 5) * 3");
        System.out.println("  (10 + 5) * 3 = " + expr3.interpret(new HashMap<>()));

        // With variables
        System.out.println("\n4. With variables:");
        Expression expr4 = ExpressionParser.parse("a + b * c");
        Map<String, Integer> vars = new HashMap<>();
        vars.put("a", 10);
        vars.put("b", 5);
        vars.put("c", 3);
        System.out.println("  a + b * c (a=10, b=5, c=3) = " + expr4.interpret(vars));

        // Complex expression
        System.out.println("\n5. Complex expression:");
        Expression expr5 = ExpressionParser.parse("(a + b) * (c - d) / e");
        Map<String, Integer> vars2 = new HashMap<>();
        vars2.put("a", 10);
        vars2.put("b", 5);
        vars2.put("c", 8);
        vars2.put("d", 3);
        vars2.put("e", 2);
        System.out.println("  (a + b) * (c - d) / e = " + expr5.interpret(vars2));

        System.out.println("\nKey points:");
        System.out.println("- Represents grammar rules as classes");
        System.out.println("- Terminal expressions: numbers, variables");
        System.out.println("- Non-terminal expressions: +, -, *, /");
        System.out.println("- Easy to extend with new operations");
        System.out.println("- Context (variable bindings) passed during interpretation");
    }
}
