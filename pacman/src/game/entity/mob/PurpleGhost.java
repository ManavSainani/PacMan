package game.entity.mob;

//import game.entity.mob.Mob.Direction;
import game.graphics.AnimatedSprite;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.SpriteSheet;
import game.level.Node;
import game.level.tile.Tile;
import game.util.Vector2i;
import java.util.ArrayList;
import java.util.List;

// Sources:
// https://www.geeksforgeeks.org/artificial-intelligence/depth-limited-search-for-ai/
// https://www.tpointtech.com/depth-limited-search-in-artificial-intelligence
// Claude Code

public class PurpleGhost extends Mob {

	private double xa = 0;
	private double ya = 0;
	private int numX = 1;
	private int numY = 1;

	// Maximum distance ghost will chase before giving up and wandering randomly
	private static final int DEPTH_LIMIT = 12;
	private int time = 0;

	private boolean isScared = false;
	private boolean walking = false;

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

	// Depth-Limited Search (DLS) - path built by recursive backtracking (destination to start) 
	// or null if unreachable according to set depth limit. Similar to DFS but with depth limit 
	// for better memory efficient and infinite loop prevention.
	private List<Node> depthLimitedSearch(Vector2i start, Vector2i target) {
    List<Node> path = new ArrayList<>();
    java.util.Set<Vector2i> visited = new java.util.HashSet<>(); 
    if (dls(start, target, DEPTH_LIMIT, path, visited)) return path;
    return null;
	}
	// Neighboring nodes are explored recursively until target is found (Pac-Man) or depth limit is reached.
	private boolean dls(Vector2i current, Vector2i target, int depth, List<Node> path, java.util.Set<Vector2i> visited) {
		// Base Case: Target found or depth limit reached
		if (current.equals(target)) return true;
		if (depth == 0) return false;
		visited.add(current);

		// Explore neighbor in cardinal directions (up, down, left, right)
		int[] dxs = {-1, 1, 0, 0};
		int[] dys = {0, 0, -1, 1};
		for (int i = 0; i < 4; i++) {
			Vector2i next = new Vector2i((int) current.getX() + dxs[i], (int) current.getY() + dys[i]);
			if (visited.contains(next)) continue; // skip visited nodes
			Tile at = level.getTile((int) next.getX(), (int) next.getY());
			if (at == null || at.solid()) continue; // skip out of bouunds or tiles
			if (dls(next, target, depth - 1, path, visited)) {
				path.add(new Node(next, null, 0, 0));
				return true; // backtrack path if target found
			}
		}
		return false; // target not found within depth limit
	}

	private void move() {
		xa = 0;
		ya = 0;
		time++;

		Vector2i start = new Vector2i((int) getX() >> 4, (int) getY() >> 4);
		int px = (int) level.getClientsPlayer().getX();
		int py = (int) level.getClientsPlayer().getY();
		Vector2i target = new Vector2i(px >> 4, py >> 4);

		List<Node> path = depthLimitedSearch(start, target);

		if (path != null && path.size() > 0) {
			// Reversed order of nodes in path due to backtracking
			Vector2i vec = path.get(0).tile;
			if (x < (int) vec.getX() << 4) xa++;
			if (x > (int) vec.getX() << 4) xa--;
			if (y < (int) vec.getY() << 4) ya++;
			if (y > (int) vec.getY() << 4) ya--;
		} else {
			// Pac-Man not reachable within depth limit — wander randomly
			if (time % 60 == 0) {
				numX = random.nextInt(3) - 1;
				numY = random.nextInt(3) - 1;
			}
			xa = -numX;
			ya = -numY;
		}

		if (xa != 0 || ya != 0) {
			move(xa * speed, ya * speed);
			walking = true;
		} else {
			walking = false;
		}
		if (time % 60 > 120) time = 0;
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
