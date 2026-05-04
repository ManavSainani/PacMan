package game.entity.mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;
import game.level.Node;
import game.util.Vector2i;

public class BlueGhost extends Mob {

	private double xa = 0;
	private double ya = 0;
	private int time = 0;

	private boolean isScared = false;

	/**
	 * BFS and A* (Blinky and Pinky) hybrid
	 * 
	 * Tracks which algorithm to use each tick.
	 * true = Blinky mode: BFS to Pac-Man's current tile
	 * false = Pinky mode: A* to 2 tiles ahead of Pac-Man
	 */
	private boolean useBFS = true;

	private AnimatedSprite animSprite = new AnimatedSprite(SpriteSheet.blueGhost, 16, 16, 3);
	private Sprite sprite;

	public BlueGhost(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}

	public void turnSpriteScared() {
		animSprite = new AnimatedSprite(SpriteSheet.scaredGhost, 16, 16, 3);
		isScared = true;
	}

	public void turnSpriteUnscared() {
		animSprite = new AnimatedSprite(SpriteSheet.blueGhost, 16, 16, 3);
		isScared = false;
	}

	public void setLocation(int x, int y) {
		this.x = x << 4;
		this.y = y << 4;
	}

	/**
	 * Hybrid pursuit movement, alternates between Blinky's BFS and Pinky's A* each
	 * tick.
	 *
	 * Inky is a hybrid of Blinky and Pinky:
	 *
	 * Blinky mode (BFS): targets Pac-Man's current tile and finds the shortest
	 * path using Breadth First Search. (direct)
	 *
	 * Pinky mode (A*): targets 2 tiles ahead of Pac-Man's current direction
	 * and navigates there using the level's A* pathfinder. (predictive)
	 *
	 * By toggling between the two every update tick, Inky runs both
	 * algorithms and exhibits both behaviors, making him harder to predict than
	 * either ghost alone.
	 *
	 * Based on:
	 * Novikov, A., Yakovlev, S., & Gushchin, I. (2025).
	 * https://nti.khai.edu/ojs/index.php/reks/article/view/reks.2025.1.21
	 */
	private void move() {
		xa = 0;
		ya = 0;
		time++;

		// Pac-Man's current tile position
		int pacTileX = (int) level.getClientsPlayer().getX() >> 4;
		int pacTileY = (int) level.getClientsPlayer().getY() >> 4;

		Vector2i start = new Vector2i((int) getX() >> 4, (int) getY() >> 4);
		Vector2i currentTile = new Vector2i(pacTileX, pacTileY);

		// Toggle algorithm every tick
		// true = BFS (Blinky), false = A* (Pinky)
		useBFS = !useBFS;

		if (useBFS) {
			// --- BLINKY MODE: BFS to Pac-Man's current tile ---
			List<Vector2i> path = bfsPath(start, currentTile);

			if (path != null && path.size() > 1) {
				Vector2i next = path.get(1);
				int nextPx = (int)next.getX() << 4;
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

		} else {
			// --- PINKY MODE: A* to 2 tiles ahead of Pac-Man's direction ---

			// Read Pac-Man's current movement direction (same as PinkGhost)
			int dirX = 0;
			int dirY = 0;
			if (Player.direction == 2)
				dirY = -1;
			if (Player.direction == 3)
				dirY = 1;
			if (Player.direction == 1)
				dirX = -1;
			if (Player.direction == 0)
				dirX = 1;

			Vector2i predicted = new Vector2i(pacTileX + 2 * dirX, pacTileY + 2 * dirY);

			// Fall back to current tile if predicted tile is a wall (same as PinkGhost)
			if (level.getTile((int) predicted.getX(), (int) predicted.getY()).solid()) {
				predicted = currentTile;
			}

			List<Node> path = level.findPath(start, predicted);

			if (path != null && path.size() > 0) {
				Vector2i vec = path.get(path.size() - 1).tile;
				if (x < (int) vec.getX() << 4)
					xa++;
				if (x > (int) vec.getX() << 4)
					xa--;
				if (y < (int) vec.getY() << 4)
					ya++;
				if (y > (int) vec.getY() << 4)
					ya--;
			}
		}

		if (xa != 0 || ya != 0) {
			move(xa * speed, ya * speed);
			walking = true;
		} else {
			walking = false;
		}

		if (time % 60 > 120)
			time = 0;
	}

	/**
	 * Breadth-First Search from start tile to goal tile.
	 *
	 * Explores all four cardinal neighbors level by level, skipping solid tiles.
	 * Records each tile's predecessor so the shortest path can be reconstructed
	 * by walking backwards from the goal.
	 *
	 * Time complexity: O(V) - visits each walkable tile at most once.
	 * Space complexity: O(V) — for the visited map and queue.
	 *
	 * @param start tile coordinate of BlueGhost
	 * @param goal  tile coordinate of Pac-Man
	 * @return ordered list of tiles [start, ..., goal], or null if no path found
	 */
	private List<Vector2i> bfsPath(Vector2i start, Vector2i goal) {
		if (start.equals(goal)) {
			List<Vector2i> trivial = new ArrayList<>();
			trivial.add(start);
			return trivial;
		}

		Queue<Vector2i> queue = new LinkedList<>();
		Map<Vector2i, Vector2i> cameFrom = new HashMap<>();

		queue.add(start);
		cameFrom.put(start, null);

		int[] dx = { 0, 0, -1, 1 };
		int[] dy = { -1, 1, 0, 0 };

		while (!queue.isEmpty()) {
			Vector2i current = queue.poll();

			if (current.equals(goal)) {
				return reconstructPath(cameFrom, start, goal);
			}

			for (int i = 0; i < 4; i++) {
				int nx = (int) current.getX() + dx[i];
				int ny = (int) current.getY() + dy[i];
				Vector2i neighbor = new Vector2i(nx, ny);

				if (!cameFrom.containsKey(neighbor) && !level.getTile(nx, ny).solid()) {
					cameFrom.put(neighbor, current);
					queue.add(neighbor);
				}
			}
		}

		return null;
	}

	/**
	 * Reconstructs the path from start to goal by walking backwards
	 * through the predecessor map built during BFS.
	 */
	private List<Vector2i> reconstructPath(Map<Vector2i, Vector2i> cameFrom,
			Vector2i start, Vector2i goal) {
		LinkedList<Vector2i> path = new LinkedList<>();
		Vector2i current = goal;

		while (current != null) {
			path.addFirst(current);
			current = cameFrom.get(current);
		}

		return path;
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