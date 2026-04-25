package game.entity;

import java.util.Random;

import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.Level;
import game.level.tile.Tile;

public abstract class Entity {
	protected double x, y;
	protected boolean removed = false;
	protected Random random = new Random(); // For A.I.
	protected Sprite sprite;
	protected Level level;
	protected Tile tile;
	
	public void update() {
	}
	
	public void render(Screen screen) {
		//if(sprite != null) screen.renderSprite((int)x, (int)y, sprite, true);
	}
	
	public void remove() {
		removed = true;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public Tile getTile() {
		return tile;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void init(Level level) {
		this.level = level;
	}
}
