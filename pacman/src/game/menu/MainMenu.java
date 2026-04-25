package game.menu;

import game.Game;
import game.graphics.Screen;
import game.graphics.SpriteSheet;
import game.input.Keyboard;

public class MainMenu {
	
	private Keyboard input;
	
	public MainMenu(Keyboard input) {
		this.input = input;
	}
	
	public void update() {
		if(input.spaceBar) {
			Game.state = Game.STATE.GAME;
		}
	}
	
	public void render(Screen screen) {
		screen.renderSheet(0, 0, SpriteSheet.mainMenu, true);
	}
}
