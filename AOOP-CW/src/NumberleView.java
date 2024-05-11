// NumberleView.java
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
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
    // label of remaining attempts
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");
    private final JLabel[][] gridLabels = new JLabel[6][7]; // grid
    private final Map<Character, JButton> buttonMap = new HashMap<>(); // store all the button

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
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // input panel
        JPanel inputPanel = createInputPanel();
        frame.add(inputPanel, BorderLayout.SOUTH);

        // keyboard panel (number and operator)
        JPanel keyboardPanel = createKeyboardPanel();
        frame.add(keyboardPanel, BorderLayout.CENTER);

        // grid panel
        JPanel gridPanel = createGridPanel();
        frame.add(gridPanel, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private JPanel createNumberPanel() {
        JPanel numberPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        numberPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        numberPanel.setBackground(Color.WHITE);
        for (int i = 0; i < 10; i++) {
            char numberChar = Integer.toString(i).charAt(0);
            JButton button = new JButton(String.valueOf(numberChar));
            setupButtonStyle(button);
            button.addActionListener(e -> inputTextField.setText(inputTextField.getText() + button.getText()));
            buttonMap.put(numberChar, button);  // Store button by character
            numberPanel.add(button);
        }
        return numberPanel;
    }

    private JPanel createOperatorPanel() {
        JPanel operatorPanel = new JPanel(new GridLayout(1, 6, 5, 5));
        operatorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        operatorPanel.setBackground(Color.WHITE);
        String[] operators = {"+", "-", "*", "/", "=", "Del"};
        for (String op : operators) {
            JButton operatorButton = new JButton(op);
            setupButtonStyle(operatorButton);
            operatorButton.addActionListener(e -> {
                if ("Del".equals(op) && inputTextField.getText().length() > 0) {
                    inputTextField.setText(inputTextField.getText().substring(0, inputTextField.getText().length() - 1));
                } else if (!"Del".equals(op)) {
                    inputTextField.setText(inputTextField.getText() + operatorButton.getText());
                }
            });
            buttonMap.put(op.charAt(0), operatorButton);  // Store operator buttons by character
            operatorPanel.add(operatorButton);
        }
        return operatorPanel;
    }

    private JPanel createInputPanel() {
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
        setupButtonStyle(submitButton);
        submitButton.addActionListener(e -> {
            controller.processInput(inputTextField.getText());
            // inputTextField.setText("");
        });
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(submitButton);

        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        attemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(attemptsLabel);

        return inputPanel;
    }

    private JPanel createKeyboardPanel() {
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        keyboardPanel.setBackground(Color.WHITE);
        keyboardPanel.add(createNumberPanel());
        keyboardPanel.add(Box.createVerticalStrut(10));
        keyboardPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        keyboardPanel.add(Box.createVerticalStrut(10));
        keyboardPanel.add(createOperatorPanel());
        return keyboardPanel;
    }

    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(6, 7, 5, 5));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(Color.LIGHT_GRAY);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                gridLabels[i][j] = new JLabel(" ", SwingConstants.CENTER);
                gridLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridLabels[i][j].setOpaque(true);
                gridLabels[i][j].setBackground(Color.WHITE);
                gridPanel.add(gridLabels[i][j]);
            }
        }
        return gridPanel;
    }

    private void setupButtonStyle(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(118, 159, 205));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
//        // hover effect
//        button.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                button.setBackground(Color.WHITE);
//                button.setForeground(new Color(118, 159, 205));
//            }
//            public void mouseExited(java.awt.event.MouseEvent evt) {
//                button.setBackground(new Color(118, 159, 205));
//                button.setForeground(Color.WHITE);
//            }
//        });
    }

    @Override
    // update remaining attempts and grid
    public void update(java.util.Observable o, Object arg) {
        if (o instanceof NumberleModel) {
            attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
            // 其他更新，例如updateViewWithMatchResults
        }
    }

    public void showGameEndMessage() {
        if (controller.isGameWon()) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You've guessed the equation correctly.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        } else if (controller.isGameOver()) {
            JOptionPane.showMessageDialog(frame, "Game Over! You've run out of attempts.", "Game Over", JOptionPane.WARNING_MESSAGE);
        }
        clearInput();
        // 清空grid中所有显示的数据与颜色
        // 将keyboard panel中所有数字和操作符的键盘恢复原样
        controller.startNewGame();
    }

    // Used to update the characters and their colors in the grid at the specified row
    public void updateGridRow(int row, int[] matchResults, StringBuilder currentGuess) {
        if (row < 0 || row >= 6) return;
        for (int i = 0; i < 7; i++) {
            gridLabels[row][i].setText(String.valueOf(currentGuess.charAt(i)));
            // Update the background color of the cell based on the match results
            switch (matchResults[i]) {
                case 0:
                    gridLabels[row][i].setBackground(Color.GREEN); // Correct position
                    break;
                case 1:
                    gridLabels[row][i].setBackground(Color.ORANGE); // Correct character, wrong position
                    break;
                case 2:
                    gridLabels[row][i].setBackground(Color.GRAY); // Incorrect character
                    break;
                default:
                    gridLabels[row][i].setBackground(Color.WHITE); // Default case
                    break;
            }
        }
        clearInput();
    }

    // Update the keyboard based on the matching status of each character
    public void updateViewWithMatchResults(int[] matchResults, StringBuilder currentGuess) {
        // 按照matchResults和currentGuess中的情况修改keyboard中的按钮的状态
        //  其中matchResults和currentGuess都是长度为7
        //  matchResults数组中有3种数值0表示将对应按钮设置为绿色，1表示将对应按钮设置为橙色，2表示将对应的按钮设置为灰色并禁止
        //  currentGuess种是一个长度为7的字符串，其中是一个数学等式（例如1+2*3=7）
        //  其中currentGuess的每个字符对应了matchResults数组的每个元素的值（第一个字符对应数组第一个值）
        //  要求基于数组中的值，补全该方法，然后将对应的按钮设置为对应的颜色与禁用按钮
        //  现在上面的createKeyboardPanel()方法中创建了数字0-9和运算符（+-*/=）等
        assert currentGuess != null : "currentGuess is null";
        assert matchResults != null : "matchResults is null";
        assert currentGuess.length() == 7 : "currentGuess must be 7 characters long";
        assert matchResults.length == 7 : "matchResults must have 7 elements";
        for (int i = 0; i < currentGuess.length(); i++) {
            char c = currentGuess.charAt(i);
            JButton button = buttonMap.get(c);
            if (button != null) {
                switch (matchResults[i]) {
                    case 0:  // Correct position
                        button.setBackground(Color.GREEN);
                        break;
                    case 1:  // Wrong position
                        button.setBackground(Color.ORANGE);
                        break;
                    case 2:  // Incorrect
                        button.setBackground(Color.GRAY);
                        button.setEnabled(false);  // Disable the button if the character is incorrect
                        break;
                }
            }
        }
        int row = 6 - model.getRemainingAttempts()-1;
        updateGridRow(row, matchResults, currentGuess);
    }

    // Clear the contents of the input text field
    public void clearInput(){
        inputTextField.setText("");
    }

    // display Error message
    public void displayError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}