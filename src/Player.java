import java.util.*;
import java.io.*;
import java.math.*;

class Player {

	public static double TARGET_RADIUS = 500.0; 
	public static double BREAK_FACTOR = 5.0; 
	
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


	private static double calcBreakDist(Pos curr, Pos targ, Pos next, Pos move, double v) {
		
		if (v<300) {
			return 0;
		}
   	    Pos vt = targ.sub(curr);
		double angMoveTarg = Math.abs(move.angle(vt));
		System.err.println("VT:"+vt+" MV:"+move+" ANGMOV:"+(360.0*angMoveTarg/2/Math.PI));
    	if (angMoveTarg*180.0/Math.PI>90.0) {
    		return 999999; 
    	}
    	if (v>=0) {
    		return 3*v;
    	}
    	if (next == null) {
	    	if (angMoveTarg*180.0/Math.PI>90.0) {
	    		System.err.println("RESULT:"+-1);
	    		return -1; 
	    	}
	    	return 4*v;
    	}

   	    Pos vn = next.sub(curr);
		double ang = Math.abs(vn.angle(vt));

		double result;
		if (ang*180.0/Math.PI<45.0) {
			result = 0;
		}
		else if (angMoveTarg*180.0/Math.PI>90.0) {
    		System.err.println("RESULT:"+-1);
    		return -1; 
    	}
		else {
			result = 4*v;
		}
		System.err.println("VN:"+vn+" ANG:"+(360.0*ang/2/Math.PI)+" RESULT:"+result);
		return result;
	}
	

    static Map<Pos, Pos> nextTarget = new HashMap<>();

    public static void main(final String args[]) {
        final Scanner in = new Scanner(System.in);

        // game loop
        int cntTargets = 0;
        Pos lastCheckpoint = null;
        Pos lastPos = null;
        boolean boosted = false;
        
        while (true) {
            final int x = in.nextInt();
            final int y = in.nextInt();
            final int nextCheckpointX = in.nextInt(); // x position of the next check point
            final int nextCheckpointY = in.nextInt(); // y position of the next check point
            final int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            final int nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
            final int opponentX = in.nextInt();
            final int opponentY = in.nextInt();

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            Pos pos = new Pos(x, y);
            Pos nextCheckpoint = new Pos(nextCheckpointX, nextCheckpointY);
            
            if (!nextCheckpoint.equals(lastCheckpoint)) {
            	if (lastCheckpoint != null) {
            		nextTarget.put(lastCheckpoint, nextCheckpoint);
            	}
            	lastCheckpoint = nextCheckpoint;
                System.err.println("TARGET: "+nextCheckpoint);
            }
            
            Pos move = (lastPos==null) ? Pos.POS0 : pos.sub(lastPos);
            double v = move.magnitude();
            lastPos = pos;
            double speed; 
            
            Pos nextTg = nextTarget.get(nextCheckpoint);
            System.err.println("CURR:"+pos+" next:"+nextCheckpoint+" next2:"+nextTg);
            
            Pos targetC = findClosestPos(pos, nextCheckpoint, nextTg, TARGET_RADIUS);
            if (nextTg != null) {
            	System.err.println("CLOSEST:"+targetC+" rel:"+targetC.sub(nextTg));
            }
            
            Pos target = correctDirection(pos, targetC, move);
            System.err.println("CORR:"+target+" rel:"+target.sub(targetC));
            
            double breakDist = calcBreakDist(pos, target, nextTg, move, v);
            speed = 100.0;
            if (breakDist == -1) {
            	target = pos.sub(move);
            }
            else if (nextCheckpointDist < breakDist) {
                speed = 5.0;
            }
            String speedStr = Integer.toString((int)speed);
            if (!boosted) {
            	speedStr = "BOOST";
            	boosted = true;
            }
            String cmd = (int)target.x + " " + (int)target.y + " "+speedStr;

            System.err.println("dist: "+nextCheckpointDist);
            System.err.println("angle: "+nextCheckpointAngle);
            System.err.println("v: "+v);

            System.out.println(cmd);
        }
    }

}