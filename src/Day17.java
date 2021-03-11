import java.util.ArrayList;
import java.util.List;

public class Day17 {
	
	private static int OFFSET = -1, COUNTLINE = -1;
	static int SPRING = -1;
	private static final String TODAY = Day17.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day17::part1, 1);
		ScanTools.wrap(INPUT, Day17::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		char[][] slice = readIn();
		dropFill(slice);
		return countWater(slice, false);
	}
	
	private static Object part2() {
		char[][] slice = readIn();
		dropFill(slice);
		return countWater(slice, true);
	}
	
	static void dropFill(char[][] slice) {
		List<Tracer> tracers = new ArrayList<>();
		List<Tracer> traceCopy;
		
		tracers.add(new Tracer());
		while (!tracers.isEmpty()) {
			traceCopy = new ArrayList<>(tracers);
			for (Tracer tracer : traceCopy) {
				tracer.fall(slice, tracers);
			}
			for (Tracer tracer : traceCopy) {
				if (tracer.dead) {
					tracers.remove(tracer);
				}
			}
		}
	}
	
	private static class Tracer {
		int x;
		int y;
		boolean dead;
		
		public Tracer() {
			x = SPRING;
			y = 0;
			dead = false;
		}
		
		public Tracer(int x, int y) {
			this.x = x;
			this.y = y;
			dead = false;
		}
		
		void fall(char[][] slice, List<Tracer> tracers) {
			if (y + 1 >= slice.length) {
				dead = true;
				return;
			}
			if (slice[y + 1][x] == '.') {
				y++;
				slice[y][x] = '|';
			} else if (slice[y + 1][x] == '|') {
				dead = true;
			} else {
				
				Tracer left = new Tracer(x, y);
				Tracer right = new Tracer(x, y);
				boolean a = left.sidehop(slice, -1, tracers), b = right.sidehop(slice, 1, tracers);
				if (a && b) {
					for (int xt = left.x; xt <= right.x; xt++) {
						slice[y][xt] = '~';
					}
					y--;
				} else {
					slice[y][x] = '|';
					dead = true;
				}
			}
		}
		
		boolean sidehop(char[][] slice, int dir, List<Tracer> tracers) {
			while (slice[y][x + dir] != '#') {
				x += dir;
				if (slice[y + 1][x] == '.' || slice[y + 1][x] == '|') {
					tracers.add(this);
					slice[y][x] = '|';
					return false;
				}
				slice[y][x] = '|';
			}
			return true;
		}
	}
	
	static int countWater(char[][] slice, boolean full) {
		int sum = 0;
		for (int i = COUNTLINE; i < slice.length; i++) {
			for (char c : slice[i]) {
				if (c == '~') {
					sum++;
				} else if (c == '|' && !full) {
					sum++;
				}
			}
		}
		return sum;
	}
	
	static char[][] readIn() {
		List<Line> lines = new ArrayList<>();
		int biggestY = Integer.MIN_VALUE, lowestY = Integer.MAX_VALUE, biggestX = Integer.MIN_VALUE, lowestX = Integer.MAX_VALUE;
		while (ScanTools.hasNext()) {
			String[] s = ScanTools.getNext().trim().split(",|\\.\\.");
			int arg0 = Integer.valueOf(s[0].trim().substring(2, s[0].trim().length()));
			int arg1a = Integer.valueOf(s[1].trim().substring(2, s[1].trim().length()));
			int arg1b = Integer.valueOf(s[2].trim());
			
			int x, y;
			boolean vert;
			if (s[0].trim().startsWith("x")) {
				x = arg0;
				y = arg1a;
				vert = true;
			} else {
				x = arg1a;
				y = arg0;
				vert = false;
			}
			
			lines.add(new Line(x, y, arg1b - arg1a, vert));
			
			if (x < lowestX)
				lowestX = x;
			else if (x > biggestX)
				biggestX = x;
			
			if (y < lowestY)
				lowestY = y;
			else if (y > biggestY)
				biggestY = y;
			
		}
		
		char[][] slice = new char[biggestY + 1][(biggestX - lowestX) + 3];
		COUNTLINE = lowestY;
		OFFSET = -lowestX + 1;
		for (int i = 0; i < slice.length; i++) {
			for (int j = 0; j < slice[i].length; j++) {
				slice[i][j] = '.';
			}
		}
		SPRING = 500 + OFFSET;
		slice[0][SPRING] = '+';
		
		for (Line line : lines) {
			for (int i = 0; i <= line.distance; i++) {
				if (line.vertical) {
					slice[line.y + i][line.x + OFFSET] = '#';
				} else {
					slice[line.y][line.x + i + OFFSET] = '#';
				}
			}
		}
		
		return slice;
	}
	
	private static class Line {
		int x, y;
		int distance;
		boolean vertical;
		
		public Line(int x, int y, int distance, boolean vertical) {
			this.x = x;
			this.y = y;
			this.distance = distance;
			this.vertical = vertical;
		}
	}
}
