import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class GraphicTest {

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

	
	public static void main(String[] args)
	{
		Pos move = new Pos(30, 30);
		Pos pos = new Pos(100, 100);
		Pos next = new Pos(200, 200);
		Pos next2 = new Pos(300, 100);
		
		Pos target = calcTarget(pos, next, next2, move);
		
	    final String title = "Test Window";
	    final int width = 1200;
	    final int height = width / 16 * 9;

	    //Creating the frame.
	    JFrame frame = new JFrame(title);

	    frame.setSize(width, height);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(false);
	    frame.setVisible(true);

	    //Creating the canvas.
	    Canvas canvas = new Canvas();

	    canvas.setSize(width, height);
	    canvas.setBackground(Color.WHITE);
	    canvas.setVisible(true);
	    canvas.setFocusable(false);


	    //Putting it all together.
	    frame.add(canvas);

	    canvas.createBufferStrategy(3);

	    boolean running = true;

	    BufferStrategy bufferStrategy;
	    Graphics graphics;

	    while (running) {
	        bufferStrategy = canvas.getBufferStrategy();
	        graphics = bufferStrategy.getDrawGraphics();
	        graphics.clearRect(0, 0, width, height);

	        graphics.setColor(Color.RED);
	        Pos prev = pos.sub(move);
	        graphics.drawLine((int)prev.x, (int)prev.y, (int)pos.x, (int)pos.y);
	        graphics.setColor(Color.BLUE);
	        graphics.drawLine((int)pos.x, (int)pos.y, (int)next.x, (int)next.y);
	        graphics.drawLine((int)next.x, (int)next.y, (int)next2.x, (int)next2.y);
	        graphics.setColor(Color.GREEN);
	        graphics.drawLine((int)pos.x, (int)pos.y, (int)target.x, (int)target.y);

	        bufferStrategy.show();
	        graphics.dispose();
	    }
	}


	private static Pos calcTarget(Pos pos, Pos next, Pos next2, Pos move) {
		Pos vn = next.sub(pos);
		Pos vn2 = next2.sub(next);
		double angle = vn.degAngle(vn2);
		double dist = vn.magnitude();
		double speed = move.magnitude();
		System.out.println("angle: "+angle);
		System.out.println("dist: "+dist);
		System.out.println("speed: "+speed);
		if (dist <= 2* speed) {
			return next;
		}
		Pos result = vn2.norm1().mul(-speed).add(next);
		return result;
	}
}
