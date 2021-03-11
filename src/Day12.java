import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day12 {
	private static final String TODAY = Day12.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day12::part1, 1);
		ScanTools.wrap(INPUT, Day12::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		List<Conversion> conversions = new ArrayList<>();
		char[] initialState = generate(conversions);
		Conversion[] c = new Conversion[conversions.size()];
		conversions.toArray(c);
		return cycle(initialState, c, 20);
	}
	
	private static int sum(char[] state) {
		int sum = 0;
		for (int i = 0; i < state.length; i++) {
			if (is(state[i]))
				sum += getPot(state.length, i);
		}
		return sum;
	}
	
	private static long cycle(char[] state, Conversion[] rules, long times) {
		long prev = 0, diff = 0;
		int consecSum = 0;
		for (long i = 0; i < times; i++) {
			update(state, rules);
			
			long n = sum(state);
			consecSum = (diff == n - prev) ? consecSum + 1 : 0;
			diff = n - prev;
			prev = n;
			if (consecSum > 10) {
				return (prev + diff * (times - i - 1));
			}
		}
		return sum(state);
	}
	
	private static void update(char[] state, Conversion[] rules) {
		char[] old = Arrays.copyOf(state, state.length);
		for (int i = 2; i < old.length - 2; i++) {
			for (Conversion c : rules) {
				char n = matches(old, i, c);
				if (n != 0)
					state[i] = n;
			}
		}
	}
	
	private static char matches(char[] state, int index, Conversion c) {
		return c.compare(Arrays.copyOfRange(state, index - 2, index + 3));
	}
	
	private static char[] generate(List<Conversion> conversions) {
		char[] initialState = null;
		while (ScanTools.hasNext()) {
			String s = ScanTools.getNext();
			if (initialState == null) {
				String init = s.split(":")[1].trim();
				int size = init.length();
				initialState = convertStoBA(init, size);
				ScanTools.getNext();
			} else {
				String[] ss = s.split("=>");
				char[] p = createPattern(ss[0].trim());
				char t = ss[1].trim().charAt(0);
				conversions.add(new Conversion(p, t));
			}
		}
		return initialState;
	}
	
	private static int getPot(int stateLength, int index) {
		return index - (stateLength / 9) * 4;
	}
	
	private static boolean is(char c) {
		return c == '#';
	}
	
	private static char[] convertStoBA(String s, int sz) {
		char[] ba = new char[sz * 9];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = '.';
		}
		int i = sz * 4;
		for (char c : s.toCharArray()) {
			ba[i] = c;
			i++;
		}
		return ba;
	}
	
	private static class Conversion {
		char turnsInto;
		char[] pattern;
		
		public Conversion(char[] pattern, char turnsInto) {
			this.pattern = pattern;
			this.turnsInto = turnsInto;
		}
		
		public char compare(char[] pattern) {
			if (this.pattern[2] != pattern[2])
				return 0;
			if (this.pattern[1] != pattern[1])
				return 0;
			if (this.pattern[3] != pattern[3])
				return 0;
			if (this.pattern[0] != pattern[0])
				return 0;
			if (this.pattern[4] != pattern[4])
				return 0;
			return turnsInto;
		}
		
		@Override
		public String toString() {
			return String.valueOf(pattern) + " => " + turnsInto;
		}
		
	}
	
	public static char to(boolean b) {
		return b ? '#' : '.';
	}
	
	public static char[] createPattern(String s) {
		char[] b = new char[5];
		int i = 0;
		for (char c : s.toCharArray()) {
			b[i] = c;
			i++;
		}
		return b;
	}
	
	private static Object part2() {
		List<Conversion> conversions = new ArrayList<>();
		char[] initialState = generate(conversions);
		Conversion[] c = new Conversion[conversions.size()];
		conversions.toArray(c);
		return cycle(initialState, c, 50000000000L);
	}
}
