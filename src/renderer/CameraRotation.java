package renderer;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
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
				previousMousePosition = mousePosition;
				return;
			} else {
				previousMousePosition = mousePosition;
				return;
			}
		} catch (Exception e) {
			
		}
	}
}