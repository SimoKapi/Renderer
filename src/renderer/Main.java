package renderer;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	public static int frameRate = 20;
	public static double FOV = 10;
	final static List<Triangle> tris = new ArrayList<Triangle>();
	public static Camera camera = new Camera(new Vector3(0, 0, -10), Vector3.zero, new Vector2(1920, 1080));
	public static GUI panel;
	static CameraMovement cameraMovement = new CameraMovement();
	static CameraRotation cameraRotation = new CameraRotation();
	public static long updateTime;
	
	static Color[] colors = {
		Color.RED,
		Color.BLUE,
		Color.CYAN,
		Color.GRAY,
		Color.PINK,
		Color.GREEN,
		Color.MAGENTA,
		Color.ORANGE,
		Color.YELLOW,
		Color.WHITE
	};
	
	static String inputFile = "/Users/simo/desktop/test.stl";
	
	public static void main(String[] args) {
		try {
			String content = readASCIIFile(inputFile);
			List<Integer> indexes = new ArrayList<Integer>();
		    int index = content.indexOf("vertex");
		    while (index >= 0) {
		    	indexes.add(index);
		    	index = content.indexOf("vertex", index + 1);
		    }
		    
		    for (int i = 0; i < indexes.size() - 3; i += 0) {
		    	Vector3[] triPoints = new Vector3[3];
		    	for (int counter = 0; counter < 3; counter++) {
		    		String vertex = content.substring(indexes.get(i), content.indexOf('\n', indexes.get(i)));
		    		String[] parts = vertex.split(" ");
		    		Vector3 point = new Vector3(Double.parseDouble(parts[1]),
							    				Double.parseDouble(parts[2]),
					    						Double.parseDouble(parts[3]));
		    		triPoints[counter] = point;
		    		i++;
		    	}
		    	int rnd = new Random().nextInt(colors.length);
		    	tris.add(new Triangle(triPoints[0], triPoints[1], triPoints[2], colors[0]));
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
//		initializeTris();
		panel = new GUI();
		panel.createWindow();
		mainLoop();
	}
	
	public static void mainLoop() {
		long taskTime = 0;
		long sleepTime = 1000/frameRate;
		while (true) {
		  taskTime = System.currentTimeMillis();
		  panel.Update();
//		  cameraMovement.Update();
//		  cameraRotation.Update();
		  taskTime = System.currentTimeMillis()-taskTime;
		  updateTime = taskTime;
		  if (sleepTime-taskTime > 0 ) {
		    try {
				Thread.sleep(sleepTime-taskTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		  }
		}
	}
	
	public static String readASCIIFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}
		
	public static void initializeTris() {
		tris.add(new Triangle(new Vector3(-50, -50, 50),
				  new Vector3(-50, 50, 50),
				  new Vector3(50, 50, 50), Color.BLUE));
		tris.add(new Triangle(new Vector3(-50, -50, 50),
				  new Vector3(50, 50, 50),
				  new Vector3(50, -50, 50), Color.BLUE));

		tris.add(new Triangle(new Vector3(-50, -50, -50),
				  new Vector3(-50, 50, -50),
				  new Vector3(50, 50, -50), Color.RED));
		tris.add(new Triangle(new Vector3(-50, -50, -50),
				  new Vector3(50, 50, -50),
				  new Vector3(50, -50, -50), Color.RED));

		tris.add(new Triangle(new Vector3(-50, -50, 50),
				  new Vector3(-50, -50, -50),
				  new Vector3(-50, 50, -50), Color.WHITE));
		tris.add(new Triangle(new Vector3(-50, -50, 50),
				  new Vector3(-50, 50, 50),
				  new Vector3(-50, 50, -50), Color.WHITE));

		tris.add(new Triangle(new Vector3(50, -50, 50),
				  new Vector3(50, -50, -50),
				  new Vector3(50, 50, -50), Color.PINK));
		tris.add(new Triangle(new Vector3(50, -50, 50),
				  new Vector3(50, 50, 50),
				  new Vector3(50, 50, -50), Color.PINK));
	}
	
	public static void changeFOV(double value) {
		FOV = value;
	}
	
	public static void changeDistance(double value) {
		camera.position.z = value;
		panel.repaint();
	}
}