package game.graphics;

public class Sprite {
	private int x, y;
	private int width, height;
	public final int SIZE;
	public int[] pixels;
	protected SpriteSheet sheet;
	
	//public static Sprite player = new Sprite(16, 16, SpriteSheet.playerStart);
	//public static Sprite greenGhost = new Sprite(48, 16, SpriteSheet.greenGhost);

	public static Sprite spawnBackground = new Sprite(16, 1, 1, SpriteSheet.spawnLevel);
	public static Sprite spawnRightEdge = new Sprite(16, 3, 2, SpriteSheet.spawnLevel);
	public static Sprite spawnLeftEdge = new Sprite(16, 3, 3, SpriteSheet.spawnLevel);
	public static Sprite spawnTopEdge = new Sprite(16, 3, 0, SpriteSheet.spawnLevel);
	public static Sprite spawnBottomEdge = new Sprite(16, 3, 1, SpriteSheet.spawnLevel);
	public static Sprite spawnTLCorner = new Sprite(16, 0, 0, SpriteSheet.spawnLevel);
	public static Sprite spawnTRCorner = new Sprite(16, 2, 0, SpriteSheet.spawnLevel);
	public static Sprite spawnBLCorner = new Sprite(16, 0, 2, SpriteSheet.spawnLevel);
	public static Sprite spawnBRCorner = new Sprite(16, 2, 2, SpriteSheet.spawnLevel);
	public static Sprite spawnLeftSide = new Sprite(16, 0, 1, SpriteSheet.spawnLevel);
	public static Sprite spawnRightSide = new Sprite(16, 2, 1, SpriteSheet.spawnLevel);
	public static Sprite spawnTopSide = new Sprite(16, 1, 0, SpriteSheet.spawnLevel);
	public static Sprite spawnBottomSide = new Sprite(16, 1, 2, SpriteSheet.spawnLevel);
	
	public static Sprite voidSprite = new Sprite(16, 0x1B87E0);
	
	public static Sprite bigDot = new Sprite(16, 0, 4, SpriteSheet.spawnLevel);
	public static Sprite smallDot = new Sprite(16, 1, 3, SpriteSheet.spawnLevel);
	
	public static Sprite pacmanLife = new Sprite(16, 0, 0, SpriteSheet.pacmanLife);
			
	//Sprite for voidSprite
	public Sprite(int size, int color) {
		SIZE = size;
		this.width = size;
		this.height = size;
		pixels = new int[SIZE * SIZE];
		setColor(color);
	}
	
	public Sprite(int size, int x, int y, SpriteSheet sheet) {
		SIZE = size;
		this.width = size;
		this.height = size;
		pixels = new int[SIZE * SIZE];
		this.x = x * size;
		this.y = y * size;
		this.sheet = sheet;
		load();
	}
	
	protected Sprite(SpriteSheet sheet, int width, int height) {
		if(width == height) SIZE = width;
		else SIZE = -1;
		this.width = width;
		this.height = height;
		this.sheet = sheet;
	}
	
	public Sprite(int width, int height, SpriteSheet sheet) {
		SIZE = width * height;
		this.width = width;
		this.height = height;
		pixels = new int[SIZE];
		this.sheet = sheet;
		load();
	}
	
	public Sprite(int[] pixels, int width, int height) {
		SIZE = (width == height) ? width : -1;
		this.width = width;
		this.height = height;
		this.pixels = new int[pixels.length];
		for(int i = 0; i < pixels.length; i++) {
			this.pixels[i] = pixels[i];
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setColor(int color) {
		for(int i = 0; i < width * height; i++) {
			pixels[i] = color;
		}
	}
	
	private void load() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				pixels[x + y * width] = sheet.pixels[(x + this.x) + (y + this.y) * sheet.SPRITE_WIDTH];
			}
		}
	}
}
