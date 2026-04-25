package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.spawn_level.SpawnBackgroundTile;
import game.level.tile.spawn_level.SpawnWallTile;
import game.level.tile.spawn_level.VoidTile;

public class Tile {
	
	public Sprite sprite;

	public static Tile spawnBackgroundTile = new SpawnBackgroundTile(Sprite.spawnBackground);
	public static Tile spawnRightEdgeTile = new SpawnWallTile(Sprite.spawnRightEdge);
	public static Tile spawnLeftEdgeTile = new SpawnWallTile(Sprite.spawnLeftEdge);
	public static Tile spawnTopEdgeTile = new SpawnWallTile(Sprite.spawnTopEdge);
	public static Tile spawnBottomEdgeTile = new SpawnWallTile(Sprite.spawnBottomEdge);
	public static Tile spawnTLCornerTile = new SpawnWallTile(Sprite.spawnTLCorner);
	public static Tile spawnTRCornerTile = new SpawnWallTile(Sprite.spawnTRCorner);
	public static Tile spawnBLCornerTile = new SpawnWallTile(Sprite.spawnBLCorner);
	public static Tile spawnBRCornerTile = new SpawnWallTile(Sprite.spawnBRCorner);
	public static Tile spawnLeftSideTile = new SpawnWallTile(Sprite.spawnLeftSide);
	public static Tile spawnRightSideTile = new SpawnWallTile(Sprite.spawnRightSide);
	public static Tile spawnTopSideTile = new SpawnWallTile(Sprite.spawnTopSide);
	public static Tile spawnBottomSideTile = new SpawnWallTile(Sprite.spawnBottomSide);

	public static Tile voidTile = new VoidTile(Sprite.voidSprite);
	
	public static final int col_spawn_voidColor = 0xff232323;
	public static final int col_spawn_background = 0xff000000;
	public static final int col_spawn_RightEdge = 0xffac3232; 
	public static final int col_spawn_LeftEdge = 0xffc22525;
	public static final int col_spawn_TopEdge = 0xfff22e2e;
	public static final int col_spawn_BottomEdge = 0xfff20f0f;
	public static final int col_spawn_TLCorner = 0xff0000fb;
	public static final int col_spawn_TRCorner = 0xff0000f2;
	public static final int col_spawn_BLCorner = 0xff0000e8;	
	public static final int col_spawn_BRCorner = 0xff0000d9;
	public static final int col_spawn_LeftSide = 0xff3f3f74;
	public static final int col_spawn_RightSide = 0xff3fff74;
	public static final int col_spawn_TopSide = 0xff3fe274;
	public static final int col_spawn_BottomSide = 0xff81e3a2;
	
	public Tile(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public void render(int x, int y, Screen screen) {
		
	}
	
	public boolean solid() {
		return false; //returns false, if method is NOT overridden in subclasses
	}
}
