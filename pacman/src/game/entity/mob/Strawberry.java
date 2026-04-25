package game.entity.mob;

import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;

public class Strawberry extends Mob {

	private double xa = 0;
	private double ya = 0; // might use
	
	private Sprite sprite;
	private AnimatedSprite animSprite = new AnimatedSprite(SpriteSheet.strawberry, 16, 16, 4);
	private boolean moveRight = true; // to change direction
	
	public Strawberry(int x, int y) { // constructor takes tile precision
		this.x = x << 4; // convert to pixel precision
		this.y = y << 4;
	}
	
	private void move() {
		
		int currX = (int)getX() >> 4;
		if(currX >= 25) moveRight = false;
		if(!moveRight) xa--;
		if(currX <= 5) moveRight = true;
		if(moveRight) xa++;
		
		move(xa, 0);
		xa = 0; // reset to avoid acceleration of strawberry
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
