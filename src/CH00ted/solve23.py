import sys, heapq, math

class Bot:
    def __init__(self, pos, r):
        self.pos = pos
        self.r = r
    def __repr__(self):
        return "Bot(%s, %s)" % (str(self.pos), str(self.r))
    def dist(self, other):
        x, y, z = self.pos
        xx, yy, zz = other.pos
        return abs(x-xx) + abs(y-yy) + abs(z-zz)
    def intersects(self, other):
        return self.dist(other)<=self.r+other.r
    def __lt__(self, other):
        #want min cord of predecessor to be before min cord of successor
        x, y, z = self.pos
        xx, yy, zz = other.pos
        return max(abs(x)+abs(y)+abs(z)-self.r, 0)<max(abs(xx)+abs(yy)+abs(zz)-other.r,0) or self.r < other.r
def parse(in_file='input.txt'):
    botl = []
    with open(in_file, 'r') as f:
        for line in f:
            p, r = line.split('r=')
            r = int(r)
            p = p.split('pos=')[1].strip('<>, ').split(',')
            p = tuple(map(int, p))
            bot = Bot(p, r)
            botl.append(bot)
    return botl
def part1(botl):
    max_r = max([b.r for b in botl])
    max_bots = [b for b in botl if b.r==max_r]
    if len(max_bots) != 1:
        raise ValueError('Expected 1 max bot, but got', len(max_bots))
    maxb = max_bots[0]
    in_range = [b for b in botl if maxb.dist(b)<=maxb.r]
    print('Part 1: There are', len(in_range) ,'bots in range of', maxb)

def n_intersects(blist, b):
    return len([o for o in blist if b.intersects(o)])

def split_bot(b):
    x,y,z = b.pos
    if b.r > 2:
        #justification for taking ceiling of radius (note frac(x)=x-floor(x))
        #assume r is not divisible by 3
        #any point within 2r/3 of (r/3,0,0) is at most
        #2r/3+frac(r/3) away from (floor(r/3), 0,0), which is
        #floor(2r/3)+frac(r/3)+frac(2r/3) away
        #but all bots are at integers, so we can take the floor
        #of (frac(r/3)+frac(2r/3)), which is < 2
        #so every point is at most ceil(2r/3)=floor(2r/3)+1 away from (r/3,0,0)
        newr = math.ceil(2*b.r/3)
        opts = [-math.floor(b.r/3),math.floor(b.r/3) ]
        for d in opts:
            yield Bot((x+d,y,z),newr)
            yield Bot((x,y+d,z),newr)
            yield Bot((x,y,z+d),newr)
    else:
        for dx in range(-b.r,b.r+1):
            for dy in range(-(b.r-abs(dx)),b.r+1-abs(dx)):
                for dz in range(-(b.r-abs(dx)-abs(dy)),b.r+1-abs(dx)-abs(dy)):
                    yield Bot((x+dx,y+dy,z+dz),0)

def bot_search(blist):
    Q = []
    r = max([abs(b.pos[0])+abs(b.pos[1])+abs(b.pos[2]) for b in blist])
    search_bot = Bot((0,0,0),r)
    intersects = n_intersects(blist, search_bot)
    heapq.heappush(Q,(-intersects, search_bot))
    n = 0
    while Q:
        oldi, b = heapq.heappop(Q)
        if b.r<=0:
            x,y,z = b.pos
            print("Part 2: At distance", abs(x)+abs(y)+abs(z), 'from (0,0,0) we are at', b.pos ,'in range of', -oldi, 'nanobots')
            return
        for test_bot in split_bot(b):
            ins = n_intersects(blist, test_bot)
            heapq.heappush(Q,(-ins, test_bot))

if __name__=='__main__':
    bots = parse(sys.argv[1])
    part1(bots)
    bot_search(bots)