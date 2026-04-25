package game.level;

import game.util.Vector2i;

public class Node {
	/*
	 * In this game a Node = a tile
	 */
	public Vector2i tile; //for location of Node
	public Node parent; //is the previous node in the path of the current node(tile)
	public double fCost, gCost, hCost; // for A* Algorithm
	 
	/*  gCost is sum of all Node to Node distances
		hCost is direct distance from start Node to finish Node
		fCost is the total Cost, which the the Cost used by the A* algorithm
		A* algorithm and it can be used to account for NOT ONLY distance 
		but speed (for example swimming through water) to obtain shortest path. This 
		is why its refereed to as Cost and not distance.
	*/
	
	public Node(Vector2i tile, Node parent, double gCost, double hCost) {
		this.tile = tile;
		this.parent = parent;
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = this.gCost + this.hCost;
	}
}
