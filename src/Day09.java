import java.util.Arrays;
import java.util.LinkedList;

public class Day09 {
	private static final String TODAY = Day09.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day09::part1, 1);
		ScanTools.wrap(INPUT, Day09::part2, 2);
		System.out.println();
	}
	
	private static long shared(int multiplier) {
		int players = -1;
		int lastMarbleWorth = -1;
		while (ScanTools.hasNext()) {
			String[] s = ScanTools.getNext().trim().split(" ");
			players = Integer.parseInt(s[0].trim());
			lastMarbleWorth = Integer.parseInt(s[6]);
		}
		LinkedList<Integer> marbles = new LinkedList<>();
		long highscore = 0;
		marbles.addFirst(0);
		int p = 1;
		long[] playerScores = new long[players];
		Arrays.fill(playerScores, 0);
		lastMarbleWorth *= multiplier;
		for (int i = 1; i <= lastMarbleWorth; i++) {
			if (i % 23 == 0) {
				rotate(-7, marbles);
				playerScores[p - 1] += i + marbles.removeFirst();
			} else {
				rotate(2, marbles);
				marbles.addLast(i);
			}
			p = (p % players) + 1;
		}
		for (int i = 0; i < playerScores.length; i++) {
			if (highscore < playerScores[i]) {
				highscore = playerScores[i];
			}
		}
		return highscore;
	}
	
	private static Object part1() {
		return shared(1);
	}
	
	private static void rotate(int rotation, LinkedList<Integer> marbles) {
		if (rotation >= 0) {
			for (int i = 0; i < rotation; i++) {
				marbles.addFirst(marbles.removeLast());
			}
		} else {
			for (int i = 0; i < 0 - rotation - 1; i++) {
				marbles.addLast(marbles.removeFirst());
			}
		}
	}
	
	private static Object part2() {
		return shared(100);
	}
	
}
