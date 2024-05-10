// NumberleView.java
import javax.swing.*;
import java.awt.*;
import java.util.Observer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
/*
* 只关心如何呈现controller传递的数据
*
* 视图负责展示游戏的界面给玩家，
* 接收玩家的输入，并根据模型的状态更新显示。
* 它不直接处理游戏逻辑，而是通过与控制器的交互来响应用户的操作。
* */

public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    // main frame
    private final JFrame frame = new JFrame("Numberle");
    // text filed
    private final JTextField inputTextField = new JTextField(3);
    // label of remaining attempts
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");

    // 初始化视图组件并设置游戏
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();

        // 将当前视图作为观察者添加到模型中，以便接收游戏状态更新
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        // 将视图设置到控制器中，以便在需要时更新视图
        this.controller.setView(this);
        // 根据模型的当前状态更新视图
        update((NumberleModel)this.model, null);
    }

    // 初始化主窗口和所有子组件
    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 350);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // 输入和提交面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);

        JLabel instructionLabel = new JLabel("Enter your equation:");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(instructionLabel);

        inputTextField.setMaximumSize(new Dimension(200, 30));
        inputTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(inputTextField);

        // submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setBackground(new Color(118, 159, 205));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));

        // hover
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(Color.WHITE);
                submitButton.setForeground(new Color(118, 159, 205));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(118, 159, 205));
                submitButton.setForeground(Color.WHITE);
            }
        });
        // action listener
        submitButton.addActionListener(e -> {
            controller.processInput(inputTextField.getText());
            inputTextField.setText("");
        });
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(submitButton);

        // label of remaining
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        attemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(attemptsLabel);
        frame.add(inputPanel, BorderLayout.NORTH);

        // Keyboard panel
        JPanel numberPanel = new JPanel();
        GridLayout numberLayout = new GridLayout(2, 5, 5, 5);
        numberPanel.setLayout(numberLayout);
        numberPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 添加外边距
        numberPanel.setBackground(Color.WHITE);

        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.setBackground(new Color(247, 251, 252));
            button.setForeground(Color.gray);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.addActionListener(e -> {
                inputTextField.setText(inputTextField.getText() + button.getText());
            });
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(214, 230, 242));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(247, 251, 252));
                }
            });
            numberPanel.add(button);
        }

        // Operator panel
        JPanel operatorPanel = new JPanel();
        GridLayout operatorLayout = new GridLayout(1, 5, 5, 5);
        operatorPanel.setLayout(operatorLayout);
        operatorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        operatorPanel.setBackground(Color.WHITE);

        String[] operators = {"+", "-", "*", "/", "=", "Del"};
        for (String op : operators) {
            JButton operatorButton = new JButton(op);
            operatorButton.setBackground(new Color(247, 251, 252));
            operatorButton.setForeground(Color.gray);
            operatorButton.setFont(new Font("Arial", Font.BOLD, 14));
            operatorButton.addActionListener(e -> {
                if ("Del".equals(op) && inputTextField.getText().length() > 0) {
                    inputTextField.setText(inputTextField.getText().substring(0, inputTextField.getText().length() - 1));
                } else if (!"Del".equals(op)) {
                    inputTextField.setText(inputTextField.getText() + operatorButton.getText());
                }
            });
            operatorPanel.add(operatorButton);
        }

        // 主键盘面板，包含数字和运算符面板
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        keyboardPanel.setBackground(Color.WHITE);
        keyboardPanel.add(numberPanel);
        keyboardPanel.add(Box.createVerticalStrut(10));

        // 使用分隔线
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(0, 2));
        separator.setBackground(Color.BLACK);
        keyboardPanel.add(separator);
        keyboardPanel.add(Box.createVerticalStrut(10));
        keyboardPanel.add(operatorPanel);

        frame.add(keyboardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
    }

    // 展示游戏状态
    public void showGameEndMessage() {
        if (model.isGameWon()) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You've guessed the equation correctly.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        } else if (model.isGameOver()) {
            JOptionPane.showMessageDialog(frame, "Game Over! You've run out of attempts.", "Game Over", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 新的方法，用于在指定行更新网格中的字符
    public void updateGridRow(int row, StringBuilder currentGuess) {
        if (row < 0 || row >= 6) return; // 确保行索引有效
        for (int i = 0; i < 7; i++) {
            gridLabels[row][i].setText(String.valueOf(currentGuess.charAt(i)));
        }
    }

    // 根据每个字符的匹配状态并更新键盘的逻辑
    public void updateViewWithMatchResults(int[] matchResults, StringBuilder currentGuess) {
        // todo:先获取当前的输入，然后按照情况修改keyboard中的按钮的状态
        //  然后修改输入框中公式的状态
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}