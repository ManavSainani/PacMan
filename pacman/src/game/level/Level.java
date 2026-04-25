package game.level;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import game.Game;
import game.entity.Entity;
import game.entity.mob.Banana;
import game.entity.mob.BigDot;
import game.entity.mob.BlueGhost;
import game.entity.mob.GreenGhost;
import game.entity.mob.OrangeGhost;
import game.entity.mob.PacmanLife;
import game.entity.mob.PinkGhost;
import game.entity.mob.Player;
import game.entity.mob.Poop;
import game.entity.mob.PurpleGhost;
import game.entity.mob.RedGhost;
import game.entity.mob.SmallDot;
import game.entity.mob.Strawberry;
import game.graphics.Screen;
import game.level.tile.Tile;
import game.util.Vector2i;

public class Level {
	
	public static Level spawn = new Level("/game/res/pac-man_level_design.png");
	
	private Player player;
	protected int width, height;
	protected int[] tilesInt;
	protected int[] tiles;
	
	private int time = 0; // for generating foods at approiate times
	public static int numberOfFoods = 3; // total number of foods left in game, intially 3 foods: poop, banana, strawberry
	private boolean spawnFoods = true;
	
	private static boolean isScared = false; // controls whether player can eat a ghost
	private static boolean canEat = false; // second flag to determine if player can eat ghost 
	private static boolean countScaredTime = false; // allow timer to start count
	public static boolean reset = false; // flag to call resetGame method
	
	private static int totalScaredTime = 1; // duration of scared ghosts
	
	// used to calculate players score
	public static int score = 0; // Players total score in game
	private static int totalDots;
	
	/*
	 *  Used to generate big dots at locations
	 *  for efficiency made them fields
	 */
	private TileCoordinate topLeft = new TileCoordinate(2, 2);
	private TileCoordinate bottomLeft = new TileCoordinate(2, 29);
	private TileCoordinate topRight = new TileCoordinate(29, 2);
	private TileCoordinate bottomRight = new TileCoordinate(29, 29);
	
	// Passage way dots that should NOT be rendered AND NOT added to dots arrayList
	private TileCoordinate leftPassage = new TileCoordinate(1, 15);
	private TileCoordinate rightPassage = new TileCoordinate(30, 15);
		
	private List<Entity> foods = new ArrayList<Entity>(); // things that can be eaten. Needs to be public to check
	private List<Entity> enemies = new ArrayList<Entity>(); // ghosts
	private List<Player> players = new ArrayList<Player>(); // pac-man
	public List<Tile> tileList = new ArrayList<Tile>();
	private List<Entity> dots = new ArrayList<Entity>(); // holds ALL dots
	private List<Entity> flashDotsRemaining = new ArrayList<Entity>(); // to turn ghost blue
	private List<Entity> lifes = new ArrayList<Entity>(); // players lifes
	
	//A* search Algorithm fields
	private Comparator<Node> nodeSorter = new Comparator<Node>() {
		public int compare(Node n0, Node n1) {
			if(n1.fCost < n0.fCost) return +1;
			if(n1.fCost > n0.fCost) return -1;
			return 0;
		}
	};
	
	
	public Level(String path) {
		loadLevel(path);
	}
	
	protected void loadLevel(String path) {
		try {
			System.out.println("Trying to load level: " + path + "...");
			BufferedImage image = ImageIO.read(Level.class.getResource(path));
			System.out.println("succeeded load level");
			int w = width = image.getWidth();
			int h = height = image.getHeight();
			tiles = new int[w * h];
			System.out.println("tiles size: " + tiles.length);
			image.getRGB(0, 0, w, h, tiles, 0, w);
		} catch(IOException e) {
			e.printStackTrace();
		}

		//Add NPCs here
		add(new PurpleGhost(15, 14));
		add(new OrangeGhost(16, 15));
		add(new RedGhost(17, 14));
		add(new PinkGhost(15, 16));
		add(new GreenGhost(17, 16));
		add(new BlueGhost(14, 16));
		
		//add player lifes
		add(new PacmanLife(27, 0));
		add(new PacmanLife(28, 0));
		add(new PacmanLife(29, 0));
			
		//testGenerate();
		generateDots();
	}
	
	public int getNumberOfFoodsRemaining() {
		return foods.size();
	}
	
	// Calculates players score
	private void computeScore() {
		int diff = totalDots - dots.size();
		if(diff != 0) {
			score += diff;
			totalDots = dots.size(); //update total dots available
		}
	}
	
	// getter method for score
	public static int getScore() {
		return score;
	}
	
	// Checks if tile location is same
	private boolean sameLocation(TileCoordinate t1, TileCoordinate t2) {
		if(t1.x() == t2.x() && t1.y() == t2.y()) return true;
		else return false;
	}
	
	//Used to generate dots in proper locations on the level
	public void generateDots() {
		for(int y = 1; y < height - 1; y++) {
			for(int x = 1; x < width - 1; x++) {
				if(tiles[x + y * width] == Tile.col_spawn_background) {
					if(x >= 13 && x<= 18 && y >= 13 && y <= 18) continue;
					else {
						TileCoordinate tileCoord = new TileCoordinate(x, y);
						
						if(sameLocation(tileCoord, topLeft)
								|| sameLocation(tileCoord, bottomLeft)
								|| sameLocation(tileCoord, topRight)
								|| sameLocation(tileCoord, bottomRight)) {
							add(new BigDot(tileCoord.x(), tileCoord.y()));
						}
						else if(sameLocation(tileCoord, leftPassage) 
								|| sameLocation(tileCoord, rightPassage)) {
							continue;
						}
						else {
							add(new SmallDot(tileCoord.x(), tileCoord.y()));
						}
					}
				}	
			}
			totalDots = dots.size(); // initialize for score computation
		}
	}
	
	public void testGenerate() {
		TileCoordinate tileCoord = new TileCoordinate(17, 21);

		add(new SmallDot(tileCoord.x(), tileCoord.y()));
		totalDots = dots.size(); // initialize for score computation

	}
	
	// checks if player gets eaten
	private void playerDead() {
		if(!canEat) {
			TileCoordinate playerTileCoord = new TileCoordinate(player.currPlayerX, player.currPlayerY);
			for(int i = 0; i < enemies.size(); i++){
				TileCoordinate ghostTileCoord = new TileCoordinate(((int)enemies.get(i).getX() >> 4), ((int)enemies.get(i).getY() >> 4)); 
				if(sameLocation(playerTileCoord, ghostTileCoord)) {
					
					// reset ghosts and players location
					for(int k = 0; k < enemies.size(); k++) {
						Entity e = enemies.get(k);
						e.init(this);
						if(e instanceof PurpleGhost) ((PurpleGhost)e).setLocation(15, 14);
						if(e instanceof OrangeGhost) ((OrangeGhost)e).setLocation(16, 15);
						if(e instanceof RedGhost) ((RedGhost)e).setLocation(17, 14);
						if(e instanceof PinkGhost) ((PinkGhost)e).setLocation(15, 16);
						if(e instanceof GreenGhost) ((GreenGhost)e).setLocation(17, 16);
						if(e instanceof BlueGhost) ((BlueGhost)e).setLocation(14, 16);
					}
					players.get(0).setPosition(15 << 4, 23 << 4);
					if(lifes.size() == 1) Game.state = Game.STATE.GAMEOVER;
					else lifes.remove(0); // remove a life
				}
			}
		}
	}
	
	private void turnGhostsScared() {
		//check locations
		TileCoordinate playerTileCoord = new TileCoordinate(player.currPlayerX, player.currPlayerY);
		
		// removes flash dot
		for(int i = 0; i < flashDotsRemaining.size(); i++) {
			TileCoordinate flashDotCoord = new TileCoordinate(((int)flashDotsRemaining.get(i).getX() >> 4), ((int)flashDotsRemaining.get(i).getY() >> 4)); 
			if(sameLocation(playerTileCoord, flashDotCoord)) {
				flashDotsRemaining.remove(i); //Must remove so dont continue turning ghost when not there
				isScared = true;
				countScaredTime = true; // allows timer to begin
			}
			if(isScared) {
				for(int k = 0; k < enemies.size(); k++) {
					Entity e = enemies.get(k);
					e.init(this);
					if(e instanceof PurpleGhost) {
						((PurpleGhost)e).turnSpriteScared();
					}
					else if(e instanceof OrangeGhost) {
						((OrangeGhost)e).turnSpriteScared();
					} 
					else if(e instanceof RedGhost) {
						((RedGhost)e).turnSpriteScared();
					}
					else if(e instanceof PinkGhost) {
						((PinkGhost)e).turnSpriteScared();
					}
					else if(e instanceof GreenGhost) {
						((GreenGhost)e).turnSpriteScared();
					}
					else {
						((BlueGhost)e).turnSpriteScared(); // then must be a blue ghost
					}
					isScared = false; // set flag false
					canEat = true;
				}
			}
		}
	}
	
	private void turnGhostUnscared() {
		for(int k = 0; k < enemies.size(); k++) {
			Entity e = enemies.get(k);
			e.init(this);
			if(e instanceof PurpleGhost) {
				((PurpleGhost)e).turnSpriteUnscared();
			}
			else if(e instanceof OrangeGhost) {
				((OrangeGhost)e).turnSpriteUnscared();
			} 
			else if(e instanceof RedGhost) {
				((RedGhost)e).turnSpriteUnscared();
			}
			else if(e instanceof PinkGhost) {
				((PinkGhost)e).turnSpriteUnscared();
			}
			else if(e instanceof GreenGhost) {
				((GreenGhost)e).turnSpriteUnscared();
			}
			else {
				((BlueGhost)e).turnSpriteUnscared(); // then must be a blue ghost
			}
			isScared = false; // set flag false
		}
	}
	
	private void ghostEaten() {
		TileCoordinate playerTileCoord = new TileCoordinate(player.currPlayerX, player.currPlayerY);
		for(int i = 0; i < enemies.size(); i++){
			TileCoordinate ghostTileCoord = new TileCoordinate(((int)enemies.get(i).getX() >> 4), ((int)enemies.get(i).getY() >> 4)); 
			if(sameLocation(playerTileCoord, ghostTileCoord)) {
				Entity e = enemies.get(i);
				e.init(this);
				if(e instanceof PurpleGhost) {
					((PurpleGhost)e).turnSpriteUnscared();
					((PurpleGhost)e).setLocation(15, 14);
				}
				else if(e instanceof OrangeGhost) {
					((OrangeGhost)e).turnSpriteUnscared();
					((OrangeGhost)e).setLocation(16, 15);
				} 
				else if(e instanceof RedGhost) {
					((RedGhost)e).turnSpriteUnscared();
					((RedGhost)e).setLocation(17, 14);
				}
				else if(e instanceof PinkGhost) {
					((PinkGhost)e).turnSpriteUnscared();
					((PinkGhost)e).setLocation(15, 16);
				}
				else if(e instanceof GreenGhost) {
					((GreenGhost)e).turnSpriteUnscared();
					((GreenGhost)e).setLocation(17, 16);				
				}
				else {
					((BlueGhost)e).turnSpriteUnscared(); // then must be a blue ghost
					((BlueGhost)e).setLocation(14, 16);
				}
			}
		}
	}
	
	private void foodsEaten() {
		TileCoordinate playerTileCoord = new TileCoordinate(player.currPlayerX, player.currPlayerY);
		for(int i = 0; i < foods.size(); i++) {
			TileCoordinate foodTileCoord = new TileCoordinate(((int)foods.get(i).getX() >> 4), ((int)foods.get(i).getY() >> 4)); 
			if(sameLocation(playerTileCoord, foodTileCoord)) {
				foods.remove(i);
				score += 100; // 100 points to score
			}
		}
	}
	
	public void resetGameLevel() {
		generateDots(); //reset all dots to level
		score = 0; // reset score
		time = 0;
		spawnFoods = true; // allow for spawning of foods again
		foods.clear();
		players.get(0).setPosition(15 << 4, 23 << 4); //reset player position
		// *Note: Game state is updated in menu screen classes

		for(int i = 0; i < enemies.size(); i++) {
			Entity e = enemies.get(i);
			e.init(this);
			
			if(e instanceof PurpleGhost) ((PurpleGhost)e).setLocation(15, 14);
			if(e instanceof OrangeGhost) ((OrangeGhost)e).setLocation(16, 15);
			if(e instanceof RedGhost) ((RedGhost)e).setLocation(17, 14);
			if(e instanceof PinkGhost) ((PinkGhost)e).setLocation(15, 16);
			if(e instanceof GreenGhost) ((GreenGhost)e).setLocation(17, 16);
			if(e instanceof BlueGhost) ((BlueGhost)e).setLocation(14, 16);
		}
		// add player lifes
		lifes.clear();
		add(new PacmanLife(27, 0));
		add(new PacmanLife(28, 0));
		add(new PacmanLife(29, 0));	
		reset = false;
	}
	
	public void update() {
		
		for(int i = 0; i < players.size(); i++) {
			players.get(i).update(); //updates players
		}
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).update();
		}
		 // must update dots ArrayList for animated flashDots
		for(int i = 0; i < dots.size(); i++) {
			dots.get(i).update();
		}
		for(int i = 0; i < foods.size(); i++) {
			foods.get(i).update();
		}
		playerDead();
		computeScore(); // updates players score
		foodsEaten(); // update foods
		
		// changing and unchanging ghosts
		turnGhostsScared(); // turns ghost blue when big dot is eaten
		if(canEat) ghostEaten(); //checks if player hits same spot as that ghost and removes the ghost
		
		if(countScaredTime) totalScaredTime++; // timer begins
		if(totalScaredTime % 601 == 0) { // NOTE updating 60 times per second, 600 = 60 * 10 seconds
			turnGhostUnscared();
			totalScaredTime = 1;// rest scared time
			countScaredTime = false; // reset
			canEat = false; // reset
		}
		
		// add & remove foods
		time++;
		if(spawnFoods && time < 601 && time % 600 == 0) { // spawns 10 seconds into game
			add(new Poop(8, 23));
		}
		if(spawnFoods && time < 3601 && time % 3600 == 0) { // spawns 60 seconds into the game
			if(!foods.isEmpty()) foods.remove(0); // removes Poop ONLY if player has not eaten it
			add(new Banana(2, 2));
		}
		if(spawnFoods && time < 6001 && time % 6000 == 0) { // spawns after 100 seconds from game start
			if(!foods.isEmpty()) foods.remove(0); // removes Banana ONLY if player has not eaten it
			add(new Strawberry(5, 29));
		}
		if(time > 9000) {
			time = 0; //reset value to avoid game crash
			spawnFoods = false; // set flag to no more respawns
		}
		
		if(dots.isEmpty()) Game.state = Game.state.GAMEWIN; // if no dots left, player has won, change state			
		if(reset) resetGameLevel(); // check to replay
	}
	
	public void render(Screen screen) {
		int x1 = screen.width;
		int y1 = screen.height;
		for(int y = 0; y < y1; y++) {
			for(int x = 0; x < x1; x++) {
				getTile(x, y).render(x, y, screen);
			}
		}
		for(int i = 0; i < players.size(); i++) {
			players.get(i).render(screen);
		}
		for(int i = 0; i < lifes.size(); i++) {
			lifes.get(i).render(screen);
		}
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).render(screen);
		}
		for(int i = 0; i < dots.size(); i++) {
			dots.get(i).render(screen);
		}
		for(int i = 0; i < foods.size(); i++) {
			foods.get(i).render(screen);
		}
	}
	
	public void add(Entity e) {
		e.init(this);
		if(e instanceof Player) {
			players.add((Player)e);
		}
		else if(e instanceof PacmanLife) {
			lifes.add((PacmanLife)e);
		}
		else if(e instanceof SmallDot) {
			dots.add((SmallDot)e);
		}
		else if(e instanceof BigDot) {
			dots.add((BigDot)e);
			flashDotsRemaining.add((BigDot)e); 
		}
		else if(e instanceof Poop || e instanceof Banana || e instanceof Strawberry) {
			foods.add(e);
		}
		else {
			enemies.add(e);
		}
	}
	
	// Removes the dots from level when player hits them
	public void removeDots(int x, int y) {
		TileCoordinate coord = new TileCoordinate((x >> 4), (y >> 4));
		for (int i = 0; i < dots.size(); i++) {
			if(coord.x() == dots.get(i).getX() && coord.y() == dots.get(i).getY()) {
				dots.remove(i);
			}
		}
	}
	
	public void remove(Entity e) {
		e.init(this);
		if(e instanceof SmallDot) {
			dots.remove((SmallDot)e);
		}
		else if(e instanceof PacmanLife) {
			lifes.remove((PacmanLife)e);
		}
	}
	
	// A* Algorithm search Algorithm
	public List<Node> findPath(Vector2i start, Vector2i goal) {
		List<Node> openList = new ArrayList<Node>(); //All possible Nodes(tiles) that could be shortest path
		List<Node> closedList = new ArrayList<Node>(); //All no longer considered Nodes(tiles)
		Node current = new Node(start, null, 0, getDistance(start, goal)); //Current Node that is being considered(first tile)			openList.add(current);
		//System.out.println("start = " + start + ", getDistance = " + getDistance(start, goal));
		openList.add(current);
		while(openList.size() > 0) {
			Collections.sort(openList, nodeSorter); // will sort open list based on what's specified in the comparator
				current = openList.get(0); // sets current Node to first possible element in openList
				if(current.tile.equals(goal)) {
					List<Node> path = new ArrayList<Node>(); //adds the nodes that make the path 
					while(current.parent != null) { //retraces steps from finish back to start
						path.add(current); // add current node to list
						current = current.parent; //sets current node to previous node to trace path back to start
					}
					openList.clear(); //erases from memory since algorithm is finished, ensures performance is not affected since garbage collection may not be called
					closedList.clear();
					return path; //returns the desired result shortest/quickest path
				}
				openList.remove(current); //if current Node is not part of path to goal remove
				closedList.add(current); //and puts it in closedList, because it's not used
				for(int i = 0; i < 9; i++ ) { //8-adjacent tile possibilities
					if(i == 4) continue; //index 4 is the middle tile (tile player currently stands on), no reason to check it
					int x = (int)current.tile.getX();
					int y = (int)current.tile.getY();
					int xi = (i % 3) - 1; //will be either -1, 0 or 1
					int yi = (i / 3) - 1; // sets up a coordinate position for Nodes (tiles)
					Tile at = getTile(x + xi, y + yi); // at tile be all surrounding tiles when iteration is run
					if(at == null) continue; //if empty tile skip it
					//Commented out code to avoid game crash until collision is more exact
					//if(at.solid()) continue; //if solid cant pass through so skip/ don't consider this tile
					Vector2i a = new Vector2i(x + xi, y + yi); //Same thing as node(tile), but changed to a vector
					double gCost = current.gCost + (getDistance(current.tile, a) == 1 ? 1 : 0.95); //*calculates only adjacent nodes* current tile (initial start is 0) plus distance between current tile to tile being considered (a)
					double hCost = getDistance(a, goal);								// conditional piece above for gCost makes a more realist chasing, because without it mob will NOT use diagonals because higher gCost
					Node node = new Node(a, current, gCost, hCost);
					if(vecInList(closedList, a) && gCost >= node.gCost) continue; //is node has already been checked 
					if(!vecInList(openList, a) || gCost < node.gCost) openList.add(node);
				}
			}
			closedList.clear(); //clear the list, openList will have already been clear if no path was found
			return null; //a default return if no possible path was found
	}
	
	private boolean vecInList(List<Node> list, Vector2i vector) {
		for(Node n : list) {
			if(n.tile.equals(vector)) return true;
		}
		return false; // if gone through entire list and NOT there return false
	  }
	  
	  // Distance Method used in A* Algorithm above
	  private double getDistance(Vector2i tile, Vector2i goal) {
		  double dx = tile.getX() - goal.getX();
		  double dy = tile.getY() - goal.getY();
		  return Math.sqrt(dx * dx + dy * dy); //distance 
	  }
	//END OF A* SEARCH ALGORITHM CODE
	
	public Player getClientsPlayer() {
		return players.get(0); //returns the first player, the main player is always first in Player List
	}
	
	public Tile getTile(int x, int y) {
		
		/* This must be before evaluating if statement of tiles to avoid crash. 
		Fixes array index out of bounds on the map to avoid crashing when player 
		goes left too far or up too far OR right or down too far.Crashes because 
		either index becomes negative OR we exceed the index.
		*/
		if(x < 0 || y < 0 || x >= width || y >= height) return Tile.voidTile;
		
		if(tiles[x + y * width] == Tile.col_spawn_background) return Tile.spawnBackgroundTile;
		if(tiles[x + y * width] == Tile.col_spawn_RightEdge) return Tile.spawnRightEdgeTile;
		if(tiles[x + y * width] == Tile.col_spawn_LeftEdge) return Tile.spawnLeftEdgeTile;
		if(tiles[x + y * width] == Tile.col_spawn_TopEdge) return Tile.spawnTopEdgeTile;
		if(tiles[x + y * width] == Tile.col_spawn_BottomEdge) return Tile.spawnBottomEdgeTile;
		if(tiles[x + y * width] == Tile.col_spawn_TLCorner) return Tile.spawnTLCornerTile;
		if(tiles[x + y * width] == Tile.col_spawn_TRCorner) return Tile.spawnTRCornerTile;
		if(tiles[x + y * width] == Tile.col_spawn_BLCorner) return Tile.spawnBLCornerTile;
		if(tiles[x + y * width] == Tile.col_spawn_BRCorner) return Tile.spawnBRCornerTile;
		if(tiles[x + y * width] == Tile.col_spawn_LeftSide) return Tile.spawnLeftSideTile;
		if(tiles[x + y * width] == Tile.col_spawn_RightSide) return Tile.spawnRightSideTile;
		if(tiles[x + y * width] == Tile.col_spawn_TopSide) return Tile.spawnTopSideTile;
		if(tiles[x + y * width] == Tile.col_spawn_BottomSide) return Tile.spawnBottomSideTile;
		
		return Tile.voidTile;
	}
}
