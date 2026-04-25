package game.entity.mob;

import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;

public class RedGhost extends Mob {

	private double xa = 0;
	private double ya = 0;
	private double speed = 0.6;
	
	private boolean isScared = false;
	private boolean walking = false;
	
	private AnimatedSprite animSprite = new AnimatedSprite(SpriteSheet.redGhost, 16, 16, 3);
	private Sprite sprite;
	
	public RedGhost(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}
	
	public void turnSpriteScared() {
		animSprite = new AnimatedSprite(SpriteSheet.scaredGhost, 16, 16, 3);
		isScared = true;
	}
	
	public void turnSpriteUnscared() {
		animSprite = new AnimatedSprite(SpriteSheet.redGhost, 16, 16, 3);
		isScared = false;
	}
	
	public void setLocation(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}
	
	private void move() {
		xa = 0;
		ya = 0;

		Player player = level.getClientsPlayer();
		//List <Player> players = level.getPlayers(this, 100);
		//if(player.size() > 0) {
			//Player player = players.get(0); //Since only one player
			if((int)x < (int)player.getX()) xa += speed; // then move forward
			if((int)x > (int)player.getX()) xa -= speed; //MUST CAST X AND PLAYER.GETX() TO INTS OTHRWISE MOVEMENT IS NOT SMOOTH AND PRECISE!!!!
			if((int)y < (int)player.getY()) ya += speed;
			if((int)y > (int)player.getY()) ya -= speed;
		//}
		if(xa != 0 || ya != 0) {
			move(xa, ya); // method from mob class
			walking = true;
		} else {
			walking = false;
		}
	}
	
	@Override
	public void update() {
		// does shaky walk if scared
		if(isScared) {
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
		else {
			move();		
			animSprite.update();
		 
		    if(xa != 0 || ya != 0) {
		      move(xa, ya);
		      walking = true;
		    } else {
		      walking = false;
		    } 
		}
	}

	@Override
	public void render(Screen screen) {
		sprite = animSprite.getSprites();
		screen.renderPlayerDynamic((int)x - 7, (int)y - 8, sprite, false);
	}
}
