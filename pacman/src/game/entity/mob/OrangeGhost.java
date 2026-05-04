package game.entity.mob;

import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;
import game.util.Vector2i;

public class OrangeGhost extends Mob {

	private double xa = 0;
	private double ya = 0;
	private int time = 0;

	private boolean isScared = false;

	/**
	 * Clyde flees to his scatter corner when Pac-Man is within this many tiles
	 * (Euclidean distance). 8 tiles.
	 */
	private static final double FLEE_RADIUS = 8.0;

	/**
	 * Clyde's scatter corner in tile coordinates: where he retreats when
	 * Pac-Man gets too close. Bottom left area of the maze.
	 */
	private static final Vector2i SCATTER_CORNER = new Vector2i(2, 29);

	private AnimatedSprite animSprite = new AnimatedSprite(SpriteSheet.orangeGhost, 16, 16, 3);
	private Sprite sprite;

	public OrangeGhost(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}

	public void turnSpriteScared() {
		animSprite = new AnimatedSprite(SpriteSheet.scaredGhost, 16, 16, 3);
		isScared = true;
	}

	public void turnSpriteUnscared() {
		animSprite = new AnimatedSprite(SpriteSheet.orangeGhost, 16, 16, 3);
		isScared = false;
	}

	public void setLocation(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}

	private double getDistance(Vector2i tile, Vector2i goal) {
		double dx = tile.getX() - goal.getX();
		double dy = tile.getY() - goal.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Classic Clyde (OrangeGhost) pursuit/scatter movement.
	 *
	 * When far from Pac-Man (distance > 8 tiles): chases Pac-Man directly
	 * using the level's A* pathfinder, identical to how other ghosts chase.
	 *
	 * When close to Pac-Man (distance <= 8 tiles): panics and retreats to
	 * his scatter corner (bottom-left of the maze) instead of continuing
	 * the chase. He approaches, gets scared, runs away, gets far enough,
	 * then turns back around to chase again, making him unpredictable.
	 *
	 * Reference:
	 * https://pacman.fandom.com/wiki/Maze_Ghost_AI_Behaviors
	 * https://nti.khai.edu/ojs/index.php/reks/article/view/reks.2025.1.21
	 */
	private void move() {
		xa = 0;
		ya = 0;
		time++;

		int px = (int) level.getClientsPlayer().getX();
		int py = (int) level.getClientsPlayer().getY();
		Vector2i start = new Vector2i((int) getX() >> 4, (int) getY() >> 4);
		Vector2i pacmanTile = new Vector2i(px >> 4, py >> 4);

		double distToPacman = getDistance(start, pacmanTile);

		// Choose target: chase Pac-Man if far, scatter to corner if close
		Vector2i target = (distToPacman > FLEE_RADIUS) ? pacmanTile : SCATTER_CORNER;

		// Use the level's existing A* pathfinder to reach the target
		java.util.List<game.level.Node> path = level.findPath(start, target);

		if (path != null && path.size() > 0) {
			// A* returns path from goal back to start, so last element is the next step
			Vector2i next = path.get(path.size() - 1).tile;
			int nextPx = (int) next.getX() << 4;
			int nextPy = (int) next.getY() << 4;

			if (x < nextPx)
				xa++;
			if (x > nextPx)
				xa--;
			if (y < nextPy)
				ya++;
			if (y > nextPy)
				ya--;
		}

		if (xa != 0 || ya != 0) {
			move(xa, ya);
			walking = true;
		} else {
			walking = false;
		}

		if (time % 60 > 120)
			time = 0;
	}

	@Override
	public void update() {
		if (isScared) {
			// Random walk when frightened — same pattern as other ghosts
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