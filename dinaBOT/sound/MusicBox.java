package dinaBOT.sound;

import lejos.nxt.Sound;

public class MusicBox extends Thread implements MusicPlayer{

	int[] frequencies;
	int[] durations;
	int[] currentNotes = new int[100];
	boolean[] playStatus = new boolean[100];
	int songCount;
	
	public MusicBox(){
		for(int i = 0; i < 100; i++){
			currentNotes[i] = 0;
			playStatus[i] = false;
		}
		this.start();
	}
	
	public void run(){
		while(true){
			play();
		}
	}
	
	public boolean abort(){
		return true;
	}
	
	public boolean next(){
		return true;
	}
	
	public boolean previous(){
		return true;
	}
	
	public void setSong(){
		int id = songCount;
		songCount++;
	}
	
	public void playNonStop(){
		for(int i = 0; i < frequencies.length; i++){
			if(frequencies[i] != 0)
				Sound.playTone(frequencies[i], durations[i] - 20);
			try{
				Thread.sleep(durations[i]);
			}catch(Exception e){}
		}
	}
	
	public boolean pause(){
		playStatus[1] = false;
		return true;
	}
	
	public void unpause(){
		
	}
	
	public boolean play(){
		return true;
	}
	
	public void playNote(int id){
		int currentNote = currentNotes[id];
		if(frequencies[currentNote] != 0)
			Sound.playTone(frequencies[currentNote], durations[currentNote] - 20);
		try{
			Thread.sleep(durations[currentNote]);
		}catch(Exception e){}
		currentNotes[id]++;
	}

}
