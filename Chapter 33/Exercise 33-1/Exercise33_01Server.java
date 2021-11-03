// Exercise31_01Server.java: The server can communicate with
// multiple clients concurrently using the multiple threads
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Exercise33_01Server extends Application {
  // Text area for displaying contents
  private TextArea ta = new TextArea();

  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    ta.setWrapText(true);
   
    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(ta), 400, 200);
    primaryStage.setTitle("Exercise33_01Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    new Thread(() -> connectToClient()).start();
  }
  
  public void connectToClient() {
	  try {
  		// Create a server socket
  		ServerSocket serverSocket = new ServerSocket(8000);
  		Platform.runLater(() ->
  			ta.appendText("Exercise33_01Server started a " + new Date() + '\n'));
  			
  			// Listen for a connection request
  			Socket socket = serverSocket.accept();
  			
  			// Display client number
  			Platform.runLater( () -> {ta.appendText("Connected to a client " + 
  					" at " + new Date() + '\n');});
  			
  			// Create data input and output streams
  			DataInputStream inputFromClient = new DataInputStream(
  				socket.getInputStream());
  			DataOutputStream outputToClient = new DataOutputStream(
  				socket.getOutputStream());
  			
  			while (true) {
  				// Receive object data from client
  				double annualInterestRate = inputFromClient.readDouble();
  				int numberOfYears = inputFromClient.readInt();
  				double loanAmount = inputFromClient.readDouble();
  				
  				// Create Loan object
  				Loan loan = new Loan(annualInterestRate, numberOfYears, loanAmount);
  				
  				// Compute monthlyPayment and totalPayment
  				double monthlyPayment = loan.getMonthlyPayment();
  				double totalPayment = loan.getTotalPayment();
  				
  				// Send monthlyPayment and totalPayment back to client
  				outputToClient.writeDouble(monthlyPayment);
  				outputToClient.writeDouble(totalPayment);
  				
  				Platform.runLater(() -> {
  					ta.appendText("Annual Interest Rate: " + annualInterestRate + 
  							'\n' + "\nNumber of Years: " + numberOfYears + 
  							"\nLoan Amount " + loanAmount + '\n');
  					ta.appendText("Monthly Payment: " + monthlyPayment + 
  							"\nTotal Payment " + totalPayment + '\n');
  				});
  			}
  		}
  		catch(IOException ex) {
  			ex.printStackTrace();
  		}
  }
    
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
