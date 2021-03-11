import java.util.ArrayList;
import java.util.List;

public class Day07 {
	
	private static List<Task> tasks = new ArrayList<>();
	private static final String TODAY = Day07.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day07::part1, 1);
		ScanTools.wrap(INPUT, Day07::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		List<Task> todo = new ArrayList<>();
		List<Task> done = new ArrayList<>();
		shared();
		todo.addAll(tasks);
		while (!todo.isEmpty()) {
			Task task = popFirstFree(todo);
			done.add(task);
			for (Task taskNext : todo) {
				if (taskNext.predecessors.contains(task)) {
					taskNext.predecessors.remove(task);
				}
			}
		}
		return getIds(done);
	}
	
	private static Task popFirstFree(List<Task> todo) {
		for (Task task : todo) {
			if (task.predecessors.isEmpty()) {
				todo.remove(task);
				return task;
			}
		}
		return null;
	}
	
	private static void shared() {
		tasks.clear();
		while (ScanTools.hasNext()) {
			String s = ScanTools.getNext();
			String[] task = s.split(" ");
			char firstId = task[1].charAt(0);
			char thenId = task[7].charAt(0);
			Task first = new Task(firstId);
			Task then = new Task(thenId);
			
			if (tasks.contains(first))
				first = tasks.get(tasks.indexOf(first));
			else
				tasks.add(first);
			
			if (tasks.contains(then))
				then = tasks.get(tasks.indexOf(then));
			else
				tasks.add(then);
			
			then.predecessors.add(first);
		}
		tasks.sort(Task::compareTo);
	}
	
	private static Object part2() {
		final int WORKERS = 5, DURATION = 60;
		Worker[] workers = new Worker[WORKERS];
		int timeRequired = 0;
		shared();
		List<Task> todo = new ArrayList<>();
		todo.addAll(tasks);
		
		for (Task task : todo) {
			task.timeRequired += DURATION;
		}
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new Worker(todo);
		}
		
		boolean working = true;
		while (working) {
			working = false;
			for (Worker worker : workers) {
				if (worker.isWorking()) {
					worker.step();
					if (worker.isWorking()) {
						working = true;
					}
				}
			}
			for (Worker worker : workers) {
				if (!worker.isWorking()) {
					Task task = popFirstFree(todo);
					if (task != null) {
						worker.doing = task;
						worker.step();
						working = true;
					}
				}
			}
			if (working) {
				timeRequired++;
			}
		}
		return timeRequired;
	}
	
	static String getIds(List<Task> tasks) {
		StringBuilder sb = new StringBuilder();
		for (Task task : tasks) {
			sb.append(task.id);
		}
		return sb.toString();
	}
	
	private static class Worker {
		private final List<Task> todo;
		Task doing;
		
		public Worker(List<Task> refList) {
			todo = refList;
		}
		
		/**
		 * Makes the worker work on its task.<br>
		 * time = 0: WORKING -> stop -> IDLE<br>
		 * time = 1: WORKING -> time-- -> WORKING
		 * 
		 * time = 0: IDLE -> NaN -> IDLE<br>
		 * time = 1: IDLE -> time-- -> WORKING
		 */
		public void step() {
			if (doing != null) {
				if (doing.timeRequired > 0) {
					doing.timeRequired--;
				} else {
					for (final Task taskNext : todo) {
						if (taskNext.predecessors.contains(doing)) {
							taskNext.predecessors.remove(doing);
						}
					}
					doing = null;
				}
			}
		}
		
		public boolean isWorking() {
			return doing != null;
		}
	}
	
	private static class Task implements Comparable<Task> {
		
		char id;
		List<Task> predecessors;
		int timeRequired;
		
		public Task(char id) {
			this.id = id;
			predecessors = new ArrayList<>();
			timeRequired = 1 + (id - 'A');
		}
		
		@Override
		public int hashCode() {
			return 31 + id;
		}
		
		@Override
		public String toString() {
			return "Task " + (predecessors.isEmpty() ? "START" : "" + getIds(predecessors)) + " -> " + id;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Task other = (Task) obj;
			return id == other.id;
		}
		
		@Override
		public int compareTo(Task o) {
			return this.id - o.id; // ONLY FOR ALPHABETICAL SORTING
		}
	}
}
