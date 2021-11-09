/*
 * Author: Joshua Gray
 * Date: 11/8/21
 * 
 * This program uses JavaFX to create an investment value calculator. 
 * The user fills in the text fields and can either press the 
 * "Calculate" button, or the "Calculate" option in the top 
 * menu to see the future value. The use can exit the 
 * program by selecting the "Exit" option in the menu.
 */

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.lang.Math;

public class Exercise31_17 extends Application {
	
	// Create text fields
	private TextField tfAmount = new TextField();
	private TextField tfYears = new TextField();
	private TextField tfAIR = new TextField();
	private TextField tfValue = new TextField();
	
	// Create button
	private Button btCalculate = new Button("Calculate");
		
	@Override
	public void start(Stage primaryStage) {
		// Set properties for text fields
		tfValue.setEditable(false);
		tfAmount.setAlignment(Pos.BASELINE_RIGHT);
		tfYears.setAlignment(Pos.BASELINE_RIGHT);
		tfAIR.setAlignment(Pos.BASELINE_RIGHT);
		tfValue.setAlignment(Pos.BASELINE_RIGHT);
		
		// Create menu
		MenuBar menuBar = new MenuBar();
			
		Menu menuOperation = new Menu("Operation");
		menuBar.getMenus().addAll(menuOperation);
			
		MenuItem menuItemCalculate = new MenuItem("Calculate");
		MenuItem menuItemExit = new MenuItem("Exit");
		menuOperation.getItems().addAll(menuItemCalculate, menuItemExit);
			
		HBox hBox1 = new HBox(10);

		hBox1.getChildren().addAll(new Label("Investment Amount:"), tfAmount);
			
		HBox hBox2 = new HBox(25);
		hBox2.getChildren().addAll(new Label("Number of Years:"), tfYears);
			
		HBox hBox3 = new HBox(8);
		hBox3.getChildren().addAll(new Label("Annual Interest Rate:"), tfAIR);
			
		HBox hBox4 = new HBox(50);
		hBox4.getChildren().addAll(new Label("Future value:"), tfValue);
		
		HBox hBox5 = new HBox(210);
		hBox5.getChildren().addAll(new Label(), btCalculate);
			
		VBox vBox = new VBox(5);
		vBox.getChildren().addAll(menuBar, hBox1, hBox2, hBox3, hBox4, hBox5);
			
		Scene scene = new Scene(vBox, 300, 200);
		primaryStage.setTitle("Exercise31_17");
		primaryStage.setScene(scene);
		primaryStage.show();
			
		// Handle menu actions
		menuItemCalculate.setOnAction(e -> calculate());
		menuItemExit.setOnAction(e -> System.exit(0));
			
		// Handle button actions
		btCalculate.setOnAction(e -> calculate());
	}
	
	public void calculate() {
		double investmentAmount = Double.parseDouble(tfAmount.getText().trim());
		double years = Double.parseDouble(tfYears.getText().trim());
		double annualInterestRate = Double.parseDouble(tfAIR.getText().trim());
		double monthlyInterestRate = annualInterestRate / 1200;
		
		// Compute future value
		double futureValue = ((investmentAmount) * (Math.pow((1 + monthlyInterestRate), (years * 12))));
		
		// Display future value
		tfValue.setText("$" + String.format("%.2f", futureValue));
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
