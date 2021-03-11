import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Day18 {
	
	private static final int SIZE = 50;
	private static final char OPEN = '.', TREE = '|', LUMB = '#', NAN = ' ';
	private static final String TODAY = Day18.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		initPrinter();
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day18::part1, 1);
		ScanTools.wrap(INPUT, Day18::part2, 2);
		System.out.println();
		exitPrinter();
	}
	
	static PrintWriter OUT;
	
	static void initPrinter() {
		try {
			OUT = new PrintWriter(new File("output.txt"));
		} catch (FileNotFoundException e) {
			OUT = new PrintWriter(System.out);
			e.printStackTrace();
		}
	}
	
	static void exitPrinter() {
		OUT.flush();
		OUT.close();
	}
	
	private static Object part1() {
		char[][] map = readIn();
		for (int i = 0; i < 10; i++) {
			map = act(map);
		}
		return count(map);
	}
	
	private static Object part2() {
		char[][] map = readIn();
		
		int run = 1000000000;
		final int patLength = 28;
		int dex = 0;
		int[] pattern = new int[patLength];
		
		final int recordPattern = 1000;
		
		int c = 0;
		int i = 0;
		for (; i < run; i++) {
			map = act(map);
			c = count(map);
			if (i > recordPattern) {
				pattern[dex++] = c;
				if (dex >= patLength) {
					i++;
					break;
				}
			}
		}
		dex += run - i - 1; // needed a thicc hint for this one, because wtf why?
		dex %= patLength;
		return pattern[dex];
	}
	
	static int count(char[][] map) {
		int trees = 0, lumber = 0;
		for (int i = 1; i < map.length - 1; i++) {
			for (int j = 1; j < map[i].length - 1; j++) {
				if (map[i][j] == LUMB) {
					lumber++;
				} else if (map[i][j] == TREE) {
					trees++;
				}
			}
		}
		return trees * lumber;
	}
	
	static char[][] act(char[][] map) {
		char[][] swap = deepCopy(map);
		
		for (int i = 1; i < map.length - 1; i++) {
			for (int j = 1; j < map[i].length - 1; j++) {
				swap[i][j] = nCheck(map, i, j);
			}
		}
		return swap;
	}
	
	static char nCheck(char[][] map, int i, int j) {
		char result = map[i][j];
		int[] surrounding = { 0, 0, 0 }; // 0 = . // 1 = | // 2 = #
		for (int k = -1; k <= 1; k++) {
			for (int l = -1; l <= 1; l++) {
				if (k == 0 && l == 0)
					continue;
				switch (map[i + k][j + l]) {
					case OPEN:
						surrounding[0]++;
						break;
					case TREE:
						surrounding[1]++;
						break;
					case LUMB:
						surrounding[2]++;
						break;
					default:
						break;
				}
			}
		}
		if (map[i][j] == OPEN && surrounding[1] >= 3) {
			result = TREE;
		} else if (map[i][j] == TREE && surrounding[2] >= 3) {
			result = LUMB;
		} else if (map[i][j] == LUMB && (surrounding[1] == 0 || surrounding[2] == 0)) {
			result = OPEN;
		}
		return result;
	}
	
	static char[][] deepCopy(char[][] map) {
		char[][] swap = new char[map.length][];
		
		for (int i = 0; i < map.length; i++) {
			swap[i] = new char[map[i].length];
			for (int j = 0; j < map[i].length; j++) {
				swap[i][j] = map[i][j];
			}
		}
		
		return swap;
	}
	
	static void print(char[][] map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				OUT.print(map[i][j]);
			}
			OUT.println();
		}
	}
	
	static char[][] readIn() {
		char[][] map = new char[SIZE + 2][SIZE + 2];
		String[] strings = new String[SIZE];
		int dex = 0;
		while (ScanTools.hasNext()) {
			strings[dex++] = ScanTools.getNext();
		}
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (i == 0 || i == map.length - 1 || j == 0 || j == map[i].length - 1) {
					map[i][j] = NAN;
					continue;
				}
				map[i][j] = strings[i - 1].charAt(j - 1);
			}
		}
		return map;
	}
}
