package game.graphics;

import game.level.tile.Tile;

// Pac-Man

public class Screen {
	
	public int width, height;
	public int xOffset, yOffset;
	public int[] pixels;
	
	private final int ALPHA_COL = 0x000000; // Alpha channel color NOT to be rendered
	
	
	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];		
	}
	
	public void clear() {
		for(int i = 0; i < pixels.length; i++) {
			pixels[0] = 0;
		}
	}
	
	public void renderPlayer(int xp, int yp, Sprite sprite) {
		xp -= xOffset;
		xp -= yOffset;
		for(int y = 0; y < 32; y++) {
			int ya = y + yp;
			for(int x = 0; x < 32; x++) {
				int xa = x + xp;
				if(xa < -32 || x >= width || ya < 0 || y >= height) break;
				if(xa < 0) xa = 0;
				int col = sprite.pixels[x + y * 32];
				if(col != ALPHA_COL) pixels[xa + ya * width] = col;
			}
		}
	}
	
	// USED FOR RENDERING CHARACTER SPRITE IN 1 PIECE with varying(dynamic) size. Fixed means player is attached to map
		public void renderPlayerDynamic(int xp, int yp, Sprite sprite, boolean fixed) { //used to render Player/Sprites **AND Mobs
			if(fixed) {
				xp -= xOffset;
				yp -= yOffset;
			}
			for(int y = 0; y < sprite.getHeight(); y++) {
				int ya =  y + yp;  //absolute position, position relative to the world, from x = 0, y = 0
				for(int x = 0; x < sprite.getWidth(); x++) { //Want to make a universal way of getting size of tiles, since NOT every tile will 16X16 pixels all the time
					int xa =  x + xp;
					if(xa < -sprite.getWidth() || xa >= width || ya < 0 || ya >= height) break; // If tile exits screen area, stop rendering it, IMPORTANT without this code game can crash
					if(xa < 0) xa = 0; //makes smooth tile render.
					int col = sprite.pixels[x + y * sprite.getWidth()]; // col is color
					if(col != ALPHA_COL) pixels[xa + ya * width] = col; // if col is the sprite sheet background color it wont render.
					//pixels[xa + ya * width] = sprite.pixels[x + y * 16]; //Actually renders sprite to pixels on the screen
				}
			}
		}
	
	public void renderSheet(int xp, int yp, SpriteSheet sheet, boolean fixed) {
		if(fixed) {
			xp = -xOffset;
			xp = -yOffset;
		}
		for(int y = 0; y < sheet.SPRITE_HEIGHT; y++) { 
			int ya = y + yp;
			for(int x = 0; x < sheet.SPRITE_WIDTH; x++) {
				int xa = x + xp;
				if(xa < 0 || xa >= width || ya < 0 || y >= height) continue;
				pixels[xa + ya * width] = sheet.pixels[x + y * sheet.SPRITE_WIDTH];
			}
		}
	}
	
	public void renderSprite(int xp, int yp, Sprite sprite, boolean fixed) {
		if(fixed) {
			xp -= xOffset;
			yp -= yOffset;
		}
		for(int y = 0; y < sprite.getHeight(); y++) {
			int ya = y + yp;
			for(int x = 0; x < sprite.getWidth(); x++) {
				int xa = x + xp;
				if(xa < 0 || xa >= width || ya < 0 || ya >= height) continue;
				int col = sprite.pixels[x + y * sprite.getWidth()];
				if(col != ALPHA_COL) pixels[xa + ya * width] = col;
			}
		}
	}
	
	public void renderTile(int xp, int yp, Tile tile) {
		xp -= xOffset;
		yp -= yOffset;
		for(int y = 0; y < tile.sprite.SIZE; y++) {
			int ya = y + yp;
			for(int x = 0; x < tile.sprite.SIZE; x++) {
				int xa = x + xp;
				if(xa < -tile.sprite.SIZE || xa >= width || ya < 0 || ya >= height) break;
				if(xa < 0) xa = 0;
				pixels[xa + ya * width] = tile.sprite.pixels[x + y * tile.sprite.SIZE];
			}
		}
	}
	
	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
