import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day16 {
	private static final String TODAY = Day16.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day16::part1, 1);
		ScanTools.wrap(INPUT, Day16::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		List<int[]> registersBefore = new ArrayList<>();
		List<int[]> instructions = new ArrayList<>();
		List<int[]> registersAfter = new ArrayList<>();
		readResults(registersBefore, instructions, registersAfter);
		
		int ambiguities = 0;
		for (int i = 0; i < registersBefore.size(); i++) {
			if (test(registersBefore.get(i), instructions.get(i), registersAfter.get(i)) >= 3) {
				ambiguities++;
			}
		}
		
		return ambiguities;
	}
	
	private static Object part2() {
		List<int[]> registersBefore = new ArrayList<>();
		List<int[]> instructions = new ArrayList<>();
		List<int[]> registersAfter = new ArrayList<>();
		List<int[]> program = new ArrayList<>();
		Map<Integer, List<Operation>> ops = new HashMap<>();
		
		for (int i = 0; i < 16; i++) {
			List<Operation> operationList = new ArrayList<>();
			for (Operation o : operations) {
				operationList.add(o);
			}
			ops.put(i, operationList);
		}
		read(registersBefore, instructions, registersAfter, program);
		
		for (int i = 0; i < registersBefore.size(); i++) {
			pinPoint(ops, registersBefore.get(i), instructions.get(i), registersAfter.get(i));
		}
		
		Operation[] operations = new Operation[16];
		while (!ops.isEmpty()) {
			Operation op = null;
			int key = -1;
			for (int i : ops.keySet()) {
				if (ops.get(i).size() == 1) {
					op = ops.get(i).get(0);
					ops.remove(i);
					key = i;
					break;
				}
			}
			if (op == null) {
				break;
			}
			for (int i : ops.keySet()) {
				ops.get(i).remove(op);
			}
			operations[key] = op;
		}
		
		int[] registers = new int[4];
		for (int[] instruction : program) {
			Instruction it = new Instruction(registers, instruction, operations[instruction[0]]);
			it.execute();
			registers = it.registers;
		}
		return registers[0];
	}
	
	static void pinPoint(Map<Integer, List<Operation>> valids, int[] registerBefore, int[] instruction, int[] registerAfter) {
		List<Operation> newValids = new ArrayList<>(valids.get(instruction[0]));
		for (Operation operation : newValids) {
			Instruction inst = new Instruction(registerBefore, instruction, operation);
			inst.execute();
			if (!Arrays.equals(inst.registers, registerAfter)) {
				valids.get(instruction[0]).remove(operation);
			}
		}
	}
	
	static int test(int[] registerBefore, int[] instruction, int[] registerAfter) {
		int match = 0;
		for (Operation operation : operations) {
			Instruction inst = new Instruction(registerBefore, instruction, operation);
			inst.execute();
			if (Arrays.equals(inst.registers, registerAfter)) {
				match++;
			}
		}
		return match;
	}
	
	private static class Instruction {
		int[] registers;
		int a;
		int b;
		int c;
		Operation operation;
		
		Instruction(int[] init, int[] instruction, Operation operation) {
			registers = Arrays.copyOf(init, 4);
			a = instruction[1];
			b = instruction[2];
			c = instruction[3];
			this.operation = operation;
		}
		
		void execute() {
			operation.exec(a, b, c, registers);
		}
		
	}
	
	//@formatter:off
	static Operation[] operations = {
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
	
	static void read(List<int[]> registersBefore, List<int[]> instructions, List<int[]> registersAfter, List<int[]> program) {
		readResults(registersBefore, instructions, registersAfter);
		readTestProgram(program);
	}
	
	static void readResults(List<int[]> registersBefore, List<int[]> instructions, List<int[]> registersAfter) {
		while (ScanTools.hasNext()) {
			String s = ScanTools.getNext();
			if (s.startsWith("Before")) {
				String[] sp = s.split("[\\[,\\]]");
				init(registersBefore, sp, 1);
				
				s = ScanTools.getNext();
				sp = s.split("\\s");
				init(instructions, sp, 0);
				
				s = ScanTools.getNext();
				sp = s.split("[\\[,\\]]");
				init(registersAfter, sp, 1);
				
			} else if (!s.trim().isEmpty()) {
				break;
			}
		}
	}
	
	static void init(List<int[]> what, String[] from, int offset) {
		int[] to = new int[4];
		for (int k = 0; k < 4; k++)
			to[k] = Integer.valueOf(from[k + offset].trim());
		what.add(to);
	}
	
	static void readTestProgram(List<int[]> program) {
		while (ScanTools.hasNext()) {
			String s = ScanTools.getNext();
			String[] sp = s.split("\\s");
			init(program, sp, 0);
		}
	}
	
}
