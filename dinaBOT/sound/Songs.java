package dinaBOT.sound;

public class Songs {

	// 	LEGEND : {note, alteration, octave, duration}

	//	notes : half-steps from la (A), 10 means silence
	int	ut = -9,
		re = -7,
		mi = -5,
		fa = -4,
		sol = -2,
		la = 0,
		si = 2,
		silence = 10;
	
	//	alterations : d = diese, b = bemol
	int	d = 1,
		b = -1;
	
	//	octaves : center is from 0 to 7, center is 4
	
	//	durations : 1 beat is 24
		
	
	static final int[][] exampleChart = {	{1, 2, 3, 4},
											{1, 2, 3, 4}	};
									
	static final Song example = new Song(100, "Example", exampleChart);
	
}
