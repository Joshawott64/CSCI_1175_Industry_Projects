import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class BattleshipOnlineClient extends Application 
	implements BattleshipOnlineConstants {
	// Indicate whether the player has the turn
	private boolean myTurn = false;
	
	// Indicate whether the player is ready
	private boolean playerIsReady = false;
	private boolean enemyIsReady = false;
	
	// Create and initialize cells
	private PlayerCell[][] playerGrid = new PlayerCell[10][10];
	private EnemyCell[][] enemyGrid = new EnemyCell[10][10];
	
	// Create grid panes
	GridPane playerGridPane;
	GridPane enemyGridPane;
	
	// Create and initialize a title label
	private Label lblTitle = new Label();
	
	// Create and initialize a log text area
	private TextArea taLog;
	
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
	
	// Create and initialize player ships
	Ship carrier = new Ship(5, false, "Carrier", new Rectangle(10, 10));
	Ship battleship = new Ship(4, false, "Battleship", new Rectangle(10, 10));
	Ship destroyer = new Ship(3, false, "Destroyer", new Rectangle(10, 10));
	Ship submarine = new Ship(3, false, "Submarine", new Rectangle(10, 10));
	Ship patrolBoat = new Ship(2, false, "Patrol Boat", new Rectangle(10, 10));
	
	// Create and initialize enemy ships
	Ship enemyCarrier = new Ship(5, false, "Carrier", new Rectangle(10, 10));
	Ship enemyBattleship = new Ship(4, false, "Battleship", new Rectangle(10, 10));
	Ship enemyDestroyer = new Ship(3, false, "Destroyer", new Rectangle(10, 10));
	Ship enemySubmarine = new Ship(3, false, "Submarine", new Rectangle(10, 10));
	Ship enemyPatrolBoat = new Ship(2, false, "Patrol Boat", new Rectangle(10, 10));
	
	// Create rectangles
	Rectangle carrierRectangle;
	Rectangle battleshipRectangle;
	Rectangle destroyerRectangle;
	Rectangle submarineRectangle;
	Rectangle patrolRectangle;
	
	Rectangle enemyCarrierRectangle;
	Rectangle enemyBattleshipRectangle;
	Rectangle enemyDestroyerRectangle;
	Rectangle enemySubmarineRectangle;
	Rectangle enemyPatrolRectangle;
	
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
		shipsToBePlaced.add(0, carrier);
		shipsToBePlaced.add(1, battleship);
		shipsToBePlaced.add(2, destroyer);
		shipsToBePlaced.add(3, submarine);
		shipsToBePlaced.add(4, patrolBoat);
		
		shipsToBeSunk.add(0, enemyCarrier);
		shipsToBeSunk.add(1, enemyBattleship);
		shipsToBeSunk.add(2, enemyDestroyer);
		shipsToBeSunk.add(3, enemySubmarine);
		shipsToBeSunk.add(4, enemyPatrolBoat);
				
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
		taLog = new TextArea();
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
		lblTitle.setAlignment(Pos.BASELINE_CENTER);
		page.setCenter(playerSplitPane);
		page.setTop(titleBox);
		page.setBottom(bottomBox);
		page.setLeft(logPane);
		
		btReady.setOnAction(e -> {
			if (shipsToBePlaced.isEmpty()) {
				playerIsReady = pageTransition(playerSplitPane, enemyGridPane, stackPane, 
						bottomBox, btReady, lblTitle, playerIsReady);
			}
			else {
				Platform.runLater(() -> 
					taLog.appendText("Place down all ships before readying up \n"));
			}
		});
		btQuit.setOnAction(e -> continueToPlay = false);
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(page, 1280, 720);
		primaryStage.setTitle("Battleship Online");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Connect to the server
		connectToServer();
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
					
					// Sleep until player is ready
					while (playerIsReady != true) {
						Thread.sleep(100);
					}
					toServer.writeBoolean(playerIsReady); // Write to server when player is ready
					Platform.runLater(() -> taLog.appendText("Player 1 is ready \n"));
					
					enemyIsReady = fromServer.readBoolean();
					// Sleep until enemy is ready
					while (enemyIsReady == false) {
						Thread.sleep(100);
						enemyIsReady = fromServer.readBoolean(); // Read enemyIsReady until true
					}
					Platform.runLater(() -> taLog.appendText("Player 2 is ready \n"));
					
					// The other player has joined
					Platform.runLater(() -> 
						taLog.appendText("Player 2 has joined. You go first \n"));
					
					if (playerIsReady == true && enemyIsReady == true) {
						Platform.runLater(() -> {
							taLog.appendText("Both players are ready, sending coordinates \n");
						});
						sendPlayerShips(); // Send ship coordinates to server
						receiveEnemyShips(); // Receive enemy ships from server
					}
					
					// It is my turn
					myTurn = true;
					
				}
				else if (player == PLAYER2) {
					Platform.runLater(() -> {
						lblTitle.setText("Player 2");
						taLog.appendText("Waiting for Player 1 to go \n");
					});
					
					// Sleep until player is ready
					while (playerIsReady != true) {
						Thread.sleep(100);
					}
					toServer.writeBoolean(playerIsReady); // Write to server when player is ready
					Platform.runLater(() -> taLog.appendText("Player 2 is ready \n"));
					
					enemyIsReady = fromServer.readBoolean();
					// Sleep until enemy is ready
					while (enemyIsReady == false) {
						Thread.sleep(100);
						enemyIsReady = fromServer.readBoolean(); // Read enemyIsReady until true
					}
					Platform.runLater(() -> taLog.appendText("Player 1 is ready \n"));
					
					if (playerIsReady == true && enemyIsReady == true) {
						Platform.runLater(() -> {
							taLog.appendText("Both players are ready, sending coordinates \n");
						});
						sendPlayerShips(); // Send ship coordinates to server
						receiveEnemyShips(); // Receive enemy ships from server
					}
				}
				
				// Continue to play
				while (continueToPlay) {
					if (player == PLAYER1) {
						waitForPlayerAction(); // Wait for player 1 to move
						sendMove();
						receiveInfoFromServer(); // Receive info from the server
					}
					else if (player == PLAYER2) {
						receiveInfoFromServer(); // Receive info from the server
						waitForPlayerAction(); // Wait for player 2 to move
						sendMove();
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
			Platform.runLater(() -> taLog.appendText("My turn \n"));
			myTurn = true; // It is my turn
		}
	}
	
	private void receiveMove() throws IOException {
		System.out.println("receiveMove() has been called");
		// Get the other player's move
		int row = fromServer.readInt();
		int column = fromServer.readInt();
		Platform.runLater(() -> playerGrid[row][column].repaint()); // ! TEST THIS !
	}
	
	private void sendPlayerShips() throws IOException {
		toServer.writeInt(GridPane.getRowIndex(carrierRectangle));
		toServer.writeInt(GridPane.getColumnIndex(carrierRectangle));
		toServer.writeInt(GridPane.getRowIndex(battleshipRectangle));
		toServer.writeInt(GridPane.getColumnIndex(battleshipRectangle));
		toServer.writeInt(GridPane.getRowIndex(destroyerRectangle));
		toServer.writeInt(GridPane.getColumnIndex(destroyerRectangle));
		toServer.writeInt(GridPane.getRowIndex(submarineRectangle));
		toServer.writeInt(GridPane.getColumnIndex(submarineRectangle));
		toServer.writeInt(GridPane.getRowIndex(patrolRectangle));
		toServer.writeInt(GridPane.getColumnIndex(patrolRectangle));
	}
	
	private void receiveEnemyShips() throws IOException {
		// Add carrier to enemyGridPane
		int carrierRow = fromServer.readInt();
		int carrierColumn = fromServer.readInt();
		enemyCarrierRectangle = enemyCarrier.getRectangle();
		enemyCarrierRectangle.setFill(Color.WHITE);
		Platform.runLater(() -> {
			enemyGrid[carrierRow][carrierColumn].paintEnemyShips(enemyCarrierRectangle);
		});
		
		// Add battleship to enemyGridPane
		int battleshipRow = fromServer.readInt();
		int battleshipColumn = fromServer.readInt();
		enemyBattleshipRectangle = enemyBattleship.getRectangle();
		enemyBattleshipRectangle.setFill(Color.WHITE);
		Platform.runLater(() -> {
			enemyGrid[battleshipRow][battleshipColumn].paintEnemyShips(enemyBattleshipRectangle);
		});
		
		// Add destroyer to enemyGridPane
		int destroyerRow = fromServer.readInt();
		int destroyerColumn = fromServer.readInt();
		enemyDestroyerRectangle = enemyDestroyer.getRectangle();
		enemyDestroyerRectangle.setFill(Color.WHITE);
		Platform.runLater(() -> {
			enemyGrid[destroyerRow][destroyerColumn].paintEnemyShips(enemyDestroyerRectangle);
		});
		
		// Add submarine to enemyGridPane
		int submarineRow = fromServer.readInt();
		int submarineColumn = fromServer.readInt();
		enemySubmarineRectangle = enemySubmarine.getRectangle();
		enemySubmarineRectangle.setFill(Color.WHITE);
		Platform.runLater(() -> {
			enemyGrid[submarineRow][submarineColumn].paintEnemyShips(enemySubmarineRectangle);
		});
		
		// Add patrolBoat to enemyGridPane
		int patrolBoatRow = fromServer.readInt();
		int patrolBoatColumn = fromServer.readInt();
		enemyPatrolRectangle = enemyPatrolBoat.getRectangle();
		enemyPatrolRectangle.setFill(Color.WHITE);
		Platform.runLater(() -> {
			enemyGrid[patrolBoatRow][patrolBoatColumn].paintEnemyShips(enemyPatrolRectangle);
		});
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
				carrierRectangle = carrier.getRectangle();
				this.getChildren().add(carrierRectangle);
				GridPane.setRowIndex(carrierRectangle, row);
				GridPane.setColumnIndex(carrierRectangle, column);
				shipBox.getChildren().remove(rbCarrier);
				shipsToBePlaced.remove(carrier);
				selectedShip = null;
			}
			else if (ship == battleship) {
				battleshipRectangle = battleship.getRectangle();
				this.getChildren().add(battleshipRectangle);
				GridPane.setRowIndex(battleshipRectangle, row);
				GridPane.setColumnIndex(battleshipRectangle, column);
				shipBox.getChildren().remove(rbBattleship);
				shipsToBePlaced.remove(battleship);
				selectedShip = null;
			}
			else if (ship == destroyer) {
				destroyerRectangle = destroyer.getRectangle();
				this.getChildren().add(destroyerRectangle);
				GridPane.setRowIndex(destroyerRectangle, row);
				GridPane.setColumnIndex(destroyerRectangle, column);
				shipBox.getChildren().remove(rbDestroyer);
				shipsToBePlaced.remove(destroyer);
				selectedShip = null;
			}
			else if (ship == submarine) {
				submarineRectangle = submarine.getRectangle();
				this.getChildren().add(submarineRectangle);
				GridPane.setRowIndex(submarineRectangle, row);
				GridPane.setColumnIndex(submarineRectangle, column);
				shipBox.getChildren().remove(rbSubmarine);
				shipsToBePlaced.remove(submarine);
				selectedShip = null;
			}
			else if (ship == patrolBoat) {
				patrolRectangle = patrolBoat.getRectangle();
				this.getChildren().add(patrolRectangle);
				GridPane.setRowIndex(patrolRectangle, row);
				GridPane.setColumnIndex(patrolRectangle, column);
				shipBox.getChildren().remove(rbPatrol);
				shipsToBePlaced.remove(patrolBoat);
				selectedShip = null;
			}
		}
		/* Handle a mouse click event */
		private void handleMouseClick() {
			// Player placing ships on grid
			if (playerIsReady == false) {
				// If cell is not occupied
				if (ship == null) {
					setShip(selectedShip);
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
		
		protected void paintEnemyShips(Rectangle rectangle) {
			this.getChildren().add(rectangle);
		}
		
		protected void repaint() {
			if (this.getChildren().isEmpty()) {
				Marker missMarker = new Marker(row, column, false, new Circle(10));
				this.getChildren().add(missMarker.getCircle());
				Platform.runLater(() -> {
					taLog.appendText("You missed! \n");
				});
			}
			else {
				Marker hitMarker = new Marker(row, column, true, new Circle(10));
				this.getChildren().add(hitMarker.getCircle());
				this.getChildren().remove(ship.getRectangle());
				Platform.runLater(() -> {
					taLog.appendText("You sunk your enemy's " + ship.getName() + "\n");
				});
				shipsToBeSunk.remove(ship);
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
			
			if (this.getChildren().contains(enemyCarrierRectangle)) {
				ship = enemyCarrier;
			}
			else if (this.getChildren().contains(enemyBattleshipRectangle)) {
				ship = enemyBattleship;
			}
			else if (this.getChildren().contains(enemyDestroyerRectangle)) {
				ship = enemyDestroyer;
			}
			else if (this.getChildren().contains(enemySubmarineRectangle)) {
				ship = enemySubmarine;
			}
			else if (this.getChildren().contains(enemyPatrolRectangle)) {
				ship = enemyPatrolBoat;
			}
			else if (this.getChildren().isEmpty()) {
				ship = null;
			}
			else {
				Platform.runLater(() -> {
					taLog.appendText("You have already fired here \n");
				});
			}
			
			menuItemFire.setOnAction(e -> {
				try {
					sendMove();
					repaint();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			menuItemCancel.setOnAction(e -> contextMenu.hide());
		}
	}
	
	// An inner class for hit/miss markers
	public class Marker extends Pane {
		// Indicate row, column,and isHit
		private int row;
		private int column;
		private boolean isHit;
		private Circle circle;
		
		public Marker(int row, int column, boolean isHit, Circle circle) {
			this.row = row;
			this.column = column;
			this.isHit = isHit;
			this.circle = circle;
		}
		
		public Circle getCircle() {
			circle.setStroke(Color.BLACK);
			if (isHit == true) {
				circle.setFill(Color.RED);
			}
			else {
				circle.setFill(Color.WHITE);
			}
			
			return circle;
		}
	}
	
	/* Handle 'ready' button pressed */
	public boolean pageTransition(SplitPane playerSplitPane, GridPane enemyGridPane, 
			StackPane stackPane, HBox box, Button button, Label title, boolean isReady) {
		// Replace stackPane with enemyGridPane in playerSplitPane
		playerSplitPane.getItems().remove(stackPane);
		playerSplitPane.getItems().add(enemyGridPane);
		
		// Remove button from box
		box.getChildren().remove(button);
		
		// Indicate that the player is ready
		return isReady = true;
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
