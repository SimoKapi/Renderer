package renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

public class GUI extends JPanel {
	private static final long serialVersionUID = 1L;
	List<Triangle> tris = Main.tris;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		Matrix rotationX = rotX(Math.toRadians(Main.camera.rotation.x));
		Matrix rotationY = rotY(Math.toRadians(Main.camera.rotation.y));
		Matrix rotationZ = rotZ(Math.toRadians(Main.camera.rotation.z));
		
		
		Matrix rotMultiplier = rotationZ.multiply(rotationY).multiply(rotationX);

		Vector3 e = new Vector3(0, 0, Main.FOV);
		
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		java.util.HashMap<Vector2, Double> zBuffer = new java.util.HashMap<Vector2, Double>();
		
		for (Triangle t : tris) {
			g2d.setColor(t.color);
			Vector2[] projectedVertices = new Vector2[3];
			Vector3[] vertices = new Vector3[] {t.v1, t.v2, t.v3};
			for (int i = 0; i < vertices.length; i++) {
				Vector3 vertexRelative = rotMultiplier.transform(vertices[i]).subtract(Main.camera.position);
				int nextIndex = i < vertices.length - 1 ? i + 1 : 0;
				Vector3 nextVertexRelative = rotMultiplier.transform(vertices[nextIndex]).subtract(Main.camera.position);

				Vector3[] splitPoints = pointSplit(vertexRelative, nextVertexRelative, 100);
				
				for (int splitIndex = 0; splitIndex < splitPoints.length - 1; splitIndex++) {
					if (!inPyramid(splitPoints[splitIndex + 1])) continue;
					
					Vector2 projectionA = projection2D(e, splitPoints[splitIndex]);
					Vector2 projectionB = projection2D(e, splitPoints[splitIndex + 1]);
					
					g2d.drawLine((int) projectionA.x, (int) projectionA.y, (int) projectionB.x, (int) projectionB.y);
				}
			}
						
//			zBuffer.put(v1Proj, Math.max(v1Relative.z, zBuffer.get(v1Proj) != null ? zBuffer.get(v1Proj) : 0));
//			zBuffer.put(v2Proj, Math.max(v2Relative.z, zBuffer.get(v2Proj) != null ? zBuffer.get(v2Proj) : 0));
//			zBuffer.put(v3Proj, Math.max(v3Relative.z, zBuffer.get(v3Proj) != null ? zBuffer.get(v3Proj) : 0));			

//			int minX = (int) Math.max(Math.min(v1Proj.x, Math.min(v2Proj.x, v3Proj.x)), 0);
//			int maxX = (int) Math.min(Math.max(v1Proj.x, Math.max(v2Proj.x, v3Proj.x)), getWidth());
//			int minY = (int) Math.max(Math.min(v1Proj.y, Math.min(v2Proj.y, v3Proj.y)), 0);
//			int maxY = (int) Math.min(Math.max(v1Proj.y, Math.max(v2Proj.y, v3Proj.y)), getHeight());
//			double triZ = (v1Relative.z + v2Relative.z + v3Relative.z)/3;
//			for (int x = minX; x < maxX; x++) {
//				for (int y = minY; y < maxY; y++) {
//					if (inTriangle(x, y, v1Proj, v2Proj, v3Proj)) {
//						Vector2 currentXY = new Vector2(x, y);
//						if (!zBuffer.keySet().contains(currentXY) || zBuffer.get(currentXY) > triZ) {
//							zBuffer.put(currentXY, triZ);
//							img.setRGB(x, y, t.color.getRGB());
//						}
//					}
//				}
//			}
		}
		g2d.drawImage(img, 0, 0, null);
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
		int screenWidth = getWidth();
		int screenHeight = getHeight();
		
		Vector3[] cornerPoints = new Vector3[] {
			new Vector3(0, screenHeight, point.z),
			new Vector3(screenWidth, screenHeight, point.z),
			new Vector3(screenWidth, 0, point.z),
			new Vector3(0, 0, point.z)
		};
		
		System.out.println(cornerPoints[0]);
		
		return (Vector3.vector(point, cornerPoints[0]).x + Vector3.vector(point, cornerPoints[1]).x == screenWidth && Vector3.vector(point, cornerPoints[0]).y + Vector3.vector(point, cornerPoints[2]).y == screenHeight);
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
		
		return new Vector2(x += getWidth()/2, y += getHeight()/2);
		
//		Vector3 f = new Matrix(new double[][] {
//			new double[] {1, 0, e.x/e.z},
//			new double[] {0, 1, e.y/e.z},
//			new double[] {0, 0, 1/e.z}
//		}).transform(d);
//		
//		return new Vector2(f.x/f.z, f.y/f.z);
	}
	
	public double trianglePointDepth() {
		return 0;
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
	
	public Vector3 convert(Matrix in) {
		return new Vector3(in.values[0][0], in.values[1][0], in.values[2][0]);
	}
}
