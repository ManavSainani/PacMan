package game.level.tile.spawn_level;

import java.awt.Rectangle;

import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;

public class SpawnWallTile extends Tile {
	

	public SpawnWallTile(Sprite sprite) {
		super(sprite);
	}
	
	public boolean solid() { //Can't pass through the mystery box
		return true;
	}
	
	public void render(int x, int y, Screen screen) {
		screen.renderTile(x << 4, y << 4, this);
	}
}
