# Battleship Online
## Synopsis
This project is my final project for the Industry CSCI 1175 Industry Projects course at Southwest Technical College. It is meant to provide users with the ability to play a game of Battleship over the internet.
## Motivation
I built this program to challenge my understanding and ability to create client-server programs. While I may have fallen short of my original goal, this program still deepened my understanding of GUI and client/server programming, and can still be improved upon in the future.
## How to Run
The minimum files required to run this program are: BattleshipOnlineClient.java, BattleshipOnlineConstants.java, and Ship.java. In the current state of the program, BattleshipOnlineServer.java is also required, but can be tweaked in order to let the clients connect remotely.
[Clientside Running](RunningDemo.png)
## Code Example
This code is the two methods used to send and receive ship coordinates to and from the clients. I am particularly proud of this small snippet because it was both challenging, and essential for the current version of the program.
```
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
```
