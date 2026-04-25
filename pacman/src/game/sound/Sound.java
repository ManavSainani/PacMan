package game.sound;

import java.io.File;
import java.util.ArrayList;

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

	private ArrayList<String> musicFiles; // holds sound files
	private int soundFileIndex;
	private Clip clip;	
	
	public Sound(String...files) {
		musicFiles = new ArrayList<String>();
		for(String file : files) {
			musicFiles.add("src/game/res_sounds/" + file + ".wav");
		}
	}
	
	private void playSound(String fileName) {
		try {
			File soundFile = new File(fileName);
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
			AudioFormat format = ais.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(ais);
			//FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			//gainControl.setValue(-10);
			clip.start();
			//clip.stop();
			//clip.close();
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		playSound(musicFiles.get(0));
		
	}
	
	/*
	 * 
	 * Only works with proper formats
	 * 
	public Sound(String fileName) {
		try {
			AudioInputStream ais = 
					AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/game/res_sounds/" + fileName + ".wav"));
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = 
					new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, 
							baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), 
							false);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais); // Streams audio into decode format
			clip = AudioSystem.getClip();
			clip.open(dais);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	*/
	public void play() {
		if(clip == null) return;
		stop();
		clip.setFramePosition(0); // reset clip
		clip.start();
	}
	
	public void stop() {
		if(clip.isRunning()) clip.stop();
	}
	
	public void close() {
		stop();
		clip.close();
	}

}
