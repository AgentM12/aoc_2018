
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day25 {
	private static final String TODAY = Day25.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day25::part1, 1);
		ScanTools.wrap(INPUT, Day25::part2, 2);
		System.out.println();
	}
	
	// One stupid off-by-one for my puzzle input that I can't explain.
	private static Object part1() {
		List<Point4d> points = read();
		Point4d[] pts = new Point4d[points.size()];
		points.toArray(pts);
		UnionFind u = new UnionFind(pts);
		merge(u, 3);
		return u.amountOfTrees() - 1;
	}
	
	private static Object part2() {
		return "There is no part 2 on this day!";
	}
	
	private static void merge(UnionFind u, int d) {
		for (int i = 0; i < u.points.size() - 1; i++) {
			for (int k = i + 1; k < u.points.size(); k++) {
				u.unionIfRange(i, k, d);
			}
		}
	}
	
	private static List<Point4d> read() {
		List<Point4d> points = new ArrayList<>();
		while (ScanTools.hasNext()) {
			String line = ScanTools.getNext();
			points.add(Point4d.parse(line));
		}
		return points;
	}
	
	private static class UnionFind {
		int rank[];
		int parent[];
		Map<Integer, Point4d> points;
		
		public UnionFind(Point4d[] set) {
			parent = new int[set.length];
			points = new HashMap<>();
			rank = new int[set.length];
			int i = 0;
			for (Point4d point : set) {
				parent[i] = i;
				points.put(i++, point);
			}
		}
		
		void unionIfRange(int a, int b, int amount) {
			if (points.get(a).distance(points.get(b)) <= amount)
				union(a, b);
		}
		
		// Merge two subtrees if they have a different root, input is array indices
		void union(int i, int j) {
			int a = find(i);
			int b = find(j);
			
			if (a == b) {
				return;
			}
			
			if (rank[a] < rank[b]) {
				parent[a] = b;
			} else if (rank[a] > rank[b]) {
				parent[b] = a;
			} else {
				parent[b] = a;
				rank[a]++;
			}
		}
		
		int amountOfTrees() {
			Set<Integer> seen = new HashSet<>();
			for (int i : parent) {
				int p = find(i);
				if (!seen.contains(p)) {
					seen.add(i);
				}
			}
			return seen.size();
		}
		
		// Return the root of a node (input is node index)
		int find(int i) {
			if (parent[i] != i) {
				parent[i] = find(parent[i]);
			}
			return parent[i];
		}
	}
	
	private static class Point4d {
		int x;
		int y;
		int z;
		int w;
		
		public Point4d(int x, int y, int z, int w) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}
		
		public int distance(Point4d pb) {
			return Math.abs(this.x - pb.x) + Math.abs(this.y - pb.y) + Math.abs(this.z - pb.z) + Math.abs(this.w - pb.w);
		}
		
		static Point4d parse(String line) {
			String[] coords = line.trim().split(",");
			int x = Integer.valueOf(coords[0].trim());
			int y = Integer.valueOf(coords[1].trim());
			int z = Integer.valueOf(coords[2].trim());
			int w = Integer.valueOf(coords[3].trim());
			return new Point4d(x, y, z, w);
		}
		
		@Override
		public String toString() {
			return "(" + x + ", " + y + ", " + z + ", " + w + ")";
		}
		
	}
}
