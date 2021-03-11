import java.util.ArrayList;
import java.util.List;

public class Day24 {
	private static final String TODAY = Day24.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day24::part1, 1);
		ScanTools.wrap(INPUT, Day24::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		List<Group> armies = new ArrayList<>();
		read(armies);
		battle(armies, 0);
		return countUnits(armies);
	}
	
	// 1959 too low
	private static Object part2() {
		List<Group> armies = new ArrayList<>();
		Group.groupReset();
		read(armies);
		List<Group> armiesCopy = new ArrayList<>();
		resetCopy(armiesCopy, armies);
		int count = -1;
		for (int boost = 1000; immuneAlive(armiesCopy); boost--) {
			resetCopy(armiesCopy, armies);
			incWithBoost(armiesCopy, boost);
			if (!battle(armiesCopy, 10000)) {
				break;
			}
			count = countUnits(armiesCopy);
		}
		return count;
	}
	
	static void resetCopy(List<Group> copy, List<Group> original) {
		copy.clear();
		for (Group group : original) {
			copy.add(new Group(group));
		}
	}
	
	static void incWithBoost(List<Group> armies, int boost) {
		for (Group group : armies) {
			if (group.team == Group.IMMUNE)
				group.attackDamage += boost;
		}
	}
	
	private static boolean immuneAlive(List<Group> armies) {
		for (Group group : armies) {
			if (group.alive() && group.team == Group.IMMUNE) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean battle(List<Group> armies, int timeout) {
		List<Group> attackables = new ArrayList<>();
		attackables.addAll(armies);
		int cycles = 0;
		while (!oneManStanding(armies)) {
			armies.sort(Day24::sort);
			for (Group group : armies) {
				if (group.alive()) {
					group.selectTarget(attackables);
				}
			}
			armies.sort(Day24::initiative);
			for (Group group : armies) {
				if (group.alive()) {
					group.attack();
				}
			}
			for (Group group : armies) {
				if (!group.alive()) {
					group.target = null;
					group.units = 0;
				}
			}
			if (timeout > 0) {
				cycles++;
			}
			attackables.clear();
			addAllAlive(attackables, armies);
			if (cycles > timeout) {
				return false;
			}
		}
		return true;
	}
	
	private static void addAllAlive(List<Group> to, List<Group> from) {
		for (Group group : from) {
			if (group.alive())
				to.add(group);
		}
	}
	
	private static boolean oneManStanding(List<Group> armies) {
		boolean infect = false, immune = false;
		for (Group group : armies) {
			if (!group.alive())
				continue;
			if (group.team == Group.INFECT) {
				infect = true;
			} else if (group.team == Group.IMMUNE) {
				immune = true;
			}
			if (immune && infect)
				return false;
		}
		return true;
	}
	
	private static int sort(Group a, Group b) {
		int eff = b.effectivePower() - a.effectivePower();
		if (eff != 0)
			return eff;
		return initiative(a, b);
	}
	
	private static int initiative(Group a, Group b) {
		return b.initiative - a.initiative;
	}
	
	private static int countUnits(List<Group> army) {
		int unitCount = 0;
		for (Group group : army) {
			unitCount += group.units;
		}
		return unitCount;
	}
	
	private static void read(List<Group> army) {
		char readin = '\0';
		while (ScanTools.hasNext()) {
			String next = ScanTools.getNext().trim();
			if (next.startsWith("Immune System")) {
				readin = 'g';
				continue;
			} else if (next.startsWith("Infection")) {
				readin = 'b';
				continue;
			} else if (next.trim().isEmpty()) {
				continue;
			}
			
			String[] parts = next.split("\\s");
			int units = Integer.valueOf(parts[0]);
			int hp = Integer.valueOf(parts[4]);
			
			String[] specialties = next.split("[\\(\\)]");
			
			List<Trait> immuneTo = new ArrayList<>();
			List<Trait> weakTo = new ArrayList<>();
			String[] rest;
			int addI;
			if (specialties.length > 1) {
				String[] immunities;
				String[] weaknesses;
				if (specialties[1].contains(";")) {
					String[] specs = specialties[1].split(";");
					if (specs[0].startsWith("immune to")) {
						immunities = specs[0].split("to|,");
						weaknesses = specs[1].split("to|,");
					} else {
						weaknesses = specs[0].split("to|,");
						immunities = specs[1].split("to|,");
					}
				} else {
					if (specialties[1].startsWith("immune to")) {
						immunities = specialties[1].split("to|,");
						weaknesses = new String[0];
					} else {
						weaknesses = specialties[1].split("to|,");
						immunities = new String[0];
					}
				}
				
				for (int i = 1; i < immunities.length; i++) {
					immuneTo.add(Trait.parse(immunities[i].trim()));
				}
				for (int i = 1; i < weaknesses.length; i++) {
					weakTo.add(Trait.parse(weaknesses[i].trim()));
				}
				rest = specialties[2].split(" ");
				addI = 0;
			} else {
				rest = specialties[0].split(" ");
				addI = 6;
			}
			
			int attackDamage = Integer.valueOf(rest[addI + 6].trim());
			Trait attackType = Trait.parse(rest[addI + 7].trim());
			int initative = Integer.valueOf(rest[addI + 11].trim());
			
			if (readin == 'g') {
				army.add(new Group(units, hp, immuneTo, weakTo, attackType, attackDamage, initative, Group.IMMUNE));
			} else if (readin == 'b') {
				army.add(new Group(units, hp, immuneTo, weakTo, attackType, attackDamage, initative, Group.INFECT));
			}
		}
	}
	
	private static class Group {
		
		static final int IMMUNE = 0;
		static final int INFECT = 1;
		int units;
		int hp;
		final List<Trait> immuneTo;
		final List<Trait> weakTo;
		Trait attackType;
		int attackDamage;
		int initiative;
		Group target;
		int team;
		int arbitraryGroupNumber;
		static int immCount = 1;
		static int infCount = 1;
		
		public Group(Group g) {
			this(g.units, g.hp, g.immuneTo, g.weakTo, g.attackType, g.attackDamage, g.initiative, g.team);
			this.arbitraryGroupNumber = g.arbitraryGroupNumber;
		}
		
		static void groupReset() {
			immCount = 1;
			infCount = 1;
		}
		
		public Group(int units, int hp, List<Trait> immuneTo, List<Trait> weakTo, Trait attackType, int attackDamage, int initiative, int team) {
			this.units = units;
			this.hp = hp;
			this.immuneTo = new ArrayList<>(immuneTo);
			this.weakTo = new ArrayList<>(weakTo);
			this.attackType = attackType;
			this.attackDamage = attackDamage;
			this.initiative = initiative;
			this.target = null;
			this.team = team;
			this.arbitraryGroupNumber = (team == IMMUNE ? immCount++ : infCount++);
		}
		
		public String abn() {
			return (team == IMMUNE ? "Immune" : "Infection") + " group " + arbitraryGroupNumber;
		}
		
		public boolean alive() {
			return units > 0;
		}
		
		int effectivePower() {
			return units * attackDamage;
		}
		
		Group getSorted(List<Group> parts) {
			List<Group> others = new ArrayList<>();
			for (Group group : parts) {
				if (group.team != this.team)
					others.add(group);
			}
			
			return bestTarget(others);
		}
		
		Group bestTarget(List<Group> enemies) {
			int bestScore = 0;
			Group bestTarget = null;
			for (Group group : enemies) {
				int nscore = calculateScore(group);
				if (nscore == bestScore) {
					bestTarget = best(group, bestTarget);
					bestScore = nscore;
				} else if (nscore > bestScore) {
					bestTarget = group;
					bestScore = nscore;
				}
			}
			return bestTarget;
		}
		
		private int calculateScore(Group a) {
			if (a.immuneTo.contains(this.attackType)) {
				return -1;
			} else if (a.weakTo.contains(this.attackType)) {
				return (2 * effectivePower());
			} else {
				return effectivePower();
			}
		}
		
		private static Group best(Group a, Group b) {
			if (a == null)
				return b;
			if (b == null || a.effectivePower() > b.effectivePower()) {
				return a;
			}
			if (a.effectivePower() < b.effectivePower() || a.initiative < b.initiative) {
				return b;
			}
			return a;
		}
		
		void selectTarget(List<Group> attackables) {
			Group t = getSorted(attackables);
			if (t != null) {
				attackables.remove(t);
			}
			target = t;
		}
		
		void attack() {
			if (target == null || target.immuneTo.contains(this.attackType))
				return;
			int damage = effectivePower();
			if (target.weakTo.contains(this.attackType)) {
				damage *= 2;
			}
			target.units -= (damage / target.hp);
			if (target.units < 0) {
				target.units = 0;
			}
		}
		
		@Override
		public String toString() {
			StringBuilder immunitiesAndWeaknesses = new StringBuilder();
			if (!immuneTo.isEmpty() || !weakTo.isEmpty()) {
				immunitiesAndWeaknesses.append("(");
			}
			boolean f = true;
			for (Trait trait : immuneTo) {
				if (f) {
					immunitiesAndWeaknesses.append("immune to ").append(trait.type);
					f = false;
				} else {
					immunitiesAndWeaknesses.append(", ").append(trait.type);
				}
			}
			if (!immuneTo.isEmpty() && !weakTo.isEmpty()) {
				immunitiesAndWeaknesses.append("; ");
			}
			f = true;
			for (Trait trait : weakTo) {
				if (f) {
					immunitiesAndWeaknesses.append("weak to ").append(trait.type);
					f = false;
				} else {
					immunitiesAndWeaknesses.append(", ").append(trait.type);
				}
			}
			if (!immuneTo.isEmpty() || !weakTo.isEmpty()) {
				immunitiesAndWeaknesses.append(") ");
			}
			
			return abn() + ": " + units + " units each with " + hp + " hit points " + immunitiesAndWeaknesses.toString() + "with an attack that does "
					+ attackDamage + " " + attackType.type + " damage at initiative " + initiative + ". [EP=" + effectivePower() + "; T="
					+ (target == null ? null : target.initiative) + "]";
		}
		
	}
	
	private enum Trait {
		SLASHING("slashing"), RADIATION("radiation"), FIRE("fire"), COLD("cold"), BLUDGEONING("bludgeoning");
		
		String type;
		
		Trait(String type) {
			this.type = type;
		}
		
		public static Trait parse(String type) {
			String t = type.trim().toLowerCase();
			for (Trait trait : Trait.values()) {
				if (trait.type.equals(t))
					return trait;
			}
			throw new IllegalArgumentException("We don't support \"" + t + "\"!");
		}
	}
}
