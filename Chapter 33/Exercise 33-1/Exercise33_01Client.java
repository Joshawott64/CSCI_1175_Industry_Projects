// Exercise31_01Client.java: The client sends the input to the server and receives
// result back from the server
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Exercise33_01Client extends Application {
  // Text field for receiving object data
  private TextField tfAnnualInterestRate = new TextField();
  private TextField tfNumOfYears = new TextField();
  private TextField tfLoanAmount = new TextField();
  private Button btSubmit= new Button("Submit"); // Submit data to server
  
  // Host name or ip
  String host = "localhost";
  
  // IO streams
  DataOutputStream toServer;
  DataInputStream fromServer;

  // Text area to display contents
  private TextArea ta = new TextArea();
  
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    ta.setWrapText(true);
   
    GridPane gridPane = new GridPane();
    gridPane.add(new Label("Annual Interest Rate"), 0, 0);
    gridPane.add(new Label("Number Of Years"), 0, 1);
    gridPane.add(new Label("Loan Amount"), 0, 2);
    gridPane.add(tfAnnualInterestRate, 1, 0);
    gridPane.add(tfNumOfYears, 1, 1);
    gridPane.add(tfLoanAmount, 1, 2);
    gridPane.add(btSubmit, 2, 1);
    
    tfAnnualInterestRate.setAlignment(Pos.BASELINE_RIGHT);
    tfNumOfYears.setAlignment(Pos.BASELINE_RIGHT);
    tfLoanAmount.setAlignment(Pos.BASELINE_RIGHT);
    
    tfLoanAmount.setPrefColumnCount(5);
    tfNumOfYears.setPrefColumnCount(5);
    tfLoanAmount.setPrefColumnCount(5);
            
    BorderPane pane = new BorderPane();
    pane.setCenter(new ScrollPane(ta));
    pane.setTop(gridPane);
    
    // Create a scene and place it in the stage
    Scene scene = new Scene(pane, 400, 250);
    primaryStage.setTitle("Exercise33_01Client"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    btSubmit.setOnAction(e -> connectToServer());
    
    try {
    	// Establish connection with the server
		Socket socket = new Socket(host, 8000);
		
		// Create an input stream from the server
		fromServer = new DataInputStream(socket.getInputStream());
		  
		// Create an output stream to the server
		toServer = new DataOutputStream(socket.getOutputStream());
		 
    }
    catch (IOException ex) {
    	ta.appendText(ex.toString() + '\n');
    }
  }
  
  public void connectToServer() {
	  try {			  
  		// Get object data from text fields
			double annualInterestRate = Double.parseDouble(tfAnnualInterestRate.getText().trim());
			int numberOfYears = Integer.parseInt(tfNumOfYears.getText().trim());
			double loanAmount = Double.parseDouble(tfLoanAmount.getText().trim());
			  
			// Send data to server
			toServer.writeDouble(annualInterestRate);
			toServer.write(numberOfYears);
			toServer.writeDouble(loanAmount);
			toServer.flush();
			  
			// Get data from server
			double monthlyPayment = fromServer.readDouble();
			double totalPayment = fromServer.readDouble();
			  
			// Display to the text area
			ta.appendText("Annual Interest Rate: " + annualInterestRate + '\n');
			ta.appendText("Number of years: " + numberOfYears + '\n');
			ta.appendText("Loan Amount: " + loanAmount + '\n');
			ta.appendText("monthlyPayment: " + monthlyPayment + '\n');
			ta.appendText("totalPayment: " + totalPayment + '\n');
			  
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
