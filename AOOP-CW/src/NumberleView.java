// NumberleView.java
import javax.swing.*;
import java.awt.*;
import java.util.Observer;
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
    // 显示剩余尝试次数的标签
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");

    // 构造函数，初始化视图组件并设置游戏
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();

        // 将当前视图作为观察者添加到模型中，以便接收游戏状态更新
        ((NumberleModel)this.model).addObserver(this);
        // 初始化窗口组件
        initializeFrame();
        // 将视图设置到控制器中，以便在需要时更新视图
        this.controller.setView(this);
        // 根据模型的当前状态更新视图
        update((NumberleModel)this.model, null);
    }

    // 初始化主窗口和所有子组件
    public void initializeFrame() {
        // 设置窗口关闭时退出程序
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口大小
        frame.setSize(600, 200);
        frame.setLayout(new BorderLayout());

        // 创建中心面板，用于放置输入字段和提交按钮
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel());

        // 输入面板，放置输入文本字段和提交按钮
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1));
        inputPanel.add(inputTextField);

        // 提交按钮，用于提交用户猜测的方程
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // 当按钮被点击时，将输入文本发送到控制器处理
            controller.processInput(inputTextField.getText());
            // 清空输入字段，准备下一次输入
            inputTextField.setText("");
        });
        inputPanel.add(submitButton);

        // 设置剩余尝试次数的标签
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        inputPanel.add(attemptsLabel);
        center.add(inputPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.NORTH);

        // 键盘面板，包含数字按钮，用于快速输入
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.X_AXIS));
        keyboardPanel.add(new JPanel());
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(2, 5));
        keyboardPanel.add(numberPanel);

        // 为数字0-9添加按钮，方便用户输入
        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.addActionListener(e -> {
                // 当数字按钮被点击时，将数字添加到输入字段
                inputTextField.setText(inputTextField.getText() + button.getText());
            });
            button.setPreferredSize(new Dimension(50, 50));
            numberPanel.add(button);
        }

        // 添加运算符面板，包含运算符按钮
        JPanel operatorPanel = new JPanel();
        operatorPanel.setLayout(new GridLayout(1, 5)); // 一行五列的格局用于容纳五个运算符
        // 定义运算符数组
        String[] operators = {"+", "-", "*", "/", "="};
        // 循环添加运算符按钮
        for (String op : operators) {
            JButton operatorButton = new JButton(op);
            operatorButton.addActionListener(e -> {
                // 当运算符按钮被点击时，将运算符添加到输入字段
                inputTextField.setText(inputTextField.getText() + operatorButton.getText());
            });
            operatorButton.setPreferredSize(new Dimension(50, 50)); // 设置按钮的大小
            operatorPanel.add(operatorButton);
        }
        // 将运算符面板添加到键盘面板中
        keyboardPanel.add(operatorPanel);

        // 添加布局间隙的空白面板以保持界面整洁
        keyboardPanel.add(new JPanel());

        frame.add(keyboardPanel, BorderLayout.CENTER);

        keyboardPanel.add(new JPanel());

        frame.add(keyboardPanel, BorderLayout.CENTER);
        // 使窗口可见
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

    public void updateViewWithMatchResults(String input) {
        // 实现检查每个字符的匹配状态并更新键盘的逻辑
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}