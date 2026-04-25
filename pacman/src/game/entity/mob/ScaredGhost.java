package game.entity.mob;

import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;

public class ScaredGhost extends Mob {

	private double xa = 0;
	private double ya = 0;
	
	private boolean walking = false;
	
	private AnimatedSprite animSprite = new AnimatedSprite(SpriteSheet.scaredGhost, 16, 16, 3);
	private Sprite sprite;
	
	public ScaredGhost(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}
	
	@Override
	public void update() {
		xa = random.nextInt(3) - 1; //gives random number from 0 to 2, subtracting one give either -1, 0 or 1 randomly.
		ya = random.nextInt(3) - 1;		
		animSprite.update();
	 
	    if(xa != 0 || ya != 0) {
	      move(xa, ya);
	      walking = true;
	    } else {
	      walking = false;
	    } 
	}

	@Override
	public void render(Screen screen) {
		sprite = animSprite.getSprites();
		screen.renderPlayerDynamic((int)x - 7, (int)y - 8, sprite, false);
	}
}
