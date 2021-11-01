/*
* Author: Joshua Gray
* Date: 11/1/21
* This program alters Listing 15.13 to use a thread to animate a flag being raised.
*/
import javafx.animation.PathTransition; 
import javafx.application.Application; 
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.text.html.*;

public class FlagRisingAnimation extends Application {
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
		// Create a pane
		Pane pane = new Pane();
	
		// Add an image view and add it to pane
		ImageView imageView = new ImageView("image/us.gif");
		pane.getChildren().add(imageView);

		// Create a path transition
		PathTransition pt = new PathTransition(Duration.millis(10000),
							new Line(100, 200, 100, 0), imageView); pt.setCycleCount(5);
						
		// Create task
		Runnable raiseFlag = new animation(imageView, pt);
		
		// Create thread
		Thread thread1 = new Thread(raiseFlag);
		
		// Start thread
		thread1.start();
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(pane, 250, 200); 
		primaryStage.setTitle("FlagRisingAnimation"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}

class animation implements Runnable {
	private ImageView imageView; // The image/gif to animate
	private PathTransition pt; // The PathTransition the animation takes
	
	// Construct a task with specified ImageView and PathTransition
	public animation(ImageView i, PathTransition p) {
		imageView = i;
		pt = p;
	}
	
	@Override
	public void run() {
		pt.play();
	}
}
