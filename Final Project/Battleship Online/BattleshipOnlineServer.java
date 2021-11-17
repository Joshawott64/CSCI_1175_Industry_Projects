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
				
				// Continuously serve the players and report game status
				
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

	}

}
