package game.entity.mob;

import game.graphics.Screen;
import game.graphics.Sprite;

public class PacmanLife extends Mob {

	private Sprite sprite = Sprite.pacmanLife;
	
	public PacmanLife(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}
	
	@Override
	public void update() {
		
	}

	@Override
	public void render(Screen screen) {
		screen.renderSprite((int)x, (int)y, sprite, true);
	}

	
}
