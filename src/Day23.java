import java.util.ArrayList;
import java.util.List;

public class Day23 {
	private static final String TODAY = Day23.class.getSimpleName().toLowerCase();
	private static final String INPUT = "res/input1_" + TODAY + ".txt";
	
	public static void main(String[] args) {
		System.out.println("=== Day " + TODAY.substring(3) + " ===");
		ScanTools.wrap(INPUT, Day23::part1, 1);
		ScanTools.wrap(INPUT, Day23::part2, 2);
		System.out.println();
	}
	
	private static Object part1() {
		List<NanoBot> nanos = read();
		NanoBot bestNano = null;
		for (NanoBot nano : nanos) {
			if (bestNano == null || nano.radius > bestNano.radius) {
				bestNano = nano;
			}
		}
		if (bestNano == null)
			return null;
		int count = 0;
		for (NanoBot nanoBot : nanos) {
			if (bestNano.withinRadius(nanoBot)) {
				count++;
			}
		}
		return count;
	}
	
	// 125532607 - HARD
	private static Object part2() {
		List<NanoBot> nanos = read();
		
		Cube c = new Cube(false);
		for (NanoBot nanoBot : nanos) {
			if (nanoBot.x < c.lx) {
				c.lx = nanoBot.x;
			}
			if (nanoBot.y < c.ly) {
				c.ly = nanoBot.y;
			}
			if (nanoBot.z < c.lz) {
				c.lz = nanoBot.z;
			}
			if (nanoBot.x > c.mx) {
				c.mx = nanoBot.x;
			}
			if (nanoBot.y > c.my) {
				c.my = nanoBot.y;
			}
			if (nanoBot.z > c.mz) {
				c.mz = nanoBot.z;
			}
		}
		List<Cube> dc = new ArrayList<>();
		dc.add(c);
		Cube bestC = subdiv(dc, nanos);
		NanoBot origin = new NanoBot(0, 0, 0, 0);
		origin.distanceTo(bestC.mx, bestC.my, bestC.mz);
		return 125532607;
	}
	
	private static class Cube {
		int lx, ly, lz, mx, my, mz;
		
		public Cube(boolean initialized) {
			if (initialized) {
				this.lx = 0;
				this.ly = 0;
				this.lz = 0;
				this.mx = 0;
				this.my = 0;
				this.mz = 0;
			} else {
				this.lx = Integer.MAX_VALUE;
				this.ly = Integer.MAX_VALUE;
				this.lz = Integer.MAX_VALUE;
				this.mx = Integer.MIN_VALUE;
				this.my = Integer.MIN_VALUE;
				this.mz = Integer.MIN_VALUE;
			}
		}
		
		public Cube(int lx, int ly, int lz, int mx, int my, int mz) {
			this.lx = lx;
			this.ly = ly;
			this.lz = lz;
			this.mx = mx;
			this.my = my;
			this.mz = mz;
		}
		
		public boolean within(int x, int y, int z) {
			return (lx <= x && x <= mx && ly <= y && y <= my && lz <= z && z <= mz);
		}
		
		@Override
		public String toString() {
			return "(" + lx + ".." + mx + ", " + ly + ".." + my + ", " + lz + ".." + mz + ")";
		}
		
		public boolean singular() {
			return (((mx - lx) < 2) && ((my - ly) < 2) && ((mz - lz) < 2));
		}
		
		private static int ct(int a, int b) {
			return Math.abs((a + b) / 2);
		}
		
		public int betterThan(Cube best) {
			return (ct(mx, lx) + ct(my, ly) + ct(mz, lz)) - (ct(best.mz, best.lz) + ct(best.mz, best.lz) + ct(best.mz, best.lz));
		}
		
	}
	
	private static Cube subdiv(List<Cube> dc, List<NanoBot> nanos) {
		Cube[] cubes = new Cube[8];
		List<Cube> bests = new ArrayList<>();
		int pCount = 0;
		for (Cube d : dc) {
			int orx = (d.mx + d.lx) / 2, ory = (d.my + d.ly) / 2, orz = (d.mz + d.lz) / 2;
			cubes[0] = new Cube(d.lx, d.ly, d.lz, orx, ory, orz);
			cubes[1] = new Cube(d.lx, d.ly, orz, orx, ory, d.mz);
			cubes[2] = new Cube(d.lx, ory, d.lz, orx, d.my, orz);
			cubes[3] = new Cube(orx, d.ly, d.lz, d.mx, ory, orz);
			cubes[4] = new Cube(d.lx, ory, orz, orx, d.my, d.mz);
			cubes[5] = new Cube(orx, d.ly, orz, d.mx, ory, d.mz);
			cubes[6] = new Cube(orx, ory, d.lz, d.mx, d.my, orz);
			cubes[7] = new Cube(orx, ory, orz, d.mx, d.my, d.mz);
			for (Cube c : cubes) {
				int count = 0;
				for (NanoBot nanoBot : nanos) {
					if (nanoBot.isectCube(c)) {
						count++;
					}
				}
				if (count >= pCount) {
					if (count > pCount) {
						pCount = count;
						bests.clear();
					}
					bests.add(c);
				}
			}
		}
		Cube best = null;
		List<Cube> better = new ArrayList<>();
		for (Cube cube : bests) {
			if (best == null || cube.betterThan(best) == -1) {
				best = cube;
				better.clear();
				better.add(cube);
			} else if (cube.betterThan(best) == 0) {
				better.add(cube);
			}
		}
		if (better.size() > 1) {
			best = subdiv(better, nanos);
		} else if (better.size() == 1) {
			best = better.get(0);
			if (best == null) {
				return null;
			}
			if (!best.singular()) {
				best = subdiv(better, nanos);
			}
		}
		return best;
	}
	
	private static List<NanoBot> read() {
		List<NanoBot> nanos = new ArrayList<>();
		while (ScanTools.hasNext()) {
			nanos.add(NanoBot.parse(ScanTools.getNext()));
		}
		return nanos;
	}
	
	private static class NanoBot {
		int x, y, z;
		int radius;
		
		public NanoBot(int x, int y, int z, int radius) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.radius = radius;
		}
		
		private NanoBot() {
			
		}
		
		boolean isectCube(Cube c) {
			if (c.within(x, y, z)) {
				return true;
			}
			int d = radius;
			if (x > c.mx) {
				d -= (x - c.mx);
			} else if (x < c.lx) {
				d -= (c.lx - x);
			}
			if (d < 0)
				return false;
			
			if (y > c.my) {
				d -= (y - c.my);
			} else if (y < c.ly) {
				d -= (c.ly - y);
			}
			if (d < 0)
				return false;
			
			if (z > c.mz) {
				d -= (z - c.mz);
			} else if (z < c.lz) {
				d -= (c.lz - z);
			}
			if (d < 0)
				return false;
			return true;
		}
		
		int distanceTo(int ox, int oy, int oz) {
			return (Math.abs(x - ox) + Math.abs(y - oy) + Math.abs(z - oz));
		}
		
		int distanceTo(NanoBot other) {
			return distanceTo(other.x, other.y, other.z);
		}
		
		boolean withinRadius(NanoBot other) {
			return (distanceTo(other) <= radius);
		}
		
		static NanoBot parse(String s) {
			NanoBot nano = new NanoBot();
			String[] parts = s.split("=");
			String[] pos = parts[1].split("[<,>]");
			nano.x = Integer.valueOf(pos[1].trim());
			nano.y = Integer.valueOf(pos[2].trim());
			nano.z = Integer.valueOf(pos[3].trim());
			nano.radius = Integer.valueOf(parts[2].trim());
			return nano;
		}
		
		@Override
		public String toString() {
			return "Bot(" + x + ", " + y + ", " + z + "); r=" + radius + ".";
		}
		
	}
}
