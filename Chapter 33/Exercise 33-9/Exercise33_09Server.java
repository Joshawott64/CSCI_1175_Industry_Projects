import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Exercise33_09Server extends Application {
  private TextArea taServer = new TextArea();
  private TextArea taClient = new TextArea();
  
  // Create IO streams
  DataOutputStream toClient;
  DataInputStream fromClient;
  
  // Create line variable
  String line = "";
 
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    taServer.setWrapText(true);
    taClient.setWrapText(true);
    taServer.setDisable(true);

    BorderPane pane1 = new BorderPane();
    pane1.setTop(new Label("History"));
    pane1.setCenter(new ScrollPane(taServer));
    BorderPane pane2 = new BorderPane();
    pane2.setTop(new Label("New Message"));
    pane2.setCenter(new ScrollPane(taClient));
    
    VBox vBox = new VBox(5);
    vBox.getChildren().addAll(pane1, pane2);

    // Create a scene and place it in the stage
    Scene scene = new Scene(vBox, 200, 200);
    primaryStage.setTitle("Exercise31_09Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage

    // To complete later
    
    // Sent data to client
    taClient.setOnKeyPressed(e -> {
    	if (e.getCode().equals(KeyCode.ENTER)) {
    		sendToClient();
    		taClient.clear();
    	}
    });
    
    // Receive data from server
    new Thread(() -> connectToClient()).start();
  }
  
  public void connectToClient() {
	  try {
		  // Create a server socket
		  ServerSocket serverSocket = new ServerSocket(8000);
		  
		  // Listen for a connection request
		  Socket socket = serverSocket.accept();
		  
		  // Create data input and output streams
		  DataInputStream inputFromClient = new DataInputStream(
				  socket.getInputStream());
		  DataOutputStream outputFromClient = new DataOutputStream(
				  socket.getOutputStream());
		  
		  while (true) {
			  // Receive line from server
			  String line = inputFromClient.readUTF();
			  
			  // Add line to history
			  Platform.runLater(() -> {
				  taServer.appendText("C: " + line + '\n');
			  });
		  }
	  }
	  catch(IOException ex) {
		  ex.printStackTrace();
	  }
  }
  
  public void sendToClient() {
	  try {
		  // Get line from text area
		  line = taClient.getText().trim();
		  
		  // Add line to history and send line to server
		  taServer.appendText("S: " + line + '\n');
		  toClient.writeUTF(line);
		  
		  // Clear line
		  Platform.runLater(() -> {
			  taClient.clear();
		  });
	  }
	  catch (IOException ex) {
		  System.err.println(ex);
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
