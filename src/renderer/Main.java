package renderer;

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
	public static Vector3 inputAxis = new Vector3(0, 0, 0);
		
	static String inputFile = "/Users/simo/desktop/intersect.stl";
	
	public static void main(String[] args) {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				switch (e.getID()) {
				case KeyEvent.KEY_PRESSED:
					if (e.getKeyCode() == KeyEvent.VK_W) {
						Main.inputAxis.z = 1;
					}
					if (e.getKeyCode() == KeyEvent.VK_S) {
						Main.inputAxis.z = -1;
					}
					if (e.getKeyCode() == KeyEvent.VK_A) {
						Main.inputAxis.x = -1;
					}
					if (e.getKeyCode() == KeyEvent.VK_D) {
						Main.inputAxis.x = 1;
					} 
					if (e.getKeyCode() == KeyEvent.VK_E) {
						Main.inputAxis.y = 1;
					}
					if (e.getKeyCode() == KeyEvent.VK_Q) {
						Main.inputAxis.y = -1;
					} 
					break;
					
				case KeyEvent.KEY_RELEASED:
					if (e.getKeyCode() == KeyEvent.VK_W) {
						Main.inputAxis.z = 0;
					}
					if (e.getKeyCode() == KeyEvent.VK_S) {
						Main.inputAxis.z = 0;
					}
					if (e.getKeyCode() == KeyEvent.VK_A) {
						Main.inputAxis.x = 0;
					}
					if (e.getKeyCode() == KeyEvent.VK_D) {
						Main.inputAxis.x = 0;
					} 
					if (e.getKeyCode() == KeyEvent.VK_E) {
						Main.inputAxis.y = 0;
					}
					if (e.getKeyCode() == KeyEvent.VK_Q) {
						Main.inputAxis.y = 0;
					} 
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
			cameraMovement.move(inputAxis);
			taskTime = System.currentTimeMillis();
			panel.Update();
			cameraRotation.Update();
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