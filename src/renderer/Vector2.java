package renderer;

public class Vector2 {
	double x, y;
	Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return new String(x + ", " + y);
	}
}
