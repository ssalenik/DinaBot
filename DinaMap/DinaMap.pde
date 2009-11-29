BufferedReader reader;
String line;

int[][] map;

void setup() {
	size(screen.width, screen.height);
	reader = createReader("map.txt");
	map = new int[13][9];
	for(int x = 0;x < 13;x++) {
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			stop();
		}
		println(line);
		String[] row = split(line, TAB);
		println(row[0]);
		
		for(int y = 0; y < 9; y++) {
			map[x][y] = int(row[y]);
		}
	}
	noLoop();
	draw();
}
 
void draw() {
	background(0);
	strokeWeight(2);
	for(int x = 0;x < map.length;x++) {
		for(int y = 0; y < map[x].length; y++) {
			fill(0);
			stroke(0);
			if(map[x][y] >= 3) {
				stroke(100, 0, 0);
				fill(100, 0, 0);
			}
			if(map[x][y] == 5) {
				stroke(0, 100, 0);
				fill(0, 100, 0);
			}
			if(map[x][y] == 10)  {
				stroke(50);
				fill(50);
			}
			if(map[x][y] == 2) {
				stroke(0, 0, 100);
				fill(0, 0, 100);
			}
			rect(x*width/map.length, height-y*height/map[x].length, width/map.length, -height/map[x].length);
		}
	}
	
	stroke(100);
	for(int x = 0;x < map.length;x++) {
		line(x*width/map.length+width/map.length/2, 0, x*width/map.length+width/map.length/2, height);
	}
	
	for(int y = 0; y < map[0].length; y++) {
		line(0, y*height/map[0].length+height/map[0].length/2, width, y*height/map[0].length+height/map[0].length/2);
	}
}