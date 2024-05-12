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
    public int[] matchInput(char[] inputChars);
    public void setTargetNumber(String newTarget);
}