package dinaBOT.sound;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.NXT;
import lejos.nxt.Sound;

public class Test {
	public static void main(String[] args) {
		Song[] songSet = {Songs.jurassicPark};
		final MusicPlayer music = new MusicBox(songSet, false);

		Button.ESCAPE.addButtonListener(new ButtonListener() {

			@Override
			public void buttonPressed(Button arg0) {
				NXT.exit(0);
			}

			@Override
			public void buttonReleased(Button arg0) {
				NXT.exit(0);

			}

		});

		Button.ENTER.addButtonListener(new ButtonListener() {
			boolean paused = false;

			@Override
			public void buttonPressed(Button arg0) {
				if(!paused) {
				music.pause();
				paused = true;
				} else {
					music.play();
					paused = false;
				}
			}

			@Override
			public void buttonReleased(Button arg0) {
				//Do nothing
			}


		});

		music.play();
	}
}
