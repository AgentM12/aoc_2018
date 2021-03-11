public class Day05 {
	
	private static final char[] alpha = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	private static final char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	
	private static final String TODAY = Day05.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day05::part1, 1);
		ScanTools.wrap(INPUT, Day05::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		String s = "";
		while (ScanTools.hasNext()) {
			s = ScanTools.getNext();
		}
		int previousLength = s.length() + 1;
		while (s.length() < previousLength) {
			previousLength = s.length();
			for (int i = 0; i < ALPHA.length; i++) {
				s = s.replaceAll("" + alpha[i] + ALPHA[i] + "|" + ALPHA[i] + alpha[i], "");
			}
		}
		return s.length();
	}
	
	private static Object part2() {
		int bestLength = -1;
		String polymer = "";
		while (ScanTools.hasNext()) {
			polymer = ScanTools.getNext();
		}
		bestLength = polymer.length();
		for (int k = 0; k < alpha.length; k++) {
			String s = polymer;
			s = s.replaceAll("[" + alpha[k] + ALPHA[k] + "]", "");
			StringBuilder sb = new StringBuilder(s);
			int previousLength = sb.length() + 1;
			while (sb.length() < previousLength) {
				previousLength = sb.length();
				for (int i = 0; i < sb.length() - 1; i++) {
					char c = sb.charAt(i);
					if ((Character.isLowerCase(c) && sb.charAt(i + 1) == Character.toUpperCase(c))
							|| (Character.isUpperCase(c) && sb.charAt(i + 1) == Character.toLowerCase(c))) {
						sb = sb.delete(i, i + 2);
						i--;
					}
				}
			}
			if (sb.length() < bestLength) {
				bestLength = sb.length();
			}
		}
		return bestLength;
	}
}
