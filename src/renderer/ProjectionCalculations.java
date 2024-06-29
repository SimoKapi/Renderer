package renderer;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

public class ProjectionCalculations {
	Vector3 e;
	HashMap<Vector2, Double> zBuffer;
	HashMap<Vector2, Color> pointsCollection;
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
		pointsCollection = new HashMap<Vector2, Color>();
		zBuffer = new HashMap<Vector2, Double>();
		
		List<Triangle> tris = loadTris();
		Matrix rotMultiplier = getRotationMultiplier();
		
		double maxDepth = Float.NEGATIVE_INFINITY;
		for (Triangle t: tris) {
			double avZ = 0;
			for (Vector3 vertex : new Vector3[] {t.v1, t.v2, t.v3}) {
				Vector3 localVertex = rotMultiplier.transform(vertex.subtract(Main.camera.position));
				avZ += Math.abs(localVertex.z);
			}
			avZ = avZ/3;
			
			maxDepth = Math.max(avZ, maxDepth);
		}
		
		HashMap<List<Vector3>, Color> projectedTris = new HashMap<List<Vector3>, Color>();
		for (Triangle t : tris) {
			Vector3[] localVertices = new Vector3[] {rotMultiplier.transform(t.v1.subtract(Main.camera.position)),
					rotMultiplier.transform(t.v2.subtract(Main.camera.position)),
					rotMultiplier.transform(t.v3.subtract(Main.camera.position)),};
			
			if (!normalFacingCamera(localVertices)) continue;
			
			List<Vector3> trianglePoints = new ArrayList<Vector3>();
			
			for (int i = 0; i < localVertices.length; i++) {
				Vector3 vertexRelative = localVertices[i];
				int nextIndex = i < localVertices.length - 1 ? i + 1 : 0;
				Vector3 nextVertexRelative = localVertices[nextIndex];
				
				Vector3[] splitPoints = pointSplit(vertexRelative, nextVertexRelative, (int) Math.round(Math.max((maxDepth * 2)/vertexRelative.z, 200)));
				
				if (!inPyramid(splitPoints[0]) || !inPyramid(splitPoints[1])) continue;
					
				Vector2 projectionA = projection2D(e, splitPoints[0]);
				Vector2 projectionB = projection2D(e, splitPoints[1]);
								
//				Vector2 projectionA = projection2D(e, vertexRelative);
//				Vector2 projectionB = projection2D(e, nextVertexRelative);
								
				Vector3 additionA = new Vector3(projectionA.x, projectionA.y, splitPoints[0].z);
				Vector3 additionB = new Vector3(projectionB.x, projectionB.y, splitPoints[1].z);

//				Vector3 additionA = new Vector3(projectionA.x, projectionA.y, vertexRelative.z);
//				Vector3 additionB = new Vector3(projectionB.x, projectionB.y, nextVertexRelative.z);
				
				trianglePoints.add(additionA);
				trianglePoints.add(additionB);

				Vector2[] pointsTuple = new Vector2[] {projectionA, projectionB};
					
				result.put(pointsTuple, t.color);
			}
			projectedTris.put(trianglePoints, t.color);
		}
		pointsCollection = calculateRenderPoints(projectedTris);
		return result;
	}
	
	Boolean normalFacingCamera(Vector3[] corners) {
		Vector3 v1 = corners[1].subtract(corners[0]);
		Vector3 v2 = corners[2].subtract(corners[0]);
		
		double Nx = v1.y * v2.z - v1.z * v2.y;
		double Ny = v1.z * v2.x - v1.x * v2.z;
		double Nz = v1.x * v2.y - v1.y * v2.x;
		
		Vector3 normal = new Vector3(Nx, Ny, Nz);
		
		Vector3 vertex = corners[0];
		double dot_product = normal.multiply(vertex);
		System.out.println(dot_product);
		
		return dot_product < 0;
	}
	
	HashMap<Vector2, Color> calculateRenderPoints(HashMap<List<Vector3>, Color> projectedTris) {
		int scaler = 2;
		HashMap<Vector2, Double> depthPoints = new HashMap<Vector2, Double>();
		HashMap<Vector2, Color> renderPoints = new HashMap<Vector2, Color>();
		
		for (List<Vector3> corners : projectedTris.keySet()) {
			if (corners.size() < 3) continue;
			int minX = screenWidth;
			int maxX = 0;
			int minY = screenHeight;
			int maxY = 0;
			
			List<Vector2> flatCorners = new ArrayList<Vector2>();
			
			double depth = 0;
			for (Vector3 corner : corners) {
				flatCorners.add(new Vector2(corner.x, corner.y));
				minX = (int) Math.round(Math.max(Math.min(corner.x, minX), 0));
				maxX = (int) Math.round(Math.min(Math.max(corner.x, maxX), screenWidth));
				minY = (int) Math.round(Math.max(Math.min(corner.y, minY), 0));
				maxY = (int) Math.round(Math.min(Math.max(corner.y, maxY), screenHeight));
				depth += corner.z;
			}
			
			depth = depth/corners.size();
			
			for (int x = minX; x < maxX; x+=scaler) {
				for (int y = minY; y < maxY; y+=scaler) {
					Vector2 xyVector = new Vector2(x, y);
					if (!inPolygon(flatCorners, xyVector)) continue;
					Double currentEntry = depthPoints.get(xyVector);
					if (currentEntry == null || currentEntry > depth) {
						for (int i = 0; i < scaler; i++) {	
							Vector2 newVector = new Vector2(x + i, y + i);
							depthPoints.put(newVector, depth);
							renderPoints.put(newVector, projectedTris.get(corners));
						}
					}
				}
			}
		}
		return renderPoints;
	}
	
	public HashMap<Vector2, Color> getPointsCollection() {
		return pointsCollection;
	}
		
	public HashMap<Vector2, Double> getZBuffer() {
		return zBuffer;
	}
	
	void addToZBuffer(Vector2 point, double depth) {
		if (zBuffer.get(point) == null || zBuffer.get(point) > depth) {
			zBuffer.put(point, depth);
		}
	}
	
	void addToZBuffer(Vector2 point, double depth, Color color) {
		if (zBuffer.get(point) == null || zBuffer.get(point) > depth) {
			zBuffer.put(point, depth);
			pointsCollection.put(point, color);
		}
	}
	
	public Vector3[] pointSplit(Vector3 v1, Vector3 v2, int segmentCount) {
		Vector3 vector = v2.subtract(v1);
		Vector3[] result = new Vector3[] {v1, v2};
				
		for (int i = 1; i < segmentCount + 1; i++) {
			Vector3 newPoint = v1.add(vector.divide(segmentCount).multiply(i));
			if (inPyramid(newPoint)) {
				if (result[0] == v1) {
					result[0] = newPoint;
				} else {					
					result[1] = newPoint;
				}
			}
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
	
	Boolean inPolygon(List<Vector2> corners, Vector2 point) {
		if (corners.size() == 3) return inTriangle(point, corners.get(0), corners.get(1), corners.get(2));
		for (Vector2 corner1 : corners) {
			for (Vector2 corner2 : corners) {
				if (corner1 == corner2) continue;
				for (Vector2 corner3 : corners) {
					if (corner1 == corner3 || corner2 == corner3) continue;
					if (inTriangle(point, corner1, corner2, corner3)) return true;
				}
			}
		}
		return false;
	}
	
	public Boolean inTriangle(Vector2 point, Vector2 v1, Vector2 v2, Vector2 v3) {
		float triArea = area(v1, v2, v3);
		float area1 = area(point, v2, v3);
		float area2 = area(v1, point, v3);
		float area3 = area(v1, v2, point);
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
