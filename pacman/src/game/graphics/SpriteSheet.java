package game.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.level.tile.Tile;

public class SpriteSheet {
	private String path;
	public final int SIZE;
	public final int SPRITE_WIDTH, SPRITE_HEIGHT;
	private int width, height;
	public int[] pixels;
	private Sprite[] sprites;
	
	public static SpriteSheet spawnLevel = new SpriteSheet("/game/res/pac-manLevel.png", 64, 64);

	public static SpriteSheet player = new SpriteSheet("/game/res/pac-manV2.png", 64, 32); //Pac-Man
	public static SpriteSheet player_right = new SpriteSheet(player, 0, 0, 3, 1, 16, 16);
	public static SpriteSheet player_left = new SpriteSheet(player, 0, 1, 3, 1, 16, 16);
	public static SpriteSheet player_up = new SpriteSheet(player, 0, 2, 3, 1, 16, 16);
	public static SpriteSheet player_down = new SpriteSheet(player, 0, 3, 3, 1, 16, 16);
	
	public static SpriteSheet pacmanLife = new SpriteSheet("/game/res/pacman_life.png", 16, 16);

	public static SpriteSheet flashDot = new SpriteSheet(spawnLevel, 0, 4, 3, 1, 16, 16);
	
	public static SpriteSheet ghosts = new SpriteSheet("/game/res/pacman_ghostV2.png", 48, 48);
	public static SpriteSheet greenGhost = new SpriteSheet(ghosts, 0, 0, 3, 1, 16, 16);
	public static SpriteSheet pinkGhost = new SpriteSheet(ghosts, 0, 1, 3, 1, 16, 16);
	public static SpriteSheet orangeGhost = new SpriteSheet(ghosts, 0, 2, 3, 1, 16, 16);
	public static SpriteSheet redGhost = new SpriteSheet(ghosts, 0, 3, 3, 1, 16, 16);
	public static SpriteSheet purpleGhost = new SpriteSheet(ghosts, 0, 4, 3, 1, 16, 16);
	public static SpriteSheet blueGhost = new SpriteSheet(ghosts, 0, 5, 3, 1, 16, 16);
	public static SpriteSheet scaredGhost = new SpriteSheet(ghosts, 0, 6, 3, 1, 16, 16);
	
	public static SpriteSheet poop = new SpriteSheet(spawnLevel, 0, 5, 4, 1, 16, 16);
	public static SpriteSheet banana = new SpriteSheet(spawnLevel, 0, 6, 4, 1, 16, 16);
	public static SpriteSheet strawberry = new SpriteSheet(spawnLevel, 0, 7, 4, 1, 16, 16);
	
	public static SpriteSheet mainMenu = new SpriteSheet("/game/res/Pacman_Cartoon.jpg", 512, 512);
	public static SpriteSheet winMenu = new SpriteSheet("/game/res/Pacman_Cartoon_Winner.jpg", 512, 512);
	public static SpriteSheet gameOverMenu = new SpriteSheet("/game/res/gameover.jpg", 512, 512);
	
	public SpriteSheet(SpriteSheet sheet, int x, int y, int width, int height, int spriteWidth, int spriteHeight) {
		int xx = x * spriteWidth;
		int yy = y * spriteHeight;
		int w = width * spriteWidth;
		int h = height * spriteHeight;
		if(width == height) SIZE = width;
		else SIZE = -1;
		SPRITE_WIDTH = w;
		SPRITE_HEIGHT = h;
		pixels = new int[w * h];
		for(int y0 = 0; y0 < h; y0++) {
			int yp = yy + y0;
			for(int x0 = 0; x0 < w; x0++) {
				int xp = xx + x0;
				pixels[x0 + y0 * w] = sheet.pixels[xp + yp * sheet.SPRITE_WIDTH];
			}
		}
		int frame = 0;
		sprites = new Sprite[width * height];
		for(int ya = 0; ya < height; ya++) {
			for(int xa = 0; xa < width; xa++) {
				int[] spritePixels = new int[spriteWidth * spriteHeight];
				for(int y0 = 0; y0 < spriteHeight; y0++) {
					for(int x0 = 0; x0 < spriteWidth; x0++) {
						spritePixels[x0 + y0 * spriteWidth] = pixels[(x0 + xa * spriteWidth) +(y0 + ya * spriteHeight) * SPRITE_WIDTH];
					}
				}
				Sprite sprite = new Sprite(spritePixels, spriteWidth, spriteHeight);
				sprites[frame++] = sprite;
			}
		}
	}
	
	public SpriteSheet(String path, int width, int height) {
		this.path = path;
		SIZE = -1;
		SPRITE_WIDTH = width;
		SPRITE_HEIGHT = height;
		pixels = new int[SPRITE_WIDTH * SPRITE_HEIGHT];
		load();
	}
	
	public Sprite[] getSprites() {
		return sprites;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private void load() {
		try {
			System.out.print("Trying to load: " + path + "...");
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
			System.out.println(" succeeded!");
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			System.err.println(" failed!");
		}
	}
}
