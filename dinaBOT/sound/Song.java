package dinaBOT.sound;

public class Song {
	
	int tempo;
	String title;
	int id;
	int[] frequencies;
	int[] durations;
		
	public Song(int tempo, String title, int[][] chart){
		this.tempo = tempo;
		this.title = title;
		this.frequencies = getFrequencies(chart);
		this.durations = getDurations(tempo, chart);
	}
	
	private int getFrequency(int note, int alteration, int octave){
		if(note == 10)
			return 0;
		int halfSteps = note + alteration + (12*octave); //number of half-steps away from A4
		double frequency = 440 * Math.pow(2.0, halfSteps/12);
		return (int)(frequency);
	}
	
	private int[] getFrequencies(int[][] chart){
		int[] frequencies = new int[chart.length];
		for(int i = 0; i < frequencies.length; i++)
			frequencies[i] = getFrequency(chart[i][0], chart[i][1], chart[i][2]);
		return frequencies;
	}
	
	private int[] getDurations(int tempo, int[][] chart){
		int[] durations = new int[chart.length];
		int millisecondsPerBeat = (int) 60000 / tempo;
		for(int i = 0; i < durations.length; i++)
			durations[i] = (int) millisecondsPerBeat * (chart[i][3] / 24);
		return durations;
	}
}
