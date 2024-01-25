package origin;

import java.util.Scanner;

import static origin.GameLauncher.dbManager;

public class GameSession {
    private GameBoard gameBoard;
    private final String playerName;
    private final Scanner scanner;
    private GamePersistenceManager persistenceManager;

    // A constructor that initializes the game state, the player name, and the input reader
    public GameSession(GameBoard gameBoard, String playerName, GamePersistenceManager persistenceManager) {
        this.gameBoard = gameBoard;
        this.playerName = playerName;
        this.scanner = new Scanner(System.in);
        this.persistenceManager = persistenceManager;
    }

    // Gameplay management
    public void start() {
        System.out.println("Starting " + playerName + "'s game...");

        gameBoard.draw();

        while (!gameBoard.isGameOver()) {
            System.out.println("What would you like to do? (1: straight, 2: turns right, 3: turns left, 4: shoot, 5: pick up gold, 6: give up, 7: save game)");
            String action = scanner.nextLine();

            if (gameBoard.getHero() == null) {
                System.out.println("There is no hero on the board.");
                break;
            }

            processAction(action);

            gameBoard.checkForHazards(gameBoard.getHero().getRow(), gameBoard.getHero().getColumn());
            gameBoard.draw();

            checkGameStatus();
        }

        System.out.println("The game is over. Thanks for playing!");
    }

    // Processing the command entered by the player
    private void processAction(String action) {
        switch (action) {
            case "1":
                moveHero();
                break;
            case "2":
                gameBoard.getHero().turnRight();
                break;
            case "3":
                gameBoard.getHero().turnLeft();
                break;
            case "4":
                gameBoard.getHero().shootArrow();
                break;
            case "5":
                gameBoard.pickUpGoldCommand();
                break;
            case "6":
                System.out.println("Game Over.");
                gameBoard.setGameOver(true);
                break;
            case "7":
                saveGameState();
                break;
            default:
                System.out.println("Invalid command. Try again!");
        }
    }

    // Checks game status
    void checkGameStatus() {
        Hero hero = gameBoard.getHero();
        if (gameBoard.getHero() == null) {
            System.out.println("Error: Hero is not initialized.");
            return;
        }

        if (hero.hasGold() && hero.getRow() == gameBoard.getStartingRow() && hero.getColumn() == gameBoard.getStartingColumn()) {
            System.out.println("Congratulation, " + playerName + "! You have successfully obtained the gold and returned to the starting point.");
            gameBoard.setGameOver(true);
            updatePlayerWinCount();
        } else if (gameBoard.isGameOver()) {
            System.out.println(playerName + ", the game is over!");
        }
    }

    // Updates the player's win count in the database
    private void updatePlayerWinCount() {
        dbManager.updateWinCount(playerName);
    }

    // Save state
    private void saveGameState() {
        if (persistenceManager != null) {
            boolean isSaved = persistenceManager.saveGameState(gameBoard, playerName);
            if (isSaved) {
                System.out.println("Game state saved.");
            } else {
                System.out.println("Failed to save game state.");
            }
        } else {
            System.out.println("Failed to save game state because persistenceManager is null.");
        }
    }

    // Hero movement
    private void moveHero() {
        int currentRow = gameBoard.getHero().getRow();
        int currentCol = gameBoard.getHero().getColumn();

        gameBoard.moveHeroBasedOnDirection();

        gameBoard.draw();
    }


    public void saveGame() {
        // Implement the logic to save the game
        persistenceManager.saveGameState(gameBoard, playerName);
        System.out.println("Game saved successfully.");

    }


    // existing code...

    public void loadGame() {
        // Implement the logic to load the game
        GameBoard loadedGameBoard = persistenceManager.loadGameState("gameStateName");
        if (loadedGameBoard != null) {
            this.gameBoard = loadedGameBoard;
            System.out.println("Game loaded successfully.");
        } else {
            System.out.println("Failed to load the game.");
        }
    }

}

