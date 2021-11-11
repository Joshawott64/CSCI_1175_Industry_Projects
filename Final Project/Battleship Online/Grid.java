import javafx.scene.layout.*;

public class Grid {
	// Data fields
	GridPane gridPane;
	final int columns = 10;
	final int rows = 10;
	boolean hasShip;
	
	// Construct default grid
	public Grid() {
	}
	
	// Construct grid with specified grid, columns, rows, and hasShip
	public Grid(GridPane gridPane, int columns, int rows, boolean hasShip) {
		this.gridPane = gridPane;
		columns = this.columns;
		rows = this.rows;
		this.hasShip = hasShip;
		}
	
	// Return gridPane
	public GridPane getGridPane() {
		return gridPane;
	}
	
	// Set a new gridPane
	public void setGridPane(GridPane newGridPane) {
		gridPane = newGridPane;
	}
	
	// Return columns
	public int getColumns() {
		return columns;
	}
	
	// Return rows
	public int getRows() {
		return rows;
	}
	
	// Return hasShip
	public boolean getHasShip() {
		return hasShip;
	}
	
	// Set a new hasShip
	public void isHasShip(boolean newHasShip) {
		hasShip = newHasShip;
	}
	
	// Create gridpane constraints
	public void getGridConstraints(int columns, int rows) {
		gridPane.setGridLinesVisible(true);
		for (int i = 0; i < columns; i++) {
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPercentWidth(100 / columns);
			gridPane.getColumnConstraints().add(colConst);
		}
		for (int i = 0; i < rows; i++) {
			RowConstraints rowConst = new RowConstraints();
			rowConst.setPercentHeight(100 / rows);
			gridPane.getRowConstraints().add(rowConst);
		}
	}
}
