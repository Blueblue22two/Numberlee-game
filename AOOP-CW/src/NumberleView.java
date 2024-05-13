// NumberleView.java
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;


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
    private JButton newGameButton;  // new game button

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();

        // set view to observer
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
    }

    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // input and button panel
        JPanel inputPanel = createInputAndButtomPanel();
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

    private JPanel createInputAndButtomPanel() {
        // input panel
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

        // button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        // Submit button
        JButton submitButton = new JButton("Submit");
        setupButtonStyle(submitButton);
        submitButton.addActionListener(e -> {
            controller.processInput(inputTextField.getText());
        });
        buttonPanel.add(submitButton);

        // New Game button
        newGameButton = new JButton("New Game");
        setupButtonStyle(newGameButton);
        newGameButton.setVisible(false);  // Initially invisible
        newGameButton.addActionListener(e -> showGameEndMessage());
        buttonPanel.add(newGameButton);

        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(buttonPanel);

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
    }

    @Override
    // update remaining attempts and grid
    public void update(java.util.Observable o, Object arg) {
        if (o instanceof NumberleModel) {
            attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        }
    }

    public void showNewGameButton() {
        newGameButton.setVisible(true);
    }

    public void showGameEndMessage() {
        if (controller.isGameWon()) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You've guessed the equation correctly.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        } else if (controller.isGameOver()) {
            JOptionPane.showMessageDialog(frame, "Game Over! You've run out of attempts.", "Game Over", JOptionPane.WARNING_MESSAGE);
        }
        // Clear grid display
        for (JLabel[] row : gridLabels) {
            for (JLabel label : row) {
                label.setText(" ");
                label.setBackground(Color.WHITE);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }
        // Reset keyboard buttons
        for (Map.Entry<Character, JButton> entry : buttonMap.entrySet()) {
            JButton button = entry.getValue();
            button.setBackground(new Color(118, 159, 205));
            button.setEnabled(true); // Enable button if it was disabled
        }
        clearInput();
        newGameButton.setVisible(false);  // invisible
        controller.startNewGame();
    }

    // Used to update the characters and their colors in the grid at the specified row
    public void updateGridRow(int row, int[] matchResults, StringBuilder currentGuess) {
        if (row < 0 || row >= 6) return;
        for (int i = 0; i < 7; i++) {
            gridLabels[row][i].setText(String.valueOf(currentGuess.charAt(i)));
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
                    gridLabels[row][i].setBackground(Color.WHITE);
                    break;
            }
        }
        clearInput();
    }

    // Update the keyboard based on the matching status of each character
    public void updateViewWithMatchResults(int[] matchResults, StringBuilder currentGuess) {
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
                        // button.setEnabled(false);  // Disable the button if the character is incorrect
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

    // Display Error message
    public void displayError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}