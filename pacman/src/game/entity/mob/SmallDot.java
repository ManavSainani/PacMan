package game.entity.mob;

import game.graphics.Screen;
import game.graphics.Sprite;

public class SmallDot extends Mob {

	private Sprite dot = Sprite.smallDot;
	
	public SmallDot(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Screen screen) {
		screen.renderSprite((int)x, (int)y, dot, true);
	}
}
