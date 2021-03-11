import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class Day20 {
	private static final String TODAY = Day20.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day20::part1, 1);
		ScanTools.wrap(INPUT, Day20::part2, 2);
		System.out.println();
	}
	
	// 4948 > hig || 3422 < low
	private static Object part1() {
		Room start = assembleRooms();
		return bfsTraverse(start, 0);
	}
	
	private static Object part2() {
		Room start = assembleRooms();
		return bfsTraverse(start, 1000);
	}
	
	// traverse all possible regices and construct a room.
	private static Room assembleRooms() {
		Map<Pos, Room> map = new HashMap<>();
		char[] regex = null;
		while (ScanTools.hasNext()) {
			regex = ScanTools.getNext().toCharArray();
		}
		Room curr = new Room();
		Pos prevPos = new Pos(0, 0);
		map.put(new Pos(prevPos), curr);
		
		Stack<Pos> posStack = new Stack<>();
		posStack.push(new Pos(prevPos));
		
		Room prev = null;
		if (regex != null) {
			for (char c : regex) {
				prev = map.get(prevPos);
				switch (c) {
					case 'N':
						if ((curr = map.get(new Pos(prevPos.x, prevPos.y - 1))) == null) {
							curr = new Room(null, null, prev, null);
							map.put(new Pos(prevPos.x, prevPos.y - 1), curr);
						} else {
							curr.south = prev;
						}
						prev.north = curr;
						prevPos = new Pos(prevPos.x, prevPos.y - 1);
						break;
					case 'E':
						if ((curr = map.get(new Pos(prevPos.x + 1, prevPos.y))) == null) {
							curr = new Room(null, null, null, prev);
							map.put(new Pos(prevPos.x + 1, prevPos.y), curr);
						} else {
							curr.west = prev;
						}
						prev.east = curr;
						prevPos = new Pos(prevPos.x + 1, prevPos.y);
						break;
					case 'S':
						if ((curr = map.get(new Pos(prevPos.x, prevPos.y + 1))) == null) {
							curr = new Room(prev, null, null, null);
							map.put(new Pos(prevPos.x, prevPos.y + 1), curr);
						} else {
							curr.north = prev;
						}
						prev.south = curr;
						prevPos = new Pos(prevPos.x, prevPos.y + 1);
						break;
					case 'W':
						if ((curr = map.get(new Pos(prevPos.x - 1, prevPos.y))) == null) {
							curr = new Room(null, prev, null, null);
							map.put(new Pos(prevPos.x - 1, prevPos.y), curr);
						} else {
							curr.east = prev;
						}
						prev.west = curr;
						prevPos = new Pos(prevPos.x - 1, prevPos.y);
						break;
					case '(':
						posStack.push(new Pos(prevPos));
						break;
					case '|':
						prevPos = posStack.peek();
						break;
					case ')':
						posStack.pop();
						break;
					default:
						break;
				}
			}
		}
		return map.get(new Pos(0, 0));
	}
	
	// Traverse all possible paths. Keep track of distance
	/**
	 * Traverses a Room system.
	 * 
	 * @param start The room to start in.
	 * @param countDistance if 0 it will return distance. if higher it will function as which distance to surpass.
	 * @return the distance if countDistance <= 0 otherwise the amount of rooms that surpassed the countDistance.
	 */
	private static int bfsTraverse(Room start, int countDistance) {
		Queue<List<Room>> process = new LinkedList<>();
		{
			List<Room> rooms = new ArrayList<>();
			rooms.add(start);
			process.add(rooms);
		}
		Set<Room> visited = new HashSet<>();
		visited.add(start);
		int distance = -1;
		int count = 0;
		// process every room until no more rooms can be reached.
		while (!process.isEmpty()) {
			distance++;
			List<Room> prev = process.poll();
			// Process each room with equal distance to origin.
			if (prev != null) {
				List<Room> next = new ArrayList<>();
				for (Room room : prev) {
					if (room == null)
						continue;
					if (distance >= countDistance) {
						count++;
					}
					// Add each room to a list of rooms to be processed next.
					if (room.north != null && !visited.contains(room.north)) {
						next.add(room.north);
						visited.add(room.north);
					}
					if (room.south != null && !visited.contains(room.south)) {
						next.add(room.south);
						visited.add(room.south);
					}
					if (room.east != null && !visited.contains(room.east)) {
						next.add(room.east);
						visited.add(room.east);
					}
					if (room.west != null && !visited.contains(room.west)) {
						next.add(room.west);
						visited.add(room.west);
					}
					// Add a list of all the rooms adjacent to this room
				}
				if (!next.isEmpty())
					process.add(next);
			}
		}
		return (countDistance <= 0 ? distance : count);
	}
	
	private static class Pos {
		int x, y;
		
		Pos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public Pos(Pos pos) {
			this(pos.x, pos.y);
		}
		
		@Override
		public int hashCode() {
			return 9769 * (9769 + x) + y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Pos other = (Pos) obj;
			return (x == other.x && y == other.y);
		}
		
		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
		
	}
	
	private static class Room {
		Room north;
		Room east;
		Room south;
		Room west;
		
		public Room() {
		}
		
		public Room(Room north, Room east, Room south, Room west) {
			this.north = north;
			this.east = east;
			this.south = south;
			this.west = west;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(north == null ? "" : "N");
			sb.append(east == null ? "" : "E");
			sb.append(south == null ? "" : "S");
			sb.append(west == null ? "" : "W");
			return sb.toString();
		}
		
	}
}
