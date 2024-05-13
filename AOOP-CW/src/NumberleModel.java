// NumberleModel.java
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Observable;
import java.io.*;
import java.nio.file.*;

public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;

    @Override
    // Initialize the game and set up the equations
    public void initialize() {
        try {
            // Load all equations from the file into a list
            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.dir"), "equations.txt"), StandardCharsets.UTF_8);
            assert lines.size() == 108 : "File should contain 108 lines, but actually contains " + lines.size() + " lines.";

            // Randomly select one equation
            Random rand = new Random();
            targetNumber = lines.get(rand.nextInt(lines.size()));
            // System.out.println("---(Testing) targetNumber:"+targetNumber+"---");
            assert !targetNumber.isEmpty() : "Target equation is empty, unable to select an equation.";
        } catch (IOException e) {
            // Handle possible I/O errors here
            System.err.println("Error reading from equations.txt file: " + e.getMessage());
            e.printStackTrace();
            String targetNumber = "7-2*3=1"; // Default value in case of error
        } catch (AssertionError e) {
            System.err.println("Assertion failed: " + e.getMessage());
            targetNumber = "7-2*3=1"; // Default fallback equation
        }

        currentGuess = new StringBuilder("       ");
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;
        setChanged();
        notifyObservers("New Equation Loaded");
    }

    @Override
    // Check whether the input is equal to the target number
    public boolean processInput(String input) {
        assert getRemainingAttempts() > 0 : "No remaining attempts left";
        assert !input.isEmpty() : "Input string cannot be null";
        assert input.length() == 7 : "Input string must be exactly 7 characters long";
        if (getRemainingAttempts()>0){
            currentGuess = new StringBuilder(input);
            remainingAttempts-=1;
            setChanged();
            notifyObservers("remainingAttempts-1");
            String target = getTargetNumber();
            if(target.equals(input)){
                gameWon=true;
                setChanged();
                notifyObservers("Game Won");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    // Check if the input matches the target character and return an array of the matches
    @Override
    public int[] matchInput(char[] inputChars){
        assert inputChars != null : "Input characters must not be null";
        int[] result = new int[7];
        String target = getTargetNumber();
        char[] targetChars = target.toCharArray();

        // Used to mark whether a character in the target has been matched to prevent repeated matching
        boolean[] matched = new boolean[targetChars.length];

        for (int i = 0; i < inputChars.length; i++) {
            char inputChar = inputChars[i];
            boolean isMatched = false;

            for (int j = 0; j < targetChars.length; j++) {
                if (inputChar == targetChars[j]) {
                    if (i == j) {
                        result[i] = 0; // The characters and positions match correctly
                        matched[j] = true;
                        isMatched = true;
                        break;
                    } else if (!matched[j]) {
                        result[i] = 1; // The character exists, but in the wrong position
                        matched[j] = true;
                        isMatched = true;
                    }
                }
            }
            // The character does not exist
            if (!isMatched) {
                result[i] = 2;
            }
        }
        return result;
    }

    // set the input as target Number (Testing)
    @Override
    public void setTargetNumber(String newTarget) {
        this.targetNumber = newTarget;
    }

}
