package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import game.entity.mob.Player;
import game.graphics.Screen;
import game.input.Keyboard;
import game.level.Level;
import game.level.TileCoordinate;
import game.menu.GameOverMenu;
import game.menu.MainMenu;
import game.menu.WinMenu;
import game.sound.Sound;

/*
 * This is a recreation of the classic Pac-Man game.
 * 
 * @author Zev Yirmiyahu
 * 
 * E-Mail: zy@zevyirmiyahu.com
 * 
 * GitHub: https://github.com/zevyirmiyahu 
 * 
 * Personal Website: www.zevyirmiyahu.com
 * 
 */

public class Game extends Canvas implements Runnable {

	private static int width = 512;
	private static int height = width;
	private static int scale = 2;
	private static int decrement = 128; // for size reducing
	
	private static String title = "Pac-Man";
	private boolean running = false;
	private static boolean beginSound = true;
	
	private Level level;
	private Thread thread;
	private JFrame frame;
	
	public static Keyboard key;
	
	private Screen screen;
	private Player player;
	
	private static Sound sound = new Sound("pacman_intro", "pacman_chomp", "pacman_dies_y");
	private static int time = 0; // used for sound clips
	
	public static ExecutorService pool = Executors.newFixedThreadPool(2); // use multi threads together
	
	private static TileCoordinate playerSpawn = new TileCoordinate(16, 23);
	
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	
	private static MainMenu mainMenu;
	private static WinMenu winMenu;
	private static GameOverMenu gameOverMenu;
	
	public static enum STATE {
		MENU, //main menu screen
		GAME, //game
		GAMEWIN, //game winner menu
		GAMEOVER; //game over screen
	};
	
	public static STATE state = STATE.MENU;
	
	public Game() {
		Dimension size = new Dimension(width * scale - decrement, height * scale - decrement);
		
		screen = new Screen(width, height);
		frame = new JFrame();
		setPreferredSize(size);
		setFocusable(true);
		
		level = Level.spawn;
		key = new Keyboard();
		mainMenu = new MainMenu(key);
		winMenu = new WinMenu(key);
		gameOverMenu = new GameOverMenu(key);
		player = new Player (playerSpawn.x(), playerSpawn.y(), key);
		level.add(player);
		addKeyListener(key);
	}
	
	public static Keyboard getKey() {
		return key;
	}
	
	public synchronized void start() {
		thread = new Thread(this, "Display");
		thread.start();
	}
	
	public void stop() {
		running = false;
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		running = true;
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1_000_000_000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				update();
				updates++;
				delta--;
			}
			render();
			frames++;
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(title + " | " + updates + "UPS, " + frames + "FPS");
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}
	
	public void update() {
		time++;
		if(state.equals(STATE.MENU)) {
			mainMenu.update();
			key.update();
		}
		if(state.equals(STATE.GAME)) {
			level.update();
			key.update();
		}
		if(state.equals(STATE.GAMEWIN)) {
			winMenu.update();
			level.update();
			key.update();
		}
		if(state.equals(STATE.GAMEOVER)) {
			gameOverMenu.update();
			level.update();
			key.update();
		}
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		screen.clear();
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image,  0, 0, width * scale - decrement, height * scale - decrement, null);
		
		if(state.equals(STATE.MENU)) {
			g.setFont(new java.awt.Font("Courier", 1, 35));
			g.setColor(Color.WHITE);
			g.drawString("Press Spacebar to Play", 200 * 2, 128 * 2);
			g.setFont(new java.awt.Font("Courier", 0, 20));
			g.drawString("Author: Zev Yirmiyahu", 8 * 2, 432 * 2);
			mainMenu.render(screen);
			for(int i = 0; i < pixels.length; i++) {
				pixels[i] = screen.pixels[i];
			}
		}
		
		if(state.equals(STATE.GAME)) {
			if(beginSound) {
				pool.execute(sound);
				beginSound = false;
			}
			g.setFont(new java.awt.Font("Verdana", 1, 25));
			g.setColor(Color.WHITE);
			level.render(screen);
			for(int i = 0; i < pixels.length; i++) {
				pixels[i] = screen.pixels[i];
			}
			g.drawString("Score: " + Level.getScore(), 10, 25);
			g.setFont(new java.awt.Font("Verdana", 1, 20));
			g.drawString("Life: ", 42 << 4, 1 << 4);
		}
		if(state.equals(STATE.GAMEWIN)) {
			g.setFont(new java.awt.Font("Verdana", 0, 35));
			g.setColor(Color.WHITE);
			winMenu.render(screen);
			for(int i = 0; i < pixels.length; i++) {
				pixels[i] = screen.pixels[i];
			}
			g.drawString("SCORE: " + Level.getScore(), 512, 96);
			g.setFont(new java.awt.Font("Courier", 1, 25));
			g.drawString("Replay : Press Spacebar", 512, 160);

		}
		if(state.equals(STATE.GAMEOVER)) {
			beginSound = true; // reset sound flag
			gameOverMenu.render(screen);
			for(int i = 0; i < pixels.length; i++) {
				pixels[i] = screen.pixels[i];
			}
			g.setFont(new java.awt.Font("Courier", 1, 35));
			g.setColor(Color.WHITE);
			g.drawString("Replay : Press Spacebar", 12 * 16, 50 * 16);
		}
		g.dispose();
		bs.show();
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.setTitle(title);
		game.frame.add(game);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);
		pool.execute(game);
		//game.start();
	}
}
