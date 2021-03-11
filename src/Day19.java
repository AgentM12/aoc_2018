import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// HIGHLY ANALYZED INPUT TO OPTIMIZE ALGORITHM OF PROGRAM.
public class Day19 {
	
	private static int IP;
	private static List<Instruction> program;
	static final Map<String, Operation> operations = new HashMap<>();
	private static int[] registers = { 0, 0, 0, 0, 0, 0 };
	
	private static final String TODAY = Day19.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day19::part1, 1);
		ScanTools.wrap(INPUT, Day19::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		read();
		return runProgram(0);
	}
	
	private static int runProgram(int output) {
		int ip = 0;
		try {
			while (true) {
				registers[IP] = ip;
				program.get(registers[IP]).execute(registers);
				ip = registers[IP];
				ip++;
			}
		} catch (IndexOutOfBoundsException ignored) {
		}
		return registers[output];
	}
	
	private static Object part2() {
		read();
		return primeFactors(10551418);
	}
	
	private static void read() {
		program = new ArrayList<>();
		if (operations.isEmpty()) {
			initOperations();
		}
		if (ScanTools.hasNext()) {
			IP = Integer.valueOf(ScanTools.getNext().split("\\s")[1].trim());
		}
		while (ScanTools.hasNext()) {
			String s = ScanTools.getNext();
			program.add(Instruction.parseFromString(s));
		}
		
	}
	
	private static class Instruction {
		
		Operation op;
		int a, b, c;
		
		public Instruction(String strop, int a, int b, int c) {
			this.op = operations.get(strop);
			this.a = a;
			this.b = b;
			this.c = c;
		}
		
		void execute(int[] register) {
			op.exec(a, b, c, register);
		}
		
		static Instruction parseFromString(String s) {
			String[] tokens = s.split("\\s");
			String strop = tokens[0].trim();
			int a = Integer.valueOf(tokens[1].trim());
			int b = Integer.valueOf(tokens[2].trim());
			int c = Integer.valueOf(tokens[3].trim());
			return new Instruction(strop, a, b, c);
		}
	}
	
	static void initOperations() {
		operations.put("addr", operation[0]);
		operations.put("addi", operation[1]);
		operations.put("mulr", operation[2]);
		operations.put("muli", operation[3]);
		operations.put("banr", operation[4]);
		operations.put("bani", operation[5]);
		operations.put("borr", operation[6]);
		operations.put("bori", operation[7]);
		operations.put("setr", operation[8]);
		operations.put("seti", operation[9]);
		operations.put("gtir", operation[10]);
		operations.put("gtri", operation[11]);
		operations.put("gtrr", operation[12]);
		operations.put("eqir", operation[13]);
		operations.put("eqri", operation[14]);
		operations.put("eqrr", operation[15]);
	}
	
	//@formatter:off
		private static Operation[] operation = {
			(a, b, c, r) -> r[c] = r[a] + r[b], // addr
			(a, b, c, r) -> r[c] = r[a] + b, // addi
			(a, b, c, r) -> r[c] = r[a] * r[b], // mulr
			(a, b, c, r) -> r[c] = r[a] * b, // muli
			(a, b, c, r) -> r[c] = r[a] & r[b], // banr
			(a, b, c, r) -> r[c] = r[a] & b, // bani
			(a, b, c, r) -> r[c] = r[a] | r[b], // borr
			(a, b, c, r) -> r[c] = r[a] | b, // bori
			(a, b, c, r) -> r[c] = r[a], // setr
			(a, b, c, r) -> r[c] = a, // seti
			(a, b, c, r) -> r[c] = (a > r[b] ? 1 : 0), // gtir
			(a, b, c, r) -> r[c] = (r[a] > b ? 1 : 0), // gtri
			(a, b, c, r) -> r[c] = (r[a] > r[b] ? 1 : 0), // gtrr
			(a, b, c, r) -> r[c] = (a == r[b] ? 1 : 0), // eqir
			(a, b, c, r) -> r[c] = (r[a] == b ? 1 : 0), // eqri
			(a, b, c, r) -> r[c] = (r[a] == r[b] ? 1 : 0), // eqrr
		};
		//@formatter:on	
	
	interface Operation {
		int exec(int a, int b, int c, int[] registers);
	}
	
	public static long primeFactors(int number) {
		int n = number;
		List<Integer> factors = new ArrayList<>();
		
		for (int i = 1; i <= n; i++)
			if (n % i == 0)
				factors.add(i);
		long sum = 0;
		for (Integer integer : factors) {
			sum += integer;
		}
		return sum;
	}
}
