import java.util.ArrayList;
import java.util.List;

public class Day13 {
	private static final String TODAY = Day13.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day13::part1, 1);
		ScanTools.wrap(INPUT, Day13::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		Network network = gen();
		int x = -1;
		int y = -1;
		try {
			while (network.tick()) {
			}
			
		} catch (CrashException e) {
			x = e.x;
			y = e.y;
		}
		return x + "," + y;
	}
	
	private static Object part2() {
		Network network = gen();
		while (!isLastCart(network)) {
			try {
				while (network.tick()) {
				}
				
			} catch (CrashException ignored) {
			}
		}
		return lastCart(network);
	}
	
	private static String lastCart(Network network) {
		for (int y = 0; y < network.h; y++) {
			for (int x = 0; x < network.w; x++) {
				Track t = network.tracks[y * network.w + x];
				if (t != null && t.isOccupied()) {
					return x + "," + y;
				}
			}
		}
		return "NONE FOUND";
	}
	
	private static boolean isLastCart(Network network) {
		boolean fo = false;
		for (int y = 0; y < network.h; y++) {
			for (int x = 0; x < network.w; x++) {
				Track t = network.tracks[y * network.w + x];
				if (t != null && t.isOccupied()) {
					if (fo) {
						return false;
					}
					fo = true;
				}
			}
		}
		return true;
	}
	
	private static Network gen() {
		List<String> lines = new ArrayList<>();
		while (ScanTools.hasNext())
			lines.add(ScanTools.getNext());
		
		return generateNetwork(lines);
	}
	
	private static Network generateNetwork(List<String> lines) {
		Network net = new Network(lines.get(0).length(), lines.size());
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				if (isValid(c)) {
					net.add(x, y, c);
				}
			}
		}
		return net;
	}
	
	private static boolean isValid(char c) {
		return (c == '<' || c == '^' || c == '>' || c == 'v' || c == '|' || c == '-' || c == '+' || c == '/' || c == '\\');
	}
	
	private static class Network {
		Track[] tracks;
		int w, h;
		
		public Network(int width, int height) {
			this.w = width;
			this.h = height;
			tracks = new Track[w * h];
		}
		
		void add(int x, int y, char c) {
			add(x, y, Track.build(c));
		}
		
		void add(int x, int y, Track t) {
			tracks[y * w + x] = t;
		}
		
		boolean tick() {
			RuntimeException e = null;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					Track ct = tracks[y * w + x];
					if (ct == null)
						continue;
					if (ct.isOccupied() && !ct.occupant.moved) {
						findNextDir(ct);
						int destX = getDestX(ct, x);
						int destY = getDestY(ct, y);
						Track t = getTrackTo(ct, destX, destY);
						if (!move(ct, t) && e == null)
							e = new CrashException("First crash occurred at:", destX, destY);
					}
				}
			}
			settle();
			if (e != null)
				throw e;
			return true;
		}
		
		void settle() {
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					Track ct = tracks[y * w + x];
					if (ct != null && ct.occupant != null)
						ct.occupant.moved = false;
				}
			}
		}
		
		void findNextDir(Track from) {
			if (from.tt == TrackType.Plus) {
				from.occupant.changeDirection();
			} else {
				from.occupant.dir = from.tt.getNext(from.occupant.dir);
			}
		}
		
		int getDestX(Track from, int x) {
			return (x + from.occupant.dir.vx);
		}
		
		int getDestY(Track from, int y) {
			return (y + from.occupant.dir.vy);
		}
		
		Track getTrackTo(Track from, int destX, int destY) {
			Track to = tracks[destY * w + destX];
			if (to == null) {
				throw new IllegalArgumentException("NO TRACK AT " + destX + ", " + destY + "!!");
			}
			return to;
		}
		
		boolean move(Track from, Track to) {
			if (to.isOccupied()) {
				to.occupant = null;
				from.occupant = null;
				return false;
			}
			to.occupant = from.occupant;
			from.occupant = null;
			to.occupant.moved = true;
			return true;
		}
		
	}
	
	private static class Track {
		Cart occupant;
		TrackType tt;
		
		private Track() {
		}
		
		static Track build(char c) {
			Track t = new Track();
			if (c == '<' || c == '>') {
				t.tt = TrackType.Min;
				t.occupant = new Cart(D.of(c));
			} else if (c == '^' || c == 'v') {
				t.tt = TrackType.Pipe;
				t.occupant = new Cart(D.of(c));
			} else {
				t.tt = TrackType.of(c);
				t.occupant = null;
			}
			return t;
		}
		
		boolean isOccupied() {
			return occupant != null;
		}
		
	}
	
	private static class Cart {
		
		D dir;
		int lastChoice;
		boolean moved;
		
		public Cart(D dir) {
			this.dir = dir;
			lastChoice = 0;
			moved = false;
		}
		
		void changeDirection() {
			switch (lastChoice) {
				case 0:
					dir = dir.left();
					//$FALL-THROUGH$
				case 1:
					lastChoice++;
					break;
				case 2:
					dir = dir.right();
					lastChoice = 0;
					break;
				default:
					throw new RuntimeException("This should never happen!");
			}
		}
		
	}
	
	enum TrackType {
		//@formatter:off
		FSlash(D.WEST, D.SOUTH, D.EAST, D.NORTH),  // /
		BSlash(D.EAST, D.NORTH, D.WEST, D.SOUTH),  // \
		Min(D.NONE, D.WEST, D.NONE, D.EAST),       // -
		Pipe(D.SOUTH, D.NONE, D.NORTH, D.NONE),    // |
		Plus(D.NONE, D.NONE, D.NONE, D.NONE);      // +
		//@formatter:on
		
		D fromNorth;
		D fromEast;
		D fromSouth;
		D fromWest;
		
		TrackType(D n, D e, D s, D w) {
			fromNorth = n;
			fromEast = e;
			fromSouth = s;
			fromWest = w;
		}
		
		public static TrackType of(char c) {
			switch (c) {
				case '/':
					return FSlash;
				case '\\':
					return BSlash;
				case '-':
					return Min;
				case '|':
					return Pipe;
				case '+':
					return Plus;
				default:
					throw new RuntimeException("This should never happen!");
			}
		}
		
		D getNext(D prev) {
			D ret;
			switch (prev) {
				case NORTH:
					ret = fromSouth;
					break;
				case EAST:
					ret = fromWest;
					break;
				case SOUTH:
					ret = fromNorth;
					break;
				case WEST:
					ret = fromEast;
					break;
				default:
					throw new RuntimeException("This should never happen!");
			}
			if (ret == D.NONE)
				throw new RuntimeException("RAN INTO MALFORMED TRACK!");
			return ret;
		}
	}
	
	enum D {
		NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0), NONE(0, 0);
		
		int vx, vy;
		
		D(int vx, int vy) {
			this.vx = vx;
			this.vy = vy;
		}
		
		public static D of(char c) {
			switch (c) {
				case '^':
					return NORTH;
				case '>':
					return EAST;
				case 'v':
					return SOUTH;
				case '<':
					return WEST;
				default:
					return NONE;
			}
		}
		
		D left() {
			return rot(-1);
		}
		
		D right() {
			return rot(1);
		}
		
		D rot(int by) {
			int n = this.ordinal() + by;
			return D.values()[Math.floorMod(n, 4)];
		}
	}
	
	private static class CrashException extends RuntimeException {
		
		private static final long serialVersionUID = 1L;
		
		int x;
		int y;
		
		CrashException(String message, int x, int y) {
			super(message);
			this.x = x;
			this.y = y;
		}
		
	}
	
}
