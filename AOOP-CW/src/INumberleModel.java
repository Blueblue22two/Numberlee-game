// INumberleModel.java

import java.util.List;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6; // Maximum guesses

    void initialize();
    boolean processInput(String input);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
    int[] matchInput(char[] inputChars);
    void setTargetNumber(String newTarget);
}