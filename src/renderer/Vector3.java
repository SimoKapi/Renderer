package renderer;

public class Vector3 {
	double x, y, z;
	Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3 subtract(Vector3 v2) {
		return new Vector3(this.x - v2.x, this.y - v2.y, this.z - v2.z);
	}
	
	public Vector3 add(Vector3 v2) {
		return new Vector3(this.x + v2.x, this.y + v2.y, this.z + v2.z);
	}
	
	public static Vector3 zero = new Vector3(0, 0, 0);
	
	public String toString() {
		return new String(x + ", " + y + ", " + z);
	}
	
	public static double distance(Vector3 first, Vector3 second) {
		return (double) Math.sqrt(Math.pow(second.x - first.x, 2) + Math.pow(second.y - first.y, 2) + Math.pow(second.z - first.z, 2));
	}
}
