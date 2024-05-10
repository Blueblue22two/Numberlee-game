// NumberleModel.java
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Observable;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/*
* 只关心用于处理数据逻辑与数据库进行交接
*
* 模型负责游戏的数据和逻辑处理，
* 包括生成目标方程式（这里用targetNumber代表），
* 处理玩家的输入，并跟踪游戏的状态（如剩余尝试次数、是否获胜等）。
* */


public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;

    @Override
    public void initialize() {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        try {
            // Load all equations from the file into a list
            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.dir"), "src/equations.txt"), StandardCharsets.UTF_8);
            assert lines.size() == 108 : "File should contain 108 lines, but actually contains " + lines.size() + " lines.";

            // Randomly select one equation
            Random rand = new Random();
            targetNumber = lines.get(rand.nextInt(lines.size()));
            assert !targetNumber.isEmpty() : "Target equation is empty, unable to select an equation.";
            System.out.println("Target Number:"+targetNumber);
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
        notifyObservers();
    }

    @Override
    // 检查input与目标等式是否相等，返回检查结果
    public boolean processInput(String input) {
        assert getRemainingAttempts() > 0 : "No remaining attempts left";
        if (getRemainingAttempts()>0){
            remainingAttempts-=1;
            // 验证input 与 TargetNumber是否相等，相等则返回true,不相等则返回false
            String target = getTargetNumber();
            if(target.equals(input)){
                gameWon=true;
                return true;
            }
        }
        return false;
    }

    @Override
    // 检测input与target字符的匹配情况，并返回记录匹配情况的数组
    // TODO:检测如果当前字符与target对应的字符相等，则将其在res数组中数值设置为0
    //  若检测到该字符在target中存在一样的字符，但是字符所在位置不同，则res数组中设置为1
    //  若该字符在target字符中不存在，则res数组中数值设置为2
    public int[] matchInput(char[] inputChars){
        assert inputChars != null : "Input characters must not be null";
        int[] result = new int[7];
        String target = getTargetNumber();
        char[] targetChars = target.toCharArray();
        // 用于标记target中的字符是否已匹配，防止重复匹配
        boolean[] matched = new boolean[targetChars.length];

        for (int i = 0; i < inputChars.length; i++) {
            char inputChar = inputChars[i];
            boolean isMatched = false;
            // 检查当前字符与target中的每个字符是否相等
            for (int j = 0; j < targetChars.length; j++) {
                if (inputChar == targetChars[j]) {
                    if (i == j) {
                        result[i] = 0; // 完全匹配，则标记已匹配
                        matched[j] = true;
                        isMatched = true;
                        break;
                    } else if (!matched[j]) {
                        result[i] = 1; // 字符存在，但位置不同
                        matched[j] = true; // 标记为已匹配
                        isMatched = true;
                    }
                }
            }
            // 如果字符在target中完全不存在
            if (!isMatched) {
                result[i] = 2;
            }
        }
        return result;
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
}
