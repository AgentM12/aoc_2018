import java.util.HashSet;
import java.util.Set;

// Semi-cheated :/ like bruh, elfcode fu0g off.
public class Day21 {
	
	private static final String TODAY = Day21.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day21::part1, 1);
		ScanTools.wrap(INPUT, Day21::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		return Program.execute(true);
	}
	
	private static Object part2() {
		return Program.execute(false);
	}
	
	private static class Program {
		
		static int execute(boolean part1) {
			int result = -1;
			Set<Integer> r3_found = new HashSet<>();
			int prev_r3 = 0;
			
			int r2 = 0, r3 = 0;
			
			// Translated from ElfCode (Input) to Java.
			while (true) {
				r2 = r3 | 0x10000;
				r3 = 1099159;
				
				while (true) {
					r3 = (r3 + (r2 & 0xff)) & 0xffffff;
					r3 = (r3 * 65899) & 0xffffff;
					if (r2 < 256)
						break;
					
					r2 >>= 8;
				}
				
				// Find the result.
				if (r3_found.isEmpty()) {
					result = r3;
					if (part1)
						break;
				} else if (r3_found.contains(r3)) {
					result = prev_r3;
					break;
				}
				
				r3_found.add(prev_r3 = r3);
			}
			return result;
		}
	}
}
