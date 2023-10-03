import java.util.*;

import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player06GoldBumper1500 {

	static final double TARGET_RADIUS = 0.0;
	static final double BREAK_FACTOR = 2.0;
	static final double ANGLE_FACTOR = 0.95;
	
    public static class Pos {
        public static final Pos POS0 = new Pos(0.0,0.0);
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
        	double result = Math.atan2(other.y, other.x) - Math.atan2(y, x);
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
        @Override public String toString() { return "("+x+","+y+")"; }
    }

    static class Ship {
    	int id;
    	int cntCPs;
    	Pos pos;
    	Pos v;
    	int angle;
    	int cpID;
    	Pos nextCP;
    	boolean goMiddle;
    	int cntMiddle;
    	public Ship(int id) {
    		this.id = id;
    		this.cntCPs = 0;
    		this.pos = Pos.POS0;
    		this.v = Pos.POS0;
    		this.angle = 0;
    		this.cpID = 0;
    		this.nextCP = Pos.POS0;
    		this.goMiddle = false;
    		this.cntMiddle = 0;
    	}
    	public void update(Pos newPos, Pos newV, int newAngle, int newCpID, Pos newNextCP) {
    		if (cpID != newCpID) {
    			cntCPs++;
    		}
    		cpID = newCpID;
    		nextCP = newNextCP;
    		pos = newPos;
    		angle = newAngle;
    		v = newV;
    	}
    	public boolean isBetter(Ship other) {
    		if (cntCPs > other.cntCPs) {
    			return true;
    		}
    		if (cntCPs < other.cntCPs) {
    			return false;
    		}
    		if (pos.dist(nextCP) <= other.pos.dist(other.nextCP)) {
    			return true;
    		}
    		return false;
    	}
    	@Override public String toString() {
    		return "SHP-"+id+"["+pos+":"+cntCPs+"]";
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
	
	private static double calcSpeed(Pos pos, Pos nextCheckpoint, Pos move, double nextCheckpointAngle, double v) {
		double ang = Math.abs(nextCheckpointAngle);
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
			return 5;
		}
		return result;
	}


    static Map<Pos, Pos> nextTarget = new LinkedHashMap<>();
    static List<Pos> checkpoints = new ArrayList<>();


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int laps = in.nextInt();
        System.err.println("LAPS: "+laps);
        int checkpointCount = in.nextInt();
        System.err.println("CPS#: "+checkpointCount);
        for (int i = 0; i < checkpointCount; i++) {
            int checkpointX = in.nextInt();
            int checkpointY = in.nextInt();
            Pos cp = new Pos(checkpointX, checkpointY);
            checkpoints.add(cp);
            System.err.println("CP: "+cp);
        }

        Ship[] ships = new Ship[4];
        for (int i=0; i<ships.length; i++) {
        	ships[i] = new Ship(i);
        }
        boolean boosted = false;
        // game loop
        while (true) {
        	
            for (int i = 0; i < 4; i++) {
                int x = in.nextInt(); // x position of your pod
//                System.err.println("X: "+x);
                int y = in.nextInt(); // y position of your pod
//                System.err.println("Y: "+y);
                int vx = in.nextInt(); // x speed of your pod
//                System.err.println("VX: "+vx);
                int vy = in.nextInt(); // y speed of your pod
//                System.err.println("VY: "+vy);
                int angle = in.nextInt(); // angle of your pod
//                System.err.println("ANG: "+angle);
                int nextCheckPointId = in.nextInt(); // next check point id of your pod
//                System.err.println("NCP: "+nextCheckPointId);
                ships[i].update(new Pos(x,y), new Pos(vx,vy), angle, nextCheckPointId, checkpoints.get(nextCheckPointId));
            }
            for (int i = 0; i < 2; i++) {
                Pos pos = ships[i].pos;
                Pos move = ships[i].v;
                int angle = ships[i].angle;
                int nextCheckPointId = ships[i].cpID;
                Pos nextCheckpoint = checkpoints.get(nextCheckPointId);
                double nextCheckpointAngle = new Pos(1.0, 0.0).degAngle(nextCheckpoint.sub(pos));
                double nextCheckpointDist = nextCheckpoint.sub(pos).magnitude();
                double v = move.magnitude();

                Pos target;
                double speed; 

                if (i==0) {
                	target = attackBest(ships[i], ships[2], ships[3]);
                }
                else {
	                Pos nextTg = checkpoints.get((nextCheckPointId+1)%checkpointCount);
//	                System.err.println("CURR:"+pos+" next:"+nextCheckpoint+" next2:"+nextTg);
	                
	                Pos targetC = findClosestPos(pos, nextCheckpoint, nextTg, TARGET_RADIUS);
	                if (nextTg != null) {
//	                	System.err.println("CLOSEST:"+targetC+" rel:"+targetC.sub(nextTg));
	                }
	                
	                Pos targetT = calcTarget(pos, targetC, nextTg, move);
//	                System.err.println("CALC:"+targetT+" rel:"+targetT.sub(targetC));
	                
	                target = correctDirection(pos, targetT, move);
//	                System.err.println("CORR:"+target+" rel:"+target.sub(targetC));
	
//	                System.err.println("ANGLE: "+nextCheckpointAngle+" / "+angle);
	                speed = calcSpeed(pos, nextCheckpoint, move, nextCheckpointAngle, v);
	
	                double breakDist = calcBreakDist(pos, nextCheckpoint, nextTg, v);
	                if (nextCheckpointDist<breakDist) {
	                	speed = 5;
	                }
                }
                
                speed = 100;
                if (ships[i].goMiddle) {
                	speed = 50;
                }
                
                String speedStr = Integer.toString((int)speed);
                if (!boosted) {
                	speedStr = "BOOST";
                }
                String cmd = (int)target.x + " " + (int)target.y + " "+speedStr;

//                System.err.println("dist: "+nextCheckpointDist);
//                System.err.println("angle: "+nextCheckpointAngle);
//                System.err.println("v: "+v);

                System.out.println(cmd);
                
            }
        	boosted = true;
        }
    }

	private static Pos attackBest(Ship self, Ship shipA, Ship shipB) {
		Ship ship = shipA.isBetter(shipB) ? shipA : shipB;
		if (self.goMiddle) {
			System.err.println("GOMIDDLE");
			if (self.cntMiddle++>20) {
				self.goMiddle = false;
			}
			Pos result = ship.nextCP;
			if (result.dist(self.pos) < 3000) {
				self.goMiddle = false;
			}
			return result;
		}
		System.err.println("ATTACK: "+ship);
		if (ship.v.magnitude() >= self.v.magnitude()) {
			System.err.println("ENEMY IS FASTER: "+ship.v.magnitude()+" ("+self.v.magnitude()+")");
			double angle = Math.abs(ship.pos.sub(self.pos).degAngle(ship.v));
			System.err.println("ANGLE: "+angle);
			if (angle < 10.0) {
				System.err.println("ACTIVATE GOMIDDLE ship:"+ship.v+" self:"+self.v+" dist:"+ship.pos.sub(self.pos));
				self.goMiddle = true;
			}
		}
		double dist = ship.pos.dist(self.pos);
		double speed = self.v.magnitude();
		double enemySpeed = ship.v.magnitude();
		double hitDist = Math.min(10, enemySpeed/(speed+1));
		Pos target = ship.pos.add(ship.v.mul(hitDist));
		return target;
	}
}