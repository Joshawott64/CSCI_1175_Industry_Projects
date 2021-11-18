import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class BattleshipOnlineClient extends Application 
	implements BattleshipOnlineConstants {
	// Indicate whether the player has the turn
	private boolean myTurn = false;
	
	// Indicate whether the player is ready
	private boolean isReady = false;
	
	// Create and initialize cells
	private PlayerCell[][] playerGrid = new PlayerCell[10][10];
	private EnemyCell[][] enemyGrid = new EnemyCell[10][10];
	
	// Create grid panes
	GridPane playerGridPane;
	GridPane enemyGridPane;
	
	// Create and initialize a title label
	private Label lblTitle = new Label();
	
	// Create and initialize a log text area
	private TextArea taLog = new TextArea();
	
	// Indicate selected row and column by the current move
	private int rowSelected;
	private int columnSelected;
	
	// Input and output streams from/to server
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	// Continue to play?
	private boolean continueToPlay = true;
	
	// Wait for the player to fire on a cell
	private boolean waiting = true;
	
	// Host name or ip
	private String host = "localhost";
	
	// Create and initialize ships
	Ship carrier = new Ship(5, false, "Carrier", new Rectangle(10, 10));
	Ship battleship = new Ship(4, false, "Battleship", new Rectangle(10, 10));
	Ship destroyer = new Ship(3, false, "Destroyer", new Rectangle(10, 10));
	Ship submarine = new Ship(3, false, "Submarine", new Rectangle(10, 10));
	Ship patrolBoat = new Ship(2, false, "Patrol Boat", new Rectangle(10, 10));
	
	// Create ArrayList to store ships for prep phase
	ArrayList<Ship> shipsToBePlaced = new ArrayList<>();
	
	// Create ArrayList for store ships that haven't been sunk
	ArrayList<Ship> shipsToBeSunk = new ArrayList<>();
	
	// Selected ship to place on grid
	Ship selectedShip = null;
	
	// VBox for holding ship selection buttons
	VBox shipBox = new VBox(20);
	RadioButton rbCarrier = new RadioButton();
	RadioButton rbBattleship = new RadioButton();
	RadioButton rbDestroyer = new RadioButton();
	RadioButton rbSubmarine = new RadioButton();
	RadioButton rbPatrol = new RadioButton();
	
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
		/* Begin prep stage */
		
		// GridPanes to hold cells
		playerGridPane = new GridPane();
		playerGridPane.setAlignment(Pos.CENTER);
		Label lblPlayerGrid = new Label("Your Grid", playerGridPane);
		lblPlayerGrid.setContentDisplay(ContentDisplay.BOTTOM);
		enemyGridPane = new GridPane(); // ! Temporary, final will read from server !
		enemyGridPane.setAlignment(Pos.CENTER);
		Label lblEnemyGrid = new Label("Enemy Grid", enemyGridPane);
		lblEnemyGrid.setContentDisplay(ContentDisplay.BOTTOM);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				playerGridPane.add(playerGrid[i][j] = new PlayerCell(i, j), j, i);
				enemyGridPane.add(enemyGrid[i][j] = new EnemyCell(i, j), j, i);
			}
		}
		// Set grid constraints
		playerGridPane.setGridLinesVisible(true);
		enemyGridPane.setGridLinesVisible(true);
		for (int i = 0; i < 10; i++) {
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPercentWidth(10);
			playerGridPane.getColumnConstraints().add(colConst);
			enemyGridPane.getColumnConstraints().add(colConst);
		}
		for (int i = 0; i < 10; i++) {
			RowConstraints rowConst = new RowConstraints();
			rowConst.setPercentHeight(10);
			playerGridPane.getRowConstraints().add(rowConst);
			enemyGridPane.getRowConstraints().add(rowConst);
		}
		
		// Add ships to ArrayLists
		shipsToBePlaced.add(carrier);
		shipsToBePlaced.add(battleship);
		shipsToBePlaced.add(destroyer);
		shipsToBePlaced.add(submarine);
		shipsToBePlaced.add(patrolBoat);
		
		shipsToBeSunk.add(carrier);
		shipsToBeSunk.add(battleship);
		shipsToBeSunk.add(destroyer);
		shipsToBeSunk.add(submarine);
		shipsToBeSunk.add(patrolBoat);
				
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
		
		// Create SplitPane
		SplitPane playerSplitPane = new SplitPane();
		playerSplitPane.setDividerPositions(0.7);
		playerSplitPane.setOrientation(Orientation.HORIZONTAL);
		playerSplitPane.getItems().addAll(playerGridPane, stackPane);
		
		HBox bottomBox = new HBox(15);
		Button btReady = new Button("Ready");
		Button btQuit = new Button("Quit");
		bottomBox.getChildren().addAll(btReady, btQuit);
		bottomBox.setAlignment(Pos.TOP_CENTER);
		
		// Create ScrollPane & TextArea
		TextArea taLog = new TextArea();
		taLog.setEditable(false);
		taLog.setPrefColumnCount(20);
		taLog.setPrefRowCount(5);
		ScrollPane logPane = new ScrollPane(taLog);
		Label lblLog = new Label("Match log", taLog);
		lblLog.setContentDisplay(ContentDisplay.BOTTOM);
		
		
		// Create BorderPane
		BorderPane page = new BorderPane();
		HBox titleBox = new HBox();
		titleBox.getChildren().add(lblTitle);
		titleBox.setAlignment(Pos.CENTER);
		lblTitle.setText("Preparation Phase");
		lblTitle.setAlignment(Pos.BASELINE_CENTER);
		page.setCenter(playerSplitPane);
		page.setTop(titleBox);
		page.setBottom(bottomBox);
		page.setLeft(logPane);
		
		btReady.setOnAction(e -> {
			if (shipsToBePlaced.isEmpty()) {
				pageTransition(playerSplitPane, enemyGridPane, stackPane, 
						bottomBox, btReady, lblTitle, isReady);
			}
			else {
				taLog.appendText("Place down all ships before readying up \n");
			}
		});
		btQuit.setOnAction(e -> continueToPlay = false);
		
		/* End prep stage */
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(page, 1280, 720);
		primaryStage.setTitle("Battleship Online");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Connect to the server
		//connectToServer();
	}
	
	private void connectToServer() {
		try {
			// Create a socket to connect to the server
			Socket socket = new Socket(host, 8000);
			
			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());
			
			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		new Thread(() -> {
			try {
				// Get notification from the server
				int player = fromServer.readInt();
				
				// Am I player 1 or 2?
				if (player == PLAYER1) {
					Platform.runLater(() -> {
						lblTitle.setText("Player 1");
						taLog.appendText("Player 1 joined \n");
						taLog.appendText("Waiting for player 2 to join \n");
					});
					
					// Receive startup notification from the server
					fromServer.readInt(); // Ignored
					
					if (isReady == true) {
						Platform.runLater(() -> {
							taLog.appendText("Player 1 is ready \n");
						});
						sendPlayerShips(); // Send ship coordinates to server
					}
					
					// The other player has joined
					Platform.runLater(() -> 
						taLog.appendText("Player 2 has joined. You go first"));
					
					waitForPlayerAction();
					receiveEnemyShips(); // Receive enemy ships from server
						
					// It is my turn
					myTurn = true;
				}
				else if (player == PLAYER2) {
					Platform.runLater(() -> {
						lblTitle.setText("Player 2");
						taLog.appendText("Waiting for Player 1 to go");
					});
					
					if (isReady == true) {
						Platform.runLater(() -> {
							taLog.appendText("Player 2 is ready \n");
						});
						sendPlayerShips(); // Send ship coordinates to server
					}
					
					waitForPlayerAction();
					receiveEnemyShips(); // Receive enemy ships from server
				}
				
				// Continue to play
				while (continueToPlay) {
					if (player == PLAYER1) {
						waitForPlayerAction(); // Wait for player 1 to move
						sendMove(); // Send the move to the server
						receiveInfoFromServer(); // Receive info from the server
					}
					else if (player == PLAYER2) {
						receiveInfoFromServer(); // Receive info from the server
						waitForPlayerAction(); // Wait for player 2 to move
						sendMove(); // Send player 2's move to the server
					}
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}).start();
	}
	
	/** Wait for the player to mark a cell */
	private void waitForPlayerAction() throws InterruptedException {
		while (waiting) {
			Thread.sleep(100);
		}
		
		waiting = true;
	}
	
	/** Send this player's move to the server */
	private void sendMove() throws IOException {
		toServer.writeInt(rowSelected); // Send the selected row
		toServer.writeInt(columnSelected); // Send the selected column
		Platform.runLater(() -> enemyGrid[rowSelected][columnSelected].repaint());
	}
	
	/** Receive info from the server */
	private void receiveInfoFromServer() throws IOException {
		// Receive game status
		int status = fromServer.readInt();
		
		if (status == PLAYER1_WON) {
			// Stop playing
			continueToPlay = false;
			Platform.runLater(() -> taLog.appendText("Player 1 won! \n"));
		}
		else if (status == PLAYER2_WON) {
			// Stop playing
			continueToPlay = false;
			Platform.runLater(() -> taLog.appendText("Player 2 won! \n"));
		}
		else {
			receiveMove();
			Platform.runLater(() -> taLog.appendText("My turn"));
			myTurn = true; // It is my turn
		}
	}
	
	private void receiveMove() throws IOException {
		// Get the other player's move
		int row = fromServer.readInt();
		int column = fromServer.readInt();
		Platform.runLater(() -> playerGrid[row][column].repaint()); // ! TEST THIS !
	}
	
	private void sendPlayerShips() throws IOException {
		toServer.writeInt(GridPane.getColumnIndex(carrier.getRectangle()));
		toServer.writeInt(GridPane.getRowIndex(carrier.getRectangle()));
		toServer.writeInt(GridPane.getColumnIndex(battleship.getRectangle()));
		toServer.writeInt(GridPane.getRowIndex(battleship.getRectangle()));
		toServer.writeInt(GridPane.getColumnIndex(destroyer.getRectangle()));
		toServer.writeInt(GridPane.getRowIndex(destroyer.getRectangle()));
		toServer.writeInt(GridPane.getColumnIndex(submarine.getRectangle()));
		toServer.writeInt(GridPane.getRowIndex(submarine.getRectangle()));
		toServer.writeInt(GridPane.getColumnIndex(patrolBoat.getRectangle()));
		toServer.writeInt(GridPane.getRowIndex(patrolBoat.getRectangle()));
	}
	
	private void receiveEnemyShips() throws IOException {
		// Add carrier to enemyGridPane
		int carrierColumn = fromServer.readInt();
		int carrierRow = fromServer.readInt();
		Rectangle enemyCarrier = shipsToBeSunk.get(0).getRectangle();
		enemyGridPane.add(enemyCarrier, carrierColumn, carrierRow);
		
		// Add battleship to enemyGridPane
		int battleshipColumn = fromServer.readInt();
		int battleshipRow = fromServer.readInt();
		Rectangle enemyBattleship = shipsToBeSunk.get(1).getRectangle();
		enemyGridPane.add(enemyBattleship, battleshipColumn, battleshipRow);
		
		// Add destroyer to enemyGridPane
		int destroyerColumn = fromServer.readInt();
		int destroyerRow = fromServer.readInt();
		Rectangle enemyDestroyer = shipsToBeSunk.get(2).getRectangle();
		enemyGridPane.add(enemyDestroyer, destroyerColumn, destroyerRow);
		
		// Add submarine to enemyGridPane
		int submarineColumn = fromServer.readInt();
		int submarineRow = fromServer.readInt();
		Rectangle enemySubmarine = shipsToBeSunk.get(3).getRectangle();
		enemyGridPane.add(enemySubmarine, submarineColumn, submarineRow);
		
		// Add patrolBoat to enemyGridPane
		int patrolBoatColumn = fromServer.readInt();
		int patrolBoatRow = fromServer.readInt();
		Rectangle enemyPatrolBoat = shipsToBeSunk.get(4).getRectangle();
		enemyGridPane.add(enemyPatrolBoat, patrolBoatColumn, patrolBoatRow);
	}
	
	// An inner class for the player's cell
	public class PlayerCell extends Pane {
		// Indicate the row and column of this cell in the board
		private int row;
		private int column;
		
		// Ship used for this cell
		private Ship ship = null;
		
		public PlayerCell(int row, int column) {
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
				Rectangle carrierRectangle = carrier.getRectangle();
				this.getChildren().add(carrierRectangle);
				GridPane.setHalignment(carrierRectangle, HPos.CENTER);
				GridPane.setValignment(carrierRectangle, VPos.CENTER);
				shipBox.getChildren().remove(rbCarrier);
				shipsToBePlaced.remove(carrier);
				selectedShip = null;
			}
			else if (ship == battleship) {
				this.getChildren().add(battleship.getRectangle());
				shipBox.getChildren().remove(rbBattleship);
				shipsToBePlaced.remove(battleship);
				selectedShip = null;
			}
			else if (ship == destroyer) {
				this.getChildren().add(destroyer.getRectangle());
				shipBox.getChildren().remove(rbDestroyer);
				shipsToBePlaced.remove(destroyer);
				selectedShip = null;
			}
			else if (ship == submarine) {
				this.getChildren().add(submarine.getRectangle());
				shipBox.getChildren().remove(rbSubmarine);
				shipsToBePlaced.remove(submarine);
				selectedShip = null;
			}
			else if (ship == patrolBoat) {
				this.getChildren().add(patrolBoat.getRectangle());
				shipBox.getChildren().remove(rbPatrol);
				shipsToBePlaced.remove(patrolBoat);
				selectedShip = null;
			}
		}
		/* Handle a mouse click event */
		private void handleMouseClick() {
			// Player placing ships on grid
			if (isReady == false) {
				// If cell is not occupied
				if (ship == null) {
					setShip(selectedShip);
				}
			}
			else {
				// Enemy firing on grid
				if (ship == null) {
					this.getChildren().add(new Text("MISS!"));
				}
				else {
					this.getChildren().add(new Text("SUNK!!!"));
					shipsToBeSunk.remove(getShip());
				}
			}
		}
	}
	
	// An inner class for the enemy's cell
	public class EnemyCell extends Pane {
		// Indicate the row and column of this cell in the board
		private int row;
		private int column;
				
		// Ship used for this cell
		private Ship ship = null;
				
		public EnemyCell(int row, int column) {
			this.row = row;
			this.column = column;
			this.setOnMouseClicked(e -> handleMouseClick());
		}
		
		// Return ship
		public Ship getShip() {
			return ship;
		}
		
		protected void repaint() {
			if (ship == null) {
				this.getChildren().add(new Text("MISS!"));
			}
			else {
				this.getChildren().add(new Text("SUNK!!!"));
				shipsToBeSunk.remove(getShip());
			}
		}
		/* Handle a mouse click event */
		private void handleMouseClick() {
			ContextMenu contextMenu = new ContextMenu();
			MenuItem menuItemFire = new MenuItem("Fire");
			MenuItem menuItemCancel = new MenuItem("Cancel");
			contextMenu.getItems().add(menuItemFire);
			contextMenu.getItems().add(menuItemCancel);
			contextMenu.show(this, 100, 400);
			
			menuItemFire.setOnAction(e -> repaint());
			menuItemCancel.setOnAction(e -> contextMenu.hide());
		}
	}
	
	/* Handle 'ready' button pressed */
	public void pageTransition(SplitPane playerSplitPane, GridPane enemyGridPane, 
			StackPane stackPane, HBox box, Button button, Label title, boolean isReady) {
		// Replace stackPane with enemyGridPane in playerSplitPane
		playerSplitPane.getItems().remove(stackPane);
		playerSplitPane.getItems().add(enemyGridPane);
		
		// Remove button from box
		box.getChildren().remove(button);
		
		// Change page title
		title.setText("Battle Phase");
		
		// Indicate that the player is ready
		isReady = true;
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
	
	// Main method
	public static void main(String[] args) {
		launch(args);
	}
}
