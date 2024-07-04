package renderer;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

public class ProjectionCalculations {
	Vector3 e;
	HashMap<Vector2, Color> pointsCollection;
	public Color[][] currentView;
	int screenWidth, screenHeight;
		
	public List<Triangle> loadTris() {
		return Main.tris;
	}
	
	public void initializeValues(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		currentView = new Color[screenHeight][screenWidth];
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
		currentView = new Color[screenHeight][screenWidth];
		HashMap<Vector2[], Color> result = new HashMap<Vector2[], Color>();
		pointsCollection = new HashMap<Vector2, Color>();
		
		List<Triangle> tris = loadTris();
		Matrix rotMultiplier = getRotationMultiplier();
		
		double maxDepth = Float.NEGATIVE_INFINITY;
		for (Triangle t: tris) {
			for (Vector3 vertex : new Vector3[] {t.v1, t.v2, t.v3}) {
				Vector3 localVertex = rotMultiplier.transform(vertex.subtract(Main.camera.position));;
				maxDepth = Math.max(localVertex.z, maxDepth);
			}
		}
		
		HashMap<List<Vector3>, Color> projectedTris = new HashMap<List<Vector3>, Color>();
		for (Triangle t : tris) {
			Vector3[] localVertices = new Vector3[] {rotMultiplier.transform(t.v1.subtract(Main.camera.position)),
					rotMultiplier.transform(t.v2.subtract(Main.camera.position)),
					rotMultiplier.transform(t.v3.subtract(Main.camera.position)),};
			
			if (!normalFacingCamera(localVertices)) {
				continue;
			}
			
			List<Vector3> trianglePoints = new ArrayList<Vector3>();
			
			for (int i = 0; i < localVertices.length; i++) {
				Vector3 vertexRelative = localVertices[i];
				int nextIndex = i < localVertices.length - 1 ? i + 1 : 0;
				Vector3 nextVertexRelative = localVertices[nextIndex];
				if (vertexRelative.z < 0 && nextVertexRelative.z < 0) {
					continue;
				}
								
				Vector3 clippedVertexRelative = clipToZ(nextVertexRelative, vertexRelative);
				Vector3 clippedNextVertexRelative = clipToZ(vertexRelative, nextVertexRelative);
				
				Vector3[] splitPoints;
				
				if (vertexRelative.z > 0) {
					splitPoints = new Vector3[] {clippedNextVertexRelative};
				} else {
					splitPoints = new Vector3[] {clippedVertexRelative, clippedNextVertexRelative};
				}
				
//				Vector3[] splitPoints = pointSplit(vertexRelative, nextVertexRelative, (int) Math.round(Math.max((maxDepth * 2)/vertexRelative.z, 200)));
				for (int pointIndex = 0; pointIndex < splitPoints.length; pointIndex++) {
					Vector2 projection = projection2D(e, splitPoints[pointIndex]);
					int nextPoint = i < splitPoints.length - 1 ? i + 1 : 0;
					Vector2 nextProjection = projection2D(e, splitPoints[nextPoint]);
					Vector3 addition = new Vector3(projection.x, projection.y, nextVertexRelative.z);
					
					Boolean containsAddition = false;
					for (Vector3 point : trianglePoints) {
						if (Vector3.isEqual(point,  addition)) containsAddition = true;
					}
					if (!containsAddition) trianglePoints.add(addition);
						
					Vector2[] pointsTuple = new Vector2[] {projection, nextProjection};
					result.put(pointsTuple,  t.color);
				}
			}
			System.out.println(trianglePoints.size());
			projectedTris.put(trianglePoints, t.color);
		}
		pointsCollection = calculateRenderPoints(projectedTris);
		return result;
	}
	
	Vector3 clipToZ(Vector3 v1, Vector3 v2) {
		if (v2.z > 0) return v2;
		Vector3 directional = v2.subtract(v1);
		double multiplier = v1.z/Math.abs(directional.z);
		
		Vector3 result = v1.add(directional.multiply(multiplier));
		result.z += 0.00001;
		
		return result;
	}
			
	Boolean normalFacingCamera(Vector3[] corners) {		
		Vector3 normal = getNormalVector(corners);
		
		Vector3 vertex = corners[0];
		double dot_product = normal.multiply(vertex);
		
		return dot_product < 0;
	}
	
	Vector3 getNormalVector(Vector3[] corners) {
		Vector3 v1 = corners[1].subtract(corners[0]);
		Vector3 v2 = corners[2].subtract(corners[0]);
		
		double Nx = v1.y * v2.z - v1.z * v2.y;
		double Ny = v1.z * v2.x - v1.x * v2.z;
		double Nz = v1.x * v2.y - v1.y * v2.x;
		
		Vector3 normal = new Vector3(Nx, Ny, Nz);
		return normal;
	}
	
	HashMap<Vector2, Color> calculateRenderPoints(HashMap<List<Vector3>, Color> projectedTris) {
		int scaler = 1;
		Double[][] depthPoints = new Double[screenHeight][screenWidth];
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
					Double currentEntry = depthPoints[y][x];
					if ((currentEntry == null || currentEntry > depth) && inPyramid(new Vector3(x - screenWidth/2, y - screenHeight/2, e.z))) {
//					if ((currentEntry == null || currentEntry > depth)) {
						for (int i = 0; i < scaler; i++) {
							for (int j = 0; j < scaler; j++) {						
								try {									
									depthPoints[y + i][x + j] = depth;
									currentView[y + i][x + j] = projectedTris.get(corners);
								} catch (Exception e) {
									
								}
							}
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
	
	public Vector3[] pointSplit(Vector3 v1, Vector3 v2, int segmentCount) {
		Vector3[] result = new Vector3[] {v1, v2};
		if (inPyramid(v1) && inPyramid(v2)) return result;
				
		Vector3 vector = v2.subtract(v1);
				
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
	
	public Boolean inTriangle(Vector2 point, Vector2[] vertices) {
		return inTriangle(point, vertices[0], vertices[1], vertices[2]);
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
