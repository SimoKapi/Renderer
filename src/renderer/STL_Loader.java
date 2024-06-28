package renderer;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class STL_Loader {
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
	
	public List<Triangle> loadSTL(String path) {
		List<Triangle> tris = new ArrayList<Triangle>();
		try {
			String content = readASCIIFile(path);
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
		    	tris.add(new Triangle(triPoints[0], triPoints[1], triPoints[2], Color.RED));
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tris;

	}
	
	public static String readASCIIFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}
	
	/*
	 * No longer necessary, instead import an ASCII STL file with vertices and triangles
	 * 
	 * @deprecated use loadSTL(String path) instead
	 */
	@Deprecated
	public static List<Triangle> initializeTris() {
		List<Triangle> tris = new ArrayList<Triangle>();
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
		
		return tris;
	}

}
