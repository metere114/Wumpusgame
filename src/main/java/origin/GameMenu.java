package origin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;

public class GameMenu {


    private static Scanner scanner = new Scanner(System.in);
    private static GamePersistenceManager persistenceManager =new GamePersistenceManager();
    private static  DatabaseManager dbManager = new DatabaseManager();
    private final String playerName;

    public GameMenu(Scanner scanner, GamePersistenceManager persistenceManager, DatabaseManager dbManager, String playerName) {
        this.scanner = scanner;
        this.persistenceManager = persistenceManager;
        this.dbManager = dbManager;
        this.playerName = playerName;
    }

    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            exit = processMainMenu();
        }
    }

    private void printMainMenu() {
        System.out.println("\n--- Main menu ---");
        System.out.println("1. New game");
        System.out.println("2. Load game");
        System.out.println("3. Ranking list");
        System.out.println("4. Exit");
    }

    private boolean processMainMenu() {
        printMainMenu();
        int choice = getUserInput("Choose an option:", 1, 4);

        switch (choice) {
            case 1:
                MapEditor mapEditor = new MapEditor();
                mapEditor.setPlayerName(playerName);
                mapEditor.setPersistenceManager(persistenceManager);
                mapEditor.runEditor();
                break;
            case 2:
                GameBoard gameBoard = handleGameLoading();
                if (gameBoard != null) {
                    launchGame(gameBoard, playerName, persistenceManager);
                } else {
                    System.out.println("Failed to load game.");
                }
                break;
            case 3:
                printHighScores();
                break;
            case 4:
                System.out.println("Exiting...");
                return true;
        }
        return false;
    }
    private static GameBoard handleGameLoading() {
        File dir = new File("."); // Jelenlegi könyvtár
        FilenameFilter filter = (dir1, name) -> name.endsWith("_gameState.xml");
        File[] files = dir.listFiles(filter);

        if (files == null || files.length == 0) {
            System.out.println("There are no saves available.");
            return null;
        }

        System.out.println("Saves available:");
        for (int i = 0; i < files.length; i++) {

            System.out.println((i + 1) + ": " + files[i].getName());
        }


        int fileIndex = getUserInput("Choose a save to load (number): ", 1, files.length) - 1;
        if (fileIndex >= 0 && fileIndex < files.length) {
            String filename = files[fileIndex].getName();

            filename = filename.substring(0, filename.indexOf("_gameState.xml"));


            GameBoard loadedGameBoard = persistenceManager.loadGameState(filename);

            if (loadedGameBoard != null) {
                loadedGameBoard.postLoadInitialization();
                loadedGameBoard.draw();
                System.out.println("Game loaded.");
            } else {
                System.out.println("Failed to load game state.");
            }

            return loadedGameBoard;
        } else {
            System.out.println("Invalid file index.");
            return null;
        }
    }
    private static void printHighScores() {
        dbManager.printHighScores();
    }
    private static int getUserInput(String prompt, int min, int max) {
        int input;
        while (true) {
            System.out.print(prompt);
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println("Please enter a number between " + min + " and " + max );
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number!");
            }
        }
    }
    public static void launchGame(GameBoard gameBoard, String playerName, GamePersistenceManager persistenceManager) {
        if (gameBoard == null) {
            System.out.println("Invalid start");
            return;
        }

        System.out.println("Starting " + playerName + "'s game");
        gameBoard.draw(); // Added table drawing
        GameSession session = new GameSession(gameBoard, playerName, persistenceManager);
        session.start();
    }

}
