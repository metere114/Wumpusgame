package origin;

import java.util.Scanner;

public class MapEditor {
    private static final String ACTION_ADD_ELEMENT = "1";
    private static final String ACTION_REMOVE_ELEMENT = "2";
    private static final String ACTION_START_GAME = "3";
    private static final String ACTION_BACK_TO_MAIN_MENU = "4";

    private String playerName;
    private boolean isLoadedGame = false;
    private Scanner scanner;
    private GameBoard gameBoard;
    private GamePersistenceManager persistenceManager;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public MapEditor() {
        this.scanner = new Scanner(System.in);
    }

    public void runEditor() {
        if (!isLoadedGame) {
            int size = getUserInput("Enter the board size (between 6 and 20): ", 6, 20);
            this.gameBoard = new GameBoard(size);
        }

        gameBoard.initializeBoard();
        gameBoard.draw();

        boolean editing = true;
        while (editing) {
            System.out.println("\n--- Map Editor ---");
            System.out.println("1. Add element");
            System.out.println("2. Remove element");
            System.out.println("3. Start game");
            System.out.println("4. Back to main menu");
            System.out.print("Choose an option: ");

            String action = scanner.nextLine();
            switch (action) {
                case ACTION_ADD_ELEMENT:
                    addElement();
                    gameBoard.draw();
                    break;
                case ACTION_REMOVE_ELEMENT:
                    removeElement();
                    gameBoard.draw();
                    break;
                case ACTION_START_GAME:
                    if (gameBoard.isHeroPlaced() && gameBoard.isGoldPlaced()) {
                        GameMenu.launchGame(gameBoard, playerName, persistenceManager);
                        gameBoard.draw();
                        editing = false;
                    } else {
                        System.out.println("To start the game, first place a hero and gold on the board.");
                    }
                    break;
                case ACTION_BACK_TO_MAIN_MENU:
                    editing = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again!");
            }
        }
    }

    void addElement() {
        int row = getUserInput("Enter the row coordinate: ", 1, gameBoard.getSize() - 2);
        int col = getUserInput("Enter the column coordinate: ", 1, gameBoard.getSize() - 2);

        System.out.print("Select an element (WALL, TRAP, WUMPUS, HERO, GOLD): ");
        String elementStr = scanner.nextLine().toUpperCase();
        GameBoard.Tile element;

        switch (elementStr) {
            case "WALL":
                element = GameBoard.Tile.WALL;
                break;
            case "TRAP":
                element = GameBoard.Tile.PIT;
                break;
            case "WUMPUS":
                element = GameBoard.Tile.WUMPUS;
                break;
            case "HERO":
                element = GameBoard.Tile.HERO;
                break;
            case "GOLD":
                element = GameBoard.Tile.GOLD;
                break;
            default:
                System.out.println("Invalid element. Try again!");
                return;
        }

        gameBoard.addElement(row, col, element);
    }

    void removeElement() {
        int row = getUserInput("Enter the row coordinate: ", 1, gameBoard.getSize() - 2);
        int col = getUserInput("Enter the column coordinate: ", 1, gameBoard.getSize() - 2);
        gameBoard.removeElement(row, col);
    }

    int getUserInput(String prompt, int min, int max) {
        int input;
        while (true) {
            System.out.print(prompt);
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println("Please enter a number between " + min + " and " + max + "!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number!");
            }
        }
    }

    public void setPersistenceManager(GamePersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.isLoadedGame = true;
    }
}
