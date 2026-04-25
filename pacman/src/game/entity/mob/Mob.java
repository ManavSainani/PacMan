package game.entity.mob;

import game.entity.Entity;
import game.graphics.Screen;

public abstract class Mob extends Entity {
	
	protected boolean moving = false;
	protected boolean walking = false;
	protected int health;
	
	protected enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT,
	}
	protected Direction dir;
	
	public void move(double xa, double ya) {
		if(xa != 0 && ya != 0) {
			move(xa, 0);
			move(0, ya);
			return;
		}
		if(xa > 0) dir = Direction.RIGHT;
		if(xa < 0) dir = Direction.LEFT;
		if(ya > 0) dir = Direction.DOWN;
		if(ya < 0) dir = Direction.UP;
		
		while(xa != 0) {
			if(Math.abs(xa) > 1) {
				if(!collision(abs(xa),ya)) {
					this.x += abs(xa);
				}
				xa -= abs(xa);
			} 
			else {
				if(!collision(abs(xa), ya)) {
					this.x += xa;
				}
				xa = 0;
			}
		}
		while(ya != 0) {
			if(Math.abs(ya) > 1) {
				if(!collision(xa, abs(ya))) {
					this.y += ya;	
				}
				ya -= abs(ya);
			}
			else {
				if(!collision(xa, abs(ya))) {
					this.y += ya;
				}
				ya = 0;
			}
		}
	}
	
	private int abs(double value) {
		if(value < 0) return -1;
		return 1;
	}
	
	public abstract void update();
	
	public abstract void render(Screen screen);
	
		/*
		Tile tile = level.getTile((int)x >> 4, (int)y >> 4);
		
		System.out.println("playerX = " + ((int)x >> 4 ) + ", playerY = " + ((int)y >> 4));
		
		System.out.println(tile.getClass().toString());
		if(tile instanceof SpawnWallTile) {
			if(xa > 0 ) x = x - 1;
			else if(xa < 0) x = x + 1;
			else x = x;
			if(ya > 0) y = y - 1;
			else if(ya < 0) y = y + 1;
			else y = y;
			return true;
		}
		else return false;
		*/
		//System.out.println(tile.getClass().toString());				
	
	protected boolean collision(double xa, double ya) {
		boolean solid = false;
		int incX = 7;
		int incY = 7;
		for(int c = 0; c < 4; c++) {
			if(xa < 0) incX = -incX;
			if(ya < 0) incY = -incY;
			double xt = ((x + xa) - c % 2 * 16 + incX) / 16;
			double yt = ((y + ya) - c / 2 * 16 + incY) / 16;
			
			int ix = (int) Math.ceil(xt);
			int iy = (int) Math.ceil(yt);
			if(c % 2 == 0) ix = (int) Math.floor(xt);
			if(c / 2 == 0) iy = (int) Math.floor(yt);
			if(level.getTile(ix, iy).solid()) solid = true;
		}
		return solid;
	} 
}
