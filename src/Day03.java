import java.util.Arrays;

public class Day03 {
	
	private static int fabric[][];
	private static final String TODAY = Day03.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day03::part1, 1);
		ScanTools.wrap(INPUT, Day03::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		fabric = new int[1000][];
		for (int i = 0; i < fabric.length; i++) {
			fabric[i] = new int[1000];
			Arrays.fill(fabric[i], 0);
		}
		while (ScanTools.hasNext()) {
			String n = ScanTools.getNext();
			String[] split = n.split("[@:]");
			// int id = Integer.parseInt(split[0].substring(1).trim());
			int xOrigin = Integer.parseInt(split[1].trim().split(",")[0]);
			int yOrigin = Integer.parseInt(split[1].trim().split(",")[1]);
			int width = Integer.parseInt(split[2].trim().split("x")[0]);
			int height = Integer.parseInt(split[2].trim().split("x")[1]);
			
			for (int y = yOrigin; y < yOrigin + height; y++) {
				for (int x = xOrigin; x < xOrigin + width; x++) {
					fabric[y][x]++;
				}
			}
		}
		int overlap = 0;
		for (int i = 0; i < fabric.length; i++) {
			for (int j = 0; j < fabric.length; j++) {
				if (fabric[i][j] > 1) {
					overlap++;
				}
			}
		}
		return overlap;
	}
	
	private static Object part2() {
		outer: while (ScanTools.hasNext()) {
			String n = ScanTools.getNext();
			String[] split = n.split("[@:]");
			int id = Integer.parseInt(split[0].substring(1).trim());
			int xOrigin = Integer.parseInt(split[1].trim().split(",")[0]);
			int yOrigin = Integer.parseInt(split[1].trim().split(",")[1]);
			int width = Integer.parseInt(split[2].trim().split("x")[0]);
			int height = Integer.parseInt(split[2].trim().split("x")[1]);
			
			for (int y = yOrigin; y < yOrigin + height; y++) {
				for (int x = xOrigin; x < xOrigin + width; x++) {
					if (fabric[y][x] > 1) {
						continue outer;
					}
				}
			}
			return id;
		}
		return -1;
	}
}
