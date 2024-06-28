package renderer;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.Point;

public class CameraRotation {
	Vector2 previousMousePosition;
	double sensitivity = 0.5;
	public void Update() {
		try {
			Point point = MouseInfo.getPointerInfo().getLocation();
			Vector2 mousePosition = new Vector2(point.x, point.y);
			
			if (previousMousePosition != null) {
				Vector2 diff = mousePosition.subtract(previousMousePosition);
				Main.camera.rotation.x -= diff.y * sensitivity;
				Main.camera.rotation.y += diff.x * sensitivity;
				
				while (Main.camera.rotation.x > 360) Main.camera.rotation.x -= 360;
				while (Main.camera.rotation.y > 360) Main.camera.rotation.y -= 360;
				
				previousMousePosition = mousePosition;
				return;
			} else {
				previousMousePosition = mousePosition;
				return;
			}
		} catch (Exception e) {
			
		}
	}
	
	boolean mouseDown = false;

	public void mousePressed(MouseEvent e) {
	    if (e.getButton() == MouseEvent.BUTTON1) {
	        mouseDown = true;
	    }
	}

	public void mouseReleased(MouseEvent e) {
	    if (e.getButton() == MouseEvent.BUTTON1) {
	        mouseDown = false;
	    }
	}

}