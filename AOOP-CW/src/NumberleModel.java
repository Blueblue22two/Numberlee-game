// NumberleModel.java
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
    private StringBuilder currentGuess; // 记录用户当前猜测准确的进度，可能在view中使用
    private int remainingAttempts;
    private boolean gameWon;

    @Override
    public void initialize() {
        try {
            // Load all equations from the file into a list
            List<String> lines = Files.readAllLines(Paths.get("equations.txt"));
            assert lines.size() == 108 : "File should contain 108 lines, but actually contains " + lines.size() + " lines.";

            // Randomly select one equation
            assert !lines.isEmpty() : "File is empty, unable to select an equation.";
            Random rand = new Random();
            targetNumber = lines.get(rand.nextInt(lines.size()));

        } catch (IOException e) {
            // Handle possible I/O errors here
            System.err.println("Error reading from equations.txt file: " + e.getMessage());
            targetNumber = "7-2*3=1"; // Default
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
    public int[] matchInput(char[] inputChars){
        assert inputChars != null : "Input characters must not be null";
        int[] result = new int[7];
        String target = getTargetNumber();
        char[] targetChars = target.toCharArray();
        boolean[] matched = new boolean[7]; // 跟踪已经完全匹配的字符
        boolean[] counted = new boolean[7]; // 跟踪已经部分匹配的字符

        // step1：检查完全匹配
        for (int i = 0; i < inputChars.length; i++) {
            if (inputChars[i] == targetChars[i]) {
                result[i] = 0; // 完全匹配（绿色）
                matched[i] = true; // 标记已完全匹配（匹配成功）
                counted[i] = true; // 标记为已计算过(已完全匹配和部分匹配)
            } else {
                result[i] = 2; // 假设不匹配（灰色）
            }
        }

        // step2：检查部分匹配
        for (int i = 0; i < inputChars.length; i++) {
            if (result[i] == 0) {
                continue; // 跳过已完全匹配的字符
            }

            for (int j = 0; j < targetChars.length; j++) {
                // 如果该字符没被完全匹配（可能是部分匹配或者不匹配），并且也没被count
                if (!matched[j] && !counted[j] && inputChars[i] == targetChars[j]) {
                    result[i] = 1; // 部分匹配（橙色）
                    counted[j] = true; // 标记为已计算过
                    break;
                }
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
