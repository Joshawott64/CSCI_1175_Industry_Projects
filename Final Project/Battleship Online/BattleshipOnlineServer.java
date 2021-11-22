import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class BattleshipOnlineServer extends Application 
		implements BattleshipOnlineConstants {
	private int sessionNo = 1; // Number a session
	private TextArea taLog;
	
	@Override
	public void start(Stage primaryStage) {
		taLog = new TextArea();
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
		primaryStage.setTitle("BattleshipOnlineServer");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		new Thread(() -> {
			try {
				// Create a server socket
				ServerSocket serverSocket = new ServerSocket(8000);
				Platform.runLater(() -> taLog.appendText(new Date() +
						": Server started at socket 8000\n"));
				
				// Ready to create a session for every two players
				while (true) {
					Platform.runLater(() -> taLog.appendText(new Date() +
							": Wait for players to join session " + sessionNo + '\n'));
					
					// Connect to player 1
					Socket player1 = serverSocket.accept();
					
					Platform.runLater(() -> {
						taLog.appendText(new Date() + ": Player 1 joined session "
								+ sessionNo + '\n');
						taLog.appendText("Player 1's IP address " + 
								player1.getInetAddress().getHostAddress() + '\n');
					});
					
					// Notify that the player is Player 1
					new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);
					
					// Connect to player 2
					Socket player2 = serverSocket.accept();
					
					Platform.runLater(() -> {
						taLog.appendText(new Date() +
								": Player 2 joined session " + sessionNo + '\n');
						taLog.appendText("Player 2's IP address " +
								player2.getInetAddress().getHostAddress() + '\n');
					});
					
					// Notify that the player is Player 2
					new DataOutputStream(
							player2.getOutputStream()).writeInt(PLAYER2);
					
					// Display this session and increment session number
					Platform.runLater(() -> 
						taLog.appendText(new Date() +
								": Start a thread for session " + sessionNo++ + '\n'));
					
					// Launch a new thread for this session of two players
					new Thread(new HandleASession(player1, player2)).start();
				}
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
	
	// Define the thread class for handling a new session for two players
	class HandleASession implements Runnable, BattleshipOnlineConstants {
		private Socket player1;
		private Socket player2;
		
		// Create and initialize cells
		private String[][] player1Grid = new String[10][10];
		private String[][] player2Grid = new String[10][10];
		
		// Create and initialize array lists
		private ArrayList<String> player1RemainingShips = new ArrayList<String>();
		private ArrayList<String> player2RemainingShips = new ArrayList<String>();
		
		private DataInputStream fromPlayer1;
		private DataOutputStream toPlayer1;
		private DataInputStream fromPlayer2;
		private DataOutputStream toPlayer2;
		
		// Continue to play
		private boolean continueToPlay = true;
		
		// Players are ready
		private boolean player1IsReady = false;
		private boolean player2IsReady = false;
		
		// Column and row indexes
		int p1CarrierColumn;
		int p1CarrierRow;
		int p1BattleshipColumn;
		int p1BattleshipRow;
		int p1DestroyerColumn;
		int p1DestroyerRow;
		int p1SubmarineColumn;
		int p1SubmarineRow;
		int p1PatrolColumn;
		int p1PatrolRow;
		
		int p2CarrierColumn;
		int p2CarrierRow;
		int p2BattleshipColumn;
		int p2BattleshipRow;
		int p2DestroyerColumn;
		int p2DestroyerRow;
		int p2SubmarineColumn;
		int p2SubmarineRow;
		int p2PatrolColumn;
		int p2PatrolRow;
		
		/** Construct a thread */
		public HandleASession(Socket player1, Socket player2) {
			this.player1 = player1;
			this.player2 = player2;
			
			// Initialize cells
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					player1Grid[i][j] = "Empty";
					player2Grid[i][j] = "Empty";
				}
			}
			
			// Initialize arraylists
			ArrayList<String> player1RemainingShips = new ArrayList<String>();
			player1RemainingShips.add("Carrier");
			player1RemainingShips.add("Battleship");
			player1RemainingShips.add("Destroyer");
			player1RemainingShips.add("Submarine");
			player1RemainingShips.add("Patrol Boat");
			
			ArrayList<String> player2RemainingShips = new ArrayList<String>();
			player2RemainingShips.add("Carrier");
			player2RemainingShips.add("Battleship");
			player2RemainingShips.add("Destroyer");
			player2RemainingShips.add("Submarine");
			player2RemainingShips.add("Patrol Boat");
		}
		
		/** Implement the run() method for the thread */
		public void run() {
			try {
				// Create data input and output streams
				DataInputStream fromPlayer1 = new DataInputStream(
						player1.getInputStream());
				DataOutputStream toPlayer1 = new DataOutputStream(
						player1.getOutputStream());
				DataInputStream fromPlayer2 = new DataInputStream(
						player2.getInputStream());
				DataOutputStream toPlayer2 = new DataOutputStream(
						player2.getOutputStream());
				
				// Read both players' ready status
				player1IsReady = fromPlayer1.readBoolean();
				if (player1IsReady) {
					toPlayer2.writeBoolean(player1IsReady);
					Platform.runLater(() -> {
						taLog.appendText("Player 1 is ready \n");
					});
				}
				
				player2IsReady = fromPlayer2.readBoolean();
				if (player2IsReady) {
					toPlayer1.writeBoolean(player2IsReady);
					Platform.runLater(() -> {
						taLog.appendText("Player 2 is ready \n");
					});
				}
				
				// Write players' coordinates to each other after both players are ready
				if (player1IsReady && player2IsReady) {
					// From player 1
					p1CarrierRow = fromPlayer1.readInt();
					p1CarrierColumn = fromPlayer1.readInt();
					player1Grid[p1CarrierRow][p1CarrierColumn] = "Carrier";
					p1BattleshipRow = fromPlayer1.readInt();
					p1BattleshipColumn = fromPlayer1.readInt();
					player1Grid[p1BattleshipRow][p1BattleshipColumn] = "Battleship";
					p1DestroyerRow = fromPlayer1.readInt();
					p1DestroyerColumn = fromPlayer1.readInt();
					player1Grid[p1DestroyerRow][p1DestroyerColumn] = "Destroyer";
					p1SubmarineRow = fromPlayer1.readInt();
					p1SubmarineColumn = fromPlayer1.readInt();
					player1Grid[p1SubmarineRow][p1SubmarineColumn] = "Submarine";
					p1PatrolRow = fromPlayer1.readInt();
					p1PatrolColumn = fromPlayer1.readInt();
					player1Grid[p1PatrolRow][p1PatrolColumn] = "Patrol Boat";
					
					// From player 2
					p2CarrierRow = fromPlayer2.readInt();
					p2CarrierColumn = fromPlayer2.readInt();
					player2Grid[p2CarrierRow][p2CarrierColumn] = "Carrier";
					p2BattleshipRow = fromPlayer2.readInt();
					p2BattleshipColumn = fromPlayer2.readInt();
					player2Grid[p2BattleshipRow][p2BattleshipColumn] = "Battleship";
					p2DestroyerRow = fromPlayer2.readInt();
					p2DestroyerColumn = fromPlayer2.readInt();
					player2Grid[p2DestroyerRow][p2DestroyerColumn] = "Destroyer";
					p2SubmarineRow = fromPlayer2.readInt();
					p2SubmarineColumn = fromPlayer2.readInt();
					player2Grid[p2SubmarineRow][p2SubmarineColumn] = "Submarine";
					p2PatrolRow = fromPlayer2.readInt();
					p2PatrolColumn = fromPlayer2.readInt();
					player2Grid[p2PatrolRow][p2PatrolColumn] = "Patrol Boat";
					
					// To player 1
					toPlayer1.writeInt(p2CarrierRow);
					toPlayer1.writeInt(p2CarrierColumn);
					toPlayer1.writeInt(p2BattleshipRow);
					toPlayer1.writeInt(p2BattleshipColumn);
					toPlayer1.writeInt(p2DestroyerRow);
					toPlayer1.writeInt(p2DestroyerColumn);
					toPlayer1.writeInt(p2SubmarineRow);
					toPlayer1.writeInt(p2SubmarineColumn);
					toPlayer1.writeInt(p2PatrolRow);
					toPlayer1.writeInt(p2PatrolColumn);
					
					// To player 2
					toPlayer2.writeInt(p1CarrierRow);
					toPlayer2.writeInt(p1CarrierColumn);
					toPlayer2.writeInt(p1BattleshipRow);
					toPlayer2.writeInt(p1BattleshipColumn);
					toPlayer2.writeInt(p1DestroyerRow);
					toPlayer2.writeInt(p1DestroyerColumn);
					toPlayer2.writeInt(p1SubmarineRow);
					toPlayer2.writeInt(p1SubmarineColumn);
					toPlayer2.writeInt(p1PatrolRow);
					toPlayer2.writeInt(p1PatrolColumn);
				}
				
						
				// Continuously serve the players and report game status
				while (true) {
					// Receive a move from player 1
					int row = fromPlayer1.readInt();
					int column = fromPlayer1.readInt();
					switch (player2Grid[row][column]) {
						case "Carrier":	player2RemainingShips.remove("Carrier");
										player2Grid[row][column] = "Sunk";
										break;
						case "Battleship":	player2RemainingShips.remove("Battleship");
											player2Grid[row][column] = "Sunk";
											break;
						case "Destroyer":	player2RemainingShips.remove("Destroyer");
											player2Grid[row][column] = "Sunk";
											break;
						case "Submarine":	player2RemainingShips.remove("Submarine");
											player2Grid[row][column] = "Sunk";
											break;
						case "Patrol Boat":	player2RemainingShips.remove("Patrol Boat");
											player2Grid[row][column] = "Sunk";
											break;
						case "Empty":	player2Grid[row][column] = "Miss";
						
						// Check if Player 1 wins
						if (player2RemainingShips.isEmpty()) {
							toPlayer1.writeInt(PLAYER1_WON);
							toPlayer2.writeInt(PLAYER1_WON);
							sendMove(toPlayer2, row, column);
							break;
						}
						else {
							// Notify player 2 to take the turn
							toPlayer2.writeInt(CONTINUE);
							
							// Send player 1's selected row and column to player 2
							sendMove(toPlayer2, row, column);
						}
					}
					
					// Receive a move from player 2
					row = fromPlayer2.readInt();
					column = fromPlayer2.readInt();
					switch (player1Grid[row][column]) {
						case "Carrier":	player1RemainingShips.remove("Carrier");
										player1Grid[row][column] = "Sunk";
										break;
						case "Battleship":	player1RemainingShips.remove("Battleship");
											player1Grid[row][column] = "Sunk";
											break;
						case "Destroyer":	player1RemainingShips.remove("Destroyer");
											player1Grid[row][column] = "Sunk";
											break;
						case "Submarine":	player1RemainingShips.remove("Submarine");
											player1Grid[row][column] = "Sunk";
											break;
						case "Patrol Boat":	player1RemainingShips.remove("Patrol Boat");
											player1Grid[row][column] = "Sunk";
											break;
						case "Empty":	player1Grid[row][column] = "Miss";
						
						// Check if Player 2 wins
						if (player1RemainingShips.isEmpty()) {
							toPlayer1.writeInt(PLAYER1_WON);
							toPlayer2.writeInt(PLAYER1_WON);
							sendMove(toPlayer1, row, column);
							break;
						}
						else {
							// Notify player 1 to take the turn
							toPlayer1.writeInt(CONTINUE);
							
							// Send player 2's selected row and column to player 1
							sendMove(toPlayer1, row, column);
						}
					}
				}	
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		
		/** Send the move to other player */
		private void sendMove(DataOutputStream out, int row, int column) 
				throws IOException {
			out.writeInt(row); // Send row index
			out.writeInt(column); // Send column index
		}
		
		/** ! Determine if the cells are all occupied ! */
		
		
		/** Determine which player wins */
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}
