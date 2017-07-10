package csun.c182L;

/**
 * Represents a 2D vector
 * 
 * @author Milaud Miremadi
 *
 */
public class Vec2 {

	int x, y;

	public Vec2() {
	
	}
	
	public Vec2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vec2(Vec2 v) {
		this(v.x, v.y);
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void set(Vec2 v) {
		this.x = v.x;
		this.y = v.y;
	}

	public Vec2 sub(Vec2 v) {
		return new Vec2(x - v.x, y - v.y);
	}

	public Vec2 sub(int sx, int sy) {
		return new Vec2(x - sx, y - sy);
	}

	public double mag() {
		return Math.sqrt(x * x + y * y);
	}

	public boolean eq(Vec2 v) {
		return v.x == x && v.y == y;
	}

	public boolean eq(int x, int y) {
		return this.x == x && this.y == y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public boolean inBounds(int minX, int minY, int maxX, int maxY) {
		return x >= minX && x < maxX && y >= minY && y < maxY;
	}

}
