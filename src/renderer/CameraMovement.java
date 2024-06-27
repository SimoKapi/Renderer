package renderer;
import java.awt.event.KeyListener;

public class CameraMovement {
	public void Update() {
//		Vector3 forwardVector = getForwardVector();
//		Main.camera.position = Main.camera.position.add(forwardVector.multiply(0.1));
	}
	
	Vector3 getForwardVector() {
		Vector3 forwardVector = new Vector3(0, 0, 0);
		Vector3 cameraRotation = Main.camera.rotation.clone();
		
		forwardVector.x = Math.cos(Math.toRadians(cameraRotation.x)) * Math.sin(Math.toRadians(cameraRotation.y));
		forwardVector.y = -Math.sin(Math.toRadians(cameraRotation.x));
		forwardVector.z = Math.cos(Math.toRadians(cameraRotation.x)) * Math.cos(Math.toRadians(cameraRotation.y));
		
		return forwardVector;
	}
	
	Vector3 getRightVector() {
		Vector3 rightVector = new Vector3(0, 0, 0);
		Vector3 cameraRotation = Main.camera.rotation.clone();
		
		rightVector.x = Math.cos(Math.toRadians(cameraRotation.x)) * Math.sin(Math.toRadians(cameraRotation.y));
		rightVector.y = -Math.sin(Math.toRadians(cameraRotation.x));
		rightVector.z = Math.cos(Math.toRadians(cameraRotation.x)) * Math.cos(Math.toRadians(cameraRotation.y));
				
		return rightVector;
	}
	
	Vector3 getUpVector(Vector3 forwardVector) {
		return new Vector3(forwardVector.x, -forwardVector.z, forwardVector.y);
	}
	
	public void move(Vector2 inputAxis) {
		Vector3 forwardVector = getForwardVector();
		Vector3 rightVector = getRightVector();
		
		Main.camera.position = Main.camera.position.add(forwardVector.multiply(inputAxis.y));
		Main.camera.position = Main.camera.position.add(rightVector.multiply(inputAxis.x));
	}
}