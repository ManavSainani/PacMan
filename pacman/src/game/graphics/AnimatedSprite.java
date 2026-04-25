package game.graphics;

public class AnimatedSprite extends Sprite {
	
	private Sprite sprite;
	private int frame = 0;
	private int rate = 5;
	private int time = 0;
	private int length = -1; //Initialize to -1 to avoid errors
	
	
	public AnimatedSprite(SpriteSheet sheet, int width, int height, int length) {
		super(sheet, width, height);
		this.length = length;
		sprite = sheet.getSprites()[0];
		if(length > sheet.getSprites().length) System.err.println("ERROR! Length of animation is too long!");
	}
	
	public void update() {
		time++;
		if(time % rate == 0) {
			if(frame >= length -1) frame = 0;
			else frame++;
			sprite = sheet.getSprites()[frame];
		}
	}
	
	public Sprite getSprites() {
		return sprite;
	}

	public void setFrame(int index) {
		if(index > sheet.getSprites().length - 1) {
			System.err.println("Index out of bounds" + this);
			return;
		}
		sprite = sheet.getSprites()[index];
	}
}
