package dinaBOT.sound;

public class Song {

	String title;
	int id;
	int[] frequencies;
	int[] durations;

	public Song(double tempo, String title, int[][] chart) {
		this.title = title;
		this.frequencies = getFrequencies(chart);
		this.durations = getDurations(tempo, chart);
	}

	private int getFrequency(int note, int alteration, int octave) {
		if(note == 10)
			return 0;
		int halfSteps = note + alteration + (12*(octave-4)); //number of half-steps away from A4
		double frequencyCoefficient = 1.059463094;
		double unprocessedFrequency = 1.0;
		if(halfSteps >= 0){
			for(int i = 0; i < halfSteps; i++)
				unprocessedFrequency *= frequencyCoefficient;
		}
		else{
			for(int i = 0; i > halfSteps; i--)
				unprocessedFrequency /= frequencyCoefficient;
		}
		return (int)(440 * unprocessedFrequency);
	}

	private int[] getFrequencies(int[][] chart) {
		int[] frequencies = new int[chart.length];
		for(int i = 0; i < frequencies.length; i++)
			frequencies[i] = getFrequency(chart[i][0], chart[i][1], chart[i][2]);
		return frequencies;
	}

	private int[] getDurations(double tempo, int[][] chart) {
		int[] durations = new int[chart.length];
		int millisecondsPerBeat = (int) (60000 / tempo);
		double duration;
		for(int i = 0; i < durations.length; i++){
			 duration = millisecondsPerBeat * chart[i][3] / 24;
			 durations[i] = (int) duration;
		}
		return durations;
	}
}
