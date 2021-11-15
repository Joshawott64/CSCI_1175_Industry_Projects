import java.util.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class BattleshipOnlineClient extends Application {
	// Create ships
	Ship carrier = new Ship(5, false, "Carrier");
	Ship battleship = new Ship(4, false, "Battleship");
	Ship destroyer = new Ship(3, false, "Destroyer");
	Ship submarine = new Ship(3, false, "Submarine");
	Ship patrolBoat = new Ship(2, false, "Patrol Boat");
	
	// Selected ship on preparation menu
	Ship selectedShip = null;
	
	// VBox for holding ship selection buttons on preparation menu
	VBox shipBox = new VBox(20);
	RadioButton rbCarrier = new RadioButton();
	RadioButton rbBattleship = new RadioButton();
	RadioButton rbDestroyer = new RadioButton();
	RadioButton rbSubmarine = new RadioButton();
	RadioButton rbPatrol = new RadioButton();
	
	// Create and initialize cells
	private Cell[][] grid = new Cell[10][10];
	
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
		/* Begin preparation phase */
		SplitPane splitPane = new SplitPane();
		splitPane.setDividerPositions(0.7);
		splitPane.setOrientation(Orientation.HORIZONTAL);
		// Pane to hold cell
		GridPane playerGridPane = new GridPane();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				playerGridPane.add(grid[i][j] = new Cell(i, j), j, i);
			}
		}
		// Set grid constraints
		playerGridPane.setGridLinesVisible(true);
		for (int i = 0; i < 10; i++) {
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPercentWidth(10);
			playerGridPane.getColumnConstraints().add(colConst);
		}
		for (int i = 0; i < 10; i++) {
			RowConstraints rowConst = new RowConstraints();
			rowConst.setPercentHeight(10);
			playerGridPane.getRowConstraints().add(rowConst);
		}
		
		Label lbPlayerGrid = new Label("Your Grid", playerGridPane);
		lbPlayerGrid.setContentDisplay(ContentDisplay.BOTTOM);
				
		// Add ships to splitpane
		StackPane stackPane = new StackPane();
		HBox carrierBox = new HBox();
		rbCarrier = shipButton(carrierBox, carrier);
		HBox battleshipBox = new HBox();
		rbBattleship = shipButton(battleshipBox, battleship);
		HBox destroyerBox = new HBox();
		rbDestroyer = shipButton(destroyerBox, destroyer);
		HBox submarineBox = new HBox();
		rbSubmarine = shipButton(submarineBox, submarine);
		HBox patrolBox = new HBox();
		rbPatrol = shipButton(patrolBox, patrolBoat);
		shipBox.getChildren().addAll(rbCarrier, rbBattleship, 
				rbDestroyer, rbSubmarine, rbPatrol);
		
		ToggleGroup group = new ToggleGroup();
		rbCarrier.setToggleGroup(group);
		rbBattleship.setToggleGroup(group);
		rbDestroyer.setToggleGroup(group);
		rbSubmarine.setToggleGroup(group);
		rbPatrol.setToggleGroup(group);
		stackPane.getChildren().add(shipBox);
		shipBox.setAlignment(Pos.CENTER);
		
		rbCarrier.setOnAction(e -> selectedShip = carrier);
		rbBattleship.setOnAction(e -> selectedShip = battleship);
		rbDestroyer.setOnAction(e -> selectedShip = destroyer);
		rbSubmarine.setOnAction(e -> selectedShip = submarine);
		rbPatrol.setOnAction(e -> selectedShip = patrolBoat);
		
		HBox bottomBox = new HBox(20);
		Button btReady = new Button("Ready");
		Button btQuit = new Button("Quit");
		bottomBox.getChildren().addAll(btReady, btQuit);
		bottomBox.setAlignment(Pos.TOP_CENTER);
		
		BorderPane page = new BorderPane();
		Label pageTitle = new Label("Preparation Phase");
		pageTitle.setAlignment(Pos.BASELINE_CENTER);
		page.setCenter(splitPane);
		page.setTop(pageTitle);
		page.setBottom(bottomBox);
		
		splitPane.getItems().addAll(playerGridPane, stackPane);
		/* End preparation phase */
		
		// Create scene
		Scene scene = new Scene(page, 1280, 720);
		primaryStage.setTitle("Battleship Online");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// Creates and returns a button that represents a ship
	public RadioButton shipButton(HBox box, Ship ship) {
		for (int i = 0; i < ship.getLength(); i++) {
			box.getChildren().add(new Rectangle(30, 30));
			box.setAlignment(Pos.CENTER);
		}
		RadioButton rbNew = new RadioButton(ship.getName());
		box.getChildren().add(rbNew);
		return rbNew;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	// An inner class for a cell
	public class Cell extends Pane {
		// Indicate the row and column of this cell in the board
		private int row;
		private int column;
		
		// Ship used for this cell
		private Ship ship = null;
		
		public Cell(int row, int column) {
			this.row = row;
			this.column = column;
			this.setOnMouseClicked(e -> handleMouseClick());
		}
		
		// Return ship
		public Ship getShip() {
			return ship;
		}
		
		// Set a new ship
		public void setShip(Ship newShip) {
			ship = newShip;
			repaint();
		}
		
		protected void repaint() {
			if (ship == carrier) {
				this.getChildren().add(new Text("Carrier"));
				shipBox.getChildren().remove(rbCarrier);
				selectedShip = null;
			}
			else if (ship == battleship) {
				this.getChildren().add(new Text("Battleship"));
				shipBox.getChildren().remove(rbBattleship);
				selectedShip = null;
			}
			else if (ship == destroyer) {
				this.getChildren().add(new Text("Destroyer"));
				shipBox.getChildren().remove(rbDestroyer);
				selectedShip = null;
			}
			else if (ship == submarine) {
				this.getChildren().add(new Text("Submarine"));
				shipBox.getChildren().remove(rbSubmarine);
				selectedShip = null;
			}
			else if (ship == patrolBoat) {
				this.getChildren().add(new Text("Patrol Boat"));
				shipBox.getChildren().remove(rbPatrol);
				selectedShip = null;
			}
		}
		/* Handle a mouse click event */
		private void handleMouseClick() {
			// If cell is not occupied
			if (ship == null) {
				setShip(selectedShip);
			}
		}
	}
}
