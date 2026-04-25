package game.menu;

import game.Game;
import game.graphics.Screen;
import game.graphics.SpriteSheet;
import game.input.Keyboard;
import game.level.Level;

public class WinMenu {
	
private Keyboard input;
	
	public WinMenu(Keyboard input) {
		this.input = input;
	}
	
	public void update() {
		if(input.spaceBar) {
			Game.state = Game.STATE.GAME;
			Level.reset = true;
		}
	}
	
	public void render(Screen screen) {
		screen.renderSheet(0, 0, SpriteSheet.winMenu, true);
	}
}
