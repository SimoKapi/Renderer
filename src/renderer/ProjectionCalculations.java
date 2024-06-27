package renderer;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

public class ProjectionCalculations {
	Vector3 e;
	HashMap<Vector2[], Double> zBuffer;
	int screenWidth, screenHeight;
	
	public List<Triangle> loadTris() {
		return Main.tris;
	}
	
	public void initializeValues(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	
	public Matrix getRotationMultiplier() {
		Matrix rotationX = rotX(Math.toRadians(Main.camera.rotation.x));
		Matrix rotationY = rotY(Math.toRadians(Main.camera.rotation.y));
		Matrix rotationZ = rotZ(Math.toRadians(Main.camera.rotation.z));
		
		
		Matrix rotMultiplier = rotationZ.multiply(rotationY).multiply(rotationX);
		return rotMultiplier;
	}
	
	public HashMap<Vector2[], Color> getFinalImage() {
		e = new Vector3(0, 0, screenWidth/Math.toDegrees(Math.tan(Math.toRadians(Main.FOV/2))));
		
		HashMap<Vector2[], Color> result = new HashMap<Vector2[], Color>();
		zBuffer = new HashMap<Vector2[], Double>();
		
		List<Triangle> tris = loadTris();
		Matrix rotMultiplier = getRotationMultiplier();
		
		double minZ = Float.POSITIVE_INFINITY;
		double maxZ = Float.NEGATIVE_INFINITY;
		for (Triangle t: tris) {
			double avZ = 0;
			for (Vector3 vertex : new Vector3[] {t.v1, t.v2, t.v3}) {
				Vector3 localVertex = rotMultiplier.transform(vertex.subtract(Main.camera.position));
				avZ += Math.abs(localVertex.z);
			}
			avZ = avZ/3;
			
			minZ = Math.min(avZ, minZ);
			maxZ = Math.max(avZ, maxZ);
		}
		
		maxZ += minZ;
				
		HashMap<Vector2[], Color> projectedTris = new HashMap<Vector2[], Color>();
		for (Triangle t : tris) {
			Vector3[] vertices = new Vector3[] {t.v1, t.v2, t.v3};
			Vector2[] trianglePoints = new Vector2[vertices.length];
			
			for (int i = 0; i < vertices.length; i++) {
				Vector3 vertexRelative = rotMultiplier.transform(vertices[i].subtract(Main.camera.position));
				int nextIndex = i < vertices.length - 1 ? i + 1 : 0;
				Vector3 nextVertexRelative = rotMultiplier.transform(vertices[nextIndex].subtract(Main.camera.position));
				
				Vector3[] splitPoints = pointSplit(vertexRelative, nextVertexRelative, (int) Math.max((maxZ * 2)/vertexRelative.z, 20));
								
				for (int splitIndex = 0; splitIndex < splitPoints.length - 1; splitIndex++) {
					if (!inPyramid(splitPoints[splitIndex + 1]) || !inPyramid(splitPoints[splitIndex])) continue;
					
					Vector2 projectionA = projection2D(e, splitPoints[splitIndex]);
					Vector2 projectionB = projection2D(e, splitPoints[splitIndex + 1]);
					
					trianglePoints[i] = projectionA;
					
					Vector2[] pointsTuple = new Vector2[] {projectionA, projectionB};
					
					if (zBuffer.get(pointsTuple) == null || zBuffer.get(pointsTuple) < splitPoints[splitIndex].z) {
						zBuffer.put(pointsTuple, splitPoints[splitIndex].z);
						result.put(pointsTuple, t.color);
					}
				}
			}
			projectedTris.put(trianglePoints, t.color);
		}
		return result;
	}
		
	public HashMap<Vector2[], Double> getZBuffer() {
		return zBuffer;
	}
	
	public Vector3[] pointSplit(Vector3 v1, Vector3 v2, int segmentCount) {
		Vector3 vector = v2.subtract(v1);
		Vector3[] result = new Vector3[segmentCount + 1];
		result[0] = v1;
		
		for (int i = 1; i < segmentCount + 1; i++) {
			result[i] = v1.add(vector.divide(segmentCount).multiply(i));
		}
		
		return result;
	}
	
	public Boolean inPyramid(Vector3 point) {
		double x = Math.abs(Math.toDegrees(Math.tan(Math.toRadians(Main.FOV/2))) * point.z)/2;
		double y = ((double) screenHeight/screenWidth) * x;
				
		return (Math.abs(Math.abs(x - point.x) + Math.abs(-x - point.x) - Math.abs(2 * x)) < 0.00001 &&
				Math.abs(Math.abs(y - point.y) + Math.abs(-y - point.y) - Math.abs(2 * y)) < 0.00001 &&
				point.z > 0);
	}
	
	public Boolean inTriangle(int x, int y, Vector2 v1, Vector2 v2, Vector2 v3) {
		float triArea = area(v1, v2, v3);
		float area1 = area(new Vector2(x, y), v2, v3);
		float area2 = area(v1, new Vector2(x, y), v3);
		float area3 = area(v1, v2, new Vector2(x, y));
		return (Math.abs(triArea - (area1 + area2 + area3)) < 0.1);
	}
	
	float area(Vector2 t1, Vector2 t2, Vector2 t3) {
		return (float) Math.abs((t1.x * (t2.y - t3.y) + t2.x * (t3.y - t1.y) + t3.x * (t1.y - t2.y))/2);
	}
	
	public Vector2 projection2D(Vector3 e, Vector3 d) {
		double x = (e.z/d.z)*d.x + e.x;
		double y = (e.z/d.z)*d.y + e.y;
		
		return new Vector2(x += screenWidth/2, y += screenHeight/2);
			}
	
	public Matrix rotX(double val) {
		return new Matrix(new double[][] {
    		new double[] {1, 0, 0},
    		new double[] {0, Math.cos(val), -Math.sin(val)},
    		new double[] {0, Math.sin(val), Math.cos(val)}
	    });
	}
	
	public Matrix rotY(double val) {
		return new Matrix(new double[][] {
    		new double[] {Math.cos(val), 0, Math.sin(val)},
    		new double[] {0, 1, 0},
    		new double[] {-Math.sin(val), 0, Math.cos(val)}
	    });
	}
	
	public Matrix rotZ(double val) {
		return new Matrix(new double[][] {
    		new double[] {Math.cos(val), -Math.sin(val), 0},
    		new double[] {Math.sin(val), Math.cos(val), 0},
    		new double[] {0, 0, 1}
	    });
	}
}
