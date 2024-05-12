import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumberleModelTest {

    private NumberleModel model;

    @BeforeEach
    void setUp() {
        model = new NumberleModel();
        model.initialize();  // Preset the target number for consistency in tests
    }

    @Test
    void testCorrectGuess() {
        // Set the target number directly for the purpose of testing
        model.setTargetNumber("10+5=15");
        model.processInput("10+5=15");
        assertTrue(model.isGameWon(), "The model should indicate the game is won with a correct guess.");
        assertEquals(5, model.getRemainingAttempts(), "Remaining attempts should decrease by one.");
    }

    @Test
    void testIncorrectGuess() {
        // Set a known target number and an incorrect guess
        model.setTargetNumber("10*2=20");
        model.processInput("10*2=25");
        assertFalse(model.isGameWon(), "The game should not be won with an incorrect guess.");
        assertTrue(model.getRemainingAttempts() < NumberleModel.MAX_ATTEMPTS, "Attempts should be reduced.");
    }

    @Test
    void testGameOverCondition() {
        model.setTargetNumber("8/4+1=3");
        // Simulate the maximum number of incorrect guesses
        for (int i = 0; i < NumberleModel.MAX_ATTEMPTS; i++) {
            model.processInput("8/4+1=2");  // Incorrect guess
        }
        assertTrue(model.isGameOver(), "The game should end after the maximum number of attempts are used.");
        assertFalse(model.isGameWon(), "The game should not be won if all attempts are incorrect.");
    }
}
