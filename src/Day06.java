import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Day06 {
	
	private static final String TODAY = Day06.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day06::part1, 1);
		ScanTools.wrap(INPUT, Day06::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		Pointainer[][] grid = new Pointainer[400][400];
		List<Area> areas = new ArrayList<>();
		Queue<Area> dead = new LinkedList<>();
		List<Area> alive = new ArrayList<>();
		int id = 0, biggest = 0, cycle = 0;
		
		// INIT GRID
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				grid[y][x] = new Pointainer();
			}
		}
		
		// PUT POINT SEEDLINGS
		while (ScanTools.hasNext()) {
			String s = ScanTools.getNext();
			int x = Integer.parseInt(s.split(",")[0].trim());
			int y = Integer.parseInt(s.split(",")[1].trim());
			areas.add(new Area(id, new Point(0, id, x, y)));
			id++;
		}
		
		// SPREAD SEEDLINGS
		alive.addAll(areas);
		do {
			cycle++;
			for (Area area : alive) {
				area.traverse(grid, cycle);
			}
			for (Area area : alive) {
				area.settle(grid);
				if (area.isDead()) {
					dead.add(area);
				}
			}
			while (!dead.isEmpty()) {
				alive.remove(dead.poll());
			}
			
		} while (!alive.isEmpty());
		
		// COUNT HIGHEST PATCHSIZE
		for (Area area : areas) {
			if (area.finite && area.size > biggest) {
				biggest = area.size;
			}
		}
		return biggest;
	}
	
	private static Object part2() {
		boolean[][] grid = new boolean[400][400];
		List<Point> locs = new ArrayList<>();
		int largestGridSize = 0;
		final int MAX_DISTANCE = 10000;
		
		// INIT GRID
		for (int y = 0; y < grid.length; y++) {
			Arrays.fill(grid[y], false);
		}
		
		// PUT LOCS
		while (ScanTools.hasNext()) {
			String s = ScanTools.getNext();
			int x = Integer.parseInt(s.split(",")[0].trim());
			int y = Integer.parseInt(s.split(",")[1].trim());
			locs.add(new Point(-1, -1, x, y));
		}
		
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				int totalDistance = 0;
				for (Point point : locs) {
					totalDistance += Math.abs(point.x - x) + Math.abs(point.y - y);
				}
				if (totalDistance < MAX_DISTANCE) {
					grid[y][x] = true;
					largestGridSize++;
				}
			}
		}
		return largestGridSize;
	}
	
	private static class Area {
		
		int id;
		int size;
		boolean finite;
		Queue<Point> next;
		
		public Area(int id, Point start) {
			this.id = id;
			this.size = 0;
			this.finite = true;
			this.next = new LinkedList<>();
			next.offer(start);
		}
		
		boolean traverse(Pointainer[][] grid, int cycle) {
			if (isDead()) {
				return false;
			}
			Queue<Point> nextBuffer = new LinkedList<>();
			nextBuffer.addAll(next);
			next.clear();
			while (!nextBuffer.isEmpty()) {
				Point p = nextBuffer.poll();
				for (int i = 0; i < 4; i++) {
					int x = p.x + ((i % 4) - 1) % 2; // x = p.x + {-1, 0, 1, 0}
					int y = p.y + ((i % 4) - 2) % 2; // y = p.y + {0, -1, 0, 1}
					Point newPoint = new Point(cycle, id, x, y);
					if (within(x, y, grid.length)) {
						if (grid[y][x].size() == 0) { // fill empty square
							grid[y][x].add(newPoint);
							next.add(newPoint);
						} else { // merge multi square
							if (grid[y][x].containsId(p) || !grid[y][x].matchesCycle(cycle))
								continue;
							next.add(newPoint);
							grid[y][x].add(newPoint);
						}
					} else if (finite) {
						if (grid[p.y][p.x].size() == 1) {
							finite = false;
							size = Integer.MAX_VALUE;
						}
					}
				}
			}
			return true;
		}
		
		boolean isDead() {
			return next.isEmpty();
		}
		
		boolean settle(Pointainer grid[][]) {
			if (isDead()) {
				return false;
			}
			Queue<Point> nextCopy = new LinkedList<>();
			nextCopy.addAll(next);
			for (Point p : nextCopy) {
				int x = p.x;
				int y = p.y;
				if (grid[y][x].size() == 1) {
					incSize();
				}
			}
			return true;
		}
		
		private static boolean within(int x, int y, int size) {
			return (y < size && y >= 0 && x < size && x >= 0);
		}
		
		private void incSize() {
			if (finite && size > -1) {
				size++;
			}
		}
	}
	
	private static class Pointainer {
		
		private int size;
		private Point[] points;
		private int cycle;
		
		public Pointainer() {
			points = new Point[4];
			size = 0;
		}
		
		public boolean add(Point p) {
			if (size < 4) {
				points[size++] = p;
				if (size == 1) {
					cycle = p.cycle;
				}
				return true;
			}
			return false;
		}
		
		public boolean matchesCycle(int cycle) {
			return this.cycle == cycle;
		}
		
		public boolean containsId(Point p) {
			for (int i = 0; i < size; i++) {
				if (points[i].id == p.id) {
					return true;
				}
			}
			return false;
		}
		
		public int size() {
			return size;
		}
		
	}
	
	private static class Point {
		int cycle;
		int id;
		int x;
		int y;
		
		public Point(int cycle, int id, int x, int y) {
			this.cycle = cycle;
			this.id = id;
			this.x = x;
			this.y = y;
		}
		
	}
}
