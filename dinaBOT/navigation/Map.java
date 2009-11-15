package dinaBOT.navigation;

public class Map {
	int[][] map;
	int rez;
	
	public Map(int rez) {
		this.map = new int[rez][rez];
		this.rez = rez;
	}
	
	public int[][] getMap() {
		return this.map;
	}
	
	public int getRez() {
		return this.rez;
	}
	
	public boolean editMap(int x, int y, int value) {
		this.map[x][y] = value;
		
		return true;	//sucess
	}
}
