package game.entity.mob;

import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;

public class Banana extends Mob {

	private double xa = 0;
	private double ya = 0;
	
	private Sprite sprite;
	private AnimatedSprite animSprite = new AnimatedSprite(SpriteSheet.banana, 16, 16, 4);
	private boolean moveDown = true; // to change direction
	
	public Banana(int x, int y) { // constructor takes tile precision
		this.x = x << 4; // convert to pixel precision
		this.y = y << 4;
	}
	
	private void move() {
		
		int currY = (int)getY() >> 4;
		if(currY >= 29) moveDown = false;
		if(!moveDown) ya--;
		if(currY <= 2) moveDown = true;
		if(moveDown) ya++;
		
		move(0, ya);
		ya = 0; // reset to avoid acceleration of banana
	}
	
	@Override
	public void update() {
		animSprite.update();
		move();
	}

	@Override
	public void render(Screen screen) {
		sprite = animSprite.getSprites();
		screen.renderPlayerDynamic((int)x, (int)y, sprite, false);
	}

}
