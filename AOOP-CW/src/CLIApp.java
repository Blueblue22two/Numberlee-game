// CLIAPP.java
import java.util.Scanner;

/*
* ClI version, do not need the view and controller, only use model
* */



public class CLIApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        INumberleModel model = new NumberleModel();
        NumberleController controller = new NumberleController(model);

        controller.startNewGame(); // 初始化游戏
        while (!controller.isGameOver()) {
            System.out.print("Enter your guess: ");
            String input = scanner.nextLine();
            controller.processInput(input);

            // 可以在这里添加逻辑来显示当前猜测的状态，例如使用颜色表示

            if (controller.isGameWon()) {
                System.out.println("Congratulations! You've guessed the equation correctly.");
                break;
            } else if (controller.isGameOver()) {
                System.out.println("Game Over! You've run out of attempts.");
            }
        }
    }
}
