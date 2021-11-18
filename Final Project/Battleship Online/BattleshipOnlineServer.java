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
	
	@Override
	public void start(Stage primaryStage) {
		TextArea taLog = new TextArea();
		
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
						taLog.appendText("Player 1's IP address" + 
								player1.getInetAddress().getHostAddress() + '\n');
					});
					
					// Notify that the player is Player 1
					new DataOutputStream(player1.getOutputStream()).write(PLAYER1);
					
					// Connect to player 2
					Socket player2 = serverSocket.accept();
					
					Platform.runLater(() -> {
						taLog.appendText(new Date() +
								": Player 2 joined session " + sessionNo + '\n');
						taLog.appendText("Player 2's IP address" +
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
		
		// Create and initialize arrays
		private String[] player1RemainingShips = new String[5];
		private String[] player2RemainingShips = new String[5];
		
		private DataInputStream fromPlayer1;
		private DataOutputStream toPlayer1;
		private DataInputStream fromPlayer2;
		private DataOutputStream toPlayer2;
		
		// Continue to play
		private boolean continueToPlay = true;
		
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
			
			// Initialize arrays
			player1RemainingShips[0] = "Carrier";
			player1RemainingShips[1] = "BattleShip";
			player1RemainingShips[2] = "Destroyer";
			player1RemainingShips[3] = "Submarine";
			player1RemainingShips[4] = "Patrol Boat";
			
			player2RemainingShips[0] = "Carrier";
			player2RemainingShips[1] = "BattleShip";
			player2RemainingShips[2] = "Destroyer";
			player2RemainingShips[3] = "Submarine";
			player2RemainingShips[4] = "Patrol Boat";
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
				
				// Write anything to notify player 1 to start
				toPlayer1.writeInt(1);
				
				// Read players' ship coordinates and update grids
				int p1CarrierColumn = fromPlayer1.readInt();
				int p1CarrierRow = fromPlayer1.readInt();
				player1Grid[p1CarrierColumn][p1CarrierRow] = "Carrier";
				int p1BattleshipColumn = fromPlayer1.readInt();
				int p1BattleshipRow = fromPlayer1.readInt();
				player1Grid[p1BattleshipColumn][p1BattleshipRow] = "Battleship";
				int p1DestroyerColumn = fromPlayer1.readInt();
				int p1DestroyerRow = fromPlayer1.readInt();
				player1Grid[p1DestroyerColumn][p1DestroyerRow] = "Destroyer";
				int p1SubmarineColumn = fromPlayer1.readInt();
				int p1SubmarineRow = fromPlayer1.readInt();
				player1Grid[p1SubmarineColumn][p1SubmarineRow] = "Submarine";
				int p1PatrolColumn = fromPlayer1.readInt();
				int p1PatrolRow = fromPlayer1.readInt();
				player1Grid[p1PatrolColumn][p1PatrolRow] = "Patrol Boat";
				
				int p2CarrierColumn = fromPlayer2.readInt();
				int p2CarrierRow = fromPlayer2.readInt();
				player2Grid[p2CarrierColumn][p2CarrierRow] = "Carrier";
				int p2BattleshipColumn = fromPlayer2.readInt();
				int p2BattleshipRow = fromPlayer2.readInt();
				player2Grid[p2BattleshipColumn][p2BattleshipRow] = "Battleship";
				int p2DestroyerColumn = fromPlayer2.readInt();
				int p2DestroyerRow = fromPlayer2.readInt();
				player2Grid[p2DestroyerColumn][p2DestroyerRow] = "Destroyer";
				int p2SubmarineColumn = fromPlayer2.readInt();
				int p2SubmarineRow = fromPlayer2.readInt();
				player2Grid[p2SubmarineColumn][p2SubmarineRow] = "Submarine";
				int p2PatrolColumn = fromPlayer2.readInt();
				int p2PatrolRow = fromPlayer2.readInt();
				player2Grid[p2PatrolColumn][p2PatrolRow] = "Patrol Boat";
						
				// Continuously serve the players and report game status
				while (true) {
					// Receive a move from player 1
					int row = fromPlayer1.readInt();
					int column = fromPlayer1.readInt();
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
