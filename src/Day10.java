import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day10 {
	
	private static int seconds = -1;
	private static final String TODAY = Day10.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day10::part1, 1);
		ScanTools.wrap(INPUT, Day10::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		List<Star> stars = read();
		int estimate = estimate(stars);
		moveStars(stars, estimate);
		String s = parse(hopSearch(stars, estimate, 0));
		if (s != null) {
			return s;
		}
		return "\n" + toString(hopSearch(stars, estimate, 0));
	}
	
	private static Object part2() {
		return seconds;
	}
	
	private static void moveStars(List<Star> stars, int amount) {
		for (Star star : stars) {
			star.move(amount);
		}
	}
	
	private static String toString(char[][] grid) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				sb.append(grid[y][x]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private static int byX(Star a, Star b) {
		return a.x - b.x;
	}
	
	private static int byY(Star a, Star b) {
		return a.y - b.y;
	}
	
	private static char[][] represent(List<Star> stars) {
		Collections.sort(stars, Day10::byX);
		int lx = stars.get(0).x;
		int hx = stars.get(stars.size() - 1).x;
		Collections.sort(stars, Day10::byY);
		int ly = stars.get(0).y;
		int hy = stars.get(stars.size() - 1).y;
		
		int sx = hx - lx + 1;
		int sy = hy - ly + 1;
		
		char[][] grid = new char[sy][];
		for (int y = 0; y < sy; y++) {
			grid[y] = new char[sx];
			for (int x = 0; x < sx; x++) {
				grid[y][x] = '.';
			}
		}
		for (Star star : stars) {
			star.gy = star.y - ly;
			star.gx = star.x - lx;
			grid[star.gy][star.gx] = '#';
		}
		return grid;
	}
	
	private static char[][] hopSearch(List<Star> stars, int estimate, int hopSize) {
		moveStars(stars, estimate + hopSize);
		char[][] grid = represent(stars);
		int orthoCount = 0;
		for (Star star : stars) {
			if (!hasNeighbors(grid, star)) {
				return hopSearch(stars, estimate, nextHop(hopSize));
			}
			if (hasOrthogonalNeighbors(grid, star)) {
				orthoCount++;
			}
		}
		if (orthoCount > stars.size() / 1.5f) {
			seconds = estimate + hopSize;
			return grid;
		}
		return hopSearch(stars, estimate, nextHop(hopSize));
	}
	
	private static int nextHop(int hopSize) {
		return -(hopSize <= 0 ? hopSize - 1 : hopSize);
	}
	
	private static boolean hasNeighbors(char[][] array, Star star) {
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				if (x == 0 && y == 0)
					continue;
				int ry = star.gy + y;
				int rx = star.gx + x;
				if (ry >= 0 && ry < array.length && rx >= 0 && rx < array[0].length) {
					if (array[ry][rx] == '#') {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static boolean hasOrthogonalNeighbors(char[][] array, Star star) {
		for (int i = 0; i < 4; i++) {
			int x = star.gx + ((i % 4) - 1) % 2;
			int y = star.gy + ((i % 4) - 2) % 2;
			if (y >= 0 && y < array.length && x >= 0 && x < array[0].length) {
				if (array[y][x] == '#') {
					return true;
				}
			}
			
		}
		return false;
	}
	
	private static List<Star> read() {
		List<Star> stars = new ArrayList<>();
		while (ScanTools.hasNext()) {
			String s[] = ScanTools.getNext().split("[<,>]");
			int x = Integer.valueOf(s[1].trim());
			int y = Integer.valueOf(s[2].trim());
			
			int vx = Integer.valueOf(s[4].trim());
			int vy = Integer.valueOf(s[5].trim());
			stars.add(new Star(x, y, vx, vy));
		}
		return stars;
	}
	
	private static int estimate(List<Star> stars) {
		int estimate = 0, i = 0;
		int maxEstimate = Integer.MIN_VALUE, minEstimate = Integer.MAX_VALUE;
		for (Star star : stars) {
			if (star.vx != 0 && star.vy != 0) {
				maxEstimate = Math.max(Math.abs(star.sx / star.vx), Math.max(Math.abs(star.sy / star.vy), maxEstimate));
				minEstimate = Math.min(Math.abs(star.sx / star.vx), Math.min(Math.abs(star.sy / star.vy), minEstimate));
			}
			estimate += (maxEstimate + minEstimate) / 2;
			i++;
		}
		return estimate / i;
	}
	
	private static String parse(char[][] grid) {
		StringBuilder result = new StringBuilder();
		int from = 0, step = 8;
		while (from + 6 <= grid[0].length) {
			char[][] sub = new char[10][6];
			for (int x = 0; x < 6; x++) {
				for (int y = 0; y < grid.length; y++) {
					sub[y][x] = grid[y][from + x];
				}
			}
			char c = match(sub);
			if (c == 0) {
				return null;
			}
			result.append(c);
			from += step;
		}
		return result.toString();
	}
	
	private static char match(char[][] sub) {
		next: for (Alpha a : Alpha.values()) {
			for (int y = 0; y < sub.length; y++) {
				for (int x = 0; x < sub[y].length; x++) {
					if (sub[y][x] != a.grid[y][x]) {
						continue next;
					}
				}
			}
			return a.c;
		}
		return 0;
	}
	
	private static class Star {
		final int sx;
		final int sy;
		final int vx;
		final int vy;
		int x;
		int y;
		int gx;
		int gy;
		
		public Star(int x, int y, int vx, int vy) {
			super();
			this.sx = x;
			this.sy = y;
			this.x = this.sx;
			this.y = this.sy;
			this.vx = vx;
			this.vy = vy;
		}
		
		@Override
		public String toString() {
			return "Star from (" + sx + ", " + sy + ") with speed (" + vx + ", " + vy + ") moved to (" + x + ", " + y + ")";
		}
		
		void move(int amount) {
			x = sx + (amount * vx);
			y = sy + (amount * vy);
		}
	}
	
	enum Alpha {
		//@formatter:off
		A('A', "..##...#..#.#....##....##....########....##....##....##....#"),
		B('B',"#####.#....##....##....######.#....##....##....##....######."),
		C('C',".####.#....##.....#.....#.....#.....#.....#.....#....#.####."),
		E('E',"#######.....#.....#.....#####.#.....#.....#.....#.....######"),
		F('F',"#######.....#.....#.....#####.#.....#.....#.....#.....#....."),
		G('G',".####.#....##.....#.....#.....#..####....##....##...##.###.#"),
		H('H',"#....##....##....##....########....##....##....##....##....#"),
		J('J',"...###....#.....#.....#.....#.....#.....#.#...#.#...#..###.."),
		K('K',"#....##...#.#..#..#.#...##....##....#.#...#..#..#...#.#....#"),
		L('L',"#.....#.....#.....#.....#.....#.....#.....#.....#.....######"),
		N('N',"#....###...###...##.#..##.#..##..#.##..#.##...###...###....#"),
		P('P',"#####.#....##....##....######.#.....#.....#.....#.....#....."),
		R('R',"#####.#....##....##....######.#..#..#...#.#...#.#....##....#"),
		X('X',"#....##....#.#..#..#..#...##....##...#..#..#..#.#....##....#"),
		Z('Z',"######.....#.....#....#....#....#....#....#.....#.....######");
		//@formatter:on
		
		char[][] grid;
		char c;
		
		Alpha(char c, String s) {
			this.c = c;
			grid = new char[10][6];
			for (int y = 0; y < grid.length; y++) {
				for (int x = 0; x < grid[y].length; x++) {
					grid[y][x] = s.charAt(y * 6 + x);
				}
			}
		}
	}
}
