import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day04 {
	
	static List<Element> dates;
	static Map<Integer, Guard> guards;;
	
	private static final String TODAY = Day04.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day04::part1, 1);
		ScanTools.wrap(INPUT, Day04::part2, 2);
		System.out.println();
	}
	
	private static class Element implements Comparable<Element> {
		Date date;
		int hour;
		int minute;
		int id;
		Action action;
		
		Element(Date date, int hour, int minute, Action action) {
			this.date = date;
			this.hour = hour;
			this.minute = minute;
			this.action = action;
			this.id = -1;
		}
		
		@Override
		public int compareTo(Element o) {
			int d = date.compareTo(o.date);
			if (d != 0)
				return d;
			return (hour - o.hour) * 61 + (minute - o.minute);
		}
	}
	
	private static void shared() {
		dates = new ArrayList<>();
		guards = new HashMap<>();
		// READ IN DATA
		while (ScanTools.hasNext()) {
			String s = ScanTools.getNext();
			String[] dta = s.split("[\\[\\]]");
			String[] dt = dta[1].split(" ");
			String[] d = dt[0].split("-");
			int year = Integer.parseInt(d[0]);
			int month = Integer.parseInt(d[1]);
			int day = Integer.parseInt(d[2]);
			
			String a = dta[2];
			Action action = a.contains("falls asleep") ? Action.SLEEP : (a.contains("wakes up") ? Action.WAKE : Action.START);
			int hour = Integer.parseInt(dt[1].split(":")[0]);
			int minute = Integer.parseInt(dt[1].split(":")[1]);
			Date date = new Date(year, month, day);
			Element e = new Element(date, hour, minute, action);
			if (action == Action.START) {
				e.id = Integer.parseInt(a.split(" ")[2].substring(1));
				guards.put(e.id, new Guard(e.id));
			}
			dates.add(e);
		}
		
		// SORT DATA and ASSIGN MISSING ID'S
		dates.sort(Element::compareTo);
		int id = -1;
		for (Element e : dates) {
			if (e.id != -1) {
				id = e.id;
			} else {
				e.id = id;
			}
		}
		
		// FILL IN SCHEDULES
		int minuteSleep = -1, minuteWake = -1;
		Guard g = null;
		for (Element e : dates) {
			if (e.action == Action.START) {
				minuteSleep = -1;
				minuteWake = -1;
				g = guards.get(e.id);
				continue;
			}
			if (g == null) {
				throw new IllegalArgumentException();
			}
			if (e.action == Action.SLEEP) {
				minuteSleep = e.minute;
			} else if (e.action == Action.WAKE) {
				minuteWake = e.minute;
				if (!g.schedule.containsKey(e.date)) {
					boolean[] b = new boolean[60];
					Arrays.fill(b, false);
					g.schedule.put(e.date, b);
				}
				g.minutesSlept += (minuteWake - minuteSleep);
				Arrays.fill(g.schedule.get(e.date), minuteSleep, minuteWake, true);
				minuteSleep = -1;
				minuteWake = -1;
			}
		}
	}
	
	private static Object part1() {
		shared();
		
		// SORT GUARDS BY SLEEPIEST GUARD.
		Guard[] gl = new Guard[guards.size()];
		guards.values().toArray(gl);
		Arrays.sort(gl, Guard::compareTo);
		
		// Merge all schedules into one with weights.
		Guard best = gl[gl.length - 1];
		boolean[][] g2 = new boolean[best.schedule.size()][];
		best.schedule.values().toArray(g2);
		
		int[] minutes = new int[60];
		Arrays.fill(minutes, 0);
		for (int i = 0; i < g2.length; i++) {
			for (int j = 0; j < g2[i].length; j++) {
				if (g2[i][j])
					minutes[j]++;
			}
		}
		
		// Find the highest minute
		int bestMinute = 0;
		for (int i = 0; i < minutes.length; i++) {
			if (minutes[i] > minutes[bestMinute]) {
				bestMinute = i;
			}
		}
		
		// Return bestMinute * bestGuard.id
		return bestMinute * best.id;
	}
	
	private static Object part2() {
		shared();
		
		List<Record> records = new ArrayList<>();
		for (Guard guard : guards.values()) {
			boolean[][] g2 = new boolean[guard.schedule.size()][];
			guard.schedule.values().toArray(g2);
			
			int[] minutes = new int[60];
			Arrays.fill(minutes, 0);
			for (int i = 0; i < g2.length; i++) {
				for (int j = 0; j < g2[i].length; j++) {
					if (g2[i][j])
						minutes[j]++;
				}
			}
			
			// Find the highest minute
			int bestMinute = 0;
			int bestCount = minutes[0];
			for (int i = 0; i < minutes.length; i++) {
				if (minutes[i] > minutes[bestMinute]) {
					bestMinute = i;
					bestCount = minutes[bestMinute];
				}
			}
			records.add(new Record(guard.id, bestMinute, bestCount));
		}
		records.sort(new Comparator<Record>() {
			@Override
			public int compare(Record o1, Record o2) {
				return o2.bestCount - o1.bestCount;
			}
		});
		Record bestRecord = records.get(0);
		return bestRecord.bestMinute * bestRecord.guardId;
	}
	
	private static class Record {
		int guardId;
		int bestMinute;
		int bestCount;
		
		Record(int guardId, int bestMinute, int bestCount) {
			this.guardId = guardId;
			this.bestMinute = bestMinute;
			this.bestCount = bestCount;
		}
		
	}
	
	enum Action {
		SLEEP, WAKE, START;
	}
	
	private static class Guard implements Comparable<Guard> {
		
		int id;
		Map<Date, boolean[]> schedule;
		int minutesSlept;
		
		Guard(int id) {
			this.minutesSlept = 0;
			this.id = id;
			this.schedule = new HashMap<>();
		}
		
		@Override
		public int compareTo(Guard o) {
			return minutesSlept - o.minutesSlept;
		}
		
	}
	
	private static class Date implements Comparable<Date> {
		int year;
		int month;
		int day;
		
		Date(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 7;
			result = prime * result + day;
			result = prime * result + month;
			result = prime * result + year;
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Date other = (Date) obj;
			return (day == other.day && month == other.month && year == other.year);
		}
		
		@Override
		public int compareTo(Date o) {
			int oSum = o.day + o.month * 31 + (o.year - 1000) * 31 * 12;
			int sum = day + month * 31 + (year - 1000) * 31 * 12;
			if (sum > oSum) {
				return 1;
			} else if (sum < oSum) {
				return -1;
			}
			return 0;
		}
	}
	
}
