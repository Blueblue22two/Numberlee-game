// INumberleModel.java

import java.util.List;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6; // Maximum guesses

    void initialize();
    boolean processInput(String input);
    public int[] matchInput(char[] inputChars);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
}