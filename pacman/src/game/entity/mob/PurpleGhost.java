package game.entity.mob;

import java.util.List;

import game.entity.mob.Mob.Direction;
import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;
import game.level.Node;
import game.util.Vector2i;

public class PurpleGhost extends Mob {

	private double xa = 0;
	private double ya = 0;
	private double numX = 1;
	private double numY = 1;

	private int detectionRange = 8;
	private int time = 0;
	
	private boolean isScared = false;
	private boolean walking = false;
	
	private List<Node> path = null; //default equals null
	
	private AnimatedSprite animSprite = new AnimatedSprite(SpriteSheet.purpleGhost, 16, 16, 3);
	private Sprite sprite;
	
	public PurpleGhost(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}
	
	public void turnSpriteScared() {
		animSprite = new AnimatedSprite(SpriteSheet.scaredGhost, 16, 16, 3);
		isScared = true;
	}
	
	public void turnSpriteUnscared() {
		animSprite = new AnimatedSprite(SpriteSheet.purpleGhost, 16, 16, 3);
		isScared = false;
	}
	
	public void setLocation(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}

	private double getDistance(Vector2i tile, Vector2i goal) {
		 double dx = tile.getX() - goal.getX();
		 double dy = tile.getY() - goal.getY();
		 return Math.sqrt(dx * dx + dy *dy); //distance 
	}
	
	private void move() {
		
		xa = 0;
		ya = 0;
		time++;
		
		int px = (int) level.getClientsPlayer().getX();
		int py = (int) level.getClientsPlayer().getY();
		Vector2i start = new Vector2i((int)getX() >> 4, (int)getY() >> 4);
		Vector2i destination = new Vector2i(px >> 4, py >> 4);
		
		if(getDistance(start, destination) > detectionRange) {
			if(time % 60 == 0) {
				numX = random.nextInt(3) - 1; //reverses direction
				numY = random.nextInt(3) - 1;
			}
			xa--;
			ya--;
			xa = xa * numX;
			ya = ya * numY;		
		}
		else {
			path = level.findPath(start, destination);
			if(path != null) {
				if(path.size() > 0) {
					Vector2i vec = path.get(path.size() - 1).tile;
					if(x < (int)vec.getX() << 4) xa++;
					if(x > (int)vec.getX() << 4) xa--;
					if(y < (int)vec.getY() << 4) ya++;
					if(y > (int)vec.getY() << 4) ya--;
				}
			}
		}
		if(xa != 0 || ya != 0) {
			move(xa, ya);
			walking = true;
		} else {
			walking = false;
		}
		if(time % 60 > 120) time = 0; //reset time to avoid game crash
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
			if(walking) animSprite.update();
			else animSprite.setFrame(0);
			if(ya < 0) {
				dir = Direction.UP;
			} else if(ya > 0) {
				dir = Direction.DOWN;
			}
			if(xa < 0) {
				dir = Direction.LEFT;
			} else if(xa > 0) {
				dir = Direction.RIGHT;
			}
		}
	}

	@Override
	public void render(Screen screen) {
		sprite = animSprite.getSprites();
		screen.renderPlayerDynamic((int)x - 7, (int)y - 8, sprite, false);
	}
}
