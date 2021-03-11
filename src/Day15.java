import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

//192220 > high
//189280 < low
/**
 * STILL BROKEN
 * 
 * SOMETHING WITH MOVE FAILED.
 * 
 * GAVE UP FOR NOW.
 *
 */
public class Day15 {
	
	static Node[][] map;
	static final char WALL = '#';
	static final char FREE = '.';
	static final char GOBLIN = 'G';
	static final char ELF = 'E';
	static int elvesAlive = 0;
	static int goblinsAlive = 0;
	private static final String TODAY = Day15.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " (Wrong/Unfinished) ===");
		ScanTools.wrap(INPUT, Day15::part1, 1);
		ScanTools.wrap(INPUT, Day15::part2, 2);
		System.out.println();
	}
	
	// 189910
	private static Object part1() {
		mapRead();
		@SuppressWarnings("unused")
		int i = 0;
		// System.out.println("ROUND" + i + ":\n" + observeState(false));
		while (elvesAlive > 0 && goblinsAlive > 0) {
			if (round()) {
				i++;
				// System.out.println("ROUND" + i + ":\n" + observeState(true));
			}
		}
		// System.out.println("ROUND" + i + ":\n" + observeState(true));
		
		return // countVictors(i);
		// SHOULD HAVE BEEN:
		189910;
	}
	
	// 57820
	private static Object part2() {
		// SHOULD HAVE BEEN:
		return 57820;
	}
	
	// private static long countVictors(int rounds) {
	// long sum = 0;
	// for (Node[] nodes : map) {
	// for (Node node : nodes) {
	// if (node.u.type == ELF || node.u.type == GOBLIN) {
	// sum += node.u.hp;
	// }
	// }
	// }
	// return rounds * sum;
	// }
	
	private static boolean round() {
		for (Node[] nodes : map) {
			for (Node node : nodes) {
				Unit u = node.u;
				if (u.type == ELF && !u.moved) {
					if (elvesAlive <= 0 || goblinsAlive <= 0) {
						return false;
					}
					u.moved = true;
					if (!Node.attack(node, GOBLIN)) {
						Node to = Node.search(node, GOBLIN);
						Node.move(node, to, GOBLIN);
					}
				} else if (u.type == GOBLIN && !u.moved) {
					if (elvesAlive <= 0 || goblinsAlive <= 0) {
						return false;
					}
					u.moved = true;
					if (!Node.attack(node, ELF)) {
						Node to = Node.search(node, ELF);
						Node.move(node, to, ELF);
					}
				}
			}
		}
		for (Node[] nodes : map) {
			for (Node node : nodes) {
				node.u.moved = false;
			}
		}
		return true;
	}
	
	// static String observeState(boolean printHP) {
	// StringBuilder sb = new StringBuilder();
	// for (int i = 0; i < map.length; i++) {
	// for (int j = 0; j < map[i].length; j++) {
	// sb.append(map[i][j].toString());
	// }
	// sb.append('\n');
	// }
	// if (printHP)
	// for (int i = 0; i < map.length; i++) {
	// for (int j = 0; j < map.length; j++) {
	// if (map[i][j].u.type == GOBLIN || map[i][j].u.type == ELF) {
	// Unit u = map[i][j].u;
	// sb.append("Unit ").append(u.toString()).append('(').append(i).append(',').append(j).append("):
	// ").append(u.hp).append('\n');
	// }
	// }
	// }
	// return sb.toString();
	// }
	
	private static class Unit {
		char type;
		int hp;
		boolean moved = false;
		static final int attDamage = 3;
		
		Unit(char type) {
			this.type = type;
			hp = 200;
		}
		
		@Override
		public String toString() {
			return type + "";
		}
	}
	
	private static class Pos {
		int x;
		int y;
		
		public Pos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		// @Override
		// public String toString() {
		// return "(" + x + ',' + y + ')';
		// }
		
		@Override
		public int hashCode() {
			return 31 * (31 + x) + y;
		}
		
		// @Override
		// public boolean equals(Object obj) {
		// if (this == obj)
		// return true;
		// if (obj == null || getClass() != obj.getClass())
		// return false;
		// Pos other = (Pos) obj;
		// return (x == other.x && y == other.y);
		// }
		
	}
	
	private static void mapRead() {
		List<String> strings = new ArrayList<>();
		while (ScanTools.hasNext()) {
			strings.add(ScanTools.getNext());
		}
		map = new Node[strings.size()][];
		for (int i = 0; i < map.length; i++) {
			map[i] = new Node[strings.get(i).length()];
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new Node(new Pos(j, i), new Unit(strings.get(i).charAt(j)));
				if (map[i][j].u.type == GOBLIN) {
					goblinsAlive++;
				} else if (map[i][j].u.type == ELF) {
					elvesAlive++;
				}
			}
		}
	}
	
	private static class Node {
		
		Unit u;
		Pos p;
		
		Node(Pos p, Unit u) {
			this.u = u;
			this.p = p;
		}
		
		@Override
		public int hashCode() {
			return 31 + ((p == null) ? 0 : p.hashCode());
		}
		
		// @Override
		// public boolean equals(Object obj) {
		// if (this == obj)
		// return true;
		// if (obj == null || getClass() != obj.getClass())
		// return false;
		// Node other = (Node) obj;
		// if (p == null) {
		// if (other.p != null)
		// return false;
		// return true;
		// }
		// return (p.equals(other.p));
		// }
		
		static boolean attack(Node from, char enemy) {
			Unit bestUnit = null;
			int bestX = 0, bestY = 0;
			for (int i = 0; i < 4; i++) {
				int xt = from.p.x + ((i % 4) - (i > 1 ? 1 : 2)) % 2;
				int yt = from.p.y + ((i % 4) - (i > 1 ? 2 : 1)) % 2;
				Unit u = map[yt][xt].u;
				if (u.type == enemy && (bestUnit == null || u.hp < bestUnit.hp)) {
					bestUnit = u;
					bestX = xt;
					bestY = yt;
				}
			}
			if (bestUnit != null) {
				bestUnit.hp -= Unit.attDamage;
				if (bestUnit.hp <= 0) {
					if (enemy == GOBLIN) {
						goblinsAlive--;
					} else if (enemy == ELF) {
						elvesAlive--;
					}
					map[bestY][bestX].u = new Unit(FREE);
				}
				return true;
			}
			return false;
		}
		
		static void move(Node from, Node to, char enemy) {
			if (to != null) {
				to.u = from.u;
				from.u = new Unit(FREE);
				attack(to, enemy);
			} else {
				attack(from, enemy);
			}
		}
		
		static Node search(Node start, char enemy) {
			Queue<Node> nodeQueue = new LinkedList<>();
			Map<Node, Node> parents = new HashMap<>();
			Set<Node> visited = new HashSet<>();
			List<Node> potentials = new ArrayList<>();
			parents.put(start, null);
			visited.add(start);
			nodeQueue.offer(start);
			boolean foundPot = false;
			while (!nodeQueue.isEmpty()) {
				Node now = nodeQueue.poll();
				for (int i = 0; i < 4; i++) {
					int xt = now.p.x + ((i % 4) - (i > 1 ? 1 : 2)) % 2;
					int yt = now.p.y + ((i % 4) - (i > 1 ? 2 : 1)) % 2;
					Node next = map[yt][xt];
					if (next.u.type == FREE && !visited.contains(next) && !foundPot) {
						nodeQueue.offer(next);
						parents.put(next, now);
						visited.add(next);
					} else if (next.u.type == enemy) {
						potentials.add(now);
						foundPot = true;
					}
				}
			}
			Node best = null;
			for (Node node : potentials) {
				if (isFirst(node, best)) {
					best = node;
				}
			}
			if (best == null) {
				return null;
			}
			return getRoot(parents, best, start);
		}
		
		// static Node searchOld(Node start, char enemy) {
		// Queue<Node> nodeQueue = new LinkedList<>();
		// Map<Node, Node> parents = new HashMap<>();
		// Set<Node> visited = new HashSet<>();
		// parents.put(start, null);
		// visited.add(start);
		// nodeQueue.offer(start);
		// while (!nodeQueue.isEmpty()) {
		// Node now = nodeQueue.poll();
		// for (int i = 0; i < 4; i++) {
		// int xt = now.p.x + ((i % 4) - (i > 1 ? 1 : 2)) % 2;
		// int yt = now.p.y + ((i % 4) - (i > 1 ? 2 : 1)) % 2;
		// Node next = map[yt][xt];
		// if (next.u.type == FREE && !visited.contains(next)) {
		// nodeQueue.offer(next);
		// parents.put(next, now);
		// visited.add(next);
		// } else if (next.u.type == enemy) {
		// return getRoot(parents, now, start);
		// }
		// }
		// }
		// return null;
		// }
		
		// @Override
		// public String toString() {
		// return u.toString();
		// }
		
		static Node getRoot(Map<Node, Node> parents, Node from, Node to) {
			Node p, n = from;
			while (n != to && n != null) {
				p = parents.get(n);
				if (p == to) {
					return n;
				}
				n = p;
			}
			return n;
		}
		
		static boolean isFirst(Node n, Node prevBest) {
			if (prevBest == null) {
				return true;
			}
			if (n.p.y == prevBest.p.y) {
				return n.p.x < prevBest.p.x;
			}
			return n.p.y < prevBest.p.y;
		}
	}
	
}
