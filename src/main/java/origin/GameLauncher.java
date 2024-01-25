package origin;

import origin.DatabaseManager;

import java.util.Scanner;

public class GameLauncher {

    private static final Scanner scanner = new Scanner(System.in);

    private static final GamePersistenceManager persistenceManager = new GamePersistenceManager();

    static final DatabaseManager dbManager = new DatabaseManager();

    public static void main(String[] args) {
        // Set up the database
        setupDatabase();
        // Get the player's name
        String playerName = getPlayerName();
        // Create a new GameMenu instance and display the main menu
        GameMenu gameMenu = new GameMenu(scanner, persistenceManager, dbManager, playerName);
        gameMenu.displayMenu();

        scanner.close();
    }

    // Set up the database: create a new database and tables
    private static void setupDatabase() {
        dbManager.createNewDatabase();
        dbManager.createTables();
    }

    // Get the player's name: if the player does not exist in the database, add them
    private static String getPlayerName() {
        System.out.println("Enter the player's name:");
        String playerName = scanner.nextLine();
        if (!dbManager.playerExists(playerName)) {
            dbManager.insertPlayer(playerName, 0);
        }
        System.out.println("Welcome to the game, " + playerName + "!");
        return playerName;
    }
}
