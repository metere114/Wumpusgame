package origin;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

// The GamePersistenceManager class is responsible for saving and loading game states
public class GamePersistenceManager {

    // Saves the game state to an XML file
    // @param gameBoard the game state to save
    // @param fileName the name of the save
    // @return true, if the save was successful, false if not
    public boolean saveGameState(GameBoard gameBoard, String fileName) {
        try {
            JAXBContext context = JAXBContext.newInstance(GameBoard.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            File file = new File(fileName + "_gameState.xml");
            marshaller.marshal(gameBoard, file);
            return true;
        } catch (JAXBException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Loads a game state from an XML file
    // @param gameStateName the name of the game state to load
    // @return the loaded game state, or null if the load failed
    public GameBoard loadGameState(String gameStateName) {
        try {
            JAXBContext context = JAXBContext.newInstance(GameBoard.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            File file = new File(gameStateName + "_gameState.xml");
            GameBoard gameBoard = (GameBoard) unmarshaller.unmarshal(file);

            gameBoard.postLoadInitialization();

            return gameBoard;
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }

    }
    public GameBoard loadGame() {
        // Implement the logic to load the game
        return new GameBoard(); // return the loaded GameBoard
    }
}
