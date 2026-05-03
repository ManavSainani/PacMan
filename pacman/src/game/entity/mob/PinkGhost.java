package game.entity.mob;
import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;
import game.level.Node;
import game.util.Vector2i;
import java.util.List;

/**
	 * A* Predictive Algorithm
	 *
	 * Pinky's implementation involves the A* algorithm, which uses the predictive heuristic to anticipate
	 * Pac-Man's future position and target that location, rather than his current position (Blinky). 
	 * It does this by calculating 2 steps ahead of Pac-Man's current position, making this approach more dynamic
	 * and unpredictable.
	 * 
	 * Predictive Pursuit Reference:
	 * Novikov, A., Yakovlev, S., & Gushchin, I. (2025).
	 * Radioelectronic and Computer Systems, 1(113), 327-337.
	 * https://doi.org/10.32620/reks.2025.1.21
	 */

	// Sources: 
	// Pinky's Implementation: 
	// http://donhodges.com/pacman_pinky_explanation.htm
	// https://www.youtube.com/watch?v=GIoJgpm0F1E&t=855s
	// Claude

public class PinkGhost extends Mob {

	private double xa = 0;
	private double ya = 0;
	private int time = 0;
	
	private boolean isScared = false;

	private AnimatedSprite animSprite = new AnimatedSprite(SpriteSheet.pinkGhost, 16, 16, 3);
	private Sprite sprite;
	
	public PinkGhost(int x, int y) {
		this.x = x << 4; //Convert from tile precision to pixel precision
		this.y = y << 4;
	}
	
	public void turnSpriteScared() {
		animSprite = new AnimatedSprite(SpriteSheet.scaredGhost, 16, 16, 3);
		isScared = true;
	}
	
	public void turnSpriteUnscared() {
		animSprite = new AnimatedSprite(SpriteSheet.pinkGhost, 16, 16, 3);
		isScared = false;
	}
	
	public void setLocation(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}

	private void move() {
		xa = 0;
		ya = 0;
		time++;

		// PacMan's current tile position
		int pacTileX = (int) level.getClientsPlayer().getX() >> 4;
		int pacTileY = (int) level.getClientsPlayer().getY() >> 4;

		// PacMan's anticipated direction 
		int dirX = 0;
		int dirY = 0;
		if (Player.direction == 2) dirY = -1;
		if (Player.direction == 3) dirY =  1;
		if (Player.direction == 1) dirX = -1;
		if (Player.direction == 0) dirX =  1;

		// Pinky targets 2 tiles ahead of Pac-Man's direction (opposed to 4 in classic game)
		Vector2i start = new Vector2i((int) getX() >> 4, (int) getY() >> 4);
		Vector2i destination = new Vector2i(pacTileX + 2 * dirX, pacTileY + 2 * dirY);

		// If destination is a wall, target PacMan's current tile instead
		if (level.getTile((int) destination.getX(), (int) destination.getY()).solid()) {
			destination = new Vector2i(pacTileX, pacTileY);
		}

		// A* pathfinding to the target tile (builds path from destination back to start)
		List<Node> path = level.findPath(start, destination);
		if (path != null && path.size() > 0) {
			Vector2i vec = path.get(path.size() - 1).tile;
			if (x < (int) vec.getX() << 4) xa++;
			if (x > (int) vec.getX() << 4) xa--;
			if (y < (int) vec.getY() << 4) ya++;
			if (y > (int) vec.getY() << 4) ya--;
		}

		// Movement of ghost is determined by a feasible A* path - i.e. no path, no movement
		if (xa != 0 || ya != 0) {
			move(xa, ya);
			walking = true;
		} else {
			walking = false;
		}
		if (time % 60 > 120) time = 0;
	}
	
	@Override
    public void update() {
        if (isScared) {
            xa = random.nextInt(3) - 1;
            ya = random.nextInt(3) - 1;
            animSprite.update();

            if (xa != 0 || ya != 0) {
                move(xa, ya);
                walking = true;
            } else {
                walking = false;
            }
        } else {
            move();
            if (walking)
                animSprite.update();
            else
                animSprite.setFrame(0);

            if (ya < 0)
                dir = Direction.UP;
            else if (ya > 0)
                dir = Direction.DOWN;
            if (xa < 0)
                dir = Direction.LEFT;
            else if (xa > 0)
                dir = Direction.RIGHT;
        }
    }

    @Override
    public void render(Screen screen) {
        sprite = animSprite.getSprites();
        screen.renderPlayerDynamic((int) x - 7, (int) y - 7, sprite, false);
    }
}