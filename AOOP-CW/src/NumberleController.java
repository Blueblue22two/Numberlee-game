// NumberleController.java
import java.util.Stack;

public class NumberleController {
    private INumberleModel model;
    private NumberleView view;
    public NumberleController(INumberleModel model) {
        this.model = model;
    }
    public void setView(NumberleView view) {
        this.view = view;
    }

    // Verify that the equation entered by the user is valid
    public boolean validateInput(String input){
        if (input.isEmpty()) {
            view.displayError("Input cannot be empty");
            return false;
        }
        if (!input.matches("^[0-9+-/*=()]*$") || input.length() != 7) {
            view.displayError("The Equation is invalid。");
            return false;
        }
        if (!input.contains("=")) {
            view.displayError("Equation must include an equals sign.");
            return false;
        }
        if (getRemainingAttempts() <= 0) {
            view.showGameEndMessage();
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
            view.displayError("The left side is not equal to right side.");
            return false;
        }
        return true;
    }

    public void processInput(String input) {
        if(!validateInput(input))return;
        view.showNewGameButton();
        if (model.processInput(input)) {
            view.showGameEndMessage();
        } else {
            char[] inputChars = input.toCharArray();
            int[] matchResults = model.matchInput(inputChars);
            assert matchResults != null : "matchResults is null";

            view.updateViewWithMatchResults(matchResults, getCurrentGuess());
            if (getRemainingAttempts() <= 0) {
                view.showGameEndMessage();
            }
        }
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }

    public boolean isGameWon() {
        return model.isGameWon();
    }

    public String getTargetWord() {
        return model.getTargetNumber();
    }

    public StringBuilder getCurrentGuess() {
        return model.getCurrentGuess();
    }

    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    public void startNewGame() {
        model.startNewGame();
    }

    // Convert the expression to a character array for processing
    private int evaluateExpression(String expression) {
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
    private boolean hasPrecedence(char op1, char op2) {
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    // Operate
    private int applyOp(char op, int b, int a) {
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
}