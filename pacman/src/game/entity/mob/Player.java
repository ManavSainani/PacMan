package game.entity.mob;

import game.Game;
import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;
import game.input.Keyboard;
import game.sound.Sound;

//Pac-Man player class

public class Player extends Mob {
	
	protected static int direction;
	private int anim = 0;
	
	private boolean isSoundPlaying = false;
	
	// current players position in tile precision
	public static int currPlayerX;
	public static int currPlayerY;
	
	private boolean walking = false;
	private boolean teleport = true; //Allows player to warp from side to side
	
	private Sprite sprite;
	private Keyboard input;
		
	private AnimatedSprite right = new AnimatedSprite(SpriteSheet.player_right, 16, 16, 3);
	private AnimatedSprite left = new AnimatedSprite(SpriteSheet.player_left, 16, 16, 3); 
	private AnimatedSprite up = new AnimatedSprite(SpriteSheet.player_up, 16, 16, 3);
	private AnimatedSprite down = new AnimatedSprite(SpriteSheet.player_down, 16, 16, 3);

	
	private AnimatedSprite animSprite = right; //Initial direction
	
	public Player(int x, int y, Keyboard input) { //start position
		this.x = x;
		this.y = y;
		this.input = input;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void changeSprite() {
		if(direction == 0) animSprite = right;
		if(direction == 1) animSprite = left;
		if(direction == 2) animSprite = up;
		if(direction == 3) animSprite = down;
	}

	
	double xa = 0, ya = 0;
	@Override
	public void update() {
		
		//System.out.println("x = " + ((int)x >> 4) + " y = " + ((int)y >> 4));
		level.removeDots((int)x, (int)y); //removes dots that player hits
				
		//Send player to left side
		if(teleport && ((int)x >> 4) >= 30 && ((int)y >> 4) <= 16) {
			x = 2 << 4; //Don't need to set y since both positions lay on same vertical
			teleport = false;
		} 
		//Send player to right side
		if(teleport && ((int)x >> 4) <= 1 && ((int)y >> 4) <= 16) {
			x = 30 << 4;
			teleport = false;
		}
		//Reset ability to teleport once player is one unit way
		if(((int)x >> 4) == 3 || ((int)x >> 4) == 29) teleport = true;

		
		if(walking) animSprite.update();
		if(!walking)animSprite.setFrame(0);
		
		double speed = 1.5;
		
		if(input.right) {
			changeSprite();
			direction = 0;
			xa = speed;
			ya = 0;
		}
		if(input.left) {
			direction = 1;
			changeSprite();
			xa = -speed;
			ya = 0;
		}
		if(input.up) {
			changeSprite();
			direction = 2;
			xa = 0;
			ya = -speed;
		}	
		if(input.down) {
			direction = 3;
			changeSprite();
			xa = 0;
			ya = speed;
		}
		
		if(xa != 0 || ya != 0) {
			move(xa, ya);
			walking = true;
		}
		else {
			walking = false;
		}
		currPlayerX = ((int)(x + xa) >> 4);
		currPlayerY = ((int)(y + ya) >> 4);
	}

	@Override
	public void render(Screen screen) {
		sprite = animSprite.getSprites();
		screen.renderPlayerDynamic((int)x - 7, (int)y - 7, sprite, false);
	}
	
	
}
