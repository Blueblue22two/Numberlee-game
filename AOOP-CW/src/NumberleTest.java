// Numberle test.java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumberleTest {
    private NumberleController controller;
    private NumberleModel model;
    private NumberleView view;

    @BeforeEach
    void setUp() {
        model = new NumberleModel();
        model.initialize();
        controller = new NumberleController(model);
        view = new NumberleView(model,controller);
    }

    @Test
    void testCorrectGuess() {
        // Set the target number directly for the purpose of testing
        model.setTargetNumber("10+5=15");
        // Set a incorrect guess and check the remaining attempts
        assertTrue(controller.validateInput("2*3+1=7"),"The input is incorrect but valid; it should be processed and then returned false");
        model.processInput("2*3+1=7");
        assertEquals(5, model.getRemainingAttempts(), "Remaining attempts should decrease by one.");

        // correct guess
        model.processInput("10+5=15");
        assertTrue(model.isGameWon(), "The model should indicate the game is won with a correct guess.");
        assertEquals(4, model.getRemainingAttempts(), "Remaining attempts should be 4.");
    }


    @Test
    void testInvalidGuess(){
        model.setTargetNumber("10*2=20");
        // Test an input with invalid character
        assertFalse(controller.validateInput("10q4=4"), "Input with invalid character should not be processed.");

        // Test an input with incorrect length
        assertFalse(controller.validateInput("10*4=4"), "Input with incorrect length should not be processed.");

        // Test an input where the equation sides do not equal
        assertFalse(controller.validateInput("10*2=21"), "Input with unequal equation sides should not be processed.");
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
