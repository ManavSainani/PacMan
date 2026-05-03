package game.entity.mob;

import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;
import game.util.Vector2i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class RedGhost extends Mob {

	private double xa = 0;
	private double ya = 0;
	private int time = 0;

	private boolean isScared = false;

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

	/**
	 * BFS-based pursuit movement.
	 *
	 * Blinky uses Breadth-First Search to find the true shortest path through
	 * the maze to Pac-Man's current tile, then steps one tile along that path
	 * each update. BFS guarantees the shortest path in an unweighted grid,
	 * making RedGhost the most direct and relentless chaser.
	 *
	 * Based on the Rule-Based Pursuit Algorithm described in:
	 * Novikov, A., Yakovlev, S., & Gushchin, I. (2025).
	 * Radioelectronic and Computer Systems, 1(113), 327-337.
	 * https://doi.org/10.32620/reks.2025.1.21
	 */
	private void move() {
		xa = 0;
		ya = 0;
		time++;

		// Get Pac-Man's tile position via the level reference (same pattern as
		// PinkGhost)
		int px = (int) level.getClientsPlayer().getX();
		int py = (int) level.getClientsPlayer().getY();
		Vector2i start = new Vector2i((int) getX() >> 4, (int) getY() >> 4);
		Vector2i destination = new Vector2i(px >> 4, py >> 4);

		// Run BFS to get the shortest path to Pac-Man
		List<Vector2i> path = bfsPath(start, destination);

		// Step toward the next tile on the path
		// Index 0 is the current tile, index 1 is the next step toward Pac-Man
		if (path != null && path.size() > 1) {
			Vector2i next = path.get(1);
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

	/**
	 * Breadth-First Search from start tile to goal tile.
	 *
	 * Explores all four cardinal neighbors level by level, skipping any tile
	 * that is solid (a wall). Records each tile's predecessor in cameFrom so
	 * the shortest path can be reconstructed by walking backwards from the goal.
	 *
	 * Time complexity: O(V) — visits each walkable tile at most once.
	 * Space complexity: O(V) — for the visited map and queue.
	 *
	 * @param start tile coordinate of RedGhost
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
		// Maps each visited tile to the tile it was reached from
		Map<Vector2i, Vector2i> cameFrom = new HashMap<>();

		queue.add(start);
		cameFrom.put(start, null); // start has no predecessor

		// Cardinal directions only — no diagonals
		int[] dx = { 0, 0, -1, 1 };
		int[] dy = { -1, 1, 0, 0 };

		while (!queue.isEmpty()) {
			Vector2i current = queue.poll();

			if (current.equals(goal)) {
				return reconstructPath(cameFrom, start, goal);
			}

			for (int i = 0; i < 4; i++) {
				int nx = (int)current.getX() + dx[i];
				int ny = (int) current.getY() + dy[i];
				Vector2i neighbor = new Vector2i(nx, ny);

				// Only visit unseen, walkable (non-solid) tiles
				if (!cameFrom.containsKey(neighbor) && !level.getTile(nx, ny).solid()) {
					cameFrom.put(neighbor, current);
					queue.add(neighbor);
				}
			}
		}

		return null; // No path found
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