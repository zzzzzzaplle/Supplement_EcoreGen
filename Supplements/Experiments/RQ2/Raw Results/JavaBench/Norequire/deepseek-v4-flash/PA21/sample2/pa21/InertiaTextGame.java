package edu.pa21; // 建议放在一个单独的 engine 或 runner 包下

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Locale;
/**
 * 适配 EMF 版本的 Inertia 游戏引擎
 * 该类不属于 Ecore 模型，仅作为手写代码驱动模型运行
 */
public class InertiaTextGame {

    private static final BufferedReader STDIN_READER = new BufferedReader(new InputStreamReader(System.in));

    private final GameController controller;
    private final GameBoardView view; // 注意：根据你的 Ecore 模型，这里使用 GameBoardView
    private final boolean useUnicodeChars;

    /**
     * 适配 EMF 的构造函数
     * @param gameState 初始游戏状态（由 Serializer 解析生成）
     * @param useUnicodeChars 是否使用 Unicode 字符
     */
    public InertiaTextGame(final GameState gameState, final boolean useUnicodeChars) {
        Objects.requireNonNull(gameState);

        // 核心适配：使用 Factory 创建实例
        this.controller = Pa21Factory.eINSTANCE.createGameController();
        this.controller.setGameState(gameState);

        // 使用你模型中定义的 GameBoardView
        this.view = Pa21Factory.eINSTANCE.createGameBoardView();
        this.view.setGameBoard(gameState.getGameBoard());

        this.useUnicodeChars = useUnicodeChars;
    }

    /**
     * 运行游戏主循环
     */
    public void run() {
        // 注意：如果你在 Ecore 中为 GameBoardView 添加了 hasWon/hasLost，可以直接调用
        // 否则，通常从 GameState 层面判断
        GameState state = controller.getGameState();

        while (!state.hasWon()) {
            // 假设 GameBoardView 有 output(boolean) 方法
            view.output(useUnicodeChars);

            final String userInput = waitUserInput("Enter Your Move (UP/DOWN/LEFT/RIGHT/UNDO/QUIT): ", false);
            if (userInput == null || "quit".startsWith(userInput.toLowerCase(Locale.ENGLISH))) {
                break;
            }

            final Direction direction = parseDirection(userInput);
            if (direction == null) {
                if ("undo".startsWith(userInput.toLowerCase(Locale.ENGLISH))) {
                    final boolean result = controller.processUndo();
                    if (!result) {
                        System.out.println("No more steps to undo!");
                    }
                    continue;
                }

                System.out.println("Invalid choice!");
                waitUserInput("Press [ENTER] to continue...", true);
                continue;
            }

            final MoveResult moveResult = controller.processMove(direction);
            
            // 核心适配：利用 EMF 建立的继承体系进行判断
            if (moveResult instanceof Invalid) {
                System.out.println("Invalid move!");
                waitUserInput("Press [ENTER] to continue...", true);
            } else if (moveResult instanceof Dead) {
                System.out.println("You died!");

                if (state.hasLost()) {
                    break;
                }
                waitUserInput("Press [ENTER] to continue...", true);
            }
        }

        if (state.hasWon()) {
            System.out.println("You win!");
        } else if (state.hasLost()) {
            System.out.println("You lost!");
        }
    }

    /**
     * 解析输入方向（逻辑保持不变）
     */
    private static Direction parseDirection(final String input) {
        Objects.requireNonNull(input);
        if (input.isBlank()) return null;

        final String inputUpperCase = input.toUpperCase(Locale.ENGLISH);
        for (final Direction dir : Direction.VALUES) { // 适配 EMF Enum 的 VALUES 列表
            if (dir.toString().startsWith(inputUpperCase)) {
                return dir;
            }
        }
        return null;
    }

    private static String waitUserInput(final String prompt, boolean newLine) {
        if (newLine) {
            System.out.println(prompt);
        } else {
            System.out.print(prompt);
        }
        try {
            return STDIN_READER.readLine();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}