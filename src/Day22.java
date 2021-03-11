
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day22 {
	
	private static int tx, ty;
	private static Node target;
	// private static final char TORCH = 'i', CLIMB = '#', NONE = ' ';
	private static final char ROCKY = '.', WET = '=', NARROW = '|';
	private static final String TODAY = Day22.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day22::part1, 1);
		ScanTools.wrap(INPUT, Day22::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		return riskLevel(read(0));
	}
	
	private static Object part2() {
		char[][] cave = read(50);
		
		// Contains all possible edges.
		Node start = nodify(cave);
		
		return dijkstraSearch(start);
	}
	
	static int lowestDistance(Node a, Node b) {
		return a.distance - b.distance;
	}
	
	// 1040 too high it was 1039 xddddd
	private static int dijkstraSearch(Node current) {
		current.distance = 0;
		List<Node> unsettled = new ArrayList<>();
		Set<Node> settled = new HashSet<>();
		unsettled.add(current);
		while (!unsettled.isEmpty()) {
			unsettled.sort(Day22::lowestDistance);
			current = unsettled.get(0);
			for (Edge edge : current.edges) {
				if (edge.traversed)
					continue;
				if (!settled.contains(edge.a) && edge.a.equals(current)) {
					if (current.distance + edge.weight < edge.b.distance) {
						edge.b.distance = current.distance + edge.weight;
						unsettled.add(edge.b);
					}
				} else if (!settled.contains(edge.b) && edge.b.equals(current)) {
					if (current.distance + edge.weight < edge.a.distance) {
						edge.a.distance = current.distance + edge.weight;
						unsettled.add(edge.a);
					}
				}
			}
			settled.add(current);
			unsettled.remove(current);
		}
		return target.distance;
	}
	
	private static int riskLevel(char[][] cave) {
		int sum = 0;
		for (char[] cs : cave) {
			for (char c : cs) {
				sum += (c == ROCKY ? 0 : (c == WET ? 1 : 2));
			}
		}
		return sum;
	}
	
	private static char[][] read(int padWithExtra) {
		int depth = Integer.valueOf(ScanTools.getNext().split("\\s")[1].trim());
		String[] a = ScanTools.getNext().split("\\s|,");
		tx = Integer.valueOf(a[1].trim());
		ty = Integer.valueOf(a[2].trim());
		
		char[][] cave = new char[ty + 1 + padWithExtra][tx + 1 + padWithExtra];
		int[][] erosion = new int[ty + 1 + padWithExtra][tx + 1 + padWithExtra];
		
		for (int y = 0; y < cave.length; y++) {
			for (int x = 0; x < cave[y].length; x++) {
				int geo_index;
				if ((y == 0 && x == 0) || (y == ty && x == tx)) {
					geo_index = 0;
				} else if (y == 0) {
					geo_index = x * 16807;
				} else if (x == 0) {
					geo_index = y * 48271;
				} else {
					geo_index = erosion[y][x - 1] * erosion[y - 1][x];
				}
				int erosion_level = (geo_index + depth) % 20183;
				erosion[y][x] = erosion_level;
				switch (erosion_level % 3) {
					case 0:
						cave[y][x] = ROCKY;
						break;
					case 1:
						cave[y][x] = WET;
						break;
					case 2:
						cave[y][x] = NARROW;
						break;
					default:
						throw new IllegalArgumentException("Can't ever happen.");
				}
			}
		}
		return cave;
	}
	
	private static Node nodify(char[][] cave) {
		Node[][][] nodes = new Node[cave.length][cave[0].length][];
		
		// List<Edge> edgeset = new ArrayList<>();
		
		for (int y = 0; y < cave.length; y++) {
			for (int x = 0; x < cave[0].length; x++) {
				char c = cave[y][x];
				nodes[y][x] = new Node[3];
				if (c == ROCKY) {
					nodes[y][x][0] = new Node();
					nodes[y][x][1] = new Node();
					nodes[y][x][2] = null;
					Edge e = new Edge(nodes[y][x][0], nodes[y][x][1], 7);
					// edgeset.add(e);
					nodes[y][x][0].edges.add(e);
					nodes[y][x][1].edges.add(e);
				} else if (c == WET) {
					nodes[y][x][0] = null;
					nodes[y][x][1] = new Node();
					nodes[y][x][2] = new Node();
					Edge e = new Edge(nodes[y][x][1], nodes[y][x][2], 7);
					// edgeset.add(e);
					nodes[y][x][1].edges.add(e);
					nodes[y][x][2].edges.add(e);
				} else if (c == NARROW) {
					nodes[y][x][0] = new Node();
					nodes[y][x][1] = null;
					nodes[y][x][2] = new Node();
					Edge e = new Edge(nodes[y][x][0], nodes[y][x][2], 7);
					// edgeset.add(e);
					nodes[y][x][0].edges.add(e);
					nodes[y][x][2].edges.add(e);
				}
				if (y == ty && x == tx) {
					target = nodes[y][x][0];
				}
			}
		}
		
		for (int y = 0; y < nodes.length; y++) {
			for (int x = 0; x < nodes[0].length; x++) {
				Node t = nodes[y][x][0];
				Node c = nodes[y][x][1];
				Node n = nodes[y][x][2];
				
				for (int i = 0; i < 4; i++) {
					int xt = x + ((i % 4) - 1) % 2; // {-1, 0, 1, 0}
					int yt = y + ((i % 4) - 2) % 2; // {0, -1, 0, 1}
					if (xt < 0 || yt < 0 || xt >= nodes[0].length || yt >= nodes.length)
						continue;
					
					Node tt = nodes[yt][xt][0];
					Node tc = nodes[yt][xt][1];
					Node tn = nodes[yt][xt][2];
					
					if (t != null && tt != null /* && !edgeset.contains(new Edge(tt, t, 1)) */) {
						Edge e = new Edge(t, tt, 1);
						// edgeset.add(e);
						t.edges.add(e);
						tt.edges.add(e);
					}
					if (c != null && tc != null /* &&!edgeset.contains(new Edge(tc, c, 1)) */) {
						Edge e = new Edge(c, tc, 1);
						// edgeset.add(e);
						c.edges.add(e);
						tc.edges.add(e);
					}
					if (n != null && tn != null /* && !edgeset.contains(new Edge(tn, n, 1)) */) {
						Edge e = new Edge(n, tn, 1);
						// edgeset.add(e);
						n.edges.add(e);
						tn.edges.add(e);
					}
				}
			}
		}
		
		return nodes[0][0][0];// edgeset;
	}
	
	private static class Edge {
		
		Node a, b;
		int weight;
		boolean traversed;
		
		public Edge(Node a, Node b, int weight) {
			this.a = a;
			this.b = b;
			this.weight = weight;
			this.traversed = false;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((a == null) ? 0 : a.hashCode());
			result = prime * result + ((b == null) ? 0 : b.hashCode());
			result = prime * result + weight;
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Edge other = (Edge) obj;
			if (a == null) {
				if (other.a != null)
					return false;
			} else if (!a.equals(other.a))
				return false;
			if (b == null) {
				if (other.b != null)
					return false;
			} else if (!b.equals(other.b))
				return false;
			if (weight != other.weight)
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "[" + a + "--" + weight + "--" + b + "]";
		}
		
	}
	
	private static class Node {
		
		List<Edge> edges;
		
		public Node() {
			edges = new ArrayList<>();
		}
		
		// char type; // unused
		int distance = Integer.MAX_VALUE;
		
		// @Override
		// public int hashCode() {
		// final int prime = 31;
		// int result = 1;
		// result = prime * result + type;
		// result = prime * result + x;
		// result = prime * result + y;
		// return result;
		// }
		//
		// @Override
		// public boolean equals(Object obj) {
		// if (this == obj)
		// return true;
		// if (obj == null || getClass() != obj.getClass())
		// return false;
		// Node other = (Node) obj;
		// return (type == other.type && x == other.x && y == other.y);
		//
		// }
		
		// public Node(char type, int x, int y) {
		// this.type = type;
		// this.x = x;
		// this.y = y;
		// }
		//
		// @Override
		// public String toString() {
		// return "" + type + "(" + x + ", " + y + ")";
		// }
		
	}
}
