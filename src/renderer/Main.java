package renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class Main {
	public static int frameRate = 100;
	public static double FOV = 10;
	static List<Triangle> tris = new ArrayList<Triangle>();
	public static Camera camera = new Camera(new Vector3(0, 0, -10), Vector3.zero, new Vector2(1920, 1080));
	public static GUI panel;
	static CameraMovement cameraMovement = new CameraMovement();
	static CameraRotation cameraRotation = new CameraRotation();
	static STL_Loader stlLoader = new STL_Loader();
	public static long updateTime;
		
	static String inputFile = "/Users/simo/desktop/sphere.stl";
	
	public static void main(String[] args) {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				Vector2 inputAxis = new Vector2(0, 0);
				switch (e.getID()) {
				case KeyEvent.KEY_PRESSED:
					if (e.getKeyCode() == KeyEvent.VK_W) {
						inputAxis.y += 1;
					}
					if (e.getKeyCode() == KeyEvent.VK_S) {
						inputAxis.y -= 1;
					}
					if (e.getKeyCode() == KeyEvent.VK_A) {
						inputAxis.x -= 1;
					}
					if (e.getKeyCode() == KeyEvent.VK_D) {
						inputAxis.x += 1;
					} 
					cameraMovement.move(inputAxis);
					break;
				}
				return false;
			}
		});
		tris = stlLoader.loadSTL(inputFile);
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
		  cameraRotation.Update();
		  cameraMovement.Update();
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
		
	public static void changeFOV(double value) {
		FOV = value;
	}
	
	public static void changeDistance(double value) {
		camera.position.z = value;
		panel.repaint();
	}
}