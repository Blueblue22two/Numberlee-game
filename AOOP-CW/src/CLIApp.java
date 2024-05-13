// CLIAPP.java
import java.util.Scanner;
import java.util.Stack;

public class CLIApp {
    private static INumberleModel model = new NumberleModel();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("-----Welcome to the Numberle game!-----");
        model.initialize();
        while (true) {
            System.out.println("Enter Y to start the game or N to quit the game:");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("N")) break;
            else if (input.equalsIgnoreCase("Y")) playGame();
            else System.out.println("Invalid input. Please enter 'Y' to start or 'N' to quit.");
        }
        System.out.println("-----Exiting the game. Goodbye!-----");
        System.exit(0);
    }

    private static void playGame() {
        startNewGame();
        while (!isGameOver()) {
            displayRemainingAttempt();
            String guess = scanner.nextLine().trim();
            if (!validateInput(guess)) {
                continue;
            }
            processInput(guess);
            if (isGameOver()) return;
        }
    }

    // Verify that the equation entered by the user is valid
    private static boolean validateInput(String input){
        assert input != null : "Input string cannot be null";
        assert input.length() == 7 : "Input string must be exactly 7 characters long";

        if (input.isEmpty()) {
            displayErrorMsg("Input cannot be empty");
            return false;
        }
        if (!input.matches("^[0-9+-/*=()]*$") || input.length() != 7) {
            displayErrorMsg("The Equation is invalid。");
            return false;
        }
        if (!input.contains("=")) {
            displayErrorMsg("Equation must include an equals sign.");
            return false;
        }
        if (getRemainingAttempts() <= 0) {
            showGameEndMessage();
            return false;
        }

        // Check if the input forms a valid equation, if not, display an error message indicating that the equation is not valid.
        // Split the equation into left and right parts
        String[] parts = input.split("=");
        assert parts.length == 2 : "Equation must split into exactly two parts by the equals sign.";
        // Process the expression on the left side of the equation
        int leftResult = evaluateExpression(parts[0].trim());
        // Convert the right side of the equation to an integer
        int rightResult = evaluateExpression(parts[1].trim());
        // Check if both sides of the equation are equal
        if (leftResult != rightResult) {
            displayErrorMsg("The left side is not equal to right side.");
            return false;
        }
        return true;
    }

    private static void processInput(String input) {
        if (model.processInput(input)) {
            showGameEndMessage();
        } else {
            char[] inputChars = input.toCharArray();
            int[] matchResults = model.matchInput(inputChars);
            assert matchResults != null : "matchResults is null";
            updateViewWithMatchResults(matchResults,getCurrentGuess());
            if(isGameOver())showGameEndMessage();
        }
    }

    // Convert the expression to a character array for processing
    private static int evaluateExpression(String expression) {
        char[] tokens = expression.toCharArray();
        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            // If the token is a number, push it to the value stack
            if (Character.isDigit(tokens[i])) {
                StringBuilder sb = new StringBuilder();
                // It could be a multi-digit number
                while (i < tokens.length && Character.isDigit(tokens[i])) {
                    sb.append(tokens[i++]);
                }
                values.push(Integer.parseInt(sb.toString()));
                i--; // Necessary because the for loop also increments i
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                // Compare the precedence of the current operator with the operator on top of the stack
                while (!ops.isEmpty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                // Push the current operator onto the stack
                ops.push(tokens[i]);
            }
        }
        // The entire expression has been processed, apply the remaining operators to the remaining values
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    }

    // Determines if the precedence of op2 is greater than or equal to op1
    private static boolean hasPrecedence(char op1, char op2) {
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    // operate
    private static int applyOp(char op, int b, int a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

    private static StringBuilder getCurrentGuess() {
        return model.getCurrentGuess();
    }

    private static int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    private static void displayRemainingAttempt(){
        System.out.println(" ");
        final String ANSI_RED = "\033[31m";
        final String ANSI_RESET = "\033[0m";
        System.out.println(ANSI_RED + "Remaining attempts: " + getRemainingAttempts() + ANSI_RESET);
        System.out.println("You can use the numbers 0-9 and operators such as: + - * / =");
        System.out.println("Please enter your guess (7 characters including numbers and operators)");
    }

    private static void displayErrorMsg(String msg){
        final String ANSI_RED = "\033[31m";
        final String ANSI_RESET = "\033[0m";
        System.out.println("_________________________________________");
        System.out.println(ANSI_RED+msg+ANSI_RESET);
        System.out.println("_________________________________________");
    }

    private static void showGameEndMessage(){
        if(isGameWon()){
            // red color font
            final String ANSI_GREEN = "\033[32m";
            final String ANSI_RESET = "\033[0m";
            System.out.println(ANSI_GREEN + "-----Congratulations! You've guessed the equation correctly.-----" + ANSI_RESET);
        } else if(isGameOver()) {
            final String ANSI_GREEN = "\033[32m";
            final String ANSI_RESET = "\033[0m";
            System.out.println(ANSI_GREEN + "-----Game Over! You've run out of attempts.-----" + ANSI_RESET);
        }
    }

    // Update the number/operator based on the matching status of each character
    private static void updateViewWithMatchResults(int[] matchResults, StringBuilder currentGuess) {
        assert currentGuess != null : "currentGuess is null";
        assert matchResults != null : "matchResults is null";
        System.out.println("");
        System.out.println("The input does not match, the matching result is as follows:");
        for (int i = 0; i < currentGuess.length(); i++) {
            char c = currentGuess.charAt(i);
            switch (matchResults[i]) {
                case 0:  // Correct position
                    System.out.print("\033[32m" + c + "\033[0m");  // Green color
                    break;
                case 1:  // Wrong position
                    System.out.print("\033[93m" + c + "\033[0m");  // Yellow color
                    break;
                case 2:  // Incorrect
                    System.out.print("\033[37m" + c + "\033[0m");  // Grey color
                    break;
            }
            System.out.print(" ");
        }
        System.out.println("");
    }

    private static void startNewGame() {
        model.startNewGame();
    }

    private static boolean isGameOver() {
        return model.isGameOver();
    }

    private static boolean isGameWon() {
        return model.isGameWon();
    }

}
