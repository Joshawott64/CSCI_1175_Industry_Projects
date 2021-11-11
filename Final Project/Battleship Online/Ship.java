
public class Ship {
	// Data fields
	int length;
	boolean isSunk;
	String name;
	
	// Construct default ship
	public Ship() {
	}
	
	// Construct ship with specified length, isSunk, and name
	public Ship(int length, boolean isSunk, String name) {
		this.length = length;
		this.isSunk = isSunk;
		this.name = name;
	}
	
	// Return length
	public int getLength() {
		return length;
	}
	
	// Set a new length
	public void setLength(int newLength) {
		length = newLength;
	}
	
	// Return isSunk
	public boolean getIsSunk() {
		return isSunk;
	}
	
	// Set a new isSunk
	public void isIsSunk(boolean newIsSunk) {
		isSunk = newIsSunk;
	}
	
	// Return name
	public String getName() {
		return name;
	}
	
	// Set a new name
	public void setName(String newName) {
		name = newName;
	}
}
