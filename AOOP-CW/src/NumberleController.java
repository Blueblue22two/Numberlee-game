// NumberleController.java

import java.util.Stack;

/*
* 将 有效的请求 转发给模型，在必要时查询模型以确定请求是否有效，并且还必须启用/禁用功能需求中所述的按钮
* */
public class NumberleController {
    private INumberleModel model;
    private NumberleView view;
    public NumberleController(INumberleModel model) {
        this.model = model;
    }
    public void setView(NumberleView view) {
        this.view = view;
    }

    public void processInput(String input) {
        assert input != null : "Input string cannot be null";
        assert input.length() == 7 : "Input string must be exactly 7 characters long";
        System.out.println("input:"+input);
        // validation
        if (input.isEmpty()) {
            view.displayError("Input cannot be empty");
            return;
        }

        if (!input.matches("^[0-9+-/*=()]*$") || input.length() != 7) {
            view.displayError("The Equation is invalid。");
            return;
        }

        if (!input.contains("=")) {
            view.displayError("Equation must include an equals sign.");
            return;
        }

        if (getRemainingAttempts() <= 0) {
            view.showGameEndMessage();
            return;
        }

        // 检查input的等式是否构成等式，若不构成则在view中显示信息不构成等式
        // 分解等式为左右两部分
        String[] parts = input.split("=");
        assert parts.length == 2 : "Equation must split into exactly two parts by the equals sign.";
        // 处理等式左边的表达式
        int leftResult = evaluateExpression(parts[0].trim());
        // 将等式右边转换为整数
        int rightResult = evaluateExpression(parts[1].trim());
        // 检查等式两边是否相等
        System.out.println("left side="+parts[0].trim());
        System.out.println("right side="+parts[1].trim());
        if (leftResult != rightResult) {
            view.displayError("The left side is not equal to right side.");
            return;
        }

        if (model.processInput(input)) {
            view.showGameEndMessage();
        } else {
            if (getRemainingAttempts() <= 0) {
                view.showGameEndMessage();
                return;
            }
            char[] inputChars = input.toCharArray();
            int[] matchResults = model.matchInput(inputChars);
            assert matchResults != null : "matchResults is null";
            // 调用view的函数 updateViewWithMatchResults
            view.updateViewWithMatchResults(matchResults, getCurrentGuess());
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

    private int evaluateExpression(String expression) {
        // 将表达式转换为字符数组以便处理
        char[] tokens = expression.toCharArray();
        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            // 如果是数字，推入值栈
            if (Character.isDigit(tokens[i])) {
                StringBuilder sb = new StringBuilder();
                // 可能是多位数
                while (i < tokens.length && Character.isDigit(tokens[i])) {
                    sb.append(tokens[i++]);
                }
                values.push(Integer.parseInt(sb.toString()));
                i--; // 因为for循环会自增
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                // 当前操作符和栈顶操作符进行优先级比较
                while (!ops.isEmpty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                // 当前操作符推入栈
                ops.push(tokens[i]);
            }
        }
        // 整个表达式已处理完，按顺序应用剩余操作符
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    }

    // 判断op2的优先级是否大于或等于op1
    private boolean hasPrecedence(char op1, char op2) {
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    // operate
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