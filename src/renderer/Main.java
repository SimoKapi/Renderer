package renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class Main {
	public static int frameRate = 20;
	public static int FOV = 60;
	final static List<Triangle> tris = new ArrayList<Triangle>();
	public static Camera camera = new Camera(new Vector3(0, 0, -100), Vector3.zero, new Vector2(1920, 1080));
	public static GUI panel;
	public static JSlider distanceSlider, ySlider, FOVSlider;
	private static JFrame frame;
	private static JLabel label;
	
	public static void main(String[] args) {
		Main m = new Main();
		m.doStuff();
		long taskTime = 0;
		long sleepTime = 1000/frameRate;
		while (true) {
		  taskTime = System.currentTimeMillis();
		  m.Update();
		  taskTime = System.currentTimeMillis()-taskTime;
		  if (sleepTime-taskTime > 0 ) {
		    try {
				Thread.sleep(sleepTime-taskTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		  }
		}
	}
	
	void Update() {
		camera.rotation.y = ySlider.getValue();
		label.setText("Rotation: " + camera.rotation.toString() + " Position: " + camera.position.toString());
		panel.repaint();
		resolutionLabel.setText(frame.getWidth() + "x" + frame.getHeight());
	}
	
	JLabel resolutionLabel;
	public void doStuff() {
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


		frame = new JFrame("Renderer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		distanceSlider = new JSlider(SwingConstants.VERTICAL, -100, 100, 0);
		frame.add(distanceSlider, BorderLayout.WEST);
        ySlider = new JSlider(-180, 180, 0);
        frame.add(ySlider, BorderLayout.SOUTH);
        FOVSlider = new JSlider(SwingConstants.VERTICAL, 0, 100, 50);
        frame.add(FOVSlider, BorderLayout.EAST);
		
		panel = new GUI();

		distanceSlider.addChangeListener(e -> changeDistance());
		ySlider.addChangeListener(e -> panel.repaint());
		FOVSlider.addChangeListener(e -> changeFOV());
		frame.add(panel);
		
		JPanel infoPanel = new JPanel();
		label = new JLabel(camera.rotation.toString());
		infoPanel.add(label, BorderLayout.WEST);
		
		JSeparator sep = new JSeparator(JSeparator.VERTICAL);
		infoPanel.add(sep);
		
		resolutionLabel = new JLabel(camera.resolution.toString());
		infoPanel.add(resolutionLabel, BorderLayout.EAST);
		
		frame.add(infoPanel, BorderLayout.NORTH);
		frame.setSize((int)camera.resolution.x, (int)camera.resolution.y);
		frame.setVisible(true);
	}
	
	void changeFOV() {
		FOV = FOVSlider.getValue();
	}
	
	void changeDistance() {
		camera.position.z = distanceSlider.getValue();
		panel.repaint();
	}
}