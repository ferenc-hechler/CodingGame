import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	static final double TARGET_RADIUS = 0.0;
	static final double BREAK_FACTOR = 2.0;
	static final double ANGLE_FACTOR = 0.95;
	
    public static class Pos {
        public static final Pos POS0 = new Pos(0.0,0.0);
        public static final Pos POS_EAST = new Pos(1.0,0.0);
        public static final Pos POS_SOUTH = new Pos(0.0,1.0);
        public static final Pos POS_WEST = new Pos(-1.0,0.0);
        public static final Pos POS_NORTH = new Pos(0.0,-1.0);
		final double x;
        final double y;
        public Pos(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public double magnitude() {
            return Math.sqrt(x*x+y*y);
        }
        public Pos sub(Pos other) {
            return new Pos(x-other.x, y-other.y);
        }
        public Pos add(Pos other) {
            return new Pos(x+other.x, y+other.y);
        }
		private Pos mul(double k) {
			return new Pos(k*x,k*y);
		}
        public double dist(Pos other) {
            return sub(other).magnitude();
        }
        public double dotProduct(Pos other) {
        	return x*other.x + y*other.y;
        }
        public Pos norm1() {
        	if (POS0.equals(this)) {
        		return POS0;
        	}
        	return mul(1.0/magnitude());
        }
        public double angle(Pos other) {
        	double result = Math.atan2(y, x) - Math.atan2(other.y, other.x) ;
        	if (result < -Math.PI) {
        		result = result + 2*Math.PI;
        	}
        	if (result > Math.PI) {
        		result = result - 2*Math.PI;
        	}
        	return result;
        }
        public double degAngle(Pos other) {
        	return 360*angle(other)/(2.0*Math.PI);
        }
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(x);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(y);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pos other = (Pos) obj;
			if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
				return false;
			if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
				return false;
			return true;
		}
		public Pos rot(double degAlpha) {
			double angle = degAlpha*Math.PI/180.0;
			double cosA = Math.cos(angle);
			double sinA = Math.sin(angle);
			return new Pos(x*cosA-y*sinA, x*sinA+y*cosA);
		}
        @Override public String toString() { return "("+r(x)+","+r(y)+")"; }
		private String r(double d) {
			String result = Double.toString(d);
			if (result.endsWith(".0")) {
				result = result.substring(0, result.length()-2);
			}
			return result;
		}
    }

    public static record World(Ship[] ships, List<Pos> checkpoints, int laps) {}
    
    public static record Move(Pos target, String speed) {
    	public Move(Pos target, int speed) {
    		this(target, Integer.toString(speed));
    	}
    }
    

    
    static class Ship {
    	int id;
    	Strategy strategy;
    	int cntCPs;
    	Pos pos;
    	Pos v;
    	int angle;
    	int cpID;
    	public Ship(int id) {
    		this.id = id;
    		this.strategy = null;
    		this.cntCPs = 0;
    		this.pos = Pos.POS0;
    		this.v = Pos.POS0;
    		this.angle = 0;
    		this.cpID = 0;
    	}
    	public void update(Pos newPos, Pos newV, int newAngle, int newCpID) {
    		pos = newPos;
    		v = newV;
    		angle = newAngle;
    		if (cpID != newCpID) {
    			cntCPs++;
    		}
    		cpID = newCpID;
    	}
    	@Override public String toString() {
    		return "SHP-"+id+"["+pos+"->"+v+"("+angle+"°)|"+cntCPs+"]";
    	}
    }
    
    public static Pos findClosestPos(Pos curr, Pos targ, Pos next, double radius) {
    	if (next == null) {
    		return targ;
    	}
   	    Pos vn1 = next.sub(curr).norm1();
   	    Pos vt = targ.sub(curr);
   	    Pos pnearest = vn1.mul(vn1.dotProduct(vt)).add(curr);
   	    Pos vnearest = pnearest.sub(targ);
   	    if (vnearest.magnitude() > radius) {
   	    	pnearest = vnearest.norm1().mul(radius).add(targ);
   	    }
   	    return pnearest;
    }

	private static Pos calcTarget(Pos pos, Pos next, Pos next2, Pos move) {
		Pos vn = next.sub(pos);
		Pos vn2 = next2.sub(next);
		double angle = vn.degAngle(vn2);
		if (Math.abs(angle) > 90) {
			if (angle < -90) {
				vn2 = vn.rot(-90.0);
				System.err.println("ANG:"+angle+" rot:"+vn.degAngle(vn2));
			}
			else if (angle > 90) {
				vn = vn2.rot(90.0);
				System.err.println("ANG:"+angle+" rot:"+vn.degAngle(vn2));
			}
		}
		double dist = vn.magnitude();
		double speed = move.magnitude();
//		System.err.println("angle: "+angle);
//		System.err.println("dist: "+dist);
//		System.err.println("speed: "+speed);
		if (dist <= 4000) {
			return next;
		}
		Pos result = vn2.norm1().mul(-0.4*dist).add(next);
		return result;
	} 
	
	private static Pos correctDirection(Pos curr, Pos target, Pos move) {
		if (Pos.POS0.equals(move)) {
			return target;
		}
//		System.err.println("CURR:"+curr+" TARGET:"+target+" MOVE:"+move);
		Pos vt = target.sub(curr);
		Pos vt1 = vt.norm1();
//		System.err.println("VT:"+vt+" VT1:"+vt1);
		double dotProd = vt1.dotProduct(move);
		if (dotProd<=0.0) {
			return target;
		}
//		System.err.println("VT1 o move:"+dotProd);
		Pos pnearest = vt1.mul(dotProd);
		Pos vnearest = pnearest.sub(move);
//		System.err.println("PNEAREST:"+pnearest+" VNEAREST:"+vnearest);
		Pos moveMirrored = move.add(vnearest.mul(2.0));
//		System.err.println("MOVEMIRR:"+moveMirrored);
		Pos result = curr.add(moveMirrored.norm1().mul(vt.magnitude()));
//		System.err.println("RESULT:"+result);
		return result;
	}


	private static double calcBreakDist(Pos curr, Pos targ, Pos next, double v) {
//		System.err.println("V:"+v);
		if (v<100.0) {
			return 0;
		}
   	    Pos vn = next.sub(curr);
   	    Pos vt = targ.sub(next);
		double ang = Math.abs(vn.degAngle(vt)/180.0); 
		double result = BREAK_FACTOR*v*ang;
//		System.err.println("VN:"+vn+" VT:"+vt+"ANG:"+ang+" RESULT:"+result);
		return result;
	}
	
	private static double calcSpeed(Pos pos, Pos target, Pos move, double v) {
		double ang = Math.abs(target.sub(pos).degAngle(move));
		double result; 
		if (ang<20.0) {
			result = 100;
		}
		else if (ang<45.0) {
			result = 100*ANGLE_FACTOR;
		}
		else if (ang<60) {
			return 100*ANGLE_FACTOR*ANGLE_FACTOR;
		}
		else if (ang<90) {
			return 100*ANGLE_FACTOR*ANGLE_FACTOR*ANGLE_FACTOR;
		}
		else {
			return 20;
		}
		return result;
	}


    static List<Pos> checkpoints = new ArrayList<>();


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int laps = in.nextInt();
//        System.err.println("LAPS: "+laps);
        int checkpointCount = in.nextInt();
//        System.err.println("CPS#: "+checkpointCount);
        for (int i = 0; i < checkpointCount; i++) {
            int checkpointX = in.nextInt();
            int checkpointY = in.nextInt();
            Pos cp = new Pos(checkpointX, checkpointY);
            checkpoints.add(cp);
//            System.err.println("CP: "+cp);
        }
        Ship[] ships = new Ship[4];
        for (int i=0; i<ships.length; i++) {
        	ships[i] = new Ship(i);
        }
        World world = new World(ships, checkpoints, laps);

        // new DoNothingStrategy(ships[0], world);
        new DirectToCheckpointStrategy(ships[0], world, 50);
        new RunCircleStrategy(ships[1], world, 90, 180, 100);
        
        boolean boosted = false;
        // game loop
        while (true) {
        	
            for (int i = 0; i < 4; i++) {
                int x = in.nextInt(); // x position of your pod
                int y = in.nextInt(); // y position of your pod
                int vx = in.nextInt(); // x speed of your pod
                int vy = in.nextInt(); // y speed of your pod
                int angle = in.nextInt(); // angle of your pod 0 = east
                int nextCheckPointId = in.nextInt(); // next check point id of your pod
                world.ships[i].update(new Pos(x,y), new Pos(vx,vy), angle, nextCheckPointId);
            }
            Move m = world.ships[0].strategy.move();
            String cmd = (int)m.target.x + " " + (int)m.target.y + " "+m.speed;
            System.out.println(cmd);
            m = world.ships[1].strategy.move();
            cmd = (int)m.target.x + " " + (int)m.target.y + " "+m.speed;
            System.out.println(cmd);
            
//            for (int i = 0; i < 2; i++) {
//                Pos pos = ships[i].pos;
//                Pos move = ships[i].v;
//                int angle = ships[i].angle;
//                int nextCheckPointId = ships[i].cpID;
//                Pos nextCheckpoint = checkpoints.get(nextCheckPointId);
//                double nextCheckpointAngle = new Pos(1.0, 0.0).degAngle(nextCheckpoint.sub(pos));
//                double nextCheckpointDist = nextCheckpoint.sub(pos).magnitude();
//                double v = move.magnitude();
//
//                Pos target;
//                double speed; 
//
//                if (i==0) {
//                	Pos targetT = attackBest(ships[i], ships[2], ships[3]);
//	                target = correctDirection(pos, targetT, move);
//	                speed = 100;
//	                if (ships[i].goMiddle) {
//	                	speed = 50;
//	                }
//                }
//                else {
//	                Pos nextTg = checkpoints.get((nextCheckPointId+1)%checkpointCount);
////	                System.err.println("CURR:"+pos+" next:"+nextCheckpoint+" next2:"+nextTg);
//	                
//	                Pos targetC = findClosestPos(pos, nextCheckpoint, nextTg, TARGET_RADIUS);
//	                if (nextTg != null) {
////	                	System.err.println("CLOSEST:"+targetC+" rel:"+targetC.sub(nextTg));
//	                }
//	                
//	                Pos targetT = calcTarget(pos, targetC, nextTg, move);
////	                System.err.println("CALC:"+targetT+" rel:"+targetT.sub(targetC));
//	                
//	                target = correctDirection(pos, targetT, move);
////	                System.err.println("CORR:"+target+" rel:"+target.sub(targetC));
//	
//	                speed = calcSpeed(pos, target, move, v);
//	
////	                double breakDist = calcBreakDist(pos, nextCheckpoint, nextTg, v);
////	                if (nextCheckpointDist<breakDist) {
////	                	speed = 5;
////	                }
//                }
//                
//                
//                String speedStr = Integer.toString((int)speed);
//                if (!boosted) {
//                	speedStr = "BOOST";
//                }
//                String cmd = (int)target.x + " " + (int)target.y + " "+speedStr;
//
////                System.err.println("dist: "+nextCheckpointDist);
////                System.err.println("angle: "+nextCheckpointAngle);
////                System.err.println("v: "+v);
//
//                System.out.println(cmd);
//                
//            }
//        	boosted = true;
        }
    }

//	private static Pos attackBest(Ship self, Ship shipA, Ship shipB) {
//		Ship ship = shipA.isBetter(shipB) ? shipA : shipB;
//		if (self.goMiddle) {
//			System.err.println("GOMIDDLE");
//			double angle = Math.abs(ship.pos.sub(self.pos).degAngle(ship.v));
//			if (angle >= 90) {
//				self.goMiddle = false;
//			}
//			Pos result = ship.next2CP;
//			if (result.dist(self.pos) < 3000) {
//				self.goMiddle = false;
//			}
//			return result;
//		}
//		System.err.println("ATTACK: "+ship);
//		if (ship.v.magnitude() >= self.v.magnitude()) {
//			System.err.println("ENEMY IS FASTER: "+ship.v.magnitude()+" ("+self.v.magnitude()+")");
//			double angle = Math.abs(ship.pos.sub(self.pos).degAngle(ship.v));
//			System.err.println("ANGLE: "+angle);
//			if (angle < 10.0) {
//				System.err.println("ACTIVATE GOMIDDLE ship:"+ship.v+" self:"+self.v+" dist:"+ship.pos.sub(self.pos));
//				self.goMiddle = true;
//				self.cntMiddle = 0;
//			}
//		}
//		double dist = ship.pos.dist(self.pos);
//		double speed = self.v.magnitude();
//		double enemySpeed = ship.v.magnitude();
//		double hitDist = Math.min(10, enemySpeed/(speed+1));
//		Pos target = ship.pos.add(ship.v.mul(hitDist));
//		return target;
//	}
    
    
    public static abstract class Strategy {
    	World world;
    	Ship self;
    	Ship partner;
    	Ship enemy1;
    	Ship enemy2;
    	public Strategy(Ship self, World world) {
    		this.world = world;
    		this.self = self;
    		this.partner = (self == world.ships[0]) ? world.ships[1] : world.ships[0];
    		this.enemy1 = world.ships[2];
    		this.enemy2 = world.ships[3];
    		this.self.strategy = this;
    	}
		protected Pos nextCP(Ship ship) { return nextCP(ship, 0); }
		protected Pos nextCP(Ship ship, int n) { return checkpoints.get((ship.cpID + n) % checkpoints.size()); }
		protected boolean isBetter(Ship ship, Ship compareShip) {
    		if (ship.cntCPs > compareShip.cntCPs) {
    			return true;
    		}
    		if (ship.cntCPs < compareShip.cntCPs) {
    			return false;
    		}
    		if (ship.pos.dist(nextCP(ship)) <= compareShip.pos.dist(nextCP(compareShip))) {
    			return true;
    		}
    		return false;
    	}
    	public abstract Move move();
    }

    public static class DoNothingStrategy extends Strategy {
		public DoNothingStrategy(Ship self, World world) {
			super(self, world);
		}
		@Override public Move move() {
			return new Move(self.pos, 0);
		}
    }

    public static class DirectToCheckpointStrategy extends Strategy {
    	int speed;
		public DirectToCheckpointStrategy(Ship self, World world, int speed) {
			super(self, world);
			this.speed = speed;
		}
		@Override public Move move() {
			Pos target = nextCP(self);
			return new Move(target, speed);
		}
    }

    public static class RunCircleStrategy extends Strategy {
    	Pos startCyclePos;
    	double rotAngle;
    	int startCycleSpeed;
    	int accelerateSpeed;
		public RunCircleStrategy(Ship self, World world, double rotAngle, int startCycleSpeed, int accelerateSpeed) {
			super(self, world);
			this.rotAngle = rotAngle;
			this.startCycleSpeed = startCycleSpeed;
			this.accelerateSpeed = accelerateSpeed;
			this.startCyclePos = Pos.POS0; 
		}
		@Override public Move move() {
			Pos move = self.v;
			if (move.equals(Pos.POS0)) {
				move = Pos.POS_EAST;
//				move = nextCP(self).sub(self.pos).norm1();
//				move = move.rot(self.angle);
			}
			if ((startCycleSpeed!=0) && (move.magnitude() >= startCycleSpeed)) {
				startCycleSpeed = 0;
				startCyclePos = self.pos;
			}
			Pos rotMove = move;
			int speed = Math.min(100, (int)(startCycleSpeed/0.85+2-move.magnitude()));
			if (startCycleSpeed == 0) {
				rotMove = rotMove.rot(rotAngle);
				speed = accelerateSpeed;
			}
			Pos rotMove1000 = rotMove.norm1().mul(1000);
			Pos target = self.pos.add(rotMove1000);
			
			System.err.println("----- RUNCIRCLE -----");
			System.err.println(self.pos.sub(startCyclePos));
			System.err.println(self);
			System.err.println("MOVE:    "+move);
			System.err.println("ROT:     "+rotMove);
			System.err.println("ROT1000: "+rotMove1000);
			System.err.println("TARGET:  "+target+" sp="+accelerateSpeed);
			System.err.println("----- --------- -----");
			return new Move(target, speed);
		}
    }
    
}