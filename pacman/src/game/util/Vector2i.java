package game.util;

/*
* Utility style vector class that can handle a lot of style inputs.
* Used for A * search algorithm.
*/
public class Vector2i {
	
	public int x, y;
	
	public Vector2i() {
		set(0, 0);
	}
	
	public Vector2i(int x, int y) {
		set(x, y);
	}
	
	public Vector2i(double x, double y) {
		set(x, y);
	}
	
	public Vector2i(Vector2i vector) { // takes vector as an input
		set(vector.x, vector.y);
	}
	
	public void set(double x, double y) {
		this.x = (int)x;
		this.y = (int)y;
	}
	
	public void set(int x, int y) { // set method for setting vector components
		this.x = x;
		this.y = y;
	}
	
	//Getter
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	//Adding vectors
	public Vector2i add(Vector2i vector) {
		this.x += vector.x;
		this.y += vector.y;
		return this; //returns type of method, Vector2i
	}

	public Vector2i add(int value) {
		this.x += value;
		this.y += value;
		return this; //returns type of method, Vector2i
	}
	
	//Subtracting vectors
	public Vector2i sub(Vector2i vector) {
		this.x -= vector.x;
		this.y -= vector.y;
		return this; //returns type of method, Vector2i
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public static double getDistance(Vector2i v0, Vector2i v1) { //Distance between two vectors
		double dx = v0.getX() - v1.getX(); //dx is change in x
		double dy = v0.getY() - v1.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public boolean equals(Object object) { //overrides equals method in the Object class in java
		if(!(object instanceof Vector2i)) return false; //if not instanceof of Vector2i then its clearly not gonna be equal so return false
		Vector2i vec = (Vector2i) object;
		if(vec.getX() == this.getX() && vec.getY() == this.getY()) return true;
		return false; //if component NOT equal method will returns false
	
	}
}
