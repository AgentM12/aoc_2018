import java.util.HashSet;
import java.util.Set;

/**
 * Day 1
 * 
 * @author Melvin
 */
public class Day01 {
	
	private static final String TODAY = Day01.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day01::part1, 1);
		ScanTools.wrap(INPUT, Day01::part2, 2);
		System.out.println();
	}
	
	/**
	 * Simply takes the value of each line of input and adds them to the result.
	 */
	private static Object part1() {
		int result = 0;
		while (ScanTools.hasNext()) {
			result += Integer.valueOf(ScanTools.getNext());
		}
		return result;
	}
	
	/**
	 * Keeps cycling through all the inputs and returns which frequency is hit twice first.
	 */
	private static Object part2() {
		int result = 0;
		Set<Integer> ints = new HashSet<>();
		ints.add(result);
		outer: while (true) {
			while (ScanTools.hasNext()) {
				result += Integer.valueOf(ScanTools.getNext());
				if (!ints.add(result)) {
					break outer;
				}
			}
			ScanTools.reset(INPUT);
		}
		return result;
	}
	
}
