public class Day11 {
	private static final String TODAY = Day11.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day11::part1, 1);
		ScanTools.wrap(INPUT, Day11::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		int gridSerial = Integer.parseInt(ScanTools.getNext());
		int[][] grid = new int[300][];
		fillGrid(grid, gridSerial);
		return findLargestNxN(grid, 3, false);
	}
	
	/**
	 * TODO: Could be optimised (time = 32s):<br>
	 * - Instead of taking the sum of each 1x1 then 2x2 etc. of a grid -> take a 1x1, increase the size<br>
	 * to a 2x2, then 3x3, 4x4, etc. This way adding to the same sum each time is much more efficient.<br>
	 * But too lazy to write that now and taking advantage of solution of part1, the way it is now.
	 */
	private static Object part2() {
		int gridSerial = Integer.parseInt(ScanTools.getNext());
		int[][] grid = new int[300][];
		fillGrid(grid, gridSerial);
		String largest = null;
		long largestSum = Long.MIN_VALUE;
		for (int n = 1; n <= 300; n++) {
			String s = findLargestNxN(grid, n, true);
			long sum = Long.parseLong(s.split(",")[2]);
			if (sum > largestSum) {
				largest = s.split(",")[0] + "," + s.split(",")[1] + "," + n;
				largestSum = Math.max(largestSum, sum);
			}
		}
		return largest;
	}
	
	private static String findLargestNxN(int[][] grid, int n, boolean returnSizeToo) {
		int resultX = 0, resultY = 0;
		long largestSum = Long.MIN_VALUE;
		for (int y = 0; y < grid.length - (n - 1); y++) {
			for (int x = 0; x < grid[y].length - (n - 1); x++) {
				long sum = sum(grid, x, y, n);
				if (sum > largestSum) {
					largestSum = sum;
					resultX = x;
					resultY = y;
				}
			}
		}
		return (resultX + 1) + "," + (resultY + 1) + (returnSizeToo ? "," + largestSum : "");
	}
	
	private static long sum(int[][] grid, int xt, int yt, int n) {
		long sum = 0;
		for (int y = yt; y < yt + n; y++) {
			for (int x = xt; x < xt + n; x++) {
				sum += grid[y][x];
			}
		}
		return sum;
	}
	
	private static void fillGrid(int[][] grid, int gridSerial) {
		for (int y = 0; y < grid.length; y++) {
			grid[y] = new int[300];
			for (int x = 0; x < grid[y].length; x++) {
				grid[y][x] = getFuel(x + 1, y + 1, gridSerial);
			}
		}
	}
	
	private static int getFuel(int x, int y, int gridSerial) {
		int rackId = x + 10;
		long powerLevel = rackId * y;
		powerLevel += gridSerial;
		powerLevel *= rackId;
		return getHundredsDigit(powerLevel) - 5;
	}
	
	private static int getHundredsDigit(long powerLevel) {
		if (powerLevel < 100)
			return 0;
		String i = Long.toString(powerLevel);
		
		return Character.getNumericValue(i.charAt(i.length() - 3));
	}
	
}
