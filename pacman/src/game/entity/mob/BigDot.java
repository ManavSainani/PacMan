package game.entity.mob;

import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;

public class BigDot extends Mob {

	//private Sprite dot = Sprite.bigDot;
	private Sprite sprite; // 'dummy variable'
	
	private int anim = 0; // Start of animation sprite index
	private AnimatedSprite animFlashDot = new AnimatedSprite(SpriteSheet.flashDot, 16, 16, 3);
	
	public BigDot(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void update() {
		animFlashDot.update();
	}

	@Override
	public void render(Screen screen) {
		sprite = animFlashDot.getSprites(); 
		screen.renderSprite((int)x, (int)y, sprite, true);
	}
}
