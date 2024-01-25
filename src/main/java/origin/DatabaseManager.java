package origin;

import java.sql.*;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:db.db";
    private final Connection connection;

    public DatabaseManager() {
        this.connection = connect();
    }

    // Establishing a connection to the database
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
        return conn;
    }

    // Create a new database
    public void createNewDatabase() {
        if (connection != null) {
            try {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("Driver name: " + meta.getDriverName());
                System.out.println("New database created.");
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create a new database", e);
            }
        }
    }

    // Creating tables in the database
    public void createTables() {
        // SQL statement to create the table
        String sql = "CREATE TABLE IF NOT EXISTS scores ("
                + " name text NOT NULL,"
                + " wins integer DEFAULT 0"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            // Create a table
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    // Add player to database
    public void insertPlayer(String name, int wins) {
        String sql = "INSERT INTO scores(name, wins) VALUES(?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, wins);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert player", e);
        }
    }

    // Checks if the player exists in the database
    public boolean playerExists(String name) {
        String sql = "SELECT count(*) FROM scores WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if player exists", e);
        }
    }

    // Updates the player's win count in the database
    public void updateWinCount(String name) {
        String sql = "UPDATE scores SET wins = wins + 1 WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update win count", e);
        }
    }

    // Lists the highest scores
    public void printHighScores() {
        String sql = "SELECT name, wins FROM scores ORDER BY wins DESC";

        try (Statement stmt  = connection.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getString("name") + "\t" +
                        rs.getInt("wins"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to print high scores", e);
        }
    }

}
