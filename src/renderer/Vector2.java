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
	
	public Vector2 subtract(Vector2 v2) {
		return new Vector2(this.x - v2.x, this.y - v2.y);
	}
	
	public Vector2 add(Vector2 v2) {
		return new Vector2(this.x + v2.x, this.y + v2.y);
	}
	
	public Vector2 divide(double x) {
		return new Vector2(this.x/x, this.y/x);
	}
	
	public Vector2 multiply(double x) {
		return new Vector2(this.x * x, this.y * x);
	}
	
	public double multiply(Vector2 v2) {
		return (this.x * v2.x + this.y * v2.y);
	}

}
