package dinaBOT.sound;

import lejos.nxt.Sound;

public class MusicBox extends Thread implements MusicPlayer{

	Song[] playlist;
	int[] songStatus;
	int currentSong;
	boolean playStatus;
		
	int[] frequencies;
	int[] durations;
	
	
	public MusicBox(Song[] songSet){
		playlist = songSet;
		songStatus = new int[songSet.length];
		for(int i = 0; i < songSet.length; i++)
			songStatus[i] = 0;
		currentSong = 0;
		
		frequencies = playlist[0].frequencies;
		durations = playlist[0].durations;
		
		playStatus = false;
		
		this.start();
	}
	
	public void run(){
		while(true){
			if(playStatus){
				playNote();
			}
			else{
				Thread.yield();
			}
		}
	}
	
	public boolean play(){
		playStatus = true;
		return true;
	}
	
	public boolean pause(){
		playStatus = false;
		return true;
	}
	
	public boolean abort(){
		playStatus = false;
		songStatus[currentSong] = 0;
		return true;
	}
	
	public boolean next(){
		if(currentSong == playlist.length)
			currentSong = 0;
		else
			currentSong++;
		return true;
	}
	
	public boolean previous(){
		if(currentSong == 0)
			currentSong = playlist.length;
		else
			currentSong--;
		return true;
	}
	
	private void playNote(){
		int frequency = playlist[currentSong].frequencies[songStatus[currentSong]];
		int duration = playlist[currentSong].durations[songStatus[currentSong]];
		if(frequencies[songStatus[currentSong]] != 0)
			Sound.playTone(frequency, duration - 20);
		try {Thread.sleep(duration);} catch(Exception e) {}
		if(currentSong == playlist[currentSong].frequencies.length)
			abort();
		else
			songStatus[currentSong]++;
	}
	
}
