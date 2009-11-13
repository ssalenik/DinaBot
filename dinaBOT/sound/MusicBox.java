package dinaBOT.sound;

import lejos.nxt.Sound;

public class MusicBox extends Thread{

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
	
	public void pause(int id){
		playStatus[id] = false;
	}
	
	public void unpause(){
		
	}
	
	public void play(){
		
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
