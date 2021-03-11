import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day02 {
	
	private static final String TODAY = Day02.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day02::part1, 1);
		ScanTools.wrap(INPUT, Day02::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		int twos = 0;
		int threes = 0;
		int array[] = new int[26];
		while (ScanTools.hasNext()) {
			boolean isTwo = false, isThree = false;
			Arrays.fill(array, 0);
			String n = ScanTools.getNext();
			for (char c : n.toCharArray()) {
				array[c - 'a']++;
			}
			for (int i = 0; i < array.length; i++) {
				if (isTwo && isThree) {
					break;
				}
				if (array[i] == 2) {
					isTwo = true;
				} else if (array[i] == 3) {
					isThree = true;
				}
			}
			if (isTwo)
				twos++;
			if (isThree)
				threes++;
		}
		return (twos * threes);
	}
	
	private static Object part2() {
		String result = null;
		List<String> strings = new ArrayList<>();
		while (ScanTools.hasNext()) {
			String n = ScanTools.getNext();
			strings.add(n);
		}
		outer: for (int i = 0; i < strings.size() - 1; i++) {
			middle: for (int j = i + 1; j < strings.size(); j++) {
				int differ = -1;
				for (int k = 0; k < strings.get(i).length(); k++) {
					char c = strings.get(i).charAt(k);
					char d = strings.get(j).charAt(k);
					if (c != d) {
						if (differ > -1) {
							continue middle;
						}
						differ = k;
					}
				}
				if (differ > -1) {
					result = new StringBuilder(strings.get(i)).deleteCharAt(differ).toString();
					break outer;
				}
			}
		}
		return result;
	}
}
