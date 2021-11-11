import java.util.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;

public class BattleshipOnlineClient extends Application {
	// Create ships
	Ship carrier = new Ship(5, false, "Carrier");
	Ship battleship = new Ship(4, false, "Battleship");
	Ship destroyer = new Ship(3, false, "Destroyer");
	Ship submarine = new Ship(3, false, "Submarine");
	Ship patrolBoat = new Ship(2, false, "Patrol Boat");
	
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
		/* Begin preparation phase */
		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.HORIZONTAL);
		Grid playerGrid = new Grid();
		playerGrid.getGridConstraints(playerGrid.getColumns(), playerGrid.getRows());
		GridPane playerGridPane = playerGrid.getGridPane();
		
		// Add ships to splitpane
		ArrayList<Ship> allShips = new ArrayList<Ship>();
		allShips.add(carrier);
		allShips.add(battleship);
		allShips.add(destroyer);
		allShips.add(submarine);
		allShips.add(patrolBoat);
		StackPane stackPane = new StackPane();
		VBox shipBox = new VBox(20);
		HBox carrierBox = new HBox();
		Button btCarrier = shipButton(carrierBox, carrier);
		HBox battleshipBox = new HBox();
		Button btBattleship = shipButton(battleshipBox, battleship);
		HBox destroyerBox = new HBox();
		Button btDestroyer = shipButton(destroyerBox, destroyer);
		HBox submarineBox = new HBox();
		Button btSubmarine = shipButton(submarineBox, submarine);
		HBox patrolBox = new HBox();
		Button btPatrol = shipButton(patrolBox, patrolBoat);
		shipBox.getChildren().addAll(btCarrier, btBattleship, 
				btDestroyer, btSubmarine, btPatrol);
		
		stackPane.getChildren().add(shipBox);
		shipBox.setAlignment(Pos.CENTER);
		
		
		splitPane.getItems().addAll(playerGridPane, stackPane);
		/* End preparation phase */
		
		// Create scene
		Scene scene = new Scene(splitPane, 1280, 720);
		primaryStage.setTitle("Battleship Online");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	// Creates and returns a button that represents a ship
	public Button shipButton(HBox box, Ship ship) {
		for (int i = 0; i < ship.getLength(); i++) {
			box.getChildren().add(new Rectangle(30, 30));
			box.setAlignment(Pos.CENTER);
		}
		return new Button(ship.getName() + " (Length: " + ship.getLength() + ")", box);
	}

}
