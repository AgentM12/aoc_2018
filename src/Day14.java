import java.util.ArrayList;
import java.util.List;

public class Day14 {
	private static final String TODAY = Day14.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day14::part1, 1);
		ScanTools.wrap(INPUT, Day14::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		List<Integer> scoreboard = new ArrayList<>();
		int recipe = boardInit(scoreboard);
		int elf1 = 0;
		int elf2 = 1;
		while (scoreboard.size() < (recipe + 10)) {
			create(scoreboard, elf1, elf2);
			elf1 = recipePick(elf1, scoreboard);
			elf2 = recipePick(elf2, scoreboard);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			sb.append(scoreboard.get(recipe + i));
		}
		return sb.toString();
	}
	
	private static Object part2() {
		List<Integer> scoreboard = new ArrayList<>();
		boardInit(scoreboard);
		int[] pattern = createPattern();
		int elf1 = 0;
		int elf2 = 1;
		int match = 0;
		int index = 0;
		while (match < pattern.length) {
			create(scoreboard, elf1, elf2);
			elf1 = recipePick(elf1, scoreboard);
			elf2 = recipePick(elf2, scoreboard);
			while (index < scoreboard.size()) {
				if (scoreboard.get(index) == pattern[match]) {
					match++;
				} else {
					match = 0;
					if (scoreboard.get(index) == pattern[match]) {
						match++;
					}
				}
				index++;
				if (match >= pattern.length) {
					break;
				}
			}
		}
		return index - pattern.length;
	}
	
	private static int[] createPattern() {
		ScanTools.reset(INPUT);
		String recipe = "";
		while (ScanTools.hasNext()) {
			recipe = ScanTools.getNext();
		}
		int[] pat = new int[recipe.length()];
		for (int i = 0; i < pat.length; i++) {
			pat[i] = Character.getNumericValue(recipe.charAt(i));
		}
		return pat;
	}
	
	private static int boardInit(List<Integer> scoreboard) {
		int recipe = 0;
		while (ScanTools.hasNext()) {
			recipe = Integer.valueOf(ScanTools.getNext());
		}
		scoreboard.add(3);
		scoreboard.add(7);
		return recipe;
	}
	
	private static int recipePick(int elf, List<Integer> scoreboard) {
		return (elf + 1 + scoreboard.get(elf)) % scoreboard.size();
	}
	
	private static void create(List<Integer> scoreboard, int i0, int i1) {
		int res = scoreboard.get(i0) + scoreboard.get(i1);
		if (res > 9) {
			res -= 10;
			scoreboard.add(1);
		}
		scoreboard.add(res);
	}
}
