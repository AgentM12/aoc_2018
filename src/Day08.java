public class Day08 {
	
	private static Node n;
	private static final String TODAY = Day08.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day08::part1, 1);
		ScanTools.wrap(INPUT, Day08::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		n = new Node();
		String s = "";
		while (ScanTools.hasNext()) {
			s = ScanTools.getNext();
		}
		String[] dataRaw = s.trim().split(" ");
		int[] data = new int[dataRaw.length];
		int i = 0;
		for (String string : dataRaw) {
			data[i++] = Integer.parseInt(string.trim());
		}
		
		read(n, data, 0);
		return metaSum(0, n);
	}
	
	private static Object part2() {
		return rootSum(0, n);
	}
	
	private static int rootSum(int sum, Node n) {
		if (n.children.length > 0) {
			for (int i : n.metadata) {
				i--;
				if (i < n.children.length && i >= 0) {
					sum = rootSum(sum, n.children[i]);
				}
			}
			return sum;
		}
		return sum += metaSum(0, n);
	}
	
	private static int read(Node node, int[] data, int index) {
		int children = data[index++];
		int meta = data[index++];
		node.build(children, meta);
		for (int i = 0; i < node.children.length; i++) {
			node.children[i] = new Node();
			index = read(node.children[i], data, index);
		}
		for (int i = 0; i < node.metadata.length; i++) {
			node.metadata[i] = data[index++];
		}
		return index;
	}
	
	private static int metaSum(int sum, Node... nodes) {
		for (Node node : nodes) {
			sum = metaSum(sum, node.children);
			for (int meta : node.metadata) {
				sum += meta;
			}
		}
		return sum;
	}
	
	private static class Node {
		Node[] children;
		int[] metadata;
		
		public Node() {
			
		}
		
		public void build(int size, int metaDataSize) {
			this.children = new Node[size];
			this.metadata = new int[metaDataSize];
		}
	}
}
