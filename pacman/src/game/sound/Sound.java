package game.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/*
 * Put class on its own thread to avoid slow down in game logic. This class handles all sound effects
 * in the game.
 */

public class Sound implements Runnable {

	private Clip[] clips;

	public Sound(String... files) {
		clips = new Clip[files.length];
		for (int i = 0; i < files.length; i++) {
			clips[i] = loadClip("/game/res_sounds/" + files[i] + ".wav");
		}
		warmUp();
	}

	private void warmUp() {
		for (Clip clip : clips) {
			if (clip == null) continue;
			clip.start();
			clip.stop();
			clip.setFramePosition(0);
		}
	}

	private Clip loadClip(String path) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(path));
			AudioFormat format = ais.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(ais);
			return clip;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void play(int index) {
		if (index < 0 || index >= clips.length || clips[index] == null) {
			return;
		}
		Clip clip = clips[index];
		
		if (clip.isRunning()) clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}

	public boolean isPlaying(int index) {
		if (index < 0 || index >= clips.length || clips[index] == null) return false;
		return clips[index].isRunning();
	}

	public void stop() {
		for (Clip clip : clips) {
			if (clip != null && clip.isRunning()) clip.stop();
		}
	}

	public void close() {
		stop();
		for (Clip clip : clips) {
			if (clip != null) clip.close();
		}
	}

	@Override
	public void run() {
		play(0); // intro
	}

}
